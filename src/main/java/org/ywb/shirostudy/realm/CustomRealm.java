package org.ywb.shirostudy.realm;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.stereotype.Component;
import org.ywb.shirostudy.dao.PermissionMapper;
import org.ywb.shirostudy.dao.UserRolesMapper;
import org.ywb.shirostudy.dao.UserMapper;
import org.ywb.shirostudy.pojo.User;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.Set;

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

    @Resource
    private UserRolesMapper roleMapper;

    @Resource
    private PermissionMapper permissionMapper;


    /**
     * 用于授权使用
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        //获取用户名
        String userName = (String) principals.getPrimaryPrincipal();
        Set<String> roles = getRolesByUserName(userName);
        Set<String> permission = getPermissionByRole(roles);
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        authorizationInfo.setStringPermissions(permission);
        authorizationInfo.setRoles(roles);
        return authorizationInfo;
    }

    private Set<String> getPermissionByRole(Set<String> roles) {
        if(roles==null){
            return null;
        }
        Set<String> permissions = new HashSet<>();
        roles.forEach(roleName->permissions.addAll(permissionMapper.findPermissionByRoleName(roleName)));
        return permissions;
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
        User user = getUserByUserName(userName);
        //用户不存在返回null
        if(user==null){
            return null;
        }
        //用户存在创建返回对象
        SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(userName,user.getPassword(),"customRealm");
        //加盐
        authenticationInfo.setCredentialsSalt(ByteSource.Util.bytes(user.getSalt()));
        return authenticationInfo;
    }

    private User getUserByUserName(String userName) {
         return userMapper.findPasswordByUserName(userName);
    }
}

