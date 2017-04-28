# zlikun-log-slf4j-logback-appender

研究Logback的Appender机制

#### Appender类图
- ConsoleAppender  
![ConsoleAppender](http://img0.ph.126.net/u30ejhckk69U63Cqoy1FfQ==/6632058425678611647.png)
- RollingFileAppender  
![RollingFileAppender](http://img1.ph.126.net/DYEGd7CjQ_3CLlWYlTtqkA==/6632036435446056436.png)

#### Appender配置
- ConsoleAppender  
```xml
<appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
    <!-- 指定输出流，默认：System.out，这里修改为：System.err，允许取值参考：ch.qos.logback.core.joran.spi.ConsoleTarget -->
    <target>System.err</target>
    <!-- 默认Encoder：ch.qos.logback.classic.encoder.PatternLayoutEncoder -->
    <encoder>
        <!-- 测试发现：未在Action中解析的属性，应在子标签中设定才能生效 -->
        <charset>UTF-8</charset>
        <!-- ch.qos.logback.classic.PatternLayout -->
        <pattern>%d{yyyy/MM/dd HH:mm:ss} | %contextName | %t | %level | %logger | %msg%n</pattern>
    </encoder>
</appender>
```
- FileAppender  
```xml

```

#### Appender使用
```

```