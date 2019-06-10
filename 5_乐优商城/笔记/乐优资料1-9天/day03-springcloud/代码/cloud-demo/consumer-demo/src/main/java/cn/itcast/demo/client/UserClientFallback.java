package cn.itcast.demo.client;

import cn.itcast.demo.pojo.User;
import org.springframework.stereotype.Component;

/**
 * @author: HuYi.Zhang
 * @create: 2018-06-17 15:52
 **/
@Component
public class UserClientFallback implements UserClient {
    @Override
    public User queryById(Long id) {
        User user = new User();
        user.setId(0L);
        user.setName("未知异常，服务器忙");
        return user;
    }
}
