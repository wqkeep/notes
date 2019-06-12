[TOC]



# 0.学习目标

- 会配置Hystix熔断
- 会使用Feign进行远程调用
- 能独立搭建Zuul网关
- 能编写Zuul的拦截器



# 1.Hystrix

## 1.1.简介

Hystrix英文意思是豪猪，全身都是刺，看起来不好惹。一种保护机制。

Hystrix是Netfix的一款组件。

Hystrix，即熔断器。

主页：https://github.com/Netflix/Hystrix/

![1525658740266](assets/1525658740266.png)

那么Hystrix的作用是什么呢，具体要保护什么呢？



Hystix是Netflix开源的一个延迟和容错库，用于隔离访问远程服务、第三方库，防止出现级联失败。

![1525658562507](assets/1525658562507.png)



## 1.2.雪崩问题

![1554385309442](assets/1554385309442.png)

![1554385696120](assets/1554385696120.png)

![1554385845932](assets/1554385845932.png)

![1554385944392](assets/1554385944392.png)

## 8.3.线程隔离，服务降级

正常工作的情况下，客户端请求调用服务API接口：

![1525658906255](assets/1525658906255.png)

当有服务出现异常时，直接进行失败回滚，服务降级处理：

![1525658983518](assets/1525658983518.png)

当服务繁忙时，如果服务出现异常，不是粗暴的直接报错，而是返回一个友好的提示，虽然拒绝了用户的访问，但是会返回一个结果。

这就好比去买鱼，平常超市买鱼会额外赠送杀鱼的服务。等到逢年过节，超时繁忙时，可能就不提供杀鱼服务了，这就是服务的降级。

系统特别繁忙时，一些次要服务暂时中断，优先保证主要服务的畅通，一切资源优先让给主要服务来使用，在双十一、618时，京东天猫都会采用这样的策略。

![1554386076621](assets/1554386076621.png)

![1554386326815](assets/1554386326815.png)



![1554387513826](assets/1554387513826.png)

在服务的消费方做降级逻辑处理。

首先在consumer-demo中引入Hystix依赖：

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-hystrix</artifactId>
</dependency>
```



ConsumerApplication中加注解@SpringCloudApplication 这个注解是上面三个注解的组合

注解@SpringCloudApplication

![1554428220207](assets/1554428220207.png)

```java
package cn.itcast;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

//@EnableCircuitBreaker  //开启熔断
//@SpringBootApplication
//@EnableDiscoveryClient  // 开启Eureka客户端

@SpringCloudApplication //这个注解是上面三个注解的组合
public class ConsumerApplication {

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        //如果我们使用OkHttp客户端,只需要注入工厂即可，参数为new OkHttp3ClientHttpRequestFactory()
        return new RestTemplate();
    }

    public static void main(String[] args) {
        SpringApplication.run(ConsumerApplication.class, args);
    }
}

```

**编写降级逻辑**

我们不想让服务堵塞，所以进行降级处理，一旦堵塞超时超过了我们的默认时长，我们就快速返回一个失败信息。

![1554428424488](assets/1554428424488.png)

改造**消费者**consumer-demo，并且声明一个失败时的回滚处理函数

修改ConsumerController代码

------



单独使用：只能这个类使用

```java
package cn.itcast.consumer.web;

@RestController
@RequestMapping("/consumer")
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
    @HystrixCommand(fallbackMethod = "queryByIdFallBack")//开启线程隔离和服务降级，失败就调失败方法
    public String queryById(@PathVariable("id") Long id) {
        String url = "http://user-service/user/" + id;
        String user = restTemplate.getForObject(url, String.class);
        //因为返回的是json格式，所以这儿可以是String类型
        return user;
    }

    //失败时回滚方法
    public String queryByIdFallBack(Long id) {//单独使用参数必须一致
        //返回一个友好的提示
        return "不好意思，服务器太拥挤了！";
    }
}
```

通用：都可以使用

```java
package cn.itcast.consumer.web;

@RestController
@RequestMapping("/consumer")
@DefaultProperties(defaultFallback = "queryByIdFallBack")  //开启默认的回滚方法
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
    @HystrixCommand  //开启线程隔离和服务降级，一旦失败就调用默认的回滚方法
    public String queryById(@PathVariable("id") Long id) {
        String url = "http://user-service/user/" + id;
        String user = restTemplate.getForObject(url, String.class);
        //因为返回的是json格式，所以这儿可以是String类型
        return user;
    }
    
     //失败时回滚方法
    public String queryByIdFallBack(Long id) {//单独使用
        //返回一个友好的提示
        return "不好意思，服务器太拥挤了！";
    }

    //失败时默认回滚方法
    public String dafaultFallBack() {//通用的方法不能有参数
        //返回一个友好的提示
        return "不好意思，服务器太拥挤了！";
    }
}
```



------



写完。

改造**服务提供者**user-service的userService，随机休眠一段时间，以触发熔断：

 // 为了演示超时现象，我们在这里然线程休眠,时间2000毫秒

```
package cn.itcast.user.service;

import cn.itcast.user.mapper.UserMapper;
import cn.itcast.user.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    public User queryById(Long id) {
        // 为了演示超时现象，我们在这里然线程休眠,时间2000毫秒
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return userMapper.selectByPrimaryKey(id);
    }
}
```

**测试**：启动EurekaServer,然后启动UserApplication

![1554432186563](assets/1554432186563.png)

成功。

------

 

**设置较长的超时时长**：一些用时较长的业务，比如：银行转账等业务

改造**消费者**consumer-demo

修改ConsumerController代码

（我们这里为了显示超时现象，在user-service设置线程睡眠为2秒，所以现在设置开启线程隔离和服务降级为3s，才可能成功访问服务）

**自定义超时时长**

------

某个类**单独使用**，类中配置：

```java
package cn.itcast.consumer.web;

@RestController
@RequestMapping("/consumer")
@DefaultProperties(defaultFallback = "defaultFallBack")
public class ConsumerController {

    @Autowired
    private RestTemplate restTemplate;

    //成功时方法
    @GetMapping("/{id}")
    @HystrixCommand(commandProperties = {
            //key是类里面找到的
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "3000")
    }) //开启线程隔离和服务降级,并设置时长
    public String queryById(@PathVariable("id") Long id) {
        String url = "http://user-service/user/" + id;
        String user = restTemplate.getForObject(url, String.class);
        //因为返回的是json格式，所以这儿可以是String类型
        return user;
    }

    //失败时回滚方法
    public String queryByIdFallBack(Long id) {
        //返回一个友好的提示
        return "不好意思，服务器太拥挤了！";
    }

    //失败时默认回滚方法
    public String defaultFallBack() {
        //返回一个友好的提示
        return "不好意思，服务器太拥挤了！";
    }
}
```

![1554435163186](assets/1554435163186.png)

成功。

//////////////



**全局配置**

先注释掉单独使用时类中的自定义时长配置

整体使用要放在配置文件application.yaml文件中，consumer-demo中的

```yaml
server:
  port: 8088
spring:
  application:
    name: consumer # 应用名称
eureka:
  client:
    service-url: # EurekaServer地址 ,多个地址以','隔开
      defaultZone: http://127.0.0.1:10086/eureka,http://127.0.0.1:10087/eureka
# 开启全局的自定义超时时长
hystrix:
  command:
    default:
      execution:
        isolation:
          thread:
            timeoutInMilliseconds: 3000
```

测试，成功。





////////

**单独使用**，配置文件application.yaml文件中，consumer-demo中的

问题：类中是要写代码的，但上面单独使用时类中写了那么多臃肿的配置文件，很low，我们在yaml配置文件中配置即可。（自由选择，都可以）

```yaml
server:
  port: 8088
spring:
  application:
    name: consumer # 应用名称
eureka:
  client:
    service-url: # EurekaServer地址 ,多个地址以','隔开
      defaultZone: http://127.0.0.1:10086/eureka,http://127.0.0.1:10087/eureka
# 开启全局的自定义超时时长
hystrix:
  command:
    user-service:   # 只针对于某个服务 或者 某个方法（user-service 或者 queryById）,全局为 default
      execution:
        isolation:
          thread:
            timeoutInMilliseconds: 3000
```

------













## 8.4服务熔断

熔断器，也叫断路器，英文Circuit Breaker

熔断的作用就是保护。

### 1.2.熔断器的工作机制：

![1525658640314](assets/1525658640314.png)

![1554438636163](assets/1554438636163.png)

![1554470678580](assets/1554470678580.png)

![1554471921316](assets/1554471921316.png)

`ConsumerController`中：

因为我们要测试熔断，**为了测试方便**，我们把 默认失败比例的阈值(默认50%)设置为60% ；请求次数(默认不低于20次)设置为10次，休眠时间窗(默认5秒)设置为10秒。

进入到hystrix包中的HystrixCommandProperties 类，寻找我们要修改的key值：

![1554472844829](assets/1554472844829.png)

![1554472526649](assets/1554472526649.png)

找到我们要修改的key值：

![1554472616892](assets/1554472616892.png)

tips：【1】【2】表示第一步和第一步加入的代码！

```java
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

    //成功时方法
    @GetMapping("/{id}")
【2】    @HystrixCommand(commandProperties = {
        //请求次数
        @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "10"),
        //休眠时间    
       	@HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds", value = "10000"),
        //失败阈值
        @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = "60")
    })【2】
    public String queryById(@PathVariable("id") Long id) {
【1】
        //为了控制请求的成功与失败，假如此逻辑
        if(id % 2 == 0) {
            //随便抛个异常来触发熔断
            throw new RuntimeException("");
        }
 【1】       
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
```

![1554475611119](assets/1554475611119.png)

因为我们是开发阶段，为了方便修改了默认参数，但==**生产阶段不建议修改默认参数**。==

---

测试：

这样如果参数id为奇数，一定成功！id参数为偶数一定失败！

我们准备了两个请求窗口：

- http://localhost:8088/consumer/13    一定成功
- http://localhost:8088/consumer/14    一定失败

当我们疯狂访问id为14的请求时（超过10次），就会触发熔断。断路器会打开，一切请求都会被降级处理。

此时你访问id为13的请求，会发现返回的也是失败，而且失败的时间很短，只有20毫秒左右。

![1554475460310](assets/1554475460310.png)

**成功**。



tips：熔断的时长，我们一般不用调，但是线程的超时时长一般会是人为控制的，因为超时时长默认一秒实在是太短了，因为网络波动等等原因很容易就超过一秒了，所以程的超时时长是要调的。







---





# 学习目标：

- 会使用feign进行远程调用
- 能独立单间zull网关
- 能编写zull的拦截器



# 2.Feign

在前面的学习中，我们使用了Ribbon的负载均衡功能，大大简化了远程调用时的代码：

```java
String baseUrl = "http://user-service/user/";
User user = this.restTemplate.getForObject(baseUrl + id, User.class)
```

如果就学到这里，你可能以后需要编写类似的大量重复代码，格式基本相同，无非参数不一样。有没有更优雅的方式，来对这些代码再次优化呢？

这就是我们接下来要学的Feign的功能了。

## 2.1.简介

有道词典的英文解释：

​	![1525662976679](assets/1525662976679.png)

为什么叫伪装？

Feign可以把Rest的请求进行隐藏，伪装成类似SpringMVC的Controller一样。你不用再自己拼接url，拼接参数等等操作，一切都交给Feign去做。



项目主页：https://github.com/OpenFeign/feign

![1525652009416](assets/1525652009416.png)

## 2.2.快速入门

> 第一步：

### 2.2.1.导入依赖

consumer-demo

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-openfeign</artifactId>
</dependency>
```

> 第二步：

### 2.2.2.开启Feign功能

我们在启动类ConsumerApplication上，添加注解，开启Feign功能

```java
package cn.itcast;

//@EnableCircuitBreaker  //开启熔断
//@SpringBootApplication
//@EnableDiscoveryClient  // 开启Eureka客户端

【1】@EnableFeignClients
@SpringCloudApplication //这个注解是上面三个注解的组合
public class ConsumerApplication {

【2】注释掉不再需要
//    @Bean
//    @LoadBalanced
//    public RestTemplate restTemplate() {
//        //如果我们使用OkHttp客户端,只需要注入工厂即可，参数为new OkHttp3ClientHttpRequestFactory()
//        return new RestTemplate();
//    }
【2】

    public static void main(String[] args) {
        SpringApplication.run(ConsumerApplication.class, args);
    }
}

```

- 你会发现RestTemplate的注册被我删除了。Feign中已经自动集成了Ribbon负载均衡，因此我们不需要自己定义RestTemplate了

---

> 第三步：

### 2.2.3.Feign的客户端

编写UserClient接口：

![1554517568175](assets/1554517568175.png)



```java
package cn.itcast.consumer.client;

import cn.itcast.consumer.pojo.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("user-service") //声明这是一个feign客户端，服务名称
public interface UserClient {

    @GetMapping("/user/{id}") //声明请求方式和参数
    User queryById(@PathVariable("id") Long id);//声明请求方法和返回值
    
     /*
        Feign的客户端拿着user-service服务，去Eureka拉取这个服务所对应的服务列表，拿到
        以后底层就可以利用ribbon实现负载均衡，去挑选任意一个服务，紧接着向"/user/{id}"
        这个地址发起请求，并且传递id作为参数，得到结果以后它会帮你转换成User，全部自动完成。
     */
}
```

- 首先这是一个接口，Feign会通过动态代理，帮我们生成实现类。这点跟mybatis的mapper很像
- `@FeignClient`，声明这是一个Feign客户端，类似`@Mapper`注解。同时通过`value`属性指定服务名称
- 接口中的定义方法，完全采用SpringMVC的注解，Feign会根据注解帮我们生成URL，并访问获取结果

---

> 第四步：

ConsumerController 改造原来的调用逻辑:

```java
package cn.itcast.consumer.web;

@RestController
@RequestMapping("/consumer")
@DefaultProperties(defaultFallback = "defaultFallBack")
public class ConsumerController {

【1】 注释掉不再需要   
//    @Autowired
//    private RestTemplate restTemplate;
【1】   
    
    @Autowired
    private UserClient userClient; //自动注入Feign客户端

【2】
    @GetMapping("/{id}")
    public User queryById(@PathVariable("id") Long id) {
        return userClient.queryById(id);  //Feign客户端帮我们做了以前的事情
    }
【2】

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
```

> 第五步：

改造：UserService

```
package cn.itcast.user.service;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    public User queryById(Long id) {
    
【1】 注释掉不再需要   
        // 为了演示超时现象，我们在这里然线程休眠,时间2000毫秒
//        try {
//            Thread.sleep(2000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
【1】

        return userMapper.selectByPrimaryKey(id);
    }
}

```

---

> 第六步：

### 2.2.4.启动测试：

![1554521151706](assets/1554521151706.png)

==**成功**。==

---



Feign的依赖包含ribbon的依赖，引入Feign的依赖后，可以删除以前添加的ribbon依赖。

![1554604711920](assets/1554604711920.png)

此时的consumer-demo的pom文件 :

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <parent>
        <artifactId>cloud-demo</artifactId>
        <groupId>cn.itcast.demo</groupId>
        <version>1.0.0-SNAPSHOT</version>
    </parent>
    <modelVersion>4.0.0</modelVersion>

    <artifactId>consumer-demo</artifactId>

    <!--因为是调用者，所以web工程可以启动就够了，远程调用user-service即可-->
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <!-- Eureka客户端 -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
        </dependency>
        <!--hystrix-->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-hystrix</artifactId>
        </dependency>
        <!--Feign-->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-openfeign</artifactId>
        </dependency>
    </dependencies>
</project>
```

但此时熔断已经不能使用了，因为使用Feign的熔断需要改变使用方法。详情见2.4.Hystrix支持。







## 2.3.负载均衡

Feign中本身已经集成了Ribbon依赖和自动配置：

​	![1525672070679](assets/1525672070679.png)

因此我们不需要额外引入依赖，也不需要再注册`RestTemplate`对象。

consumer-demo下，另外，我们可以像上节课中讲的那样去配置Ribbon，可以通过`ribbon.xx`来进行全局配置。也可以通过`服务名.ribbon.xx`来对指定服务配置：

通过`服务名.ribbon.xx`来对**指定服务配置**：

```yaml
user-service:
  ribbon:
    ConnectTimeout: 250 # 连接超时时间(ms)
    ReadTimeout: 1000 # 通信超时时间(ms)
    OkToRetryOnAllOperations: true # 是否对所有操作重试
    MaxAutoRetriesNextServer: 1 # 同一服务不同实例的重试次数
    MaxAutoRetries: 1 # 同一实例的重试次数
```

通过`ribbon.xx`来进行全局配置**全局的负载均衡配置**：

```yaml
ribbon:
  ConnectTimeout: 250 # Ribbon的连接超时时间
  ReadTimeout: 1000 # Ribbon的数据读取超时时间
  OkToRetryOnAllOperations: true # 是否对所有操作都进行重试
  MaxAutoRetriesNextServer: 1 # 切换实例的重试次数
  MaxAutoRetries: 1 # 对当前实例的重试次数
```





## 2.4.Hystix支持

Feign默认也有对Hystix的集成：

​	![1525672466192](assets/1525672466192.png)

> 第1步：consumer-demo开启Feign的hystrix熔断

只不过，默认情况下是关闭的。我们需要通过下面的参数来开启：

```yaml
feign:
  hystrix:
    enabled: true # 开启Feign的熔断功能
```

但是，Feign中的Fallback配置不像Ribbon中那样简单了。

> 第2步：ConsumerApplication加注解

```java
package cn.itcast;

//@EnableCircuitBreaker  //开启熔断
//@SpringBootApplication
//@EnableDiscoveryClient  // 开启Eureka客户端

@EnableFeignClients
【1】@SpringCloudApplication //这个注解是上面三个注解的组合
public class ConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConsumerApplication.class, args);
    }
}

```

> 第3步：编写熔断接口的实现类，写熔断的业务逻辑

1）首先，我们要定义一个类UserClientFallback，实现刚才编写的Feign客户端UserClient，作为fallback的处理类

```java
package cn.itcast.consumer.client;

public class UserClientFallback implements UserClient {
    @Override
    public User queryById(Long id) {
        return null;
    }
}
```

2）然后在UserClient中，指定刚才编写的实现类

```java
package cn.itcast.consumer.client;

//声明这是一个feign客户端，服务名称。指定刚才编写的实现类
【1】@FeignClient(value = "user-service", fallback = UserClientFallback.class) 
public interface UserClient {

    @GetMapping("/user/{id}") //声明请求方式和地址和参数
    User queryById(@PathVariable("id") Long id);//声明请求方法和返回值

    /*
        Feign的客户端拿着user-service服务，根据服务名拿到了ip和端口，去Eureka拉取这个服务所对应的服务列表，拿到
        以后底层就可以利用ribbon实现负载均衡，去挑选任意一个服务，紧接着向"/user/{id}"
        这个地址发起请求，并且传递id作为参数，得到结果以后它会帮你转换成User，全部自动完成。
     */
}

```

3）编写UserClientFallback，写熔断的业务逻辑

```java
package cn.itcast.consumer.client;

import cn.itcast.consumer.pojo.User;
import org.springframework.stereotype.Component;

【1】@Component  //注入到spring里面去
public class UserClientFallback implements UserClient {
    @Override
    public User queryById(Long id) {
【2】        
        User user = new User();
        user.setId(id);
        user.setName("未知用户");
        return user;
【2】       
    }
}


```

> 第4步：重启测试

此时consumer-demo的yaml

```yaml
server:
  port: 8088
spring:
  application:
    name: consumer # 应用名称
eureka:
  client:
    service-url: # EurekaServer地址 ,多个地址以','隔开
      defaultZone: http://127.0.0.1:10086/eureka,http://127.0.0.1:10087/eureka
# 开启Feign的熔断功能
feign:
  hystrix:
    enabled: true
# 通过`服务名.ribbon.xx`来对指定服务配置
user-service:
  ribbon:
    ConnectTimeout: 500 # 连接超时时间(ms)
    ReadTimeout: 2000 # 通信超时时间(ms)
# 开启全局的自定义超时时长
hystrix:
  command:
    default:
      execution:
        isolation:
          thread:
            timeoutInMilliseconds: 3000

```



访问页面：

![1554605048455](assets/1554605048455.png)

我们关闭user-service服务，然后再访问页面：

![1554605582781](assets/1554605582781.png)

返回了未知用户，超时为2000毫秒以后，我们之前配置的。

**成功。**













## 2.5.请求压缩(了解)

应用场景：如果**请求的数据比较大，比如需求为文件的上传或者下载**，那么就可以考虑使用请求压缩。

Spring Cloud Feign 支持对请求和响应进行GZIP压缩，以减少通信过程中的性能损耗。通过下面的参数即可开启请求与响应的压缩功能：

```yaml
feign:
  compression:
    request:
      enabled: true # 开启请求压缩
    response:
      enabled: true # 开启响应压缩
```

同时，我们也可以对请求的数据类型，以及触发压缩的大小下限进行设置：

```yaml
feign:
  compression:
    request:
      enabled: true # 开启请求压缩
      mime-types: text/html,application/xml,application/json # 设置压缩的数据类型
      min-request-size: 2048 # 设置触发压缩的大小下限
```

注：上面的数据类型、压缩大小下限均为默认值。



## 2.6.日志级别(了解)

前面讲过，通过`logging.level.xx=debug`来设置日志级别。然而这个对Fegin客户端而言不会产生效果。因为`@FeignClient`注解修改的客户端在被代理时，都会创建一个新的Fegin.Logger实例。我们需要额外指定这个日志的级别才可以。

1）设置cn.itcast包下的日志级别都为debug

```yaml
logging:
  level:
    com.leyou: debug
```

2）编写配置类，定义日志级别

```java
@Configuration
public class FeignConfig {
    @Bean
    Logger.Level feignLoggerLevel(){
        return Logger.Level.FULL;
    }
}
```

这里指定的Level级别是FULL，Feign支持4种级别：

​	![1525674373507](assets/1525674373507.png)

- NONE：不记录任何日志信息，这是默认值。
- BASIC：仅记录请求的方法，URL以及响应状态码和执行时间
- HEADERS：在BASIC的基础上，额外记录了请求和响应的头信息
- FULL：记录所有请求和响应的明细，包括头信息、请求体、元数据。



3）在FeignClient中指定配置类：

```java
@FeignClient(value = "user-service", fallback = UserClientFallback.class, configuration = FeignConfig.class)
public interface UserClient {
    @GetMapping("/user/{id}")
    User queryUserById(@PathVariable("id") Long id);
}
```

4）重启项目，即可看到每次访问的日志：

![1525674544569](assets/1525674544569.png)



# 3.Zuul网关

通过前面的学习，使用Spring Cloud实现微服务的架构基本成型，大致是这样的：

![1525674644660](assets/1525674644660.png)



我们使用Spring Cloud Netflix中的Eureka实现了服务注册中心以及服务注册与发现；而服务间通过Ribbon或Feign实现服务的消费以及均衡负载；通过Spring Cloud Config实现了应用多环境的外部化配置以及版本管理。为了使得服务集群更为健壮，使用Hystrix的融断机制来避免在微服务架构中个别服务出现异常时引起的故障蔓延。

 

在该架构中，我们的服务集群包含：内部服务Service A和Service B，他们都会注册与订阅服务至Eureka Server，而Open Service是一个对外的服务，通过均衡负载公开至服务调用方。我们把焦点聚集在对外服务这块，直接暴露我们的服务地址，这样的实现是否合理，或者是否有更好的实现方式呢？

 

先来说说这样架构需要做的一些事儿以及存在的不足：

- 首先，破坏了服务无状态特点。
  - 为了保证对外服务的安全性，我们需要实现对服务访问的权限控制，而开放服务的权限控制机制将会贯穿并污染整个开放服务的业务逻辑，这会带来的最直接问题是，破坏了服务集群中REST API无状态的特点。
  -  从具体开发和测试的角度来说，在工作中除了要考虑实际的业务逻辑之外，还需要额外考虑对接口访问的控制处理。
- 其次，无法直接复用既有接口。
  - 当我们需要对一个即有的集群内访问接口，实现外部服务访问时，我们不得不通过在原有接口上增加校验逻辑，或增加一个代理调用来实现权限控制，无法直接复用原有的接口。



面对类似上面的问题，我们要如何解决呢？答案是：服务网关！



为了解决上面这些问题，我们需要将权限控制这样的东西从我们的服务单元中抽离出去，而最适合这些逻辑的地方就是处于对外访问最前端的地方，我们需要一个更强大一些的均衡负载器的 服务网关。

 

**服务网关是微服务架构中一个不可或缺的部分。通过服务网关统一向外系统提供REST API的过程中，除了具备服务路由、均衡负载功能之外，它还具备了`权限控制`等功能。Spring Cloud Netflix中的Zuul就担任了这样的一个角色，为微服务架构提供了前门保护的作用，同时将权限控制这些较重的非业务逻辑内容迁移到服务路由层面，使得服务集群主体能够具备更高的可复用性和可测试性。**



## 3.1.简介

官网：https://github.com/Netflix/zuul

​	![1525675037152](assets/1525675037152.png)

Zuul：维基百科：

电影《捉鬼敢死队》中的怪兽，Zuul，在纽约引发了巨大骚乱。

事实上，在微服务架构中，Zuul就是守门的大Boss！一夫当关，万夫莫开！

![1525675168152](assets/1525675168152.png)





## 3.2.Zuul加入后的架构

![1525675648881](assets/1525675648881.png)



- 不管是来自于客户端（PC或移动端）的请求，还是服务内部调用。一切对服务的请求都会经过Zuul这个网关，然后再由网关来实现 鉴权、动态路由等等操作。Zuul就是我们服务的统一入口。

## 3.3.快速入门

### 3.3.1.新建工程

> 第1步：

在父工程上新建模块工程 gateway：

填写基本信息：

![1554607348402](assets/1554607348402.png)

> ---
>
> 第2步：

添加Zuul网关依赖：

tips： zuul依赖带有web依赖，所以我们不需要再添加web依赖了。

![1554607544937](assets/1554607675188.png)

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <parent>
        <artifactId>cloud-demo</artifactId>
        <groupId>cn.itcast.demo</groupId>
        <version>1.0.0-SNAPSHOT</version>
    </parent>
    <modelVersion>4.0.0</modelVersion>

    <groupId>cn.itcast.demo</groupId>
    <artifactId>gateway</artifactId>

    <!--zuul网关-->
    <dependencies>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-zuul</artifactId>
        </dependency>
    </dependencies>
</project>
```

> 第3步：
>

### 3.3.2.编写启动类

通过`@EnableZuulProxy `注解开启Zuul的功能：

```java
package cn.itcast;

@EnableZuulProxy // 开启Zuul的网关功能
@SpringBootApplication
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }
}

```

---



### 3.3.3.编写配置

```yaml
server:
  port: 10010 #服务端口
spring: 
 application:  
   name: gateway #指定服务名
```

### 3.3.4.编写路由规则

我们需要用Zuul来代理user-service服务，先看一下控制面板中的服务状态：

![1525676797879](assets/1525676797879.png)

- ip为：127.0.0.1
- 端口为：8082

映射规则：

```yaml
zuul:
  routes:
    user-service: # 这里是路由id，随意写
      path: /user-service/** # 这里是映射路径
      url: http://127.0.0.1:8082 # 映射路径对应的实际url地址
```

我们将符合`path` 规则的一切请求，都代理到 `url`参数指定的地址

本例中，我们将 `/user-service/**`开头的请求，代理到http://127.0.0.1:8082

> 第4步：

此时的yaml：

```
server:
  port: 10010 #服务端口
spring: 
  application:  
    name: gateway #指定服务名
zuul:
  routes:
    user-service: # 这里是路由id，随意写
      path: /user-service/** # 这里是映射路径
      url: http://127.0.0.1:8082 # 映射路径对应的实际url地址
```

> 第5步：

### 3.3.5.启动测试：

访问的路径中需要加上配置规则的映射路径，我们访问：http://127.0.0.1:8082/user-service/user/15

​	![1554610309936](assets/1554610309936.png)

**成功。**（以上是相面url地址的配置，有问题）



## 3.4.面向服务的路由

问题： 在刚才的路由规则中，我们把路径对应的服务地址写死了！如果同一服务有多个实例的话，这样做显然就不合理了。

我们应该根据服务的名称，去Eureka注册中心查找 服务对应的所有实例列表，然后进行动态路由才对！

> ==第1步：==
>

### 3.4.1.添加Eureka客户端依赖

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <parent>
        <artifactId>cloud-demo</artifactId>
        <groupId>cn.itcast.demo</groupId>
        <version>1.0.0-SNAPSHOT</version>
    </parent>
    <modelVersion>4.0.0</modelVersion>

    <groupId>cn.itcast.demo</groupId>
    <artifactId>gateway</artifactId>

    <!--zuul网关-->
    <dependencies>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-zuul</artifactId>
        </dependency>
【1】
        <!--Eureka客户端-->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
        </dependency>
【1】
    </dependencies>
</project>
```

---



### 3.4.2.开启Eureka客户端发现功能

```java
@SpringBootApplication
@EnableZuulProxy // 开启Zuul的网关功能
@EnableDiscoveryClient
public class ZuulDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(ZuulDemoApplication.class, args);
	}
}
```

### 3.4.3.添加Eureka配置，获取服务信息

```
server:
  port: 10010 #服务端口
spring: 
  application:  
    name: gateway #指定服务名
eureka:
  client:
    service-url:
      defaultZone: http://127.0.0.1:10086/eureka
zuul:
  routes:
    user-service: # 这里是路由id，随意写
      path: /user-service/** # 这里是映射路径
      url: http://127.0.0.1:8082 # 映射路径对应的实际url地址


```

指定ip和获取服务列表的周期配置：

```yaml
eureka:
  client:
    registry-fetch-interval-seconds: 5 # 获取服务列表的周期：5s
    service-url:
      defaultZone: http://127.0.0.1:10086/eureka
  instance:
    prefer-ip-address: true
    ip-address: 127.0.0.1
```

---

不使用这种配置（为了测试），我们以后使用的是下面的简化配置。

### 3.4.4.修改映射配置，通过服务名称获取

因为已经有了Eureka客户端，我们可以从Eureka获取服务的地址信息，因此映射时无需指定IP地址，而是通过服务名称来访问，而且Zuul已经集成了Ribbon的负载均衡功能。

```yaml
server:
  port: 10010 #服务端口
spring: 
  application:  
    name: gateway #指定服务名
eureka:
  client:
    service-url:
      defaultZone: http://127.0.0.1:10086/eureka
【1】
zuul:
  routes:
    user-service: # 这里是路由id，随意写
      path: /user-service/** # 这里是映射路径
      serviceId: user-service # 指定服务名称
【1】

```

流程回顾：当用户输入路径后，请求会转发到user-service服务，但是这个服务没有具体的地址，它的底层就会调用Eureka，去拉取服务列表，然后利用负载均衡算法，动态的去里面过去一个地址

### 3.4.5.启动测试

再次启动，这次Zuul进行代理时，会利用Ribbon进行负载均衡访问：

​	![1525677821212](assets/1525677821212.png)

日志中可以看到使用了负载均衡器：

![1525677891119](assets/1525677891119.png)**成功。**

---

> ==第2步==：使用这种配置，我们以后使用的简化配置。

## 3.5.简化的路由配置

在刚才的配置中，我们的规则是这样的：

- `zuul.routes.<route>.path=/xxx/**`： 来指定映射路径。`<route>`是自定义的路由名
- `zuul.routes.<route>.serviceId=/user-service`：来指定服务名。

而大多数情况下，我们的`<route>`路由名称往往和 服务名会写成一样的。因此Zuul就提供了一种简化的配置语法：`zuul.routes.<serviceId>=<path>`

比方说上面我们关于user-service的配置可以简化为一条：

```yaml
server:
  port: 10010 #服务端口
spring: 
  application:  
    name: gateway #指定服务名
eureka:
  client:
    service-url:
      defaultZone: http://127.0.0.1:10086/eureka
【1】
zuul:
  routes:
    user-service: /user-service/** # key是服务的id，值是服务的映射路径(其实默认值就是这样配置的，这儿不写也可以)
【1】
```

省去了对服务名称的配置。

再次测试：

![1554612105403](assets/1554612105403.png)

**成功。**

---













## 3.6.默认的路由规则

在使用Zuul的过程中，上面讲述的规则已经大大的简化了配置项。但是当服务较多时，配置也是比较繁琐的。因此Zuul就指定了默认的路由规则：

- 默认情况下，一切服务的映射路径就是服务名本身。
  - 例如服务名为：`user-service`，则默认的映射路径就是：`/user-service/**`

也就是说，刚才的映射规则我们完全不配置也是OK的，不信就试试看。



自定义配置：（既可以使用默认配置访问（上面的那种），也可以使用自定义配置的访问）

​	我们之前consumer-demo的id为consumer，现在修改为：consumer-service

​	可以简化服务的映射路径， 忽略指定的服务不想对外暴露(禁用某个路由规则)：

```
server:
  port: 10010 #服务端口
spring:
  application:
    name: gateway #指定服务名
eureka:
  client:
    service-url:
      defaultZone: http://127.0.0.1:10086/eureka
【1】     
zuul:
  routes:
    user-service: /user/**      # 简化服务的映射路径，key是服务的id，值是服务的映射路径
  ignored-services:      #忽略指定的服务不想对外暴露
    - consumer-service
【1】

```

测试：

 ![1554613779367](assets/1554613779367.png)

 ![1554613823867](assets/1554613823867.png)

成功。

------



## 3.7.路由前缀

配置示例：

```yaml
zuul:
  prefix: /api # 添加路由前缀
  routes:
      user-service: /user-service/** # 这里是映射路径
```

我们通过`zuul.prefix=/api`来指定了路由的前缀，这样在发起请求时，路径就要以/api开头。

路径`/api/user-service/user/1`将会被代理到`/user-service/user/1`

```yaml
server:
  port: 10010 #服务端口
spring:
  application:
    name: gateway #指定服务名
eureka:
  client:
    service-url:
      defaultZone: http://127.0.0.1:10086/eureka
zuul:
  prefix: /api   【1】
  routes:
    user-service: # 这里是路由id，随意写
      path: /user/** # 这里是映射路径
      serviceId: user-service # 指定服务名称
      strip-prefix: false
  ignored-services: #忽略指定的服务不想对外暴露
    - consumer-service

```



---

![1554625976949](assets/1554625976949.png)

保留前缀：（局部）strip-prefix: false 

去除前缀：（局部）strip-prefix: true

```yaml
server:
  port: 10010 #服务端口
spring:
  application:
    name: gateway #指定服务名
eureka:
  client:
    service-url:
      defaultZone: http://127.0.0.1:10086/eureka
zuul:【1】
  routes:
    user-service: # 这里是路由id，随意写
      path: /user/** # 这里是映射路径
      serviceId: user-service # 指定服务名称
      strip-prefix: false   【1】
  ignored-services: #忽略指定的服务不想对外暴露
    - consumer-service

```



重启测试：

![1554625241267](assets/1554625241267.png)





## 3.8.过滤器

Zuul作为网关的其中一个重要功能，就是实现请求的鉴权。而这个动作我们往往是通过Zuul提供的过滤器来实现的。

### 3.8.1.ZuulFilter

ZuulFilter是过滤器的顶级父类。在这里我们看一下其中定义的4个最重要的方法：

```java
public abstract ZuulFilter implements IZuulFilter{

    abstract public String filterType();//过滤器类型

    abstract public int filterOrder();//过滤器顺序
    
    boolean shouldFilter();// 来自IZuulFilter，要不要过滤

    Object run() throws ZuulException;// IZuulFilter，过滤逻辑
}
```

- `shouldFilter`：返回一个`Boolean`值，判断该过滤器是否需要执行。返回true执行，返回false不执行。
- `run`：过滤器的具体业务逻辑。
- `filterType`：返回字符串，代表过滤器的类型。包含以下4种：
  - `pre`：请求在被路由之前执行
  - `routing`：在路由请求时调用
  - `post`：在routing和errror过滤器之后调用
  - `error`：处理请求时发生错误调用
- `filterOrder`：通过返回的int值来定义过滤器的执行顺序，数字越小优先级越高。



### 3.8.2.过滤器执行生命周期：

这张是Zuul官网提供的请求生命周期图，清晰的表现了一个请求在各个过滤器的执行顺序。

​	![1525681866862](assets/1525681866862.png)

- 应用场景：
  - “pre”fifters: 权限控制（比如商品的删除）、限流。
- 正常流程：
  - 请求到达首先会经过pre类型过滤器，而后到达routing类型，进行路由，请求就到达真正的服务提供者，执行请求，返回结果后，会到达post过滤器。而后返回响应。
- 异常流程：
  - 整个过程中，pre或者routing过滤器出现异常，都会直接进入error过滤器，再error处理完毕后，会将请求交给POST过滤器，最后返回给用户。
  - 如果是error过滤器自己出现异常，最终也会进入POST过滤器，而后返回。
  - 如果是POST过滤器出现异常，会跳转到error过滤器，但是与pre和routing不同的时，请求不会再到达POST过滤器了。

所有内置过滤器列表：

​	![1525682427811](assets/1525682427811.png)

### 3.8.3.使用场景

场景非常多：

- 请求鉴权：一般放在pre类型，如果发现没有访问权限，直接就拦截了
- 异常处理：一般会在error类型和post类型过滤器中结合来处理。
- 服务调用时长统计：pre和post结合使用。

## 3.9.自定义过滤器

接下来我们来自定义一个过滤器，模拟一个登录的校验。基本逻辑：如果请求中有access-token参数，则认为请求有效，放行。



> 第1步：

添加依赖：在 gateway 中

因为我们要使用StringUtils工具类

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <parent>
        <artifactId>cloud-demo</artifactId>
        <groupId>cn.itcast.demo</groupId>
        <version>1.0.0-SNAPSHOT</version>
    </parent>
    <modelVersion>4.0.0</modelVersion>

    <groupId>cn.itcast.demo</groupId>
    <artifactId>gateway</artifactId>

    <!--zuul网关-->
    <dependencies>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-zuul</artifactId>
        </dependency>
        <!--Eureka客户端-->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
        </dependency>
        <!--Apache提供的StringUtils的依赖-->【1】
        <dependency>
            <groupId>org.apache.commons</groupId>
            <artifactId>commons-lang3</artifactId>
        </dependency>					【1】
    </dependencies>
</project>
```

> 第2步：
>

### 3.9.1.定义过滤器类

```java
package cn.itcast.filter;

@Component  //自动加入到spring
public class LoginFilter extends ZuulFilter {
    @Override
    public String filterType() {
        // 登录校验，肯定是在前置拦截
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        //顺序设置为PRE_DECORATION_FILTER_ORDER - 1
        return FilterConstants.PRE_DECORATION_FILTER_ORDER - 1;
    }

    @Override
    public boolean shouldFilter() {
        // 返回true，代表过滤器生效。
        return true;
    }

    @Override
    public Object run() throws ZuulException {
        // 登录校验逻辑。
        // 获取Zuul提供的请求上下文对象
        RequestContext ctx = RequestContext.getCurrentContext();
        // 从上下文中获取request对象
        HttpServletRequest request = ctx.getRequest();
        // 获取请求参数access-token
        String token = request.getParameter("access-token");
        //判断是否存在（StringUtils）
        if (StringUtils.isBlank(token)) {
            //不存在token，登录校验失败，则拦截
            ctx.setSendZuulResponse(false);//通过这个方法来拦截，如果判断条件不成立，默认为true放行
            //返回403状态码，也可以考虑重定向到登录页。
            ctx.setResponseStatusCode(HttpStatus.FORBIDDEN.value());
        }
        // 校验通过，可以考虑把用户信息放入上下文，继续向后执行
        return null;
    }
}
```



### 3.9.2.测试

没有token参数时，访问失败：

​	![1554639998052](assets/1554639998052.png)

添加token参数后：

​	![1554640012248](assets/1554640012248.png)

**成功**。

---





## 3.10.负载均衡和熔断

Zuul中默认就已经集成了Ribbon负载均衡和Hystix熔断机制。但是所有的超时策略都是走的默认值，比如熔断超时时间只有1S，很容易就触发了。因此建议我们手动进行配置：

```yaml
zuul:
  retryable: true
ribbon:
  ConnectTimeout: 250 # 连接超时时间(ms)
  ReadTimeout: 2000 # 通信超时时间(ms)
  OkToRetryOnAllOperations: true # 是否对所有操作重试
  MaxAutoRetriesNextServer: 2 # 同一服务不同实例的重试次数
  MaxAutoRetries: 1 # 同一实例的重试次数
hystrix:
  command:
    default:
      execution:
        isolation:
          thread:
            timeoutInMilliseconds: 6000
```

**tips**:  ConnectTimeout: 250 # 连接超时时间(ms)和 ReadTimeout: 2000 # 通信超时时间(ms)相加不能超过		     timeoutInMilliseconds: 6000，除非手动配置MaxAutoRetries: 1 # 同一实例的重试次数和MaxAutoRetriesNextServer: 2 # 同一服务不同实例的重试次数。



查看源码：![1554641630876](assets/1554641630876.png)

ribbon的默认超时时长：真实值是（read + connect） * 2  ，必须小于Histrix时长

`AbstractRibbonCommand`源码：

```java
ribbonTimeout = (ribbonReadTimeout + ribbonConnectTimeout) * (maxAutoRetries + 1) * (maxAutoRetriesNextServer + 1);
```

---



此时的yaml：

```yaml
server:
  port: 10010 #服务端口
spring:
  application:
    name: gateway #指定服务名
eureka:
  client:
    service-url:
      defaultZone: http://127.0.0.1:10086/eureka
zuul:
  prefix: /api
  routes:
    user-service: # 这里是路由id，随意写
      path: /user/** # 这里是映射路径
      serviceId: user-service # 指定服务名称
      strip-prefix: false
  ignored-services: #忽略指定的服务不想对外暴露
    - consumer-service
hystrix:
  command:
    default:
      execution:
        isolation:
          thread:
            timeoutInMilliseconds: 6000
ribbon:
  ConnectTimeout: 500 # 连接超时时间(ms)
  ReadTimeout:2000 # 通信超时时间(ms)

```

3.11.Zull的高可用

![1554691950496](assets/1554691950496.png)

我们学习的springcloud一小部分组件：

Eureka：注册中心

Ribbon：负载均衡器

Hystrix：熔断器

Feign：远程调用工具 

Zuul：整个服务的网关

