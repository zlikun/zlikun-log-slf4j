# zlikun-log-slf4j
`slf4j`日志组件研究，包含：`logback`、`slf4j-simple`等实现。  
`slf4j`日志级别从低到高是：`TRACE` < `DEBUG` < `INFO` < `WARN` < `ERROR`，还有一个`OFF`表示关闭日志.

#### 参考资料
- <http://www.slf4j.org/manual.html>
- SLF4J在日志体系中的位置
![SLF4J在日志体系中的位置](http://www.slf4j.org/images/concrete-bindings.png)
- SLF4J Bridging legacy APIs
![SLF4J Bridging legacy APIs](http://www.slf4j.org/images/legacy.png)

#### 工程清单
- zlikun-log-slf4j-simple `slf4j-api`简易实现，通常用作测试时使用
