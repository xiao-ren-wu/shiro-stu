package org.ywb.shirostudy.config;

import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.realm.text.IniRealm;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.mgt.WebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.ywb.shirostudy.cache.RedisCacheManager;
import org.ywb.shirostudy.filter.PermFilter;
import org.ywb.shirostudy.filter.RolesOrFilter;
import org.ywb.shirostudy.realm.CustomRealm;
import org.ywb.shirostudy.session.CustomSessionManager;
import org.ywb.shirostudy.session.RedisSessionDao;

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
 * <p>
 * <p>
 * <p>
 * <p>
 * Shiro内置的过滤器：
 * 认证：
 * anno------>不需要任何的认证
 * authBasic-->HttpBasic
 * authc------>需要认证之后才可以访问
 * user------->需要当前存在用户才可以访问
 * logout----->退出
 * <p>
 * 授权：
 * perms---------->具有相应的权限才可以访问
 * roles---------->需要时xxx角色才可以访问
 * ssl------------>要求是安全的协议(https)才可以
 * port----------->要求制定的端口才可以访问
 */

@Configuration
public class ShiroConfig {

    @Resource
    private RedisSessionDao sessionDao;

    @Resource
    private RedisCacheManager redisCacheManager;

    @Resource
    private CustomRealm customRealm;

    @Resource
    private IniRealm iniRealm;

    @Resource
    private RolesOrFilter rolesOrFilter;

    @Resource
    private PermFilter permFilter;

    @Bean
    public ShiroFilterFactoryBean shiroFilter(WebSecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        //拦截器
        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<>();
        // authc:所有url都必须认证(登录)通过才可以访问; anon:所有url都可以匿名访问
        filterChainDefinitionMap.put("/user/login", "anon");
        filterChainDefinitionMap.put("/**", "authc");
        shiroFilterFactoryBean.setLoginUrl("/user/need-login");
        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);

        //添加自己的过滤器
        HashMap<Object, Object> customFilterMap = new LinkedHashMap<>(16);
        customFilterMap.put("rolesOrFilter", rolesOrFilter);
        customFilterMap.put("permFilter", permFilter);

        /*
         * 设置自定义拦截器拦截路径
         * 如果多个拦截器拦截的路径相同，记得让拦截器的路径有点小差别
         * 因为map这个数据结构一个key只能对应一个value
         */
        /// filterChainDefinitionMap.put("/**/**","rolesOrFilter");
        /// filterChainDefinitionMap.put("/**/test","permFilter");
        return shiroFilterFactoryBean;
    }

    /**
     * 凭证匹配器
     * 由于我们的密码校验交给Shiro的SimpleAuthenticationInfo进行处理了
     *
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

    /**
     * 自定义Realm
     *
     * @param hashedCredentialsMatcher md5
     * @return customRealm
     */
    @Bean
    public CustomRealm customRealm(HashedCredentialsMatcher hashedCredentialsMatcher) {
        CustomRealm customRealm = new CustomRealm();
        //使用加密
        customRealm.setCredentialsMatcher(hashedCredentialsMatcher);
        return customRealm;
    }

    @Bean
    public WebSecurityManager securityManager(CustomSessionManager sessionManager) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        sessionManager.setSessionDAO(sessionDao);
        sessionManager.setSessionIdCookie(getSimpleCookie());
///        securityManager.setRealm(iniRealm);
        securityManager.setRealm(customRealm);
        securityManager.setSessionManager(sessionManager);
        securityManager.setCacheManager(redisCacheManager);
        return securityManager;
    }


    @Bean
    public IniRealm getIniRealm() {
        return new IniRealm("classpath:user.ini");
    }

    /**
     * 重新设置cookieId避免和系统自带的冲突
     *
     * @return cookie
     */
    private SimpleCookie getSimpleCookie() {
        SimpleCookie simpleCookie = new SimpleCookie("shiro-sessionId");
        simpleCookie.setPath("/");
        return simpleCookie;
    }

    /**
     * 开启shiro注解生效
     */
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(WebSecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
        return authorizationAttributeSourceAdvisor;

    }

    @Bean
    @ConditionalOnMissingBean
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator app = new DefaultAdvisorAutoProxyCreator();
        app.setProxyTargetClass(true);
        return app;

    }

}