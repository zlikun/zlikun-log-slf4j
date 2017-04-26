package com.zlikun.log;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @auther zlikun <zlikun-dev@hotmail.com>
 * @date 2017/4/26 17:33
 */
public class InheritanceTest {

    @Test
    public void x() {

        // level info
        Logger log = LoggerFactory.getLogger("x") ;

        log.trace("x");
        log.debug("x");
        // 2017/04/26 17:36:24 | main | INFO | x | x
        log.info("x");
        // 2017/04/26 17:36:24 | main | WARN | x | x
        log.warn("x");
        // 2017/04/26 17:36:24 | main | ERROR | x | x
        log.error("x");

    }

    @Test
    public void x_y() {

        // level debug
        Logger log = LoggerFactory.getLogger("x.y") ;

        log.trace("x.y");
        // 2017/04/26 17:36:49 | main | DEBUG | x.y | x.y
        log.debug("x.y");
        // 2017/04/26 17:36:49 | main | INFO | x.y | x.y
        log.info("x.y");
        // 2017/04/26 17:36:49 | main | WARN | x.y | x.y
        log.warn("x.y");
        // 2017/04/26 17:36:49 | main | ERROR | x.y | x.y
        log.error("x.y");

    }

    /**
     * 测试证明继承了最近父级`x`的日志级别
     */
    @Test
    public void x_o() {

        // level debug
        Logger log = LoggerFactory.getLogger("x.o") ;

        log.trace("x.o");
        log.debug("x.o");
        // 2017/04/26 17:40:44 | main | INFO | x.o | x.o
        log.info("x.o");
        // 2017/04/26 17:40:44 | main | WARN | x.o | x.o
        log.warn("x.o");
        // 2017/04/26 17:40:44 | main | ERROR | x.o | x.o
        log.error("x.o");

    }

    @Test
    public void x_y_z() {

        // level warn
        Logger log = LoggerFactory.getLogger("x.y.z") ;

        log.trace("x.y.z");
        log.debug("x.y.z");
        log.info("x.y.z");
        // 2017/04/26 17:37:21 | main | WARN | x.y.z | x.y.z
        log.warn("x.y.z");
        // 2017/04/26 17:37:21 | main | ERROR | x.y.z | x.y.z
        log.error("x.y.z");

    }

    /**
     * 测试证明继承了最近父级`x.y.z`的日志级别
     */
    @Test
    public void x_y_z_none() {

        // level none
        Logger log = LoggerFactory.getLogger("x.y.z.none") ;

        log.trace("x.y.z.none");
        log.debug("x.y.z.none");
        log.info("x.y.z.none");
        // 2017/04/26 17:39:11 | main | WARN | x.y.z.none | x.y.z.none
        log.warn("x.y.z.none");
        // 2017/04/26 17:39:11 | main | ERROR | x.y.z.none | x.y.z.none
        log.error("x.y.z.none");

    }

}
