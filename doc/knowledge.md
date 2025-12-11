

# Knowledge Base（知识库）

---



### 0. 多线程

##### 0.1 五大状态
- NEW
- RUNNABLE (抢夺cpu时间片ing or 占有cpu时间片运行)【不一定要有锁】
- WAITING（wait(had🔒)/join(no🔒)/park）（可有锁）
- TIMED_WAITING（sleep/wait(long)）
- BLOCKED（争锁失败 锁队列等待）
- TERMINATED(run退出)

##### 0.2 volatile

- 可见性（内存屏蔽机制直接读取共享数据而非线程内拷贝的缓存）
- 禁止指令重排序
- 不保证原子性（因为无锁，线程安全需要其它操作）

##### 0.3 synchronized

​	(如果共享的数据是synchr了，就无需volatile,因为synchr就是vloatile的plus版本，多了锁；)

- 可见性
- 原子性
- 互斥锁
- 内存屏障

##### 0.4 join / wait / park / yield
- a.join(insta)：当前线程等待某线程执行完
- wait：释放锁
- park：不释放锁
- yield：让出时间片 
  - 不论是Thread.yield(),还是instanceA/B/C.yield，都是，执行此代码的线程让出cpu时间片

- sleep: 

---



### 1. Spring & 注解

- @PostConstruct 生命周期
- @Qualifier, @Value
- 反射：
  - setAccessible(true) ：调用 private 字段/方法
  - 破坏封装
  - 慢/低效
  - 反射的优缺点、框架为什么用反射


---



### 2. SQL 知识

##### 2.0 基础要点

- count(*) ： 统计行内容
- count(u.user_code)：统计指定字段【非Null的行】的合计
- sum也是类似的逻辑
- SQL优化 均依赖合理高效的索引
- myCircus不能用 !=null, ==null之类的，只能用 is null 和is not null，因为myCircus 任何和 NULL 的比较都是 NULL，不是 true / false

##### 2.1 索引
- 失效场景
- 最左前缀
- explain type

- 真正搞懂

  - where 优化小表驱动大表 
    - 优先级1：在 WHERE 中**优先过滤驱动表（小表）**
      - 有效原因：where中的联表条件会自动被挑选出来，作为【联表过程】优先执行，从而让小婊子更小；
    - 优先级2：把被驱动表（大表）的过滤条件**写在 ON 中**（如status=1; delete_flag=0;）
      - 有效原因：内连接可写在 WHERE，左连接必须写在 ON），让大表在关联时提前过滤

  - 联表时，**小婊习惯性放左边**（left join是这样，为了方便，习惯性把inner join也这样写，便于阅读的风格吧）
- 小表驱动大表拓展

  - 复杂联表，将小表改为子查询，然后大表用
    - in：小表数据少（大约lines < = 100~3000吧)
    - exist： 适用于小表只需要【存在一条数据】
- 子查询层级不能太深 / 子查询数据多 得改为join...
- 最左前缀（此为整理已掌握而非而非学习记录）

  - 联合索引在where中：where先左后右，多个and条件必须严格从最左索引字段逐个匹配（不一定要匹配完）（or会打断）； 
  - 模糊查询也是一样的原理，%_会导致低效
  - 左连接时，**被驱动表（右表）的过滤条件：** 应该放在 `ON` 后面（左表的则是where即可）（新知识）
- Limit 大偏移问题（接 limit 10000 10）解决： where id > xxx limit 0,10;

###### Hash Join问题

- 测试数据如果少，那么SQL优化器（实测即便关闭hash join也）不会走索引，因此无法完整测试；
- 可
  - 强制使用索引：FROM 表名 force index (索引名称)
    - 索引名称可通过【show index from 表名;】查出来
  - 强制驱动顺序：将join改为straight_join
    - 备注：同样是内连接，也可以和left，right搭配，如 left straight join

###### 索引失效场景

- 隐式转换：where str字段=整型
- 显式转换：concat / substr 之类的转换
- like 以 %开头
- or 非索引字段
- where 表达式，不符合前文提到的最左前缀
- 对索引字段使用 not in / not exists

###### Explan 专题

用法：

```
expalan sql语句
```

如何确认：

| 备注 | 列名            | 含义                                                         |
| ---- | --------------- | ------------------------------------------------------------ |
|      | `id`            | 层级越大代表越深，越深优先级越高；相同的话，上面的行的索引先执行 |
|      | `select_type`   | 字句类型：primary外层/`DERIVED`子查询/                       |
| 重点 | `type`          | 实际查数据方式：详见下面的罗列                               |
|      | `possible_keys` | 候选索引，不一定使用                                         |
|      | `key`           | 实际使用索引                                                 |
|      | `ref`           | 和联表的索引比对的列                                         |
|      | `rows`          | 预估扫描行数                                                 |
|      | `filtered`      | 过滤后的行百分比                                             |
|      | `extra`         | 优化建议（index加索引/where加过滤/Using join buffer 联表条件没索引/范围查每行是索引失效） |

重点看type字段：

- 访问类型，优先级：`system> const > eq_ref > ref > range > index > ALL`

  - system：整表只有1行数据

  - const：走了索引（pk,unique)

  - eq_ref：联表走了索引（pk,unique）

  - ref：普通索引

  - range：索引范围查询

  - index：无条件，但是只select 索引字段

  - all：全表扫描，需优化



###### 慢SQL

- ```
  ----- 先开启
  -- 1. 开启慢查询日志（1=开启，0=关闭）
  SET GLOBAL slow_query_log = 1;
  -- 2. 设置慢查询阈值（单位：秒，这里设1秒，超过1秒的SQL会被记录）
  SET GLOBAL long_query_time = 1;
  -- 3. 查看慢查询日志存储路径（找到日志文件，后续分析）
  SHOW VARIABLES LIKE 'slow_query_log_file';
  -- 4. windows如果没有打开权限，那就为当前用户添加权限即可；
  ```

######  左连接专题

-  场景：
   - 一般inner即可
   - 左连接适用于：...

-  用法：

   - **驱动表（左表）的过滤条件：** 必须放在 `WHERE` 后面。

   - **被驱动表（右表）的过滤条件：** 应该放在 `ON` 后面，除非您想彻底排除左表行

###### 窗口函数专题

概述：也是对结果集进行分组，但是不会像聚合函数一样合并行

窗口函数的本质：“计算” 而非 “过滤”

面试问用法：

- 排名：rank窗口聚合： sum()`/`avg()`/`count()
- 偏移函数：上一行下一行字段的引用--lag(fieldname, offset)`/`lead(fieldname, offset)

实战时，窗口函数时常会和CTE公共表表达式 (用 WITH 别名 AS (...) 定义的临时数据集) 搭配；

- 可复用窗口函数字段结果的值
- 窗口函数，是，在一次遍历中为每条数据计算其窗口值



###### CET表达式

- CET在SQL生命周期中，是【最后计算阶段】，处于order by 后面；
- CTE可重复，例如

```
with cet1 as (select...),
cet2 as(select cet1的字段无需cet1. ...from cet1) ,
cet3,4....
#正题
select ...


```

- 每个CET项都是一次结果集 计算的结果；



##### 2.2 Join & 小表驱动大表
- left join/right join
- join 选择与执行计划

##### 2.3 窗口函数
- row_number
- rank
- lag / lead

##### 2.4 常用查询
（你未来写的 SQL 技巧合集，这里放）

##### 2.5 测试记录

​	@【\core_skill_plan日志\round2测试记录 .md】

##### 2.6 问题记录

- mysql8无法安装：

  - 错误出现【mysqld: File '.\灞辫矾鍗侀噷涓嶆崲鑲?bin.index' not found (OS errno 2 - No such file or directory)】这种乱码，说明是mysql8的更高字符集要求导致的

    解决：修改windows的语言 区域中，勾选【Beta版：使用Unicode UTF-8提供全球语言支持】

- 





### 3. 微服务架构

##### 3.1 模块结构
comm
	comm-core  
	comm-feign  
	comm-busi  
user
	user-api / user-busi / user-start  
product 同上(均有xx-api/xx-busi/xx-start)  
order 同上
payment 同上
gateway
gateWay  

##### 3.2 包结构规范
| 术语   | 精准定义                                                     | 使用场景                    |
| ------ | ------------------------------------------------------------ | --------------------------- |
| Entity | 与数据库表 1:1 映射，仅含字段 + get/set，无任何业务逻辑      | 持久层（mapper/dao）交互    |
| DTO    | 跨进程 / 跨模块传输数据，仅含字段 + get/set，无业务逻辑（对外） | 接口请求 / 响应、模块间调用 |
| BO     | 服务层内部业务逻辑载体，可包含业务方法（对内）               | 服务层处理业务时封装数据    |
| VO     | 前端展示专用，可包含格式化、展示相关逻辑（如日期格式化）     |                             |

##### 3.3 问题解决记录

feign重试/日志/异常

- ```
  - 要点：
  - 不论是factory还是fallback都需要添加熔断配置才会生效：
  - feign:
      sentinel:
    ​    enabled: true
  ```

- 遗留问题：fallback和fallbackFactory有何区别？



nacos

- ```
  startup.cmd -m standalone
  http://localhost:8848/nacos
  ```

springboot的【自动装配fallback，避免责任变成使用方的】

- 具体教程：

  - 提供方模块，添加spring.factories即可让所有使用方无需重复配置；

  - 例子，fallback的：

    - ```
      #Resources/META-INF/spring.factories
      org.springframework.boot.autoconfigure.EnableAutoConfiguration=org.first.order.config.OrderFeignAutoConfiguration
      ```

  - 例子，order的：

    - ```
      # 核心配置：让Spring Boot自动加载CommonBeanConfig
      org.springframework.boot.autoconfigure.EnableAutoConfiguration=\org.first.order.config.CommonBeanConfig
      ```

次要补充：项目扫包规则

- feign的scan：顶层包，如org.first
- bean的scan：只扫自己，以及comm的

- 注意，规范上来说，**不允许comm添加spring.factories**，而是让 每个模块的application添加comm的就是规范的
- 扫feignClient，通过：无需操作，feignClient接口会自动生成代理；
- 接口收到的数据是Null：原因--忘了加@RequestBody

微服务补充

- gateway

  - 网关yml下的gateway.routes.uri和predicates.path的区别在于，前者是真实目标地址，后者是拦截路径；

- @FeignClient的interface会被代理自动生成实现类；

  调用时会通过http调用对应微服务url；

  添加fallback

- Feign 接口为什么不需要实现类？
  - Feign 接口**确实不需要我们手动写实现类**，而是由**Spring 在运行时通过动态代理自动生成实现类**

LogBack和Log4j2

- Logback（springBoot自带日志系统）：普通CRUD项目足够了
  - 用法：yml中通过logging.level.***配置

- Log4j2：更适合微服务高并发系统使用

- RestControllerAdvice，用于处理 controller 抛出的异常

- springboot自动装配还可以配置：自定义Retryer，自定义编解码器（protobuf例如），自定义日志，自定义连接池，

sb自动装配

- 复杂依赖初始化问题，比@component这种写死无操作更灵活

  - 重点是默认不支持跨模块自动装配，需要添加文件：

    - ```
      #Resources/META-INF/spring.factories
      org.springframework.boot.autoconfigure.EnableAutoConfiguration=org.first.config.OrderFeignAutoConfiguration
      ```

ErrorDecoder：远程异常解析器 5

为什么都没有注册到nacos？

- 需要添加nacos注册发现的pom依赖

- pom报错，问题类型是老调重弹的依赖顺序小问题，可以通过【先install根 -> 公共 ->父 ->api ->busi -> start...】的顺序install，来解决；

- 搞了半小时都是【no fallback ... found for feign client order】
  - 原因：忘了 把fallback改为fallbackFactory;

maven

- 重新指定仓库，清空仓库后根pom不存在问题
  - 原因：父项目 xran 没有被 install 成功，导致子模块全部失效
  - 解决：mvn -U clean install
- comm.comm-busi引用root.properties能install，prod-busi引用comm-busi就会报错properties的问题
  - 解决：根pom的`<parent>` 必须在 project 的最前面

fallback没问题，但是需要 scan所有包，这会让每个微服务的spring容器 都包含全部bean，污染了；

- 解决：

- ```
  @SpringBootApplication//记得屏蔽掉此处的不要冲突，或者只在此处添加也可也i(scanBasePackages = {"org.first.user"})
  // 精准扫描FallbackFactory所在包，不扫描其他无关包
  @ComponentScan(
          basePackages = {
                  "org.first.order.api.fallback",
                  "org.first.user"
          }, // 仅扫描Order的Fallback包
          // 若有多个服务，追加其他Fallback包："org.first.system.api.fallback"
          lazyInit = true // 可选：懒加载，减少启动资源占用
  )
  ```

- 复盘：

  - ai没说清楚的重要的一点：**Feign 代理扫描不等于 fallback Bean 自动注册** 依旧需要bean scan扫到才行；

bean问题

- No qualifying bean of type 'org.first.order.mapper.OrderMapper'
  - 解决：添加@MapperScan("org.first.order.mapper") 

- dataSource相关。。
  - 解决：添加sql相关yml配置；
- 

****

### 4. Redis

##### 4.1 缓存穿透 / 击穿 / 雪崩
原因 + 解决方案

##### 4.2 分布式锁
- setnx
- 看门狗
- Redisson 加锁/解锁流程
- 解决**幂等性**（redission并非幂等性的唯一方案）

---



### 5. 事务/隔离级别

当前进度：50%

##### 6.0 是什么

隔离级别和传播范围都是相对事务而言，不开事务无意义

- 隔离级别：解决读的问题（脏读[行内属性] / 幻读[行]）
- 传播范围：管理事务的创建 / 合并规则（包括读写）
- 原子更新/悲观/乐观锁：解决写的问题（超卖/丢失更新）

##### 6.1 要素：

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

##### 6.2 Detail记录

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



##### 6.3 传播范围（Spring控制）

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

##### 6.4 SQL原理补充

- update会自动加行锁，执行完自动释放（如果开启了事务，那么就得等commit/rollback才会释放）
- for update也会加行锁（且自动添加隐式事务），commit/rollback才释放；

##### 6.5 解决超卖/少卖（更新丢失）的三个解决方案

- 乐观锁：
  - 乐观锁的“版本判断”必须放到 update 的 WHERE 子句里，才能保证原子性和正确性 

- 悲观锁：
  - select时就添加for update（锁住行），直到commit/rollback
  - 如果执行for update之前没有开启事务，那么会隐式开启事务。反之则不会；
  - 





****

### 6. 分布式事务

- SAGA：编排法、补偿法
- Outbox（未来60day内容）
- 二段提交（学习但不实现）

---



### 7. 设计模式

- 工厂
- 策略模式（静态map、枚举、动态注册[强大&暂时不做&超出45day task范围]）
- 适配器模式
- 

---





### 8.前端

- watchEffect无需指定属性，所有属性变化都会触发回调
- watch必须指定监听的属性；
- ref套了
- reactive不套，且重新引用会失去响应式

****



（后面你学什么就往这里加，不污染主计划）