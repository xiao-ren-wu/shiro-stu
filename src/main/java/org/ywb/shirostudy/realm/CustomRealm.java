package org.ywb.shirostudy.realm;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.stereotype.Component;
import org.ywb.shirostudy.dao.UserMapper;

import javax.annotation.Resource;

/**
 * @author XiaoRenwu
 * e-mail 18629015421@163.com
 * github https://github.com/xiao-ren-wu
 * @version 1
 * @since 2019/3/7 0:55
 */

@Component
public class CustomRealm extends AuthorizingRealm {

    @Resource
    private UserMapper userMapper;


    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        return null;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {

        //1.从主体传过来的认证信息中，获得用户名
        String userName = (String)token.getPrincipal();
        //通过用户名称到数据库中获取用户凭证
        String password = getPasswordByUserName(userName);
        return null;
    }

    private String getPasswordByUserName(String userName) {

        return null;
    }
}

