package com.zlikun.log;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @auther zlikun <zlikun-dev@hotmail.com>
 * @date 2017/4/25 18:45
 */
public class UsageTest {

    @Test
    public void usage() {

        Logger log = LoggerFactory.getLogger(UsageTest.class) ;

        // 2017/04/25 20:07:23 | main | INFO | com.zlikun.log.UsageTest | print log : info
        log.info("print log : info");

    }

}
