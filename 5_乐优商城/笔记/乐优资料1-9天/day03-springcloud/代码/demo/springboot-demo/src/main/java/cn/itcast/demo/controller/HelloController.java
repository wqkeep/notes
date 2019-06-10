package cn.itcast.demo.controller;

import cn.itcast.demo.pojo.User;
import cn.itcast.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.sql.DataSource;

/**
 * @author: HuYi.Zhang
 * @create: 2018-06-14 15:05
 **/
@Controller
public class HelloController {

//    @Autowired
//    private DataSource dataSource;

    @Autowired
    private UserService userService;

    @GetMapping("/user/{id}")
    @ResponseBody
    public User hello(@PathVariable("id") Long id) {
        User user = this.userService.queryUserById(id);

        return user;
    }
}
