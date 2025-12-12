 



# 30day练手项目





****

### 1 核心实现链路

- 下单链路 √

  - 链路：order/order-busi/orderService.createOrder 

    -> product/product-busi/InventoryService.deductStock

- 付款 ing

  - 链路：






****

### 2 架构设计

##### 图示

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



****

### 3 详细笔记

##### 规划： ./doc/30day_plan.md

##### 知识笔记：./doc/knowedge.md

##### 推进日志：./doc/progress-log.md
