package cn.itcast;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * @author: HuYi.Zhang
 * @create: 2018-06-15 18:07
 **/
@SpringBootApplication
@EnableEurekaServer
public class EurekaDemo {

    public static void main(String[] args) {
        SpringApplication.run(EurekaDemo.class, args);
    }
}
