package org.first.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.first.comm.model.CommonResponse;
import org.first.product.entity.Inventory;
import org.first.product.mapper.InventoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;


/**
 * @description 测试使用的service
 * @since 2025/12/07
 * @author 25054 xr
 * */
@Service
public class TestServiceInv {
    @Autowired
    private InventoryMapper inventoryMapper;
    @Autowired
    private Executor defExecutor;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    @Lazy
    private TestServiceInv self;
    /**
     * 以下仅供测试，没有interface声明
     * */
    private final String TEST_ID = "inv_id1";
    private final String TEST_ID2 = "inv_id2";
    private final String TEST_ID3 = "inv_id3";

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

        Inventory inv = inventoryMapper.selectOne(wrapper);
        sb.append("睡前值：").append( inv.getLockCount());
        try {
            Thread.sleep(1200);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        //再读一次
        Inventory afInv = inventoryMapper.selectOne(wrapper);
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
        inventoryMapper.update(upWrapper);
    }

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.REPEATABLE_READ)
    public void tast1P() {
        LambdaUpdateWrapper<Inventory> upWrapper = new LambdaUpdateWrapper<>();
        upWrapper.set(Inventory::getStockCount, 10)
                .eq(Inventory::getId, TEST_ID);
        inventoryMapper.update(upWrapper);
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
        inventoryMapper.update(upWrapper);
        throw new RuntimeException("测试required_new报错");
    }

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.REPEATABLE_READ)
    public void tast2P() {
        LambdaUpdateWrapper<Inventory> upWrapper = new LambdaUpdateWrapper<>();
        upWrapper.set(Inventory::getStockCount, 10)
                .eq(Inventory::getId, TEST_ID);
        inventoryMapper.update(upWrapper);
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
        inventoryMapper.update(upWrapper);
        throw new RuntimeException("测试required报错");
    }

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.REPEATABLE_READ)
    public void tast3P() {
        LambdaUpdateWrapper<Inventory> upWrapper = new LambdaUpdateWrapper<>();
        upWrapper.set(Inventory::getStockCount, 10)
                .eq(Inventory::getId, TEST_ID);
        inventoryMapper.update(upWrapper);
        try {
            self.tast3S();
        } catch (Exception e) {
            System.out.println("tast3P.tast3S异常不抛");
        }
    }
    @Transactional(propagation = Propagation.NESTED, isolation = Isolation.REPEATABLE_READ)
    public void tast3S() {
        LambdaUpdateWrapper<Inventory> upWrapper = new LambdaUpdateWrapper<>();
        upWrapper.set(Inventory::getStockCount, 10)
                .eq(Inventory::getId, TEST_ID2);
        inventoryMapper.update(upWrapper);
        throw new RuntimeException("测试required报错");
    }


    //Day3测试： 丢失更新测试 - 100线程并发扣库存
    public void day3A() {
        Inventory inv = inventoryMapper.selectById(TEST_ID3);
        int newStock = inv.getStockCount() - 1;

        inventoryMapper.update(new LambdaUpdateWrapper<Inventory>()
                .eq(Inventory::getId, TEST_ID3)
                .set(Inventory::getStockCount, newStock)
        );
    }

    //Day3测试：超卖 - 200线程并发扣库存【数据库原子递减版本（非原子更新，原子更新需要where）】
    public void day3B() {
        //System.out.println("defExecutor:" + defExecutor);
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for(int i=0; i<200; i++) {
            futures.add(CompletableFuture.runAsync(()-> {
                Inventory inv = inventoryMapper.selectById(TEST_ID3);
                if(inv.getStockCount()>0) {
                    inventoryMapper.day3B(TEST_ID3);
                } else {
                    System.out.println("逻辑检测到库存不足！");
                }
            }, defExecutor));
        }
        //junit测试得join，否则若主线程不等子线程，主线程结束, junit就会关闭spring容器，程序，其它异步任务线程任务会中止不执行；
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }

    //@important！ 重要： 只要是先查后update（更新内容是在先查的基础上进行的），大概率是’丢失更新‘而不是’超卖‘
    //Day3测试：丢失更新 - 100线程并发扣库存【数据库原子递减版本）
    //@param orderNum 订单扣减库存量（假设都是一样的）
    public void day3C(int orderNum) {
        //System.out.println("defExecutor:" + defExecutor);
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for(int i=0; i<300; i++) {
            futures.add(CompletableFuture.runAsync(()-> {
                Inventory inv = inventoryMapper.selectById(TEST_ID3);
                if(inv.getStockCount()<orderNum) {
                    System.out.println("逻辑检测到库存不足！");
                    return;
                }

                inventoryMapper.update(new LambdaUpdateWrapper<Inventory>()
                        .eq(Inventory::getId, TEST_ID3)
                        .set(Inventory::getStockCount, inv.getStockCount() - orderNum)
                );

            }, defExecutor));
        }
        //junit测试得join，否则若主线程不等子线程，主线程结束, junit就会关闭spring容器，程序，其它异步任务线程任务会中止不执行；
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }


    //Day4 本地synchronized锁住
    public void day3D(int orderNum) {
        //System.out.println("defExecutor:" + defExecutor);
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (int i = 0; i < 200; i++) {
            futures.add(CompletableFuture.runAsync(() -> {
                synchronized (InventoryServiceImpl.class) {
                    Inventory inv = inventoryMapper.selectById(TEST_ID3);
                    if (inv.getStockCount() < orderNum) {
                        System.out.println("逻辑检测到库存不足！");
                        return;
                    }
                    inventoryMapper.update(new LambdaUpdateWrapper<Inventory>()
                            .eq(Inventory::getId, TEST_ID3)
                            .set(Inventory::getStockCount, inv.getStockCount() - orderNum)
                    );
                }
            }, defExecutor));
        }
        //junit测试得join，否则若主线程不等子线程，主线程结束, junit就会关闭spring容器，程序，其它异步任务线程任务会中止不执行；
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }

    //Day4A： 乐观锁解决超卖问题
    //@param orderNum 每个订单统一的 扣减库存数量
    //@return 成功的订单数量
    public int day4A_OptLock(int orderNum){
        //System.out.println("defExecutor:" + defExecutor);
        List<CompletableFuture<Integer>> futures = new ArrayList<>();
        for (int i = 0; i < 200; i++) {
            futures.add(CompletableFuture.supplyAsync(() -> {

                int reTryTimes = 3;
                int reTryDelay = 500;
                while(reTryTimes-- > 0) {
                    Inventory inv = inventoryMapper.selectById(TEST_ID3);
                    if (inv.getStockCount() < orderNum) {
                        System.out.println("逻辑检测到库存不足！");
                        return 0;
                    }
                    int flowRow = inventoryMapper.update(new LambdaUpdateWrapper<Inventory>()
                            .eq(Inventory::getId, TEST_ID3)
                            .eq(Inventory::getVersionCode, inv.getVersionCode())
                            .set(Inventory::getStockCount, inv.getStockCount() - orderNum)
                            .set(Inventory::getVersionCode, inv.getVersionCode() + 1)
                    );
                    if(flowRow == 1) { return 1; }
                    else if(flowRow!=0) { System.out.println("#### 意料之外的情况"); }
                    else try { Thread.sleep(reTryDelay); } catch (InterruptedException e) { throw new RuntimeException(e); }//暂时throw runtime...
                }
                return 0;
            }, defExecutor));
        }
        //junit测试得join，否则若主线程不等子线程，主线程结束, junit就会关闭spring容器，程序，其它异步任务线程任务会中止不执行；
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        int sum =
                CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                        .thenApply(v -> futures.stream()
                                .map(CompletableFuture::join)   // 拿到每个 future 的结果
                                .mapToInt(Integer::intValue)     // 转为 IntStream
                                .sum())                          // 求和
                        .join();                                 // 返回最终结果
        return sum;
    }

    //Day4B： 原子更新解决超卖问题
    //Day4C： 悲观观锁解决超卖问题
}
