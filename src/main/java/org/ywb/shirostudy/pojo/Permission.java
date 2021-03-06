package org.ywb.shirostudy.pojo;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @author XiaoRenwu
 * e-mail 18629015421@163.com
 * github https://github.com/xiao-ren-wu
 * @version 1
 * @since 2019/3/6 23:51
 */

@Getter
@Setter
public class Permission {
    private Integer id;
    private String roleName;
    private String permission;
    private Date createTime;
    private Date updateTime;

    @Override
    public String toString() {
        return "Permission{" +
                "id=" + id +
                ", roleName='" + roleName + '\'' +
                ", permission='" + permission + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
