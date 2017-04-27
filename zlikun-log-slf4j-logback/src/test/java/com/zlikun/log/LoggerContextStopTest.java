package com.zlikun.log;

import ch.qos.logback.classic.LoggerContext;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @auther zlikun <zlikun-dev@hotmail.com>
 * @date 2017/4/27 14:32
 */
public class LoggerContextStopTest {

    @Test
    public void stop() {

        Logger log = LoggerFactory.getLogger(LoggerContextStopTest.class) ;
        // 2017/04/27 14:34:01 | main | INFO | com.zlikun.log.LoggerContextStopTest | LoggerContext STOP ...
        log.info("LoggerContext STOP ...");

        // https://logback.qos.ch/manual/configuration.html#stopContext
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        loggerContext.stop();

        // 测试本句无输出(当LoggerContext停止后，将不能再输出日志)
        log.info("LoggerContext STOP !!!");

    }

}
