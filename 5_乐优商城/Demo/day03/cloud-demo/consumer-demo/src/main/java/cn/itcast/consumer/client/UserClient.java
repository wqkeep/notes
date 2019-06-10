package cn.itcast.consumer.client;

        import cn.itcast.consumer.pojo.User;
        import org.springframework.cloud.openfeign.FeignClient;
        import org.springframework.web.bind.annotation.GetMapping;
        import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "user-service", fallback = UserClientFallback.class) //声明这是一个feign客户端，服务名称。指定刚才编写的实现类
public interface UserClient {

    @GetMapping("/user/{id}") //声明请求方式和地址和参数
    User queryById(@PathVariable("id") Long id);//声明请求方法和返回值

    /*
        Feign的客户端拿着user-service服务，根据服务名拿到了ip和端口，去Eureka拉取这个服务所对应的服务列表，拿到
        以后底层就可以利用ribbon实现负载均衡，去挑选任意一个服务，紧接着向"/user/{id}"
        这个地址发起请求，并且传递id作为参数，得到结果以后它会帮你转换成User，全部自动完成。
     */
}
