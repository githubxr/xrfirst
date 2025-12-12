

# 30 Day Plan

（极简可执行版）

### 🎯 总目标

做一个可跑、可展示、可复用的「商品 + 订单 + 库存 + 日志」业务链路，并能应对面试。

Redis 缓存 分布式锁 事务传播 复杂 SQL 隔离级别 模块化结构 必须有 1 个完整 README 面试官一看就懂

包含：
- 后端微服务结构完整
- Redis 缓存、锁
- 事务传播
- 下单 → 检查库存 → 扣库存 → 写订单 → 写日志
- README 能让面试官秒懂



##### 00.1 概述5个阶段

- 阶段0：细小查漏补缺

- 阶段1：搭建结构
- 阶段2：SQL训练
- 阶段3：事务/传播训练
- 阶段4：重头戏，电商业务逻辑实现





 阶段划分

### 🧩阶段0：查漏补缺（并行）
多线程、设计模式、前端 watch/ref 等内容，按需补。

- 原生多线程生产/消费 √
- 工具多线程生产/消费 wating for
- 线程池查漏补缺
- java队列数据类型要补充



### 🧩阶段1：微服务结构搭建
目标：所有模块能启动、互相调用，Feign 正确，基本链路跑通。


- **模块**：user / product(商品&库存) / order / payment /优惠券(前期可不做)
- **任务**：
  1. 搭建微服务项目结构（api / busi / start）
  2. Nacos 注册 + Feign 调用测试
  3. 网关最简路由 + 测试
  4. FallbackFactory 测试，统一异常处理
- **产出**：每个微服务接口可正常调用，能降级/fallback / 重试 & 统一异常 / 消息体

##### 测试记录

​	@【\core_skill_plan日志\day2 Feign记录 .md】



### 🧩阶段2：SQL训练
- 窗口函数
- 多表 join
- 聚合
- 排名/比较/环比
- SQL 练习 + Redis
- 

- processon 先画ER图
  完整：11张

  - user,role,user_role
  - product_inventory,goods,order_item,order
  - user_points,payments,coupon_templates,refunds

- 先建核心表：5张 √

- 悲观锁、乐观锁

- **Redis**：

  1. 双写/失效一致性实现
  2. 热点 key / 缓存穿透、击穿
  3. 流程图 + Markdown 流程说明

  **产出**：SQL 实例 + Redis 缓存实验完成，带可复用代码

  

**锁定库存**字段理解：

- 核心意义：库存原子性控制
  - 让库存检查可以高并发读取，而不用上行锁，避免超卖，又不用把扣减动作都串行化
  - 例如：10个库存，20个用户下单，查到的库存都是10，导致超卖；因为查和扣是独立操作，无法保证原子性；
  - 

##### 测试记录

【\core_skill_plan日志\round2测试记录 .md】







### 🧩阶段3：事务 + 隔离级别 
- 本地事务
- 事务传播
- 
- 超卖/少卖实验
- 



### 🧩阶段4：完整业务链路
- 链路1：下单
- 链路2：模拟支付（未完成）
- 业务幂等（未完成）
- 分布式锁 （未完成）
- Redis 缓存穿透/击穿/雪崩（未完成）
- 简易 SAGA（补偿版）
- 目标：整合前期成果，实现“企业级订单/库存/扣款业务”

  - **任务拆分**：
    1. 两个业务链路：
       - 下单申请 → 检查（锁定）库存  → 扣（锁定）库存 → 写订单 → 写日志
         - 要点：锁定库存更新，需要逐个就判断乐观锁，失败就全部回滚
       - 付款 申请 → 检查可否支付 → 写订单 → 释放锁定库存 → 写日志
         - 要点：
       - 待定的：积分发放
    2. 新概念
       - 锁定库存：未付款的订单占用的库存
       - 可售库存 = stock_count - lock_count
    3. 并发安全（分布式锁 + 本地锁混合）
    4. Redis 缓存与 DB 一致性验证（双写）
    5. 事务传播 + 异常回滚
    6. README + Markdown 流程说明
    7. 面试展示用示例数据 + 流程图
  - **产出**：可运行的“企业级订单/库存/扣款”微服务项目，具备核心能力演示

  





### 🎁 最终产出

- README（自解释）
- 架构图（简单）
- 完整链路截图
- 可展示 demo
- 关键 SQL 示例（截图）
- 锁/缓存/事务的面试话术模板.md











### 设计的架构

​	图示

（.\\pic\模块依赖关系可视化.png)

![模块依赖关系可视化](E:\workspace\firstProj\xran\doc\pic\模块依赖关系可视化.png)

****

##### 文字表示

###### common（公共依赖）

- common-feign（风格是，所有xx-start一般都会引入此模块）
  - 代码：feign统一异常；errorDecoder（前两者springboot自动装配暴露）；通用返回结构；
  - 依赖：openfeign/sentinel/nacos-discovery/json/spring-web
  - 结构初衷：避免web容器依赖出现在xx-api或xx-busi）
- common-core（风格是，所有xx-api一般都会引入此模块）
  - 代码：工具/切面/注解/常量定义

  - 依赖：lombok，json....
- common-busi（风格是，所有xx-busi一般都会引入此模块）
  - 代码：暂无
  - 依赖：jdbc/mybatis/redis/redssion
- （后续可能需要添加其它的common-xx之类的，如果不多，例如只有少数依赖内容就不需要）；

###### user

- user-api
  - 依赖：common-core
  - 代码：feignclient / vo(前端) / DTO(可开放的业务类) ...
- user-busi
  - 依赖：自己的user-api, common-busi
  - 代码：entity(entity不能开放到api里面开发，和do一样是私有的)/mapper/service...等业务代码
- user-start
  - 依赖：自己的user-busi,  common-feign
  - 代码：
  - remark：controller(不一定非得放start，后续看实际情况，有可能移回busi)，application启动类...yml web配置之类的...

###### product（子结构均同user）

- 包含产品，库存

###### order（子结构均同user）

- 订单相关业务

###### payment（子结构均同user）

- 支付相关业务（这个30day计划.md，只需要，模拟支付，写好相关业务代码即可，不需要真实对接）

###### gateway

- （30day计划 只需做好基本路由，已达成）

###### nacos

-  直接（ startup.cmd -m standalone），暂时不建立模块配置nacos

###### 前端vue 

- 暂时不管



##### 包划分规则：

- bo：业务
  dto：对外
  entity：单纯表



##### 坑：

- comm-web依赖了comm-core：因为CommonResponse.... 这个有时间得解决...
