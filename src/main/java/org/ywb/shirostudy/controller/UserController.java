package org.ywb.shirostudy.controller;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.subject.Subject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.ywb.shirostudy.pojo.User;

/**
 * @author XiaoRenwu
 * e-mail 18629015421@163.com
 * github https://github.com/xiao-ren-wu
 * @version 1
 * @since 2019/3/8 0:40
 */

@RestController
@RequestMapping("/user")
public class UserController {


    @RequiresRoles("admin")
    @RequiresPermissions("xxx")
    @RequestMapping(value = "/login")
    public String login(User user){
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(user.getUser(), user.getPassword());
        try {
            subject.login(token);
        } catch (AuthenticationException e) {
            return e.getMessage();
        }
        //权限检查
//        if(subject.hasRole("admin")){
//            return "有admin权限";
//        }
        return "登陆成功";
    }

}
