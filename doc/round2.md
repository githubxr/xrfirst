



### 隔离级别

测试失败：

- InventoryServiceImpl和TransTest.abTest测试的结论是

- 因为即使你设置成 READ UNCOMMITTED，它依旧使用 MVCC，几乎无法在 MySQL 上复现真正的脏读



