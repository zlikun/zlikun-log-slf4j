# zlikun-log-slf4j-logback-appender

研究Logback的Appender机制

#### Appender类图
![官方类图](https://logback.qos.ch/manual/images/chapters/appenders/appenderClassDiagram.jpg)
- ConsoleAppender  
![ConsoleAppender](http://img0.ph.126.net/u30ejhckk69U63Cqoy1FfQ==/6632058425678611647.png)
- RollingFileAppender  
![RollingFileAppender](http://img1.ph.126.net/DYEGd7CjQ_3CLlWYlTtqkA==/6632036435446056436.png)

#### Appender配置
- [ConsoleAppender](https://logback.qos.ch/manual/appenders.html#ConsoleAppender)  
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
- [FileAppender](https://logback.qos.ch/manual/appenders.html#FileAppender)  
```xml
<appender name="FILE" class="ch.qos.logback.core.FileAppender">
    <!-- 设置日志文件路径(只能设置为本地文件路径) -->
    <!-- 绝对路径 -->
    <!-- <file>/data/simple/server.log</file> -->
    <!-- 相对路径，相对于当前工程根目录 -->
    <file>build/data/server.log</file>
    <!-- 设置IO缓冲区，取值单位参考：ch.qos.logback.core.util.FileSize，该设置默认值：8kb -->
    <bufferSize>4 kb</bufferSize>
    <!-- 设置文件写入方式是否追加，默认：true，false表示每次都清空上次写入 -->
    <append>false</append>
    <!-- 于#start()方法中使用，用于强制append = true，并输出警告，默认：false(不启用) -->
    <prudent>true</prudent>
    <!-- 设置是否立即刷新，默认：true -->
    <immediateFlush>false</immediateFlush>
    <encoder>
        <charset>UTF-8</charset>
        <pattern>%d{yyyy/MM/dd HH:mm:ss} | %contextName | %t | %level | %logger | %msg%n</pattern>
    </encoder>
</appender>
```
- [RollingFileAppender](https://logback.qos.ch/manual/appenders.html#RollingFileAppender)  
输出到文件Appender配置时，需要注意：多个Appender不能指定重复文件
1. [TimeBasedRollingPolicy](https://logback.qos.ch/manual/appenders.html#TimeBasedRollingPolicy)
```xml
<appender name="ROLLING_FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
    <!-- 下面几项设置继承自FileAppender -->
    <file>build/data/server.rolling.log</file>
    <bufferSize>32 kb</bufferSize>
    <!-- 配置滚动策略：https://logback.qos.ch/manual/appenders.html#TimeBasedRollingPolicy -->
    <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
        <!-- 每天滚动一次，通过指定.zip/.gz压缩日志文件 -->
        <fileNamePattern>build/data/server.%d{yyyyMMdd}.log.zip</fileNamePattern>
        <!-- 最大保留30天，总日志量不能超过3G，单个日志文件不能超过100MB -->
        <maxHistory>30</maxHistory>
        <maxFileSize>100MB</maxFileSize>
        <totalSizeCap>3GB</totalSizeCap>
    </rollingPolicy>

    <encoder>
        <charset>UTF-8</charset>
        <pattern>%d{yyyy/MM/dd HH:mm:ss} | %contextName | %t | %level | %logger | %msg%n</pattern>
    </encoder>
</appender>
```
2. [FixedWindowRollingPolicy](https://logback.qos.ch/manual/appenders.html#FixedWindowRollingPolicy)
```xml
<appender name="ROLLING_FILE2" class="ch.qos.logback.core.rolling.RollingFileAppender">
    <file>build/data/server-${byDate}.log</file>
    <bufferSize>32 kb</bufferSize>

    <!-- 根据固定索引计数滚动 -->
    <rollingPolicy class="ch.qos.logback.core.rolling.FixedWindowRollingPolicy">
        <fileNamePattern>build/data/server.%i.log.zip</fileNamePattern>
        <minIndex>1</minIndex>
        <maxIndex>9</maxIndex>
    </rollingPolicy>

    <!-- 配合FixedWindowRollingPolicy滚动策略，每5MB滚动一次日志 -->
    <triggeringPolicy class="ch.qos.logback.core.rolling.SizeBasedTriggeringPolicy">
        <maxFileSize>4KB</maxFileSize>
    </triggeringPolicy>

    <encoder>
        <charset>UTF-8</charset>
        <pattern>%d{yyyy/MM/dd HH:mm:ss} | %contextName | %t | %level | %logger | %msg%n</pattern>
    </encoder>
</appender>
```
- [DBAppender](https://logback.qos.ch/manual/appenders.html#DBAppender)
```xml
<appender name="DB" class="ch.qos.logback.classic.db.DBAppender">
    <!-- 配套SQL脚本文件于下面目录中存放 -->
    <!-- logback/logback-classic/src/main/resources/ch/qos/logback/classic/db/script -->
    <!--
    <connectionSource class="ch.qos.logback.core.db.DriverManagerConnectionSource">
        <driverClass>com.mysql.jdbc.Driver</driverClass>
        <url>jdbc:mysql://192.168.9.223:3306/test</url>
        <user>root</user>
        <password>ablejava</password>
    </connectionSource>
    -->
    <!-- 使用连接池 -->
    <connectionSource class="ch.qos.logback.core.db.DataSourceConnectionSource">
        <dataSource class="com.mchange.v2.c3p0.ComboPooledDataSource">
            <driverClass>com.mysql.jdbc.Driver</driverClass>
            <jdbcUrl>jdbc:mysql://192.168.9.223:3306/test</jdbcUrl>
            <user>root</user>
            <password>ablejava</password>
            <initialPoolSize>10</initialPoolSize>
            <minPoolSize>10</minPoolSize>
            <maxPoolSize>30</maxPoolSize>
            <maxIdleTime>30</maxIdleTime>
        </dataSource>
    </connectionSource>
</appender>
```
- [SocketAppender](https://logback.qos.ch/manual/appenders.html#SocketAppender)
- [SMTPAppender](https://logback.qos.ch/manual/appenders.html#SMTPAppender)
- [SyslogAppender](https://logback.qos.ch/manual/appenders.html#SyslogAppender)
- [SiftingAppender](https://logback.qos.ch/manual/appenders.html#SiftingAppender)
- [AsyncAppender](https://logback.qos.ch/manual/appenders.html#AsyncAppender)
- [WriteYourOwnAppender](https://logback.qos.ch/manual/appenders.html#WriteYourOwnAppender)

#### Appender使用
```

```