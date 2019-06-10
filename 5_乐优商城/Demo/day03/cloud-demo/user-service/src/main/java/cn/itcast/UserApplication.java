package cn.itcast;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import tk.mybatis.spring.annotation.MapperScan;

@EnableDiscoveryClient
@SpringBootApplication
@MapperScan("cn.itcast.user.mapper") //通用mapper扫描包
public class UserApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserApplication.class);//args可写可不写
    }
}
