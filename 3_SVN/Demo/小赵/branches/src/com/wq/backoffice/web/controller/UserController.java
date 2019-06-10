package com.wq.backoffice.web.controller;

import com.sun.deploy.util.ArrayUtil;
import com.sun.org.apache.xerces.internal.xs.datatypes.ObjectList;
import com.wq.backoffice.model.User;
import com.wq.backoffice.model.UserExt;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {

    @RequestMapping("/list")
//    @RequestMapping(value = "list", method = RequestMethod.POST)
    public String list(Model model) {

        List<User> userList = new ArrayList<User>();
        User user1 = new User("Cral",15,"男",new Date());
        User user2 = new User("Jim",16,"女",new Date());
        User user3 = new User("Boone",11,"男",new Date());
        userList.add(user1);
        userList.add(user2);
        userList.add(user3);

        user1.setId(1);
        user2.setId(2);
        user3.setId(3);

        System.out.println(userList);

        model.addAttribute("userList", userList);
        return "/user/userlist";

    }

    @RequestMapping("/toRegister")
    public String toRegister() {
        return "/user/register";
    }

    @RequestMapping("/register")
    public String register(User user) {
        System.out.println(user);
        return "/user/info";
    }

    @RequestMapping("/register3")
    public String register3(UserExt userExt) {
        System.out.println(userExt);
        return "/user/info";
    }

    @RequestMapping("/register4")
    public String register4(UserExt userExt) {
        System.out.println(userExt.getMapInfo());
        return "/user/info";
    }

    @RequestMapping("/userEdit")
    public String userEdit(int id,Model model) {
        System.out.println("接收到修改的id：" + id);

        //这儿应该是根据id到数据库查询用户，这里模拟就好
        User user1 = new User("Cral",15,"男",new Date());
        user1.setId(1);

        model.addAttribute("user", user1);

        return "/user/userEdit";
    }

    @RequestMapping("/userEdit2/{id}")
    public String userEdit2(@PathVariable int id, Model model) {
        System.out.println("接收到修改的id：" + id);

        //这儿应该是根据id到数据库查询用户，这里模拟就好
        User user1 = new User("Cral",15,"男",new Date());
        user1.setId(1);

        model.addAttribute("user", user1);

        return "/user/userEdit";
    }


    @RequestMapping("/test")
    public String test01() {
        return "forward:/user/list.do";
    }

    @RequestMapping("/test2")
    public String test02() {
        return "redirect:list.do";
    }


}
