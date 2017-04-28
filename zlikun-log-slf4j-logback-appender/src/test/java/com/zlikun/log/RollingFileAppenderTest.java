package com.zlikun.log;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @auther zlikun <zlikun-dev@hotmail.com>
 * @date 2017/4/28 13:45
 */
public class RollingFileAppenderTest {

    @Test
    public void append() {
        Logger log = LoggerFactory.getLogger(RollingFileAppenderTest.class) ;
        log.info("输出日志到文件(滚动)");

//        for (int i = 0; i < 10000; i++) {
//            log.info("输出日志到文件(滚动) - {}" ,i + 1);
//        }

    }

}
