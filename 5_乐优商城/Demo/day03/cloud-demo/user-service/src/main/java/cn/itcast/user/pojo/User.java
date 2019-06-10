package cn.itcast.user.pojo;

import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;

@Data
@Table(name = "tb_user")  //哪个表
public class User {

    //id
    @Id //主键
    @KeySql(useGeneratedKeys = true) //回显
    private Long id;

    //用户名
    @Column(name = "user_name") //数据库表字段与实体类不一致时用@Column注解。
    private String username;

    //密码
    private String password;

    //姓名
    private String name;

    //年龄
    private Integer age;

    //性别
    private Integer sex;

    //出生日期
    private Date birthday;

    //备注
    @Transient  //假如这个字段不是数据库中的字段用这个注解：瞬时的，意思就是不需要持久化的
    private String note;

    //创建日期
    private Date created;

    //更新日期
    private Date updated;
}
