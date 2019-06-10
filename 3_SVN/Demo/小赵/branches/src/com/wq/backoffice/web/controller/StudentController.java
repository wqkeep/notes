package com.wq.backoffice.web.controller;

import com.wq.backoffice.model.Student;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/stu")
public class StudentController {

    @RequestMapping("/test")
    public String test01(@RequestParam(value = "uid", required = true, defaultValue = "100") Integer uid) {
        System.out.println(uid);
        return "forward:/user/list.do";
    }

    @RequestMapping("/toReg")
    public String register() {

        return "/stu/register";
//        return "forward:/stu/register";
    }


    @RequestMapping("/save")
    /**
     * RequestBody将json数据转成模型对象
     */
    public @ResponseBody Student save(@RequestBody Student student) {
        System.out.println("-------------");
        System.out.println(student);

        return student;
    }


    @RequestMapping("/save1")
    public @ResponseBody Student save1(Student student) {
        System.out.println("-------------1");
        System.out.println(student);

        return student;
    }

    @RequestMapping("/get")
    public List<Student> get() {

        List<Student> studentList = new ArrayList<Student>();
        Student stu = new Student();
        Student stu2 = new Student();
        stu.setUsername("debang");
        stu.setGender("male");
        stu2.setUsername("lisi");
        stu2.setGender("female");
        studentList.add(stu);
        studentList.add(stu2);

        return studentList;
    }

}
