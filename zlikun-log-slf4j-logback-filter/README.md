# zlikun-log-slf4j-logback-filter

#### 资料
- <https://logback.qos.ch/manual/filters.html>
- <https://logback.qos.ch/manual/filters.html#yourOwnFilter>
- <https://logback.qos.ch/manual/filters.html#levelFilter>
- <https://logback.qos.ch/manual/filters.html#thresholdFilter>
- <https://logback.qos.ch/manual/filters.html#evalutatorFilter>
- <https://logback.qos.ch/manual/filters.html#matcher>
- <https://logback.qos.ch/manual/filters.html#TurboFilter>
- <https://logback.qos.ch/manual/filters.html#DuplicateMessageFilter>

> `Filter`配置于`Appender`中，返回`ch.qos.logback.core.spi.FilterReply`实例，返回`DENY`、`NEUTRAL`、`ACCEPT`三个枚举值中的一个。
- DENY，忽略剩下的过滤器，直接返回DENY，日志不会被输出
- NEUTRAL，继续执行剩下的过滤器，由后续过滤器决定是否输出日志
- ACCEPT，跳过剩下过滤器，直接返回ACCEPT，日志会被输出

#### 示例
```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration debug="true" scan="true" scanPeriod="3 seconds">

	<contextName>zlikun-log</contextName>

	<!-- https://logback.qos.ch/manual/filters.html#DuplicateMessageFilter -->
	<!-- 检查重复消息(模板部分)，只输出一次 -->
	<turboFilter class="ch.qos.logback.classic.turbo.DuplicateMessageFilter"/>

	<appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
		<!-- 配置过滤器 -->
		<filter class="com.zlikun.log.logback.OddFilter" />
		<!-- https://logback.qos.ch/manual/filters.html#thresholdFilter -->
		<!-- 设定一个阈值，小于该级别的日志都将返回DENY，大于等于该级别的日志都将返回NEUTRAL -->
		<filter class="ch.qos.logback.classic.filter.ThresholdFilter">
			<level>INFO</level>
		</filter>
		<!-- https://logback.qos.ch/manual/filters.html#levelFilter -->
		<!-- 判断日志与指定日志级别是否匹配，并指定匹配或不匹配对应的指令 -->
		<filter class="ch.qos.logback.classic.filter.LevelFilter">
			<!--指定日志级别为：INFO-->
			<level>WARN</level>
			<!--匹配时同意输出-->
			<onMatch>ACCEPT</onMatch>
			<!--不匹配时拒绝输出-->
			<onMismatch>DENY</onMismatch>
		</filter>

		<!-- 配置其它属性 -->
		<target>System.err</target>
		<encoder>
			<charset>UTF-8</charset>
			<pattern>%d{yyyy/MM/dd HH:mm:ss} | %contextName | %t | %level | %logger | %msg%n</pattern>
		</encoder>
	</appender>

	<root level="INFO">
		<appender-ref ref="STDOUT" />
	</root>

	<logger name="com.zlikun.log" level="DEBUG" additivity="true" />

	<shutdownHook class="ch.qos.logback.core.hook.DelayingShutdownHook" />

</configuration>
```