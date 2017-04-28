package com.zlikun.log;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @auther zlikun <zlikun-dev@hotmail.com>
 * @date 2017/4/28 11:38
 */
public class ConsoleAppenderTest {

    @Test
    public void append() {

        Logger log = LoggerFactory.getLogger(ConsoleAppenderTest.class) ;

        log.info("输出日志到控制台");

    }

}
