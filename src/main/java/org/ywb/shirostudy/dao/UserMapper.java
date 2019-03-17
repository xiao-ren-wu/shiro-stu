package org.ywb.shirostudy.dao;

import org.ywb.shirostudy.pojo.User;

/**
 * @author XiaoRenwu
 * e-mail 18629015421@163.com
 * github https://github.com/xiao-ren-wu
 * @version 1
 * @since 2019/3/6 23:48
 */

public interface UserMapper {
    /**
     * 通过用户名获取用户密码
     * @param userName 用户姓名
     * @return 用户信息
     */
    User findPasswordByUserName(String userName);
}
