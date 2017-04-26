#### 参考资料
- <https://logback.qos.ch/manual/introduction.html>
- <https://logback.qos.ch/manual/architecture.html>

#### 模块说明
- logback-core Logback核心实现
- logback-classic Logback与Slf4j对接，是其完整实现
- logback-access Logback与Servlet容器集成提供通过Http来访问日志的功能

#### 架构说明
Logback主要由Logger、Appenders、Layouts三部分组成。所有的Logger共享一个LoggerContext实例，且Logger名称大小写敏感。Logger父级与子级存在继承关系，子级Logger会继承父级的配置(通过`.`或`$`分隔)，未指定级别的Logger将使用其最近父级日志级别，根为root。

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

// getSingleton() 是Slf4j的硬性要求，必须遵守(如果需要自定义实现)
public static StaticLoggerBinder getSingleton() {
    return SINGLETON;
}

// ch.qos.logback.classic.LoggerContext 构造方法(观察默认实例的属性)
public LoggerContext() {
    // ch.qos.logback.core.ContextBase 填充 objectMap
    super();
    // 使用ConcurrentHashMap缓存Logger实例，避免Logger实例过多
    this.loggerCache = new ConcurrentHashMap<String, Logger>();

    this.loggerContextRemoteView = new LoggerContextVO(this);
    // Logger.ROOT_LOGGER_NAME = "ROOT"，初始化根Logger
    this.root = new Logger(Logger.ROOT_LOGGER_NAME, null, this);
    // 设置根Logger默认日志级别为DEBUG级别
    this.root.setLevel(Level.DEBUG);
    // 缓存根Logger
    loggerCache.put(Logger.ROOT_LOGGER_NAME, root);
    initEvaluatorMap();
    // 初始化Logger计数器
    size = 1;
    this.frameworkPackages = new ArrayList<String>();
}
```