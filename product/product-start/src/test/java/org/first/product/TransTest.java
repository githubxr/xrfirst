package org.first.product;

import org.first.product.entity.Inventory;
import org.first.product.service.IInventoryService;
import org.first.product.service.impl.TestServiceInv;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

/**
 * 隔离级别实验
 * @since 25/12/04
 * */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ProductApplication.class)
//@ComponentScan("org.first.product")
public class TransTest {

    @Autowired
    private IInventoryService inventoryService;

    //单独导入impl，测试用的taskA,taskB懒得麻烦在interface声明；
    // 另外，我觉得，这里的impl和inventoryService应该是同一个堆对象吧？ 只是类型声明不同， 是泛型机制，一个是直接指向？
    @Autowired
    private TestServiceInv impl;

    @Autowired
    @Qualifier("defExecutor")
    private Executor defExecutor;

    //简单测试list
    @Test
    public void list(){
        System.out.println("shit");
        List<Inventory> list = inventoryService.list();
        for(Inventory item: list) {
            System.out.println(item.toString());
        }
    }

    //1204-Day-test
    @Test
    public void abTest() {
        //System.out.println("defExecutor:" + defExecutor);
        //CompletableFuture.supplyAsync(() ->
        CompletableFuture<String> c = CompletableFuture.supplyAsync(()->impl.taskA(), defExecutor);

        try {
            Thread.sleep(200);//睡200ms，确保taskA先执行；
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        CompletableFuture.runAsync(()->impl.taskB(), defExecutor);

        try {
            System.out.println("执行结果：" + c.get());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

   @Test
    public void t1(){
       impl.tast1P();
   }

    @Test
    public void t2(){
        impl.tast2P();
    }

    @Test
    public void t3(){
        impl.tast3P();
    }

    //Day3测试：100线程并发扣库存 （丢失更新测试）
    @Test
    public void day3A(){
        //System.out.println("defExecutor:" + defExecutor);

        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for(int i=0; i<100; i++) {
            futures.add(CompletableFuture.runAsync(()->impl.day3A(), defExecutor));
        }
        //junit测试得join，否则若主线程不等子线程，主线程结束, junit就会关闭spring容器，程序，其它异步任务线程任务会中止不执行；
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }

    //Day3测试：超卖测试 - 原子自减版-200线程并发扣库存
    @Test
    public void day3B() {
        impl.day3B();
    }

    //Day3测试：超卖测试 - 普通版 - 100线程并发扣库存(2)
    @Test
    public void day3C() {
        impl.day3C(2);
    }

    //Day3测试：超卖测试 - 乐观锁解决方案测试 - 200线程并发扣库存(3)
    @Test
    public void day4A() {
        int sum = impl.day4A_OptLock(3);
        System.out.println("运行成功， 成功出库订单数：" + sum);
    }

}
