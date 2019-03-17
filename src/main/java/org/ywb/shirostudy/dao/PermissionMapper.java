package org.ywb.shirostudy.dao;

import java.util.Set;

/**
 * @author XiaoRenwu
 * e-mail 18629015421@163.com
 * github https://github.com/xiao-ren-wu
 * @version 1
 * @since 2019/3/6 23:53
 */

public interface PermissionMapper {
    /**
     * 通过角色名称找到该角色具备的权限
     * @param roleName 角色名称
     * @return 该角色具备的权限
     */
    Set<String> findPermissionByRoleName(String roleName);
}
