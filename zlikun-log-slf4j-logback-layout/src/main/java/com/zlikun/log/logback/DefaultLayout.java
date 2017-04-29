package com.zlikun.log.logback;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.LayoutBase;

/**
 * 自定义Layout组件
 *
 * @author zlikun <zlikun-dev@hotmail.com>
 * @date 2017/4/29 10:46
 */
public class DefaultLayout extends LayoutBase<ILoggingEvent> {

    public String doLayout(ILoggingEvent event) {
        StringBuffer sbuf = new StringBuffer(128);

        sbuf.append(event.getTimeStamp() - event.getLoggerContextVO().getBirthTime());
        sbuf.append(" ");
        sbuf.append(event.getLevel());
        sbuf.append(" [");
        sbuf.append(event.getThreadName());
        sbuf.append("] ");
        sbuf.append(event.getLoggerName());
        sbuf.append(" - ");
        sbuf.append(event.getFormattedMessage());
        sbuf.append(CoreConstants.LINE_SEPARATOR);
        return sbuf.toString();
    }
}