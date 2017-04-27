#### 资料
- <https://logback.qos.ch/manual/introduction.html>
- <https://logback.qos.ch/manual/architecture.html>
- <https://logback.qos.ch/manual/configuration.html>

#### 模块
- logback-core Logback核心实现
- logback-classic Logback与Slf4j对接，是其完整实现
- logback-access Logback与Servlet容器集成提供通过Http来访问日志的功能

#### 架构
Logback主要由Logger、Appenders、Layouts三部分组成。所有的Logger共享一个LoggerContext实例，且Logger名称大小写敏感。Logger父级与子级存在继承关系，子级Logger会继承父级的配置(通过`.`或`$`分隔)，未指定级别的Logger将使用其最近父级日志级别，根为root。配置文件中的Logger将在程序启动时即初始化，其日志级别如不指定也将使用父级Logger级别。  
日志的输出目标被称作`appender`，Currently, appenders exist for the console, files, remote socket servers, to MySQL, PostgreSQL, Oracle and other databases, JMS, and remote UNIX Syslog daemons。同一个Logger可以被指定多个Appender。Appender通过`Additivity`属性控制Logger输出目标是否叠加，如为false，表示仅向当前Appender输出。  
![Under The Hood Sequence Diagram](https://logback.qos.ch/manual/images/chapters/architecture/underTheHoodSequence2.gif)

#### 配置
- <https://logback.qos.ch/manual/configuration.html>
- <https://segmentfault.com/a/1190000008315137>

Logback配置文件加载顺序
1. Logback tries to find a file called logback-test.xml in the classpath
2. If no such file is found, logback tries to find a file called logback.groovy in the classpath
3. If no such file is found, it checks for the file logback.xml in the classpath
4. 如果上述的文件都找不到，则 logback 会使用 JDK 的 SPI 机制查找 META-INF/services/ch.qos.logback.classic.spi.Configurator 中的 logback 配置实现类，这个实现类必须实现 Configuration 接口，使用它的实现来进行配置
5. 如果以上都不成功，使用默认配置：ch.qos.logback.classic.BasicConfigurator，内部使用`ConsoleAppender`和`LayoutWrappingEncoder`、`TTLLLayout`配置日志。默认日志输出格式："%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"

- 配置加载流程
```
// logback-classic-1.2.3.jar / org.slf4j.impl.StaticLoggerBinder
// 初始化日志配置语句：new ContextInitializer(defaultLoggerContext).autoConfig();
// ch.qos.logback.classic.util.ContextInitializer#autoConfig()
public void autoConfig() throws JoranException {
    StatusListenerConfigHelper.installIfAsked(loggerContext);
    // 加载配置文件URL，默认加载顺序：logback-test.xml、logback.groovy、logback.xml
    URL url = findURLOfDefaultConfigurationFile(true);
    if (url != null) {
        // 根据URL配置Logger
        configureByResource(url);
    } else {
        Configurator c = EnvUtil.loadFromServiceLoader(Configurator.class);
        if (c != null) {
            try {
                c.setContext(loggerContext);
                c.configure(loggerContext);
            } catch (Exception e) {
                throw new LogbackException(String.format("Failed to initialize Configurator: %s using ServiceLoader", c != null ? c.getClass()
                                .getCanonicalName() : "null"), e);
            }
        } else {
            BasicConfigurator basicConfigurator = new BasicConfigurator();
            basicConfigurator.setContext(loggerContext);
            basicConfigurator.configure(loggerContext);
        }
    }
}

...

public URL findURLOfDefaultConfigurationFile(boolean updateStatus) {
    ClassLoader myClassLoader = Loader.getClassLoaderOfObject(this);
    URL url = findConfigFileURLFromSystemProperties(myClassLoader, updateStatus);
    if (url != null) {
        return url;
    }
    // TEST_AUTOCONFIG_FILE = "logback-test.xml"
    url = getResource(TEST_AUTOCONFIG_FILE, myClassLoader, updateStatus);
    if (url != null) {
        return url;
    }
    // GROOVY_AUTOCONFIG_FILE = "logback.groovy"
    url = getResource(GROOVY_AUTOCONFIG_FILE, myClassLoader, updateStatus);
    if (url != null) {
        return url;
    }
    // AUTOCONFIG_FILE = "logback.xml"
    return getResource(AUTOCONFIG_FILE, myClassLoader, updateStatus);
}

...

public void configureByResource(URL url) throws JoranException {
    if (url == null) {
        throw new IllegalArgumentException("URL argument cannot be null");
    }
    final String urlString = url.toString();
    if (urlString.endsWith("groovy")) {
        if (EnvUtil.isGroovyAvailable()) {
            // avoid directly referring to GafferConfigurator so as to avoid
            // loading groovy.lang.GroovyObject . See also http://jira.qos.ch/browse/LBCLASSIC-214
            GafferUtil.runGafferConfiguratorOn(loggerContext, this, url);
        } else {
            StatusManager sm = loggerContext.getStatusManager();
            sm.add(new ErrorStatus("Groovy classes are not available on the class path. ABORTING INITIALIZATION.", loggerContext));
        }
    } else if (urlString.endsWith("xml")) {
        // Joran 是 logback 使用的一个配置加载库
        JoranConfigurator configurator = new JoranConfigurator();
        configurator.setContext(loggerContext);
        configurator.doConfigure(url);
    } else {
        throw new LogbackException("Unexpected filename extension of file [" + url.toString() + "]. Should be either .groovy or .xml");
    }
}
```
- 核心配置加载类
```
// ch.qos.logback.classic.joran.GenericConfigurator
public final void doConfigure(InputStream inputStream, String systemId) throws JoranException {
    InputSource inputSource = new InputSource(inputStream);
    inputSource.setSystemId(systemId);
    doConfigure(inputSource);
}

...

public final void doConfigure(final InputSource inputSource) throws JoranException {

    long threshold = System.currentTimeMillis();
    // if (!ConfigurationWatchListUtil.wasConfigurationWatchListReset(context)) {
    // informContextOfURLUsedForConfiguration(getContext(), null);
    // }
    SaxEventRecorder recorder = new SaxEventRecorder(context);
    recorder.recordEvents(inputSource);
    doConfigure(recorder.saxEventList);
    // no exceptions a this level
    StatusUtil statusUtil = new StatusUtil(context);
    if (statusUtil.noXMLParsingErrorsOccurred(threshold)) {
        addInfo("Registering current configuration as safe fallback point");
        registerSafeConfiguration(recorder.saxEventList);
    }
}

public void doConfigure(final List<SaxEvent> eventList) throws JoranException {
    buildInterpreter();
    // disallow simultaneous configurations of the same context
    synchronized (context.getConfigurationLock()) {
        interpreter.getEventPlayer().play(eventList);
    }
}

...

protected void buildInterpreter() {
    RuleStore rs = new SimpleRuleStore(context);
    // 由ch.qos.logback.classic.joran.JoranConfigurator类提供具体实现
    addInstanceRules(rs);
    this.interpreter = new Interpreter(context, rs, initialElementPath());
    InterpretationContext interpretationContext = interpreter.getInterpretationContext();
    interpretationContext.setContext(context);
    addImplicitRules(interpreter);
    addDefaultNestedComponentRegistryRules(interpretationContext.getDefaultNestedComponentRegistry());
}
    
// ch.qos.logback.classic.joran.JoranConfigurator
// rs.addRule()第二个参数为配置对应的具体类，包含配置的属性等
public void addInstanceRules(RuleStore rs) {
    // parent rules already added
    super.addInstanceRules(rs);

    rs.addRule(new ElementSelector("configuration"), new ConfigurationAction());

    rs.addRule(new ElementSelector("configuration/contextName"), new ContextNameAction());
    rs.addRule(new ElementSelector("configuration/contextListener"), new LoggerContextListenerAction());
    rs.addRule(new ElementSelector("configuration/insertFromJNDI"), new InsertFromJNDIAction());
    rs.addRule(new ElementSelector("configuration/evaluator"), new EvaluatorAction());

    rs.addRule(new ElementSelector("configuration/appender/sift"), new SiftAction());
    rs.addRule(new ElementSelector("configuration/appender/sift/*"), new NOPAction());

    rs.addRule(new ElementSelector("configuration/logger"), new LoggerAction());
    rs.addRule(new ElementSelector("configuration/logger/level"), new LevelAction());

    rs.addRule(new ElementSelector("configuration/root"), new RootLoggerAction());
    rs.addRule(new ElementSelector("configuration/root/level"), new LevelAction());
    rs.addRule(new ElementSelector("configuration/logger/appender-ref"), new AppenderRefAction<ILoggingEvent>());
    rs.addRule(new ElementSelector("configuration/root/appender-ref"), new AppenderRefAction<ILoggingEvent>());

    // add if-then-else support
    rs.addRule(new ElementSelector("*/if"), new IfAction());
    rs.addRule(new ElementSelector("*/if/then"), new ThenAction());
    rs.addRule(new ElementSelector("*/if/then/*"), new NOPAction());
    rs.addRule(new ElementSelector("*/if/else"), new ElseAction());
    rs.addRule(new ElementSelector("*/if/else/*"), new NOPAction());

    // add jmxConfigurator only if we have JMX available.
    // If running under JDK 1.4 (retrotranslateed logback) then we
    // might not have JMX.
    if (PlatformInfo.hasJMXObjectName()) {
        rs.addRule(new ElementSelector("configuration/jmxConfigurator"), new JMXConfiguratorAction());
    }
    rs.addRule(new ElementSelector("configuration/include"), new IncludeAction());

    rs.addRule(new ElementSelector("configuration/consolePlugin"), new ConsolePluginAction());

    rs.addRule(new ElementSelector("configuration/receiver"), new ReceiverAction());

}
```

#### Logger初始化  
```
// ch.qos.logback.classic.LoggerContext (org.slf4j.ILoggerFactory 在Logback中的实现类)
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