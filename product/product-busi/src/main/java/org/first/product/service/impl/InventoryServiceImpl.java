package org.first.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.first.comm.model.CommonResponse;
import org.first.comm.util.SimRedisUtil;
import org.first.product.entity.Inventory;
import org.first.product.mapper.InventoryMapper;
import org.first.product.request.CreateOrderRequest;
import org.first.product.service.IInventoryService;
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
public class InventoryServiceImpl extends ServiceImpl<InventoryMapper, Inventory> implements IInventoryService {

    @Autowired
    private SimRedisUtil simRedisUtil;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    @Qualifier("defExecutor")
    private Executor defExecutor;


    @Transactional//默认隔离级别，默认传播范围
    @Override
    public CommonResponse<String> deductStock(CreateOrderRequest req) {
        if(req.getItems().size() == 0) {
            return CommonResponse.error("错误 ，商品列表为空！");
        }

        Map<String, Inventory> invMap = batchQueryInfo(req);

        for(CreateOrderRequest.OrderItemReq item: req.getItems()) {
            int status = 0;
            int reTryTimes = 3;
            int reTryDelay = 500;
            do {
                Inventory inv = invMap.get(item.getGoodsCode());
                if(inv==null) {//通过null刷新
                    inv = baseMapper.selectByGoodsCode(item.getGoodsCode());
                    if(inv==null) {//还查不到那就是没库存记录了
                        throw new RuntimeException(item.getGoodsCode() + "库存不足！");
                    }
                }
                if(item.getNum() > inv.getStockCount() - inv.getLockCount()) {
                    try { Thread.sleep(reTryDelay); }
                    catch (InterruptedException e) { throw new RuntimeException(e); }//暂时throw
                    invMap.put(item.getGoodsCode(), null);//设置为null以在下次进来时重新查询
                    status = -1;
                    continue;//循环未完，再试试说不定有其他订单释放锁定库存
                }
                //执行乐观锁更新
                int flowRow = baseMapper.updateByLockCount(item.getNum(), item.getGoodsCode(), inv.getVersionCode());
                if(flowRow==1) {
                    status = 1;
                } else {
                    try { Thread.sleep(reTryDelay); }
                    catch (InterruptedException e) { throw new RuntimeException(e); }//暂时throw
                    invMap.put(item.getGoodsCode(), null);//设置为null以在下次进来时重新查询
                    status = 0;
                    continue;
                }
            } while(--reTryTimes >0);

            if(status==-1) {
                //回滚
                throw new RuntimeException(item.getGoodsCode() + "库存不足！");
                //String err = item.getGoodsCode() + "库存不足！";
                //return CommonResponse.error(err);
            } else if(status==0) {
                //回滚
                throw new RuntimeException(item.getGoodsCode() + "人太多了！稍后再试！");
                //String err = item.getGoodsCode() + "人太多了！稍后再试！";
                //return CommonResponse.error(err);
            } else {

            }
        }
        return CommonResponse.success("锁定库存成功！");
    }

    //提前批量查出版本号
    private Map<String, Inventory> batchQueryInfo(CreateOrderRequest req){
        List<String> goodsCodeList = req.getItems().stream()
                .map(CreateOrderRequest.OrderItemReq::getGoodsCode)
                .collect(Collectors.toList());

        LambdaQueryWrapper<Inventory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Inventory::getGoodsCode, goodsCodeList)
                .select(Inventory::getGoodsCode, Inventory::getVersionCode,
                        Inventory::getStockCount, Inventory::getLockCount);
        List<Inventory> invList = baseMapper.selectList(queryWrapper);
        Map<String, Inventory> map = invList.stream()
                .collect(Collectors.toMap(
                        Inventory::getGoodsCode,   // key
                        inv -> inv        // value
                ));
        return map;
    }
}
