package com.leyou.httpdemo;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leyou.httpdemo.pojo.User;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * @author: HuYi.Zhang
 * @create: 2018-05-06 09:53
 **/
public class HttpTests {

    CloseableHttpClient httpClient;

    private ObjectMapper mapper = new ObjectMapper();

    @Before
    public void init() {
        httpClient = HttpClients.createDefault();
    }

    @Test
    public void testGet() throws IOException {
        HttpGet request = new HttpGet("http://localhost/user/8");
        String response = this.httpClient.execute(request, new BasicResponseHandler());
        System.out.println(response);
    }

    @Test
    public void testPost() throws IOException {
        HttpPost request = new HttpPost("http://www.oschina.net/");
        String response = this.httpClient.execute(request, new BasicResponseHandler());
        System.out.println(response);
    }

    @Test
    public void testGetPojo() throws IOException {
        HttpGet request = new HttpGet("http://localhost/user/8");
        String json = this.httpClient.execute(request, new BasicResponseHandler());
        System.out.println(json);

        // 反序列化
        User user = mapper.readValue(json, User.class);
        System.out.println(user);
    }

    @Test
    public void test() throws IOException {
        User user = new User();
        user.setId(2L);
        user.setUserName("ly");
        user.setName("柳岩");
        user.setAge(20);

        // 序列化
        String json = mapper.writeValueAsString(Arrays.asList(user, user));
        System.out.println(json);

        // 反序列化一个集合
//        List<User> list = mapper.readValue(json,
//                mapper.getTypeFactory().constructCollectionType(List.class, User.class));

        List<User> list = mapper.readValue(json, new TypeReference<List<User>>() {
        });
        System.out.println(list);
    }
}
