package cn.itcast.user.service;

import cn.itcast.user.mapper.UserMapper;
import cn.itcast.user.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    public User queryById(Long id) {
        int time = new Random().nextInt(2000);
        try {
            Thread.sleep(Long.valueOf(time + ""));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("访问用时：" + time);
        return this.userMapper.selectByPrimaryKey(id);
    }
}