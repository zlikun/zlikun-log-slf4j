package com.zlikun.log;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @auther zlikun <zlikun-dev@hotmail.com>
 * @date 2017/4/28 17:12
 */
public class AsyncAppenderTest {

    @Test @Ignore
    public void append() {
        final Logger log = LoggerFactory.getLogger(AsyncAppenderTest.class) ;
        log.info("输出日志到文件(异步)");

        final String infoLog = fixLengthString(256) ;
        final String errorLog = fixLengthString(256) ;

        long time = System.currentTimeMillis() ;

        ExecutorService exec = Executors.newFixedThreadPool(50) ;

        // 输出100,000次日志，由于异步，写入队列的速度要远大于写入文件的速度，因此将造成日志堆积
        // 根据配置的规则，首先程序阻止记录TRACE、DEBUG、INFO级别的日志
        // 如果日志的生成速度依然过快，造成队列满，当neverBlock配置为false时，将导致阻塞，如果配置为true，将导致丢失日志(包含WARN、ERROR级别的)
        for (int i = 0; i < 100 * 1000; i++) {
            exec.execute(new Runnable() {
                @Override
                public void run() {
                    log.info(infoLog);
                    log.error(errorLog);
                }
            });
        }

        exec.shutdown();
        while(!exec.isTerminated()) ;

        // neverBlock = false   1333ms  INFO 26207  ERROR 100,000   当队列会阻塞时，由于discardingThreshold机制，INFO级别的日志丢失了一些
        // neverBlock = true    769ms   INFO 191    ERROR 881       当队列不阻塞时，其它条件相同，但丢失了大量日志
        System.out.println(String.format("程序执行耗时： %d 毫秒!" ,System.currentTimeMillis() - time));

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
