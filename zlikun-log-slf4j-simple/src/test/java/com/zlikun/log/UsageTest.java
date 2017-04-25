package com.zlikun.log;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.slf4j.helpers.BasicMarker;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @auther zlikun <zlikun-dev@hotmail.com>
 * @date 2017/4/25 18:45
 */
public class UsageTest {

    @Test
    public void usage() {

        Logger log = LoggerFactory.getLogger(UsageTest.class) ;

        // log level
        // 2017/04/25 19:19:47.047 [main] TRACE UsageTest - print log : trace
        log.trace("print log : trace");
        // 2017/04/25 19:19:47.047 [main] DEBUG UsageTest - print log : debug
        log.debug("print log : debug");
        // 2017/04/25 19:19:47.047 [main] INFO UsageTest - print log : info
        log.info("print log : info");
        // 2017/04/25 19:19:47.047 [main] WARN UsageTest - print log : warn
        log.warn("print log : warn");
        // 2017/04/25 19:19:47.047 [main] ERROR UsageTest - print log : error
        log.error("print log : error");

        // precompile
        // 2017/04/25 19:19:47.047 [main] INFO UsageTest - I'm zlikun ,age is 120 .
        log.info("I'm {} ,age is {} ." ,"zlikun" ,"120");
        // 2017/04/25 19:19:47.047 [main] INFO UsageTest - Error occurred !
        log.info("Error occurred !" ,new RuntimeException("发生了运行时错误"));
        // 2017/04/25 19:19:47.047 [main] ERROR UsageTest - Error occurred !
        log.error("Error occurred !" ,new RuntimeException("发生了运行时错误"));
        // 2017/04/25 19:19:47.047 [main] ERROR UsageTest - Error occurred - xxx!
        log.error("Error occurred - {}!" ,"xxx" ,new RuntimeException("发生了运行时错误"));

    }

    @Test
    public void logger() {
        Logger log = LoggerFactory.getLogger("com.zlikun.log.simple.Test") ;

        log.trace("print log : trace");
        log.debug("print log : debug");
        log.info("print log : info");
        // 2017/04/25 19:23:28.028 [main] WARN Test - print log : warn
        log.warn("print log : warn");
        // 2017/04/25 19:23:28.028 [main] ERROR Test - print log : error
        log.error("print log : error");

    }

}
