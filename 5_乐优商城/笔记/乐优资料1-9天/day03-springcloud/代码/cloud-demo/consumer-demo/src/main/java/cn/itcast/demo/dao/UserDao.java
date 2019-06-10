package cn.itcast.demo.dao;

import cn.itcast.demo.pojo.User;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * @author: HuYi.Zhang
 * @create: 2018-06-15 17:46
 **/
//@Component
public class UserDao {

    @Autowired
    private RestTemplate restTemplate;

//    @Autowired
//    private DiscoveryClient discoveryClient;

    @HystrixCommand(fallbackMethod = "queryUserByIdFallBack")
    public User queryUserById(Long id) {
//        List<ServiceInstance> list = this.discoveryClient.getInstances("user-service");
//
//        ServiceInstance instance = list.get(0);
//        String host = instance.getHost();
//        int port = instance.getPort();

        String url = "http://user-service/user/" + id;
        return this.restTemplate.getForObject(url, User.class);
    }

    public User queryUserByIdFallBack(Long id){
        User user = new User();
        user.setId(0L);
        user.setName("未知异常，服务器忙");
        return user;
    }
}
