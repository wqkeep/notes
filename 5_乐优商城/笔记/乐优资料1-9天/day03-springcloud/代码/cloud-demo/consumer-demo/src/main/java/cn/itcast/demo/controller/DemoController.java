package cn.itcast.demo.controller;

import cn.itcast.demo.pojo.User;
import cn.itcast.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author: HuYi.Zhang
 * @create: 2018-06-15 17:52
 **/
@RestController
public class DemoController {

    @Autowired
    private UserService userService;

    @GetMapping("demo")
    public List<User> queryUserByIds(@RequestParam("ids") List<Long> ids){
        return this.userService.queryUserByIds(ids);
    }
}
