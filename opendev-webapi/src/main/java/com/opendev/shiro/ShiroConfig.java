package com.opendev.shiro;

import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.apache.shiro.mgt.SecurityManager;

import javax.servlet.Filter;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author mojiayi
 * @date 2019-10-17 16:51
 */
@Configuration
public class ShiroConfig {

    @Bean
    public ShiroFilterFactoryBean shiroFilter(SecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        //没有登陆的用户只能访问登陆页面，前后端分离中登录界面跳转应由前端路由控制，后台仅返回json数据
        shiroFilterFactoryBean.setLoginUrl("/common/unauth");
        //自定义拦截器
        //   Map<String, Filter> filtersMap = new LinkedHashMap<>();
        //限制同一帐号同时在线的个数。
        //     filtersMap.put("kickout", kickoutSessionControlFilter());
        //    shiroFilterFactoryBean.setFilters(filtersMap);
        //权限控制map.
        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<>();
        //swagger接口权限 开放
        filterChainDefinitionMap.put("/swagger-ui.html", "anon");
        filterChainDefinitionMap.put("/swagger-resources/**", "anon");
        filterChainDefinitionMap.put("/v2/api-docs", "anon");
        filterChainDefinitionMap.put("/webjars/springfox-swagger-ui/**", "anon");
        //公共请求接口
        filterChainDefinitionMap.put("/common/**", "anon");
        //登录方法，表示可以匿名访问
        filterChainDefinitionMap.put("/api/login/**", "anon");
        //此处需要添加一个kickout，上面添加的自定义拦截器才能生效
        //  filterChainDefinitionMap.put("/**", "authc, kickout");// 表示需要认证才可以访问
        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        return shiroFilterFactoryBean;
    }

    @Bean
    public SecurityManager securityManager() {
        DefaultWebSecurityManager defaultSecurityManager = new DefaultWebSecurityManager();
        defaultSecurityManager.setRealm(myShiroReal());
        return defaultSecurityManager;
    }

    @Bean
    public MyShiroReal myShiroReal() {
        MyShiroReal MyShiroReal = new MyShiroReal();
        MyShiroReal.setCredentialsMatcher(credentialsMatcher());//加入自定义注解
        return MyShiroReal;
    }

    /**
     * 自定义密码校验
     *
     * @return
     */
    @Bean
    public CredentialsMatcher credentialsMatcher() {
        return new CredentialsMatcher();
    }

    /***
     * 使授权注解起作用，如果这个不注入，则方法前面的注解不会生效，不会进入授权（doGetAuthorizationInfo）的方法
     * @param securityManager
     * @return
     */
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
        return authorizationAttributeSourceAdvisor;
    }

}
