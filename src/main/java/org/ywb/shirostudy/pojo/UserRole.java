package org.ywb.shirostudy.pojo;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @author XiaoRenwu
 * e-mail 18629015421@163.com
 * github https://github.com/xiao-ren-wu
 * @version 1
 * @since 2019/3/6 23:50
 */

@Getter
@Setter
public class UserRole {
    private Long id;
    private String roleName;
    private Long userName;

    private Date createTime;
    private Date updateTime;

    @Override
    public String toString() {
        return "UserRole{" +
                "id=" + id +
                ", roleName='" + roleName + '\'' +
                ", userName=" + userName +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
