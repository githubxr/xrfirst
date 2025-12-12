package org.first.order.service.impl;

import org.first.comm.model.CommonResponse;
import org.first.comm.util.DateUtil;
import org.first.order.entity.Order;
import org.first.order.entity.OrderItem;
import org.first.order.mapper.OrderItemMapper;
import org.first.order.mapper.OrderMapper;
import org.first.product.request.CreateOrderRequest;
import org.first.order.service.IOrderService;
import org.first.product.api.ProductApi;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements IOrderService {
    @Autowired
    private ProductApi productApi;//feignClient of product module
    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private RedissonClient redisson;

    @Transactional//默认传播范围/默认隔离级别
    @Override
    public CommonResponse<String> createOrder(CreateOrderRequest request) {
        //step1 更新锁定库存
        CreateOrderRequest res = deductStockOrFail(request);
        //step2 创建订单
        Order order = buildOrder(res);
        //订单items
        List<OrderItem> itemList = buildOrderItems(res.getItems(), order);

        //step3 插入订单记录
        baseMapper.insert(order);
        orderItemMapper.insert(itemList);

        return CommonResponse.success("下单成功，尽快支付（30min）");
    }

    private CreateOrderRequest deductStockOrFail(CreateOrderRequest req) {
        CommonResponse<CreateOrderRequest> res = productApi.deductStock(req);
        if (res.getCode() != 200) {
            //无法确定product模块是否执行成功，在此处自己（order）回滚前，发起SAGA补偿，让product微服务取消掉可能的库存锁定；
            //注意，这种方法只能保证概率性一致，非强一致性，不太可靠的哦;
            //productApi.cancelDeductStock(req);
            //...day25添加【强一致性分布式事务】来确保可靠，这里先不搞补偿接口
            throw new RuntimeException("库存不足：" + res.getMessage());//异常触发回滚
        }
        CreateOrderRequest data = res.getData();
        if (!req.getOrderCode().equals(data.getOrderCode())) {
            throw new RuntimeException("库存服务返回订单号不一致");
        }
        return data;
    }

    private Order buildOrder(CreateOrderRequest resData) {
        Order order = new Order();
        //为新订单生成编号：36位去除横线，得32位
        String newOrderCode = UUID.randomUUID().toString().replace("-", "");
        order.setOrderCode(newOrderCode);
        order.setStatus(0);
        order.setCreateTime(DateUtil.getCurrentDateTimeStr());
        order.setTotalAmount(resData.getTotalAmount());
        return order;
    }

    //
    private List<OrderItem> buildOrderItems(List<CreateOrderRequest.OrderItemReq> resItem, Order order) {
        List<OrderItem> list = new ArrayList<>();
        for (CreateOrderRequest.OrderItemReq r : resItem) {
            OrderItem item = new OrderItem();
            item.setOrderCode(order.getOrderCode());
            item.setGoodsNum(r.getGoodsNum());
            item.setCreateTime(order.getCreateTime());
            item.setGoodsPriceSnapshot(r.getPriceSnapshot());
            list.add(item);
        }
        return list;
    }
}

