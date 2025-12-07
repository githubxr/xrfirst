package org.first.order.controller;


import org.first.comm.model.CommonResponse;
import org.first.product.rquest.CreateOrderRequest;
import org.first.order.service.IOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @description 订单接口
 * @author xr
 * @since 2025/12/07
 *
 * */
@RequestMapping("order")
public class OrderController {
    @Autowired
    private IOrderService orderService;

    //后面有时间，把
    @PostMapping("createOrder")
    public CommonResponse<String> createOrder(@RequestBody CreateOrderRequest request) {
        orderService.createOrder(request);
        return new CommonResponse();
    }

}
