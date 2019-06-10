package com.itheima.controller;

import com.itheima.domain.Items;
import com.itheima.service.ItemsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/items") //一级目录
public class ItemsController {

    @Autowired //spring容器帮我们自动注入一个值
    private ItemsService itemsService;

    @RequestMapping("/findDetail")
    public String findDetail(Model model) {//为了方便给页面传值，加个model
        Items items = itemsService.findById(1);
        //存值
        model.addAttribute("item", items);

        //需要跳转的页面
        return "/itemDetail";
    }

}
