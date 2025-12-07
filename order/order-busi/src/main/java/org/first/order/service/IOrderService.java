package org.first.order.service;


import com.baomidou.mybatisplus.extension.service.IService;
import org.first.comm.model.CommonResponse;
import org.first.order.entity.Order;
import org.first.product.rquest.CreateOrderRequest;

public interface IOrderService extends IService<Order> {

    //创建订单
    //return 是否下单成功
    CommonResponse<String> createOrder(CreateOrderRequest request);
}
