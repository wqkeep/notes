package cn.itcast.demo.client;

import cn.itcast.demo.config.FeignConfig;
import cn.itcast.demo.pojo.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author: HuYi.Zhang
 * @create: 2018-06-17 15:41
 **/
@FeignClient(value = "user-service", path = "/user",
        fallback = UserClientFallback.class,configuration = FeignConfig.class)
public interface UserClient {

    @GetMapping("/{id}")
    User queryById(@PathVariable("id") Long id);
}
