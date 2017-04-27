package com.zlikun.log;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * 日志扫描配置测试用例
 * @auther zlikun <zlikun-dev@hotmail.com>
 * @date 2017/4/27 13:50
 */
public class ScanConfigTest {

    private Logger log = LoggerFactory.getLogger(ScanConfigTest.class) ;

    @Test @Ignore
    public void scan() throws InterruptedException {

        /*
         * 程序执行过程中，动态调整buile/resources/test/logback-test.xml配置文件，可以看到配置间隔固定时间，变更会生效
         */
        for (int i = 0; i < 100; i++) {
            log.debug("输出日志编号 - {}" ,i + 1);
            TimeUnit.SECONDS.sleep(1);

//            StatusPrinter.print((Context) LoggerFactory.getILoggerFactory());
        }

    }

}
