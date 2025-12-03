




# 2025





11/20开始

### 周记11/21~23


##### 记录

- 选型，用springcloud alibaba吧 4
- nacos启动 5
    - 经过问题的四条解决，成功在nacos看到了order和user；
    - 成功调用了微服务，返回：【controller.call api:Feign result：hello + 入参：null】
      - 但是不确定是否真的走了微服务链路；且目前貌似没有走gateway（都没加...）直接访问的目标application



##### Week Detail

- nacos

    - ```
    startup.cmd -m standalone
    http://localhost:8848/nacos
    ```

-



##### 问题

- 以为EnableFeignClients只需要扫描模块所属模块，实际上需要包括整个项目所有模块；
- Feign 接口为什么不需要实现类？
    - Feign 接口**确实不需要我们手动写实现类**，而是由**Spring 在运行时通过动态代理自动生成实现类**

- 都没有注册到nacos
    - 需要添加nacos注册发现的pom*依赖*
- pom报错，又是老调重弹的【要先install根 -> 公共 ->父 ->api ->busi -> start...】的顺序
- 明明自己知道scope的含义，就是没注意这个【<scope>provided</scope>】导致浪费了时间;




### 周记11/21~23 
- 待定...
