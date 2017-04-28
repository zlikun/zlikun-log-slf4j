package com.zlikun.log.logback;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.OutputStreamAppender;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 向控制台输出指定数量的日志
 * @auther zlikun <zlikun-dev@hotmail.com>
 * @date 2017/4/28 18:42
 */
public class CountingConsoleAppender extends OutputStreamAppender<ILoggingEvent> {

    private int limit ;
    private AtomicInteger counter ;

    public CountingConsoleAppender() {
        counter = new AtomicInteger() ;
    }

    @Override
    public void start() {
        this.setOutputStream(System.err);
        super.start();
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public void append(ILoggingEvent event) {
        if(counter.getAndIncrement() >= limit) {
            return ;
        }
        super.append(event);
    }

}
