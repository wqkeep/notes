package cn.itcast.demo.service;

import cn.itcast.demo.client.UserClient;
import cn.itcast.demo.dao.UserDao;
import cn.itcast.demo.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: HuYi.Zhang
 * @create: 2018-06-15 17:50
 **/
@Service
public class UserService {

    @Autowired
    // private UserDao userDao;
    private UserClient userClient;

    public List<User> queryUserByIds(List<Long> ids){
        List<User> users = new ArrayList<>();

        for (Long id : ids) {
            User user = this.userClient.queryById(id);
            users.add(user);
        }

        return users;
    }
}
