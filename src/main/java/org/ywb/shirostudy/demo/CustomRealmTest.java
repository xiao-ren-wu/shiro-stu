package org.ywb.shirostudy.demo;

import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.junit.Test;
import org.ywb.shirostudy.realm.CustomRealm;

/**
 * @author XiaoRenwu
 * e-mail 18629015421@163.com
 * github https://github.com/xiao-ren-wu
 * @version 1
 * @since 2019/3/7 23:43
 */


public class CustomRealmTest {

    @Test
    public void test(){
        CustomRealm customRealm = new CustomRealm();
        DefaultSecurityManager defaultSecurityManager = new DefaultSecurityManager();
        defaultSecurityManager.setRealm(customRealm);

        //加密
        HashedCredentialsMatcher matcher = new HashedCredentialsMatcher();
        //选取hash散列算法
        matcher.setHashAlgorithmName("md5");
        //设置算法执行次数
        matcher.setHashIterations(1);

        //将设置好的加密规则交给customRealm
        customRealm.setCredentialsMatcher(matcher);

    }
}
