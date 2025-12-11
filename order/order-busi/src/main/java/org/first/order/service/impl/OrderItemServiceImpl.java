package org.first.order.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.first.order.entity.OrderItem;
import org.first.order.mapper.OrderItemMapper;
import org.first.order.service.IOrderItemService;
import org.springframework.stereotype.Service;

@Service
public class OrderItemServiceImpl extends ServiceImpl<OrderItemMapper, OrderItem> implements IOrderItemService {

}
