package org.first.product;

import org.first.product.entity.Inventory;
import org.first.product.service.IInventoryService;
import org.first.product.service.impl.InventoryServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
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
    private InventoryServiceImpl impl;

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
    public void abTest(){
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
    public void t13(){
        //impl.tast3P();
    }
}
