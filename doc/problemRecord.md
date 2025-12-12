# 问题记录





### 项目结构相关问题

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

****





### DB相关

 2.7 常见问题记录

- MySQL8 启动乱码（bin.index） ⇒ Win UTF8 Beta 模式

  - bin.index' not found (OS errno 2 - No such file or directory)】这种乱码，说明是mysql8的更高字符集要求导致的
  - 解决：修改windows的语言 区域中，勾选【Beta版：使用Unicode UTF-8提供全球语言支持】

- 大量测试记录

- round2 测试日志（外部引用 \core_skill_plan日志\round2测试记录 .md）

- 联表情况下 select * vs select o.*？

  - select o.*性能更优, 直接*是取所有表字段，容易冲突，不推荐

- 不要关闭ONLY_FULL_GROUP_BY ，
  因为违反该规则的 SQL **一定会产生数据错乱（非聚合列随机值）**

  - 解释：

    ```
    select u.real_name,
    	count(distinct oi.goods_code) prac_count
    	#,oi.goods_code这里这个就违背了，
    	#因为gorupBy u.user_code 合并后每个用户只有一行数据，
    	#而每个用户所有订单加起来有多条商品信息，这样会导致每次查出的
    	#goods_code都不一致，都是随机的
    	from xr_user u left join xr_order o on u.user_code=o.user_code
    	join xr_order_item oi on o.order_code=oi.order_code
    	#所以，这里也不需要加 join xr_goods g on oi.goods_code=g.goods_code
    	group by u.user_code
    ```

    

****



