# zlikun-log-slf4j-logback-mdc
- <https://logback.qos.ch/manual/mdc.html>
- <https://logback.qos.ch/manual/mdc.html#autoMDC>
- <https://logback.qos.ch/manual/mdc.html#managedThreads>

#### 用法
```
// 添加信息到MDC
MDC.put("name" ,"zlikun");

// 获取添加的信息，在日志模板中，使用`%X{属性名}`获取属性值信息
<pattern>%d{yyyy/MM/dd HH:mm:ss} | %contextName | %t | %level | %logger | [%Xname %Xversion %source] | %msg%n</pattern>```

// 输出效果，第一行是在输出之前设置了name和version属性，第二行则补充了source属性
2017/04/29 20:10:00 | zlikun-log | main | INFO | com.zlikun.log.MultiThreadTest | [zlikun 1.0.0 ] | Hello Girl !
2017/04/29 20:10:00 | zlikun-log | main | INFO | com.zlikun.log.MultiThreadTest | [zlikun 1.0.0 test] | Hello Boy !
```

#### 源码
```
// MDC实际执行程序：org.slf4j.impl.StaticMDCBinder#getMDCA()，slf4j-api要求该类必须有getSingleton()方法或SINGLETON实例，实际构成单例
// MDCAdater在Logback的实现类是：ch.qos.logback.classic.util.LogbackMDCAdapter

/**
 * Logback: the reliable, generic, fast and flexible logging framework.
 * Copyright (C) 1999-2015, QOS.ch. All rights reserved.
 *
 * This program and the accompanying materials are dual-licensed under
 * either the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation
 *
 *   or (per the licensee's choosing)
 *
 * under the terms of the GNU Lesser General Public License version 2.1
 * as published by the Free Software Foundation.
 */
package ch.qos.logback.classic.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.spi.MDCAdapter;

/**
 * A <em>Mapped Diagnostic Context</em>, or MDC in short, is an instrument for
 * distinguishing interleaved log output from different sources. Log output is
 * typically interleaved when a server handles multiple clients
 * near-simultaneously.
 * <p/>
 * <b><em>The MDC is managed on a per thread basis</em></b>. A child thread
 * automatically inherits a <em>copy</em> of the mapped diagnostic context of
 * its parent.
 * <p/>
 * <p/>
 * For more information about MDC, please refer to the online manual at
 * http://logback.qos.ch/manual/mdc.html
 *
 * @author Ceki G&uuml;lc&uuml;
 */
public class LogbackMDCAdapter implements MDCAdapter {

    // The internal map is copied so as

    // We wish to avoid unnecessarily copying of the map. To ensure
    // efficient/timely copying, we have a variable keeping track of the last
    // operation. A copy is necessary on 'put' or 'remove' but only if the last
    // operation was a 'get'. Get operations never necessitate a copy nor
    // successive 'put/remove' operations, only a get followed by a 'put/remove'
    // requires copying the map.
    // See http://jira.qos.ch/browse/LOGBACK-620 for the original discussion.

    // We no longer use CopyOnInheritThreadLocal in order to solve LBCLASSIC-183
    // Initially the contents of the thread local in parent and child threads
    // reference the same map. However, as soon as a thread invokes the put()
    // method, the maps diverge as they should.
    final ThreadLocal<Map<String, String>> copyOnThreadLocal = new ThreadLocal<Map<String, String>>();

    private static final int WRITE_OPERATION = 1;
    private static final int MAP_COPY_OPERATION = 2;

    // keeps track of the last operation performed
    final ThreadLocal<Integer> lastOperation = new ThreadLocal<Integer>();

    private Integer getAndSetLastOperation(int op) {
        Integer lastOp = lastOperation.get();
        lastOperation.set(op);
        return lastOp;
    }

    /**
     * 判断上一次操作是否为空或者是MAP_COPY_OPERATION
     * @param lastOp
     * @return
     */
    private boolean wasLastOpReadOrNull(Integer lastOp) {
        return lastOp == null || lastOp.intValue() == MAP_COPY_OPERATION;
    }

    private Map<String, String> duplicateAndInsertNewMap(Map<String, String> oldMap) {
        // 构造一个线程安全的MAP
        Map<String, String> newMap = Collections.synchronizedMap(new HashMap<String, String>());
        // 如果旧的MAP存在，将旧的MAP填充到新MAP中
        if (oldMap != null) {
            // we don't want the parent thread modifying oldMap while we are
            // iterating over it
            synchronized (oldMap) {
                newMap.putAll(oldMap);
            }
        }
        // 将新MAP作为MDC存储容器写入ThreadLocal中，并返回之
        copyOnThreadLocal.set(newMap);
        return newMap;
    }

    /**
     * Put a context value (the <code>val</code> parameter) as identified with the
     * <code>key</code> parameter into the current thread's context map. Note that
     * contrary to log4j, the <code>val</code> parameter can be null.
     * <p/>
     * <p/>
     * If the current thread does not have a context map it is created as a side
     * effect of this call.
     *
     * @throws IllegalArgumentException in case the "key" parameter is null
     */
    public void put(String key, String val) throws IllegalArgumentException {
        if (key == null) {
            throw new IllegalArgumentException("key cannot be null");
        }
        // 此句代码说明MDC信息实际是由ThreadLocal来保存，所以单个线程中，属性是相互共享的
        Map<String, String> oldMap = copyOnThreadLocal.get();
        // 返回上一次操作代码：WRITE_OPERATION(1)、MAP_COPY_OPERATION(2)，并设置为：WRITE_OPERATION
        // 意为最近一次操作为：WRITE_OPERATION，后面逻辑将判断上一次操作
        Integer lastOp = getAndSetLastOperation(WRITE_OPERATION);
        // 如果上一次操作为NULL或MAP_COPY_OPERATION，或者MDC中信息为空
        if (wasLastOpReadOrNull(lastOp) || oldMap == null) {
            // 初始化MDC，并将本次传入键值对添加到新的MDC中
            Map<String, String> newMap = duplicateAndInsertNewMap(oldMap);
            newMap.put(key, val);
        } else {
            // 直接将键值对添加到旧的MDC中
            oldMap.put(key, val);
        }
    }

    /**
     * Remove the the context identified by the <code>key</code> parameter.
     * <p/>
     */
    public void remove(String key) {
        if (key == null) {
            return;
        }
        // 如果MDC信息不存在，直接忽略删除操作
        Map<String, String> oldMap = copyOnThreadLocal.get();
        if (oldMap == null)
            return;

        Integer lastOp = getAndSetLastOperation(WRITE_OPERATION);
        // 从MDC中删除KEY
        if (wasLastOpReadOrNull(lastOp)) {
            Map<String, String> newMap = duplicateAndInsertNewMap(oldMap);
            newMap.remove(key);
        } else {
            oldMap.remove(key);
        }
    }

    /**
     * Clear all entries in the MDC.
     */
    public void clear() {
        lastOperation.set(WRITE_OPERATION);
        copyOnThreadLocal.remove();
    }

    /**
     * Get the context identified by the <code>key</code> parameter.
     * <p/>
     */
    public String get(String key) {
        final Map<String, String> map = copyOnThreadLocal.get();
        if ((map != null) && (key != null)) {
            return map.get(key);
        } else {
            return null;
        }
    }

    /**
     * 将操作置为`MAP_COPY_OPERATION`仅有的地方，用途是什么呢？
     * Get the current thread's MDC as a map. This method is intended to be used
     * internally.
     */
    public Map<String, String> getPropertyMap() {
        lastOperation.set(MAP_COPY_OPERATION);
        return copyOnThreadLocal.get();
    }

    /**
     * Returns the keys in the MDC as a {@link Set}. The returned value can be
     * null.
     */
    public Set<String> getKeys() {
        Map<String, String> map = getPropertyMap();

        if (map != null) {
            return map.keySet();
        } else {
            return null;
        }
    }

    /**
     * Return a copy of the current thread's context map. Returned value may be
     * null.
     */
    public Map<String, String> getCopyOfContextMap() {
        Map<String, String> hashMap = copyOnThreadLocal.get();
        if (hashMap == null) {
            return null;
        } else {
            return new HashMap<String, String>(hashMap);
        }
    }

    /**
     * 初始化MDC容器(覆盖)
     * @param contextMap
     */
    public void setContextMap(Map<String, String> contextMap) {
        lastOperation.set(WRITE_OPERATION);

        Map<String, String> newMap = Collections.synchronizedMap(new HashMap<String, String>());
        newMap.putAll(contextMap);

        // the newMap replaces the old one for serialisation's sake
        copyOnThreadLocal.set(newMap);
    }
}
```