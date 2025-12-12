# ---------------------------------

# **📁 0. 多线程（Java 并发）**

# ---------------------------------

##### **0.1 五大状态**

- NEW
- RUNNABLE（争夺时间片、运行中，不要求有锁）
- WAITING（wait(持锁)/join(无锁)/park，可有锁）
- TIMED_WAITING（sleep / wait(long)）
- BLOCKED（获取锁失败挂在锁队列）
- TERMINATED（run 执行完毕）

------

##### **0.2 volatile**

- 可见性（读主内存）
- 禁止指令重排序
- **不保证原子性**

> synchronized 是 volatile 的 plus 版本（包含可见性 + 原子性 + 锁）

------

##### **0.3 synchronized**

- 可见性
- 原子性
- 互斥锁
- 内存屏障

------

##### **0.4 join / wait / park / yield / sleep**

- `a.join()`：当前线程等待某线程执行结束
- `wait()`：释放锁
- `park()`：不释放锁
- `yield()`：让出时间片（不论调用哪个对象，都让出当前线程 CPU）
- `sleep()`：不释放锁，进入 TIMED_WAITING

##### 0.5 数据类型要补充queue

                 ┌──────────── Collection ────────────┐
                 │                                     │
      ┌──────────┴──────────┐             ┌───────────┴───────────┐
      │                      │             │                       │
    List                   Set           Queue                  Deque
(有序可重复) 		 (无序不重复)		(队列)				(双端队列)
  │                      	  │
  ├─ ArrayList            ├─ HashSet
  ├─ LinkedList          ├─ LinkedHashSet
  └─ Vector                 └─ TreeSet


                         ┌───────── Map ──────────┐
                         │                         │
                     HashMap                 SortedMap/TreeMap
                     LinkedHashMap
                     Hashtable
                     ConcurrentHashMap



------

# ---------------------------------

### **📁 1. Spring / 注解 / 反射 / 自动装配**

# ---------------------------------

##### **1.1 常用注解**

- @PostConstruct 生命周期回调
- @Qualifier、@Value
- 属性注入可用构造器注入：
  - `@Qualifier(value="...")`
  - `@Value("${xxx.xx}")`

------

##### **1.2 反射**

- setAccessible(true)：突破 private
- 破坏封装
- 慢 / 低效
- 框架为什么大量用反射（Spring、MyBatis、JPA）

------

##### **1.3 SpringBoot 自动装配（核心知识）**

用法示例（需要写在 META-INF/spring.factories）：

```
org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
org.first.order.config.OrderFeignAutoConfiguration
```

特点：

- 提供方写 spring.factories
- 让使用方无需再自己配，避免责任下推
- **规范：comm 不应该写 spring.factories，应由每个模块主动引入 comm**

可自动装配对象：

- Fallback（Feign）
- Retryer
- 编解码器（protobuf）
- 日志
- 连接池

------

##### **1.4 Spring 扫包规则（非常重要）**

- Feign 扫包：**顶层包，如 org.first**
- Bean 扫包：只扫自己 + comm
  - 注意，规范上来说，**不允许comm添加spring.factories**，而是让 每个模块的application添加comm的就是规范的

- FeignClient 会自动生成代理实现类
- Controller 接收对象空：通常是因为**漏写 @RequestBody**

------

##### **1.5 全局异常**

- @RestControllerAdvice

------

##### **1.6 日志系统**

- LogBack（SpringBoot 默认，CRUD 足够）
  - 用法：yml中通过logging.level.***配置

- Log4j2（高并发微服务推荐）

------

# ---------------------------------

### **📁 2. SQL（MySQL 全体系）**

# ---------------------------------

------

##### 测试记录【\core_skill_plan日志\round2测试记录 .md】

##### **2.0 SQL 基础要点**

- unique ⇒ 自动创建唯一索引（不用再建）
- count(*) ⇒ 统计总行数
- count(field) ⇒ 统计非 NULL 行
- sum 同理，只统计非 NULL
- 优化的核心：**索引 + 过滤顺序**
- MySQL 与 NULL 任何比较都是 NULL
  - 必须用 `is null / is not null`

------

##### **2.1 索引体系**

索引失效场景

- 隐式转换（string = number）
- 显式转换（concat / substr）
- like '%xxx' 前置百分号
- or 出现非索引字段
- 表达式破坏最左前缀
- not in / not exists（部分情况）

------

最左前缀原则

- 联合索引 where 必须从最左字段依次匹配
- 模糊查询同理（% 开头直接废索引）

------

小表驱动大表

WHERE 优先过滤驱动表（小表）
 ON 优先过滤被驱动表（大表）

​	有效原因：内连接可写在 WHERE，左连接必须写在 ON），让大表在关联时提前过滤

小表驱动大表拓展

- 复杂联表，将小表改为子查询，然后大表用
  - in：小表数据少（大约lines < = 100~3000吧)
  - exist： 适用于小表只需要【存在一条数据】

- 子查询层级不能太深 / 子查询数据多 得改为join...

要点：

1. where 中的小表过滤会影响 join 顺序
2. 大表过滤应写 ON，否则 left join 会被破坏
   - 左连接时，**被驱动表（右表）的过滤条件：** 应该放在 `ON` 后面（左表的则是where即可）（新知识）

3. 内连接可放 WHERE，但 left join 必须放 ON

------

###### Hash Join问题

测试数据如果少，那么SQL优化器（实测即便关闭hash join也）不会走索引，因此无法完整测试；

可用强制策略

- 强制索引：

```
FROM table FORCE INDEX(idx_name)
```

- 强制驱动顺序：

```
STRAIGHT_JOIN
```

------

JOIN 阶段深入理解

- inner join 一般谁放左都行
- left join 读顺序固定，小表要 放左边
- 小表放左是通用规范，以便阅读

------

大偏移量 limit 优化

```
where id > (select id from ... limit 1 offset N)
limit 10
```

------

##### **2.2 explain（核心）**

关键字段

| 字段          | 解释                               |
| ------------- | ---------------------------------- |
| id            | 执行层级（越大优先级越高）         |
| select_type   | primary / derived                  |
| type          | **最重要：访问方式**               |
| possible_keys | 候选索引                           |
| key           | 实际使用的索引                     |
| rows          | 扫描行数                           |
| filtered      | 过滤比例                           |
| extra         | Using index / Using join buffer 等 |

type 优先级

```
system > const > eq_ref > ref > range > index > ALL
```

- system：整表只有1行数据

- const：走了索引（pk,unique)

- eq_ref：联表走了索引（pk,unique）

- ref：普通索引

- range：索引范围查询

- index：无条件，但是只select 索引字段

- all：全表扫描，需优化



****

##### 2.3 Join 专题

- 小表驱动大表
- where / on 的正确使用规律
- left join 的过滤位置规则
- 子查询 vs join 替换条件
- in vs exists
- 复杂子查询要避免层级过深

------

##### 2.4 窗口函数

本质：计算而非过滤; 

用途：也是对结果集进行分组，但是不会像聚合函数一样合并行;

- row_number
- rank / dense_rank
- lag / lead 偏移函数：
  - 上一行下一行字段的引用--lag(fieldname, offset)`/`lead(fieldname, offset)


经常与 CTE 一起使用，便于多次引用窗口结果

- 可复用窗口函数字段结果的值
- 窗口函数，是，在一次遍历中为每条数据计算其窗口值

------

##### 2.5 CTE（WITH）

特点：

- CTE 在 SQL 生命周期中是最后一阶段（order by 之后）
- 可重复引用
- 多个 CTE 连续定义：

```
with c1 as (...),
c2 as (select * from c1),
###每一个cet都可以联表上一个cet的内容
c3 as (...)
select * from c3;
```

------

##### 2.6 慢 SQL

开启方式：

```
----- 先开启
-- 1. 开启慢查询日志（1=开启，0=关闭）
SET GLOBAL slow_query_log = 1;
-- 2. 设置慢查询阈值（单位：秒，这里设1秒，超过1秒的SQL会被记录）
SET GLOBAL long_query_time = 1;
-- 3. 查看慢查询日志存储路径（找到日志文件，后续分析）
SHOW VARIABLES LIKE 'slow_query_log_file';
-- 4. windows如果没有打开权限，那就为当前用户添加权限即可；
```

Windows 无权限 ⇒ 开 UTF8 Beta 模式（语言设置）

------

- - 


------

# ---------------------------------

### **📁 3. 微服务体系（SpringCloud / Feign / Gateway / Nacos）**

# ---------------------------------

------

##### **3.1 模块结构（DDD 模式）**

```
comm
    comm-core
    comm-feign
    comm-busi
user
    user-api / user-busi / user-start
product 同上
order 同上
payment 同上
gateway
```

------

##### **3.2 包结构规范（Entity / DTO / BO / VO）**

| 概念   | 定义                       | 用途            |
| ------ | -------------------------- | --------------- |
| Entity | 表结构映射，只有字段       | DAO、Mapper     |
| DTO    | 跨服务传输，只有字段       | 接口请求/响应   |
| BO     | 服务层业务载体，允许有方法 | 服务内部业务    |
| VO     | 展示格式（前端用）         | Controller 返参 |

------

##### **3.3 Feign（核心）**

feign重试/日志/异常：

```
- 要点：
- 不论是factory还是fallback都需要添加熔断配置才会生效：
feign:
  sentinel:
    enabled: true
```

没有这个 fallback 不生效。

fallback vs fallbackFactory 区别

- fallback：统一返回固定对象
- fallbackFactory：能拿到异常信息做细化处理

------

##### **3.4 Nacos**

```
startup.cmd -m standalone
http://localhost:8848/nacos
```

------

##### **3.5 Gateway**

调用时会通过http调用对应微服务url；

- routes.uri：真实服务地址
- predicates.path：拦截路径

------

##### **3.6 Feign 为什么接口不需要实现类？**

因为 SpringCloud 会自动生成动态代理（JDK Proxy）

------

##### **3.7 SpringBoot 自动装配示例（再次确认）**

用法

```
META-INF/spring.factories

org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
org.first.order.config.CommonBeanConfig
```

springboot自动装配还可以配置：自定义Retryer，自定义编解码器（protobuf例如），自定义日志，自定义连接池，



****

##### **3.8 其它问题记录**

- ErrorDecoder：远程异常解析
- 忘记 @RequestBody 导致 null
- 扫包范围错误

------





### 4 NoSQL



### 5. 事务/隔离级别

当前进度：50%

##### 5.0 是什么

隔离级别和传播范围都是相对事务而言，不开事务无意义

- 隔离级别：解决读的问题（脏读[行内属性] / 幻读[行]）
- 传播范围：管理事务的创建 / 合并规则（包括读写）
- 原子更新/悲观/乐观锁：解决写的问题（超卖/丢失更新）

##### 5.1 要素：

​	掌握隔离级别、事务传播、多线程一致性

​	考虑简单的分布式事务

- 隔离级别（DB控制）
- **隔离级别实验**（每种 1–2h + markdown 记录）【1130提前补习四大事务/4大隔离级别】：
  1. 多线程并发扣库存
  2. 事务传播实验（REQUIRED / REQUIRES_NEW / NESTED）
  3. 异常回滚验证
- **产出**：6 个实验 + Markdown 测试结果截图

- 
- extra内容：分布式事务简单实现
  - SAGA顺序调用（预计2Day【每天2H】）



##### 区分

| 概念            | 传播行为                      | 隔离级别             |
| --------------- | ----------------------------- | -------------------- |
| 是谁控制的？    | Spring 框架                   | 数据库（MySQL）      |
| 解决什么问题？  | 加入还是新开事务              | 锁、读写一致性       |
| 典型关键字      | REQUIRED、REQUIRES_NEW        | RR、RC、Serializable |
| 和 A 有啥关系？ | 决定 A、B、C 是否共用一个事务 | 决定事务内部的锁行为 |
| 默认值          | REQUIRED                      | RR（MySQL 默认）     |

##### 5.2 Detail记录

隔离级别概念补习

此概念，是相对于【多个事务处理的数据重合】的产物；

形象比喻：

```
假设有个架子，每个架子都有多个积木盘，小孩可以占据n个积木盘玩；
假设 事务 就是小孩玩一次积木，
架子上的，就是commit提交了的数据
架子下的，就是，未提交的数据
小孩离开并把积木放到架子上（commit）就是事务结束
小孩发脾气把积木从架子上丢下来就是rollback
- 读未提交（就是小孩只自顾自玩，架子下和架子上的积木，都能看到和玩， 别人积木盘里（别人不阻止））
- 读已提交（A看不到架子下的积木，只能看到并玩架子上的积木，不阻止其它玩A积木盘里A在玩的积木）
- 可重复读（只玩架子上的积木，阻止其它小孩玩A积木盘里A在玩的积木）
- 串行化（一次只准一个小孩进来玩）

#“A在操作的数据” = A已经读取的快照 + A未提交的修改（执行了update但是未commit/rollback）

隔离级别术语补习 
- 值变化 → 不可重复读
- 行变化 → 幻读
```

- remark：开启事务后insert，在commit之前，其它查询都查不到new line  的；



##### 5.3 传播范围（Spring控制）

- 定义：多个事务叠加场景下的规则（非嵌套事务不太影响）

- 重点：子事务是否会影响父事务

- 类型

  - 默认required：默认开启，如果多个就合并为一个事务；

  - requires_new：默认开启，如果多个，就暂停已有事务【新建新的事务】；

  - supports：完全取决于父事务，父有子有（合并），父无子无

  - not_supported：父有就暂停父，子非事务跑完，继续父事务

    - 思考：特殊用途，

      那么，当A有事务，A的事务就暂停，等B的一些列非事务的内容执行完，才继续a的事务？
      那如果是这样，B开启事务的意义何在？b不想要事务，那就不开启事务不就行了？
      还是说，就是为了让B 的非事务操作执行到commit之前，暂停a的事务？

      解答是，在「方法被强制要求加事务注解」的场景下，主动让方法脱离事务上下文，同时实现 “暂停当前外层事务” 的特殊效果

  - mandatory：父有子入，父无子报错

  - never：父有子报错，父无子非事务运行

  - nested：

    - 父有，父在子入时建立保存点，子失败父从保存点继续，不影响（无新事务）
    - 父无，子新建正常事务执行

###### 传播范围踩坑

- 死锁情况：`REQUIRES_NEW` 被 *外层* 事务调用时，如果它更新同一条数据，会引发死锁或长时间等待
- 

##### 5.4 SQL原理补充

- update会自动加行锁，执行完自动释放（如果开启了事务，那么就得等commit/rollback才会释放）
- for update也会加行锁（且自动添加隐式事务），commit/rollback才释放；

##### 5.5 解决超卖/少卖（更新丢失）的三个解决方案

-  乐观锁：
  - 乐观锁的“版本判断”必须放到 update 的 WHERE 子句里，才能保证原子性和正确性 

- 悲观锁：
  - select时就添加for update（锁住行），直到commit/rollback
  - 如果执行for update之前没有开启事务，那么会隐式开启事务。反之则不会；
  -  

##### 5.6 三重点

整合30day项目的实质三重点 —— 分布式锁 / 乐观锁 / 分布式事务 的要点

| 技术                                      | 解决什么？                                         | 作用范围           |
| ----------------------------------------- | -------------------------------------------------- | ------------------ |
| **分布式锁（Redisson）**                  | 控制“同一个商品”“同一个用户”在多个机器上并发扣库存 | 微服务层之间的互斥 |
| **乐观锁（version 或 where stock >= x）** | 控制**数据库里的库存不会被扣成负数**               | DB 层防止超卖      |
| **简单分布式事务（SAGA）**                | 控制跨服务链路 A→B→C 出错后能补偿                  | 跨服务链路一致性   |

在 `order → product（库存）` 微服务下单链路中，**三者并非必须同时实现**，核心是：

- 「超卖 / 少卖」的核心防控是 乐观锁（DB 层），这是**兜底**保障；
- 「分布式锁」是优化项（减少无效请求、降低乐观锁冲突）分布式**资源独占**；
- 「SAGA 分布式事务」
  - 是全架构**一致性**保障项（解决跨服务链路失败后的回滚）； 
  - 控制跨服务链路 A→B→C **出错后能补偿**



##### 5.7（新增）写的问题（InnoDB 行锁体系 + 隐式事务）

###### 写的问题由行级锁解决：

InnoDB 行锁三种：

1. Record Lock（记录锁）
2. Gap Lock（间隙锁）
3. Next-Key Lock（记录锁 + 间隙锁，用于防止幻读）

###### 为什么 UPDATE 会自动加锁？

- 任何 UPDATE/DELETE/INSERT 在执行时一定会：
  - 自动开启隐式事务（除非 autocommit=0）
  - 自动加行锁（先锁，再读，再写）
- 在 RR 下会默认加 next-key lock
- 在 RC 下只加记录锁（没有 gap lock）

###### 写相关的典型问题：

- 脏写：两个事务写同一行 → 只能用锁或 CAS 解决
- 丢失更新：后写覆盖前写 → 悲观锁/乐观锁（version 字段）
- 写写冲突：锁等待 / 死锁（两边都持有锁）

---



### 6 设计模式

- 工厂
- 策略模式（静态map、枚举、动态注册[强大&暂时不做&超出45day task范围]）
- 适配器模式



### 7. 前端

- watchEffect无需指定属性，所有属性变化都会触发回调
- watch必须指定监听的属性；
- ref套了
- reactive不套，且重新引用会失去响应式









# ✔️99 待定目标

（一个月后再看）

- ##### 60天冗余目标（看时间还有没有富余，有的话就搞）

  - ai-tips文件中的锐软笔试题 可以简单复做（此30day项目覆盖了此笔试题内容且更强，后续有时间看）

  - 异常回滚（本地+远程）

  - 并发扣库存（模拟100并发
    - 分布式锁 + 本地锁混用

  - 事务传播练习 

- - 简单jwt原理 + 基本安全设计
  - 链路追踪traceid
  - JVM简单走一遍
  - MVC流程
  - 限流
  - DB：行锁，间隙锁，’下键锁‘...
  - linux
  - docker
  - MQ3天上手计划

    - 下单异步扣库存
    - 订单支付成功通知
    - 异步大任务（导出报表等等）

- 只要有时间，压测要重点考虑：**JMeter / wrk / vegeta**
  压测库存扣减接口 → 测试 5000 并发

  看：

  - 是否超卖
  - 是否死锁
  - 是否 Redis 竞争锁
  - 是否出现库存扣减不一致

