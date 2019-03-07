package org.ywb.shirostudy.realm;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.ywb.shirostudy.dao.RoleMapper;
import org.ywb.shirostudy.dao.UserMapper;

import javax.annotation.Resource;
import java.util.Set;

/**
 * @author XiaoRenwu
 * e-mail 18629015421@163.com
 * github https://github.com/xiao-ren-wu
 * @version 1
 * @since 2019/3/7 0:55
 */

public class CustomRealm extends AuthorizingRealm {

    @Resource
    private UserMapper userMapper;

    @Resource
    private RoleMapper roleMapper;

    {
        //加密
        HashedCredentialsMatcher matcher = new HashedCredentialsMatcher();
        //选取hash散列算法
        matcher.setHashAlgorithmName("md5");
        //设置算法执行次数
        matcher.setHashIterations(1);

        //将设置好的加密规则交给customRealm
        this.setCredentialsMatcher(matcher);
    }

    /**
     * 用于授权使用
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        //获取用户名
        String userName = (String) principals.getPrimaryPrincipal();
        Set<String> roles = getRolesByUserName(userName);
        Set<String> permission = getPermissionByUserName(userName);
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        authorizationInfo.setStringPermissions(permission);
        authorizationInfo.setRoles(roles);
        return authorizationInfo;
    }

    private Set<String> getPermissionByUserName(String userName) {
        return null;
    }

    private Set<String> getRolesByUserName(String userName) {
        return roleMapper.getRolesByUserName(userName);
    }

    /**
     * 用于做认证
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {

        //1.从主体传过来的认证信息中，获得用户名
        String userName = (String)token.getPrincipal();
        //通过用户名称到数据库中获取用户凭证
        String password = getPasswordByUserName(userName);
        //用户不存在返回null
        if(password==null){
            return null;
        }
        //用户存在创建返回对象
        SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(userName,password,"customRealm");
        //加盐
        authenticationInfo.setCredentialsSalt(ByteSource.Util.bytes("salt"));
        return authenticationInfo;
    }

    private String getPasswordByUserName(String userName) {
         return userMapper.findPasswordByUserName(userName);
    }
}

