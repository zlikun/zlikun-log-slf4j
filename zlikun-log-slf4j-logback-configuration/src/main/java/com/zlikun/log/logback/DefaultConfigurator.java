package com.zlikun.log.logback;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.layout.TTLLLayout;
import ch.qos.logback.classic.spi.Configurator;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.encoder.LayoutWrappingEncoder;
import ch.qos.logback.core.spi.ContextAwareBase;

/**
 * 仅供测试使用
 * @see ch.qos.logback.classic.BasicConfigurator
 * @auther zlikun <zlikun-dev@hotmail.com>
 * @date 2017/4/27 17:16
 */
public class DefaultConfigurator extends ContextAwareBase implements Configurator {

    @Override
    public void configure(LoggerContext lc) {

        addInfo("使用SPI机制加载配置类实现日志配置");

        lc.setName("");

        // ConsoleAppender 日志将输出到控制台
        ConsoleAppender<ILoggingEvent> ca = new ConsoleAppender<ILoggingEvent>();
        ca.setContext(lc);
        ca.setName("console");
        LayoutWrappingEncoder<ILoggingEvent> encoder = new LayoutWrappingEncoder<ILoggingEvent>();
        encoder.setContext(lc);

        // 使用PatternLayout配置日志
        PatternLayout layout = new PatternLayout();
        layout.setPattern("%d{HH:mm:ss} | %contextName | %t | %level | %logger | %msg%n");
        layout.setContext(lc);
        layout.start();
        encoder.setLayout(layout);

        ca.setEncoder(encoder);
        ca.start();

        // 添加根Appender
        Logger rootLogger = lc.getLogger(Logger.ROOT_LOGGER_NAME);
        rootLogger.addAppender(ca);

        // 添加自定义Appender
        Logger logger = lc.getLogger("com.zlikun.log") ;
        logger.setLevel(Level.DEBUG);
        logger.setAdditive(false);
        logger.addAppender(ca);

    }

}
