package cn.itcast;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @author: HuYi.Zhang
 * @create: 2018-06-14 15:06
 **/
@SpringBootApplication
@MapperScan("cn.itcast.demo.mapper")
public class Application {
    public static void main(String[] args) {
        // 启动代码
        SpringApplication.run(Application.class, args);
    }

}
