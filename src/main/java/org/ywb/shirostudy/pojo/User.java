package org.ywb.shirostudy.pojo;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @author XiaoRenwu
 * e-mail 18629015421@163.com
 * github https://github.com/xiao-ren-wu
 * @version 1
 * @since 2019/3/6 23:49
 */

@Getter
@Setter
public class User {
    private Long id;
    private String userName;
    private String password;
    private String salt;
    private Date updateTime;
    private Date createTime;

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", updateTime=" + updateTime +
                ", createTime=" + createTime +
                '}';
    }
}
