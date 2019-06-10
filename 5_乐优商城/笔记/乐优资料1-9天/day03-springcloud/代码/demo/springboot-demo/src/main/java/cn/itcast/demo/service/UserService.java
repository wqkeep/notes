package cn.itcast.demo.service;

import cn.itcast.demo.mapper.UserMapper;
import cn.itcast.demo.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author: HuYi.Zhang
 * @create: 2018-06-14 17:54
 **/
@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;


    public User queryUserById(Long id){
        return this.userMapper.selectByPrimaryKey(id);
    }

    @Transactional
    public void insertUser(User user){
        // 新增用户
        this.userMapper.insert(user);
    }
}
