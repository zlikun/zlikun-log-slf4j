package com.zlikun.log;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 测试日志输出
 *
 * @author zlikun <zlikun-dev@hotmail.com>
 * @date 2017/4/29 9:44
 */
public class LoggerTest {

    @Test
    public void logger() {

        Logger log = LoggerFactory.getLogger(LoggerTest.class) ;
        log.info("Hello {} ~" ,"girl");
        log.warn("Hello {} ~" ,"girl");
        log.error("Hello {} ~" ,"girl");

    }

}