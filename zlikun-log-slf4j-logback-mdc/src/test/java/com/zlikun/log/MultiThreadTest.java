package com.zlikun.log;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 测试多线程条件下MDC工作情况
 *
 * @author zlikun <zlikun-dev@hotmail.com>
 * @date 2017/4/29 12:27
 */
public class MultiThreadTest {

    final Logger log = LoggerFactory.getLogger(MultiThreadTest.class) ;
    final AtomicInteger counter = new AtomicInteger() ;

    @Test
    public void logger() {

        // 测试证明main线程中的MDC信息不会被子类共享，实际是由ThreadLocal决定的
        MDC.put("name" ,"zlikun");
        MDC.put("version" ,"1.0.0");

        ExecutorService exec = Executors.newFixedThreadPool(10) ;

        for (int i = 0; i < 100; i++) {
            final int index = i ;
            exec.execute(new Runnable() {
                @Override
                public void run() {
                    // 模拟线程复用场景
                    // TODO 目前尚未发现MDC信息窜掉的情况，可能是模拟方式不对
                    for (int j = 0; j < 3; j++) {
                        doThing(index) ;
                        delay(10);
                    }
                }
            });
        }

        exec.shutdown();
        while (!exec.isTerminated()) ;

    }

    /**
     * 模拟事件(业务逻辑)
     * @param i
     */
    private void doThing(int i) {
        MDC.put("version" ,String.valueOf(i));
        log.info("事件编号：{} - {}" ,counter.getAndIncrement() ,i);
    }

    private void delay(int mills) {
        try {
            Thread.sleep(mills);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}