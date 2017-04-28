package com.zlikun.log;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @auther zlikun <zlikun-dev@hotmail.com>
 * @date 2017/4/28 18:53
 */
public class CountingConsoleAppenderTest {

    @Test
    public void append() {
        Logger log = LoggerFactory.getLogger(CountingConsoleAppenderTest.class) ;
        for (int i = 0; i < 100; i++) {
            log.info("输出日志到控制台 - {}" ,i);
        }
    }

}
