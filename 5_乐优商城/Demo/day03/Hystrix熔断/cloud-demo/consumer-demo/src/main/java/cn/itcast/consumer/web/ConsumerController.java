package cn.itcast.consumer.web;

        import cn.itcast.consumer.pojo.User;
        import com.netflix.discovery.converters.Auto;
        import com.netflix.hystrix.contrib.javanica.annotation.DefaultProperties;
        import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
        import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
        import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.cloud.client.ServiceInstance;
        import org.springframework.cloud.client.discovery.DiscoveryClient;
        import org.springframework.core.env.CommandLinePropertySource;
        import org.springframework.web.bind.annotation.*;
        import org.springframework.web.client.RestTemplate;

        import java.util.List;

@RestController
@RequestMapping("/consumer")
@DefaultProperties(defaultFallback = "defaultFallBack")
public class ConsumerController {

    @Autowired
    private RestTemplate restTemplate;

/*    @Autowired
    private DiscoveryClient discoveryClient; // Eureka客户端，可以获取到服务实例信息*/

   /* @GetMapping("/{id}")
    public User queryById(@PathVariable("id") Long id) {

*//*        //根据服务id获取实例,也就是根据服务名称，获取服务实例
        List<ServiceInstance> instances = discoveryClient.getInstances("user-service");
        //从实例中取出ip和端口,因为只有一个UserService,因此我们直接get(0)获取
        ServiceInstance instance = instances.get(0);//未来在此处要写负载均衡算法随机取
        //拼接url，实现动态获取地址
        String url = "http://" + instance.getHost() + ":" + instance.getPort() + "/user/" + id;*//*

        String url = "http://user-service/user/" + id;
        User user = restTemplate.getForObject(url, User.class);
        return user;
    }*/

    //成功时方法
    @GetMapping("/{id}")
    /*@HystrixCommand (commandProperties = {
            //key是类里面找到的
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "3000")
    }) */ //开启线程隔离和服务降级,并设置时长
    @HystrixCommand(commandProperties = {
            @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "10"),//请求次数
            @HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds", value = "10000"),//休眠时间
            @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = "60")//失败阈值
    })
    public String queryById(@PathVariable("id") Long id) {

        if(id % 2 == 0) {
            //随便抛个异常来触发熔断
            throw new RuntimeException("");
        }

        String url = "http://user-service/user/" + id;
        String user = restTemplate.getForObject(url, String.class);
        //因为返回的是json格式，所以这儿可以是String类型
        return user;
    }

    //失败时方法
    public String queryByIdFallBack(Long id) {
        //返回一个友好的提示
        return "不好意思，服务器太拥挤了！";
    }

    //失败时默认方法
    public String defaultFallBack() {
        //返回一个友好的提示
        return "不好意思，服务器太拥挤了！";
    }
}