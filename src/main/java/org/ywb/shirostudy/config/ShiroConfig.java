package org.ywb.shirostudy.config;

import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.mgt.WebSecurityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.ywb.shirostudy.filter.RolesOrFilter;
import org.ywb.shirostudy.realm.CustomRealm;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author XiaoRenwu
 * e-mail 18629015421@163.com
 * github https://github.com/xiao-ren-wu
 * @version 1
 * @since 2019/3/8 0:09
 *
 *
 *
 *
 * Shiro内置的过滤器：
 * 认证：
 * anno------>不需要任何的认证
 * authBasic-->HttpBasic
 * authc------>需要认证之后才可以访问
 * user------->需要当前存在用户才可以访问
 * logout----->退出
 *
 * 授权：
 * perms---------->具有相应的权限才可以访问
 * roles---------->需要时xxx角色才可以访问
 * ssl------------>要求是安全的协议(https)才可以
 * port----------->要求制定的端口才可以访问
 *
 */


@Configuration
public class ShiroConfig {

    @Resource
    private RolesOrFilter rolesOrFilter;


    @Bean
    public ShiroFilterFactoryBean shiroFilter(WebSecurityManager securityManager){
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        //拦截器.
        Map<String,String> filterChainDefinitionMap = new LinkedHashMap<>();
        //配置退出过滤器,其中的具体的退出代码Shiro已经替我们实现了
        filterChainDefinitionMap.put("logout", "logout");
        filterChainDefinitionMap.put("/user/login", "anon");
        // authc:所有url都必须认证通过才可以访问; anon:所有url都可以匿名访问
        filterChainDefinitionMap.put("/user/**", "anon");
        filterChainDefinitionMap.put("/test/**", "authc");

        // 如果不设置默认会自动寻找Web工程根目录下的"/login.jsp"页面
        //shiroFilterFactoryBean.setLoginUrl("/login.html");

        //未授权跳转
        //shiroFilterFactoryBean.setUnauthorizedUrl("/page/fail.html");
        //shiroFilterFactoryBean.setSuccessUrl("/page/main.html");

        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);

        HashMap filterMap = new HashMap(16);
        filterMap.put("rolesOrFilter",rolesOrFilter);
        shiroFilterFactoryBean.setFilters(filterMap);
        return shiroFilterFactoryBean;
    }

    /**
     * 凭证匹配器
     * 由于我们的密码校验交给Shiro的SimpleAuthenticationInfo进行处理了
     * @return HashedCredentialsMatcher
     */
    @Bean
    public HashedCredentialsMatcher hashedCredentialsMatcher() {
        HashedCredentialsMatcher hashedCredentialsMatcher = new HashedCredentialsMatcher();
        //散列算法:这里使用MD5算法;
        hashedCredentialsMatcher.setHashAlgorithmName("md5");
        //散列的次数，比如散列两次，相当于 md5(md5(""));
        hashedCredentialsMatcher.setHashIterations(2);
        return hashedCredentialsMatcher;
    }


    @Bean
    public CustomRealm customRealm(HashedCredentialsMatcher hashedCredentialsMatcher) {
        CustomRealm customRealm = new CustomRealm();
        //使用加密
        customRealm.setCredentialsMatcher(hashedCredentialsMatcher);
        return customRealm;
    }

    @Bean
    public WebSecurityManager securityManager(CustomRealm customrealm) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(customrealm);
        return securityManager;
    }


    @Bean
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor(){
        return new LifecycleBeanPostProcessor();
    }


}














