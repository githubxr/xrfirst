package org.first.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.first.product.entity.Inventory;
import org.first.product.mapper.InventoryMapper;
import org.first.product.service.IInventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InventoryServiceImpl extends ServiceImpl<InventoryMapper, Inventory> implements IInventoryService {


    @Autowired
    @Lazy
    private InventoryServiceImpl self;
    /**
     * 以下仅供测试，没有interface声明
     * */
    private final String TEST_ID = "inv_id1";
    private final String TEST_ID2 = "inv_id2";

    //1 A 开事务读一次，sleep;
    //2 A sleep期间 B commit
    //3 A再读一次
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.REPEATABLE_READ)
    public String taskA() {
        StringBuilder sb = new StringBuilder();
        //因为即使你设置成 READ UNCOMMITTED，它依旧使用 MVCC，几乎无法在 MySQL 上复现真正的脏读
        //需要使用，悲观锁相关的for update
        LambdaQueryWrapper<Inventory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Inventory::getId, TEST_ID)
                .last("for update");

        Inventory inv = baseMapper.selectOne(wrapper);
        sb.append("睡前值：").append( inv.getLockCount());
        try {
            Thread.sleep(1200);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        //再读一次
        Inventory afInv = baseMapper.selectOne(wrapper);
        //sb.append("\n").append(inv);
        sb.append("睡后读：").append( afInv.getLockCount());
        //线程池执行sout，有时怎么不打印在控制台？ 所以我把他们改成了 返回值
        return sb.toString();
    }

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.REPEATABLE_READ)
    public void taskB() {
        LambdaUpdateWrapper<Inventory> upWrapper = new LambdaUpdateWrapper<>();
        upWrapper.set(Inventory::getLockCount, 10)
                .eq(Inventory::getId, TEST_ID);
        baseMapper.update(upWrapper);
    }

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.REPEATABLE_READ)
    public void tast1P() {
        LambdaUpdateWrapper<Inventory> upWrapper = new LambdaUpdateWrapper<>();
        upWrapper.set(Inventory::getStockCount, 10)
                .eq(Inventory::getId, TEST_ID);
        baseMapper.update(upWrapper);
        try {
            self.tast1S();
        } catch (Exception e) {
            System.out.println("tast1P.tast1S异常不抛");
        }
    }
    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.REPEATABLE_READ)
    public void tast1S() {
        LambdaUpdateWrapper<Inventory> upWrapper = new LambdaUpdateWrapper<>();
        upWrapper.set(Inventory::getStockCount, 10)
                .eq(Inventory::getId, TEST_ID2);
        baseMapper.update(upWrapper);
        throw new RuntimeException("测试required_new报错");
    }

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.REPEATABLE_READ)
    public void tast2P() {
        LambdaUpdateWrapper<Inventory> upWrapper = new LambdaUpdateWrapper<>();
        upWrapper.set(Inventory::getStockCount, 10)
                .eq(Inventory::getId, TEST_ID);
        baseMapper.update(upWrapper);
        try {
            self.tast2S();
        } catch (Exception e) {
            System.out.println("tast2P.tast2S异常不抛");
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.REPEATABLE_READ)
    public void tast2S() {
        LambdaUpdateWrapper<Inventory> upWrapper = new LambdaUpdateWrapper<>();
        upWrapper.set(Inventory::getStockCount, 10)
                .eq(Inventory::getId, TEST_ID2);
        baseMapper.update(upWrapper);
        throw new RuntimeException("测试required报错");
    }

}
