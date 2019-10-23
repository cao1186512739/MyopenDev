package com.opendev.controller;

import com.opendev.base.APIResponse;
import com.opendev.enums.ResultStatusCode;
import com.opendev.model.User;
import com.opendev.util.JsonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author mojiayi
 * @date 2019-10-17 17:21
 */
@Api("登入")
@RestController
public class LoginController {


    @ApiOperation(value = "用户登录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "账号", required = true, dataType = "String", paramType = "form"),
            @ApiImplicitParam(name = "password", value = "密码", required = true, dataType = "String", paramType = "form"),
            @ApiImplicitParam(name = "rememberMe", value = "记住我", required = true, dataType = "Integer", paramType = "form")
    })
    @PostMapping("/login")
    public APIResponse login(@RequestParam("username") String username,
                             @RequestParam("password") String password,
                             @RequestParam("rememberMe") Integer rememberMe) {
        try {
            UsernamePasswordToken token = new UsernamePasswordToken(username, password);//根据账号和密码生成了token(令牌)
            Subject subject = SecurityUtils.getSubject();  //在SecurityUtils里面创建一个getSubject
            token.setRememberMe(rememberMe != 1 ? false : true);//设置是否记住我
            subject.login(token); //开始登入进行认证
            if (subject.isAuthenticated()) { ///如果已经认证了
                User user = (User) subject.getPrincipal();//就拿到这个用户的信息返回给前端
                return JsonResult.success(ResultStatusCode.SUCCESS.getCode(), "登录成功", user);
            } else {
                token.clear();//将已经生成的令牌清理
                return new APIResponse(ResultStatusCode.SHIRO_ERROR);
            }
        } catch (UnknownAccountException e) {
            return new APIResponse(ResultStatusCode.NOT_EXIST_USER);
        } catch (IncorrectCredentialsException e) {
            return new APIResponse(ResultStatusCode.ACCOUNT_OR_PWD_ERROR);
        } catch (LockedAccountException e) {
            return new APIResponse(ResultStatusCode.USER_FROZEN.getCode(), e.getMessage());
        }
    }


    @ApiOperation(value = "登出")
    @PostMapping("/logout")
    public APIResponse logout() {
        Subject subject = SecurityUtils.getSubject();
        if (subject != null) {
            subject.logout();
            return new APIResponse(ResultStatusCode.SUCCESS);
        }
        return new APIResponse(ResultStatusCode.ERROR);
    }
}
