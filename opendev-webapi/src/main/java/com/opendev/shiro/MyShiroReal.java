package com.opendev.shiro;

import com.opendev.model.Permission;
import com.opendev.model.Role;
import com.opendev.model.User;
import com.opendev.service.PermissionService;
import com.opendev.service.RoleService;
import com.opendev.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author mojiayi
 * @date 2019-10-17 16:55
 */
@Slf4j
public class MyShiroReal extends AuthorizingRealm {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private PermissionService permissionService;

    /**
     * 身份授权  (方法一)
     *
     * @param principalCollection
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        log.info("---------------- 执行 Shiro 权限获取 ---------------------");
        Object principal = principalCollection.getPrimaryPrincipal();
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        if (principal instanceof User) {
            User user = (User) principal;
            if(user != null){
                List<Role> roleList = roleService.getRoleByUserId(user.getUserId());
                if(CollectionUtils.isNotEmpty(roleList)){
                    for(Role role : roleList){
                        authorizationInfo.addRole(role.getRoleName());
                        List<Permission> permissionList = permissionService.getPermissionByRoleId(role.getRoleId());
                        if(CollectionUtils.isNotEmpty(permissionList)){
                            for (Permission permission : permissionList){
                                if(StringUtils.isNoneBlank(permission.getPerms())){
                                    authorizationInfo.addStringPermission(permission.getPerms());
                                }
                            }
                        }
                    }
                }
            }
        }
        log.info("---------------- 获取到以下权限 ----------------");
        log.info(authorizationInfo.getStringPermissions().toString());
        log.info("---------------- Shiro 权限获取成功 ----------------------");
        return authorizationInfo;
    }


    /**
     * 身份认证
     *
     * @param authenticationToken
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        log.info("---------------- 执行 Shiro 凭证认证 ----------------------");
        UsernamePasswordToken token = (UsernamePasswordToken) authenticationToken;
        String username = token.getUsername();

        User user = userService.getUserByUsername(username);
        if (user != null) {
            // 用户为禁用状态
            if (!user.getStatus().equals(0)) {
                throw new DisabledAccountException("你的账户已被禁用,请联系管理员激活");
            }
            log.info("---------------- Shiro 凭证认证成功 ----------------------");
            SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(
                    user, //用户
                    user.getPassword(), //密码
                    getName()  //realm name
            );
            return authenticationInfo;
        }
        throw new UnknownAccountException();
    }
}
