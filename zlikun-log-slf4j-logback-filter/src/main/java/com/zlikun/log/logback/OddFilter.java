package com.zlikun.log.logback;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 自定义过滤器：奇数过滤器，表示只输出奇数次日志
 *
 * @author zlikun <zlikun-dev@hotmail.com>
 * @date 2017/4/29 19:05
 */
public class OddFilter extends Filter<ILoggingEvent> {

    private AtomicInteger counter ;

    public OddFilter() {
        this.counter = new AtomicInteger() ;
    }

    @Override
    public FilterReply decide(ILoggingEvent event) {
        if (!this.isStarted())
            return FilterReply.NEUTRAL ;
        // 非INFO级别的日志不参与该计数规则
        if (!event.getLevel().equals(Level.INFO))
            return FilterReply.NEUTRAL ;    // 继续向下执行，由后续过滤器决定是否输出日志
        if(this.counter.getAndIncrement() % 2 == 1) {
            // 奇数时，输出日志
            return FilterReply.ACCEPT ;     // 忽略剩下过滤器，同意输出日志
        } else {
            // 偶数时，不输出
            return FilterReply.DENY ;       // 忽略剩下过滤器，拒绝输出日志
        }
    }
}