



### 1 隔离级别（DB控制）

测试失败：

- InventoryServiceImpl和TransTest.abTest测试的结论是

- 因为即使你设置成 READ UNCOMMITTED，它依旧使用 MVCC，几乎无法在 MySQL 上复现真正的脏读





### 2 传播范围（spring控制）

（父均捕获子异常不抛 ）

| 测试 | 父事务传播范围 | 子事务传播范围 | 子异常父事务是否回滚  | 解释                                            |
| ---- | -------------- | -------------- | --------------------- | ----------------------------------------------- |
| t1   | required       | required_new   | 父不回滚              | 父事务暂停<br />子的独立事务失败，父继续        |
| t2   | required       | required       | 回滚（rollback-only） | 父子事务合并<br />父catch不抛也会 rollback-only |
| t3   | required       | nested         | 父不回滚              | 父事务暂停savePoint<br />子失败父还原point      |
| t4   |                |                |                       |                                                 |
|      |                |                |                       |                                                 |
|      |                |                |                       |                                                 |
|      |                |                |                       |                                                 |



##### 案例T1：子required_new

​	(独立事务，父捕获，不会连坐回滚)<img src="E:\workspace\firstProj\xran\doc\pic\t1 required_new(独立事务，父捕获，不会连坐回滚).png" alt="t1 required_new(独立事务，父捕获，不会连坐回滚)" style="zoom: 50%;" />



##### 案例T2：子required

​	(会合并事务，就算父捕获，父也会被rollback-only连坐跟着回滚)<img src="E:\workspace\firstProj\xran\doc\pic\t2 required(会合并事务，就算父捕获，父也会被rollback-only连坐跟着回滚).png" alt="t2 required(会合并事务，就算父捕获，父也会被rollback-only连坐跟着回滚)" style="zoom: 50%;" />



##### 案例T3：子nested 

​	在父事务上savePoint，子失败了也不影响父从point恢复）

<img src="E:\workspace\firstProj\xran\doc\pic\t3 nested（在父事务上savePoint，子失败了也不影响父从point恢复）.png" alt="t3 nested（在父事务上savePoint，子失败了也不影响父从point恢复）" style="zoom:50%;" />

### 



### 3 超卖/丢失更新专题

##### 案例Day3 d1：丢失更新测试 

###### 重要：只要是先查后更新（根据查到的基础上更新）的方式，更大概率是’丢失更新‘而不是’超卖‘）

<img src="E:\workspace\firstProj\xran\doc\pic\day3丢失更新测试.png" alt="day3丢失更新测试" style="zoom:50%;" />



##### 案例Day3 d2：超卖测试（原子递减版）

<img src="E:\workspace\firstProj\xran\doc\pic\day3 d2超卖测试（原子递减）.png" alt="day3 d2超卖测试（原子递减）" style="zoom:50%;" />



##### 案例Day3 d4：本地锁串行化避免超卖测试<img src="E:\workspace\firstProj\xran\doc\pic\day3 d4本地锁串行化避免超卖测试.png" alt="day3 d4本地锁串行化避免超卖测试" style="zoom:50%;" />





### 4 悲观乐观锁

超卖/丢失更新 的【解决方案】

```
这些超卖，丢失更新的测试，如果加上各种不同@transaction设置，其实也无效,
因为传播范围是事务之间的 影响，隔离级别则是对于【读】的控制，超卖少卖，是【写】的控制
```

超卖/丢失更新的基础上，引入这两种锁来解决；

（同时，原子更新也是可以解决的【update... set ammount=ammount-${num} where ammount=${num}】）

##### 4.1 乐观锁案例

###### part1

<img src="E:\workspace\firstProj\xran\doc\pic\day4 乐观锁案例-part1.png" alt="day4 乐观锁案例-part1" style="zoom:50%;" />

###### part2![day4 乐观锁案例-part2](E:\workspace\firstProj\xran\doc\pic\day4 乐观锁案例-part2.png)



