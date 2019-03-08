package org.ywb.shirostudy.filter;

import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authz.AuthorizationFilter;
import org.springframework.stereotype.Component;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * @author XiaoRenwu
 * e-mail 18629015421@163.com
 * github https://github.com/xiao-ren-wu
 * @version 1
 * @since 2019/3/9 0:35
 *
 *
 * 自定义和认证相关的过滤器
 */

public class RolesOrFilter extends AuthorizationFilter {
    @Override
    protected boolean isAccessAllowed(ServletRequest servletRequest, ServletResponse servletResponse, Object o) throws Exception {
        String[] roles = (String[])o;
        Subject subject = getSubject(servletRequest, servletResponse);
        if(roles==null||roles.length==0){
            return true;
        }
        for(String role:roles){
            if(subject.hasRole(role)){
                return true;
            }
        }
        return false;
    }
}
