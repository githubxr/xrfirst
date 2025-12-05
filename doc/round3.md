



### 隔离级别（DB控制）

测试失败：

- InventoryServiceImpl和TransTest.abTest测试的结论是

- 因为即使你设置成 READ UNCOMMITTED，它依旧使用 MVCC，几乎无法在 MySQL 上复现真正的脏读





### 传播范围（spring控制）

（父均捕获子异常不抛 ）

| 测试 | 父事务传播范围 | 子事务传播范围 | 子异常父事务是否回滚  |      |
| ---- | -------------- | -------------- | --------------------- | ---- |
| t1   | required       | required_new   | 父不回滚              |      |
| t2   | required       | required       | 回滚（rollback-only） |      |
| t3   |                |                |                       |      |
| t4   |                |                |                       |      |
|      |                |                |                       |      |
|      |                |                |                       |      |
|      |                |                |                       |      |



##### t1 required_new(独立事务，父捕获，不会连坐回滚)<img src="E:\workspace\firstProj\xran\doc\pic\t1 required_new(独立事务，父捕获，不会连坐回滚).png" alt="t1 required_new(独立事务，父捕获，不会连坐回滚)" style="zoom:33%;" />



##### t2 required(会合并事务，就算父捕获，父也会被rollback-only连坐跟着回滚)<img src="E:\workspace\firstProj\xran\doc\pic\t2 required(会合并事务，就算父捕获，父也会被rollback-only连坐跟着回滚).png" alt="t2 required(会合并事务，就算父捕获，父也会被rollback-only连坐跟着回滚)" style="zoom:33%;" />
