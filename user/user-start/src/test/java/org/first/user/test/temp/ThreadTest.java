package org.first.user.test.temp;


/**
 * @description 原生生产消费测试
 * @since 2025/1123
 * */
public class ThreadTest {
    volatile int target = 100;//生产目标

    volatile int left = 0;//生产的产品

    boolean isContinueWrok = true;
    //使用类锁
    synchronized void comsume(){
        while(isContinueWrok && left==0) {
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        // 仍然可能因为 shutdown 唤醒
        if (!isContinueWrok && left == 0) return;
        left --;
        System.out.println("消费者："+ Thread.currentThread().getName()+"，消费1个后剩余：" + left
                );
        notifyAll();
    }

    synchronized void prod(){
        while(left>3) {//max:3
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        left ++;
        target--;
        System.out.println("生产者："+ Thread.currentThread().getName()+"，生产1个后剩余：" + left+";总生产：" + (100-target)
            );


        // 再判断 running
        if (!isContinueWrok) return;

        //生产任务完成
        if(target==0){
            isContinueWrok = false;
        }

        notifyAll();


    }


    public static void main(String[] args) {

        ThreadTest test = new ThreadTest();

        Thread prod = new Thread(new Runnable() {
            @Override
            public void run() {
                while(test.isContinueWrok){
                    test.prod();
                }
            }
        });

        Runnable consTask = new Runnable() {
            @Override
            public void run() {
                while(test.isContinueWrok) {
                    test.comsume();
                }
            }
        };

        Thread cons1 = new Thread(consTask);
        Thread cons2 = new Thread(consTask);
        Thread cons3 = new Thread(consTask);


        prod.start();
        cons1.start();
        cons2.start();
        cons3.start();


    }

}
