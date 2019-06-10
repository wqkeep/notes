package cn.itcast.mapper;

import cn.itcast.pojo.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserMapperTest {

    @Autowired
    private UserMapper userMapper;//此处接口idea无法识别，其实通用mapper会帮助我们识别，此处不用管就好

    @Test
    public void testQuery() {
        User user = userMapper.selectByPrimaryKey(15L);//查15号 Long类型
        System.out.println("user = " + user);
    }

}