package org.first.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.first.comm.model.CommonResponse;
import org.first.order.entity.Order;
import org.first.order.mapper.OrderMapper;
import org.first.product.request.CreateOrderRequest;
import org.first.order.service.IOrderService;
import org.first.product.api.ProductApi;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;


@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements IOrderService {
    @Autowired
    private ProductApi productApi;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private RedissonClient redisson;

    @Transactional//默认传播范围/默认隔离级别
    @Override
    public CommonResponse<String> createOrder(CreateOrderRequest request) {
        //为新订单生成编号：36位去除横线，得32位
        String newOrderCode = UUID.randomUUID().toString().replace("-", "");

        //step1 创建草稿订单
        Order order = new Order();
        order.setOrderCode(newOrderCode);           // 业务参数
        order.setUserCode(request.getUserCode());   // 业务参数
        order.setStatus(0);                         // 草稿状态
        order.setTotalAmount(new BigDecimal(0));////后面有时间添加计算价格的代码
        orderMapper.insert(order);

        //step2 更新锁定库存
        CommonResponse res = productApi.deductStock(request);
        if(res.getCode()!=200) {
            orderMapper.delete(new LambdaQueryWrapper<Order>()
                    .eq(Order::getOrderCode, newOrderCode));
            throw new RuntimeException("下单失败，检查库存");//通过异常回滚
            //return CommonResponse.success("下单失败，检查库存");
        }

        return CommonResponse.success("下单成功，尽快支付（30min）");
    }

}

