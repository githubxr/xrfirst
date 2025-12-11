package org.first.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.first.comm.model.CommonResponse;
import org.first.comm.util.SimRedisUtil;
import org.first.product.entity.Goods;
import org.first.product.entity.Inventory;
import org.first.product.mapper.GoodsMapper;
import org.first.product.request.CreateOrderRequest;
import org.first.product.service.IGoodsService;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;


@Service
public class GoodsServiceImpl extends ServiceImpl<GoodsMapper, Goods> implements IGoodsService {

    @Autowired
    private SimRedisUtil simRedisUtil;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    @Qualifier("defExecutor")
    private Executor defExecutor;


    @Override
    public List<Goods> queryCurrPriceByGoodsCode(List<String> goodsList) {

        return baseMapper.selectList(new LambdaQueryWrapper<Goods>()
                .select(Goods::getGoodsCode, Goods::getPrice)
                .in(Goods::getGoodsCode, goodsList));
    }
}
