package org.first.product.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.first.comm.util.SimRedisUtil;
import org.first.product.entity.Inventory;
import org.first.product.mapper.InventoryMapper;
import org.first.product.service.IInventoryService;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class InventoryServiceImpl extends ServiceImpl<InventoryMapper, Inventory> implements IInventoryService {

    @Autowired
    private SimRedisUtil redisUtil;

    @Autowired
    private RedissonClient redissonClient;




}
