package org.first.order.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("demoBusi")
@RestController
public class DemoBusiController {

    @GetMapping
    public String hello(String str){
        System.out.println("收到请求：" + str);
        return "hello " + str;
    }
}
