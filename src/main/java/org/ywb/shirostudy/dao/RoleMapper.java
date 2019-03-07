package org.ywb.shirostudy.dao;

import org.ywb.shirostudy.pojo.Role;

import java.util.Set;

/**
 * @author XiaoRenwu
 * e-mail 18629015421@163.com
 * github https://github.com/xiao-ren-wu
 * @version 1
 * @since 2019/3/6 23:53
 */

public interface RoleMapper {
    /**
     * 通过用户名称获取用户角色数据
     * @param userName 用户秘境
     * @return set<Role>
     */
    Set<String> getRolesByUserName(String userName);
}
