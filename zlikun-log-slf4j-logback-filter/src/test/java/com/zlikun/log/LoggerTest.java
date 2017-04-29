package com.zlikun.log;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 输出日志测试
 *
 * @author zlikun <zlikun-dev@hotmail.com>
 * @date 2017/4/29 12:27
 */
public class LoggerTest {

    @Test
    public void logger() {

        Logger log = LoggerFactory.getLogger(LoggerTest.class) ;

        for (int i = 0; i < 10; i++) {
            log.debug("Hello Girl - {} !" ,i);
            log.info("Hello Girl - {} !" ,i);
            log.warn("Hello Girl - {} !" ,i);
            log.error("Hello Girl - {} !" ,i);
        }

    }

}