package com.zlikun.log;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.util.StatusPrinter;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @auther zlikun <zlikun-dev@hotmail.com>
 * @date 2017/4/25 18:45
 */
public class UsageTest {

    @Test
    public void logger() {

        Logger log = LoggerFactory.getLogger(UsageTest.class) ;

        // 2017/04/25 20:07:23 | main | INFO | com.zlikun.log.UsageTest | print log : info
        log.info("print log : info");

    }

    @Test
    public void loggerContext() {

        Logger log = LoggerFactory.getLogger(UsageTest.class) ;
        log.info("打印LoggerContext状态信息!");

        // 打印Logback内部状态信息
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        StatusPrinter.print(lc);

        // 测试Logger的缓存机制(证明exists返回的Logger与前面代码声明的Logger为同一个对象)
        Assert.assertEquals(log ,lc.exists(UsageTest.class.getName()));
        // Logger名称大小写敏感
        Assert.assertNotEquals(LoggerFactory.getLogger("zlikun") ,LoggerFactory.getLogger("ZLIKUN"));

    }

}
