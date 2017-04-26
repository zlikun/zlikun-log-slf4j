#### 参考资料
- <https://logback.qos.ch/manual/introduction.html>
- <https://logback.qos.ch/manual/architecture.html>

#### 源码解析
- ch.qos.logback.classic.LoggerContext (org.slf4j.ILoggerFactory 在Logback中的实现类)
```
// Logback StaticLoggerBinder 实现位于 classic 模块下
// logback-classic-1.2.3.jar / org.slf4j.impl.StaticLoggerBinder

// org.slf4j.impl.StaticLoggerBinder 初始化 ContextSelector 代码
static {
    SINGLETON.init();
}

...

void init() {
    ...
    contextSelectorBinder.init(defaultLoggerContext, KEY);
    ...
}

// ch.qos.logback.classic.util.ContextSelectorStaticBinder
// 通过指定运行参数："-Dlogback.ContextSelector=JNDI"或者自定义ContextSelector类可以选择JNDI或自定义实现
public void init(LoggerContext defaultLoggerContext, Object key) throws ClassNotFoundException, NoSuchMethodException, InstantiationException,
                IllegalAccessException, InvocationTargetException {
    if (this.key == null) {
        this.key = key;
    } else if (this.key != key) {
        throw new IllegalAccessException("Only certain classes can access this method.");
    }

    String contextSelectorStr = OptionHelper.getSystemProperty(ClassicConstants.LOGBACK_CONTEXT_SELECTOR);
    if (contextSelectorStr == null) {
        contextSelector = new DefaultContextSelector(defaultLoggerContext);
    } else if (contextSelectorStr.equals("JNDI")) {
        // if jndi is specified, let's use the appropriate class
        contextSelector = new ContextJNDISelector(defaultLoggerContext);
    } else {
        contextSelector = dynamicalContextSelector(defaultLoggerContext, contextSelectorStr);
    }
}

// 默认情况下，将执行如下分支代码
if (contextSelectorStr == null) {
    contextSelector = new DefaultContextSelector(defaultLoggerContext);
}

// 最终返回的LoggerContext即为默认实例
// org.slf4j.impl.StaticLoggerBinder
public ILoggerFactory getLoggerFactory() {
    ...
    return contextSelectorBinder.getContextSelector().getLoggerContext();
}
```