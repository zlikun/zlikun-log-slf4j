# zlikun-log-slf4j
`slf4j`日志组件研究，包含：`logback`、`slf4j-simple`等实现。  
`slf4j`日志级别从低到高是：`TRACE` < `DEBUG` < `INFO` < `WARN` < `ERROR`，还有一个`OFF`表示关闭日志.

#### 参考资料
- <http://www.slf4j.org/manual.html>
- SLF4J在日志体系中的位置
![SLF4J在日志体系中的位置](http://www.slf4j.org/images/concrete-bindings.png)
- SLF4J Bridging legacy APIs
![SLF4J Bridging legacy APIs](http://www.slf4j.org/images/legacy.png)

#### 日志实现绑定机制
```java
// Logger工程(Logger实例生成入口)
// org.slf4j.LoggerFactory#getLogger()
// 下面是其关键实现代码
public static ILoggerFactory getILoggerFactory() {
    if (INITIALIZATION_STATE == UNINITIALIZED) {
        synchronized (LoggerFactory.class) {
            if (INITIALIZATION_STATE == UNINITIALIZED) {
                INITIALIZATION_STATE = ONGOING_INITIALIZATION;
                performInitialization();
            }
        }
    }
    switch (INITIALIZATION_STATE) {
    case SUCCESSFUL_INITIALIZATION:
        return StaticLoggerBinder.getSingleton().getLoggerFactory();
    case NOP_FALLBACK_INITIALIZATION:
        return NOP_FALLBACK_FACTORY;
    case FAILED_INITIALIZATION:
        throw new IllegalStateException(UNSUCCESSFUL_INIT_MSG);
    case ONGOING_INITIALIZATION:
        // support re-entrant behavior.
        // See also http://jira.qos.ch/browse/SLF4J-97
        return SUBST_FACTORY;
    }
    throw new IllegalStateException("Unreachable code");
}

// 实际Logger生成代码
// StaticLoggerBinder.getSingleton().getLoggerFactory();
// 在slf4j-simple.jar下找到该类(一个工程中，该类只能存在一个，所以slf4j-simple与logback不能共存)
private StaticLoggerBinder() {
    loggerFactory = new SimpleLoggerFactory();
}
```

#### 工程清单
- zlikun-log-slf4j-simple `slf4j-api`简易实现，通常用作测试时使用
