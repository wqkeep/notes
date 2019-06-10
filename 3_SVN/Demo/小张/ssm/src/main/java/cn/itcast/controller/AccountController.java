package cn.itcast.controller;

import cn.itcast.domain.Account;
import cn.itcast.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * 账户web
 */
@Controller
@RequestMapping("/account")//一级目录
public class AccountController {

    @Autowired //自动注入
    private AccountService accountService;

    /**
     * 查找所有账户
     * @param model
     * @return
     */
    @RequestMapping("/findAll")//二级目录
    public String findAll(Model model) {
        System.out.println("表现层：查询所有账户...");

        //调用service的方法
        List<Account> list = accountService.findAll();
        //存起来(添加Model，存起来)
        model.addAttribute("list", list);

        return "/list";
    }

    /**
     * 保存账户
     * @param account
     * @return
     */
    @RequestMapping("/save")//二级目录
    public void save(Account account, HttpServletRequest request, HttpServletResponse response) throws IOException {
        accountService.saveAccount(account);
        response.sendRedirect(request.getContextPath() + "/account/findAll");
        return;
    }
}
