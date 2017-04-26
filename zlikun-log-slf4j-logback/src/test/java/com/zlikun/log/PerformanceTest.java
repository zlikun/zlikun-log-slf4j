package com.zlikun.log;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 性能测试，本案输出日志到控制台
 * @auther zlikun <zlikun-dev@hotmail.com>
 * @date 2017/4/26 18:00
 */
public class PerformanceTest {

    @Test @Ignore
    public void logger_single_million() {

        final String text = fixLengthString(256) ;

        long time = System.currentTimeMillis() ;

        Logger log = LoggerFactory.getLogger(PerformanceTest.class) ;

        for (int i = 0; i < 1000 * 1000; i++) {
            log.info("{} - {}" ,text ,i + 1);
        }

        // 10772 / 12366 / 7616 / 18169
        System.err.println(String.format("百万次调用耗时：%d 毫秒!" ,System.currentTimeMillis() - time));

    }
    @Test @Ignore
    public void logger_multi_million() {

        final String text = fixLengthString(256) ;

        long time = System.currentTimeMillis() ;

        final Logger log = LoggerFactory.getLogger(PerformanceTest.class) ;

        ExecutorService exec = Executors.newFixedThreadPool(100) ;

        for (int i = 0; i < 1000 * 1000; i++) {
            final int index = i ;
            exec.execute(new Runnable() {
                @Override
                public void run() {
                    log.info("{} - {}" ,text ,index + 1);
                }
            });
        }

        exec.shutdown();

        while(!exec.isTerminated()) ;

        // 15302 / 17028
        System.err.println(String.format("百万次调用耗时：%d 毫秒!" ,System.currentTimeMillis() - time));

    }

    /**
     * 返回固定长度的随机字符串
     * @param length
     * @return
     */
    private String fixLengthString(int length) {
        if (length <= 0) throw new IllegalArgumentException() ;
        final StringBuilder sb = new StringBuilder() ;
        final Random r = new Random() ;
        for (int i = 0; i < length; i++) {
            // 生成 [A ,Z]，A ~ Z有26个字母，[0 ,26)表示0 ~ 25，加上65为65 ~ 90，即：A ~ Z
            sb.append((char) (r.nextInt(26) + 'A')) ;
        }
        return sb.toString() ;
    }

}
