package org.first.order.test;


import org.first.order.OrderApplication;
import org.first.order.mapper.OrderMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = OrderApplication.class)
public class OrderMapperTest {

    @Autowired
    private OrderMapper orderMapper;

    //测一下这个能否跑
    @Test
    public void t1(){
        //orderMapper.insertDraft("abc", "god");
        System.out.println("eat shit");
    }

}
