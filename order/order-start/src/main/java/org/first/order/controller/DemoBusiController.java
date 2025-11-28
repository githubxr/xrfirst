package org.first.order.controller;

import org.first.order.api.fallback.OrderApiFallbackFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("order/demoBusi")
@RestController
public class DemoBusiController {

    @Autowired
    OrderApiFallbackFactory factory;

    @GetMapping("orderTest")
    public String orderTest(){
        System.out.println(factory);
        return "factory是否存在：" + factory.toString();
    }

    @GetMapping("hello")
    public String hello(String str) throws InterruptedException {

        //Thread.sleep(5000);
        System.out.println("收到请求：" + str);
        return "hello + 入参：" + str;
    }
}
