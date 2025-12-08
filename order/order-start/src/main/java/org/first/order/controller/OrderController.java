package org.first.order.controller;


import org.first.comm.model.CommonResponse;
import org.first.product.request.CreateOrderRequest;
import org.first.order.service.IOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.PostConstruct;

/**
 * @description 订单接口
 * @author xr
 * @since 2025/12/07
 *
 * */
@Controller
@RequestMapping("order")
public class OrderController {
    @Autowired
    private IOrderService orderService;

    //后面有时间，把
    @PostMapping("createOrder")
    @ResponseBody
    public CommonResponse<String> createOrder(@RequestBody CreateOrderRequest request) {
        orderService.createOrder(request);
        return CommonResponse.success("下单成功！");
    }


    @PostConstruct
    public void init() {
        System.out.println("OrderController loaded!");
    }
}
