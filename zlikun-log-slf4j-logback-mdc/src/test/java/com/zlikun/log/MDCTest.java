package com.zlikun.log;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

/**
 * MDC机制测试
 *
 * @author zlikun <zlikun-dev@hotmail.com>
 * @date 2017/4/29 20:03
 */
public class MDCTest {

    @Test
    public void logger() {

        MDC.put("name" ,"zlikun");

        Logger log = LoggerFactory.getLogger(MultiThreadTest.class) ;

        MDC.put("version" ,"1.0.0");

        log.info("Hello Girl !");

        MDC.put("source" ,"test");
        MDC.remove("version");

        log.info("Hello {} !" ,MDC.get("name"));

    }

}