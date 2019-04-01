/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2019 All Rights Reserved.
 */
package com.shinnlove.wechatpay.util;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程工厂工具类。
 *
 * @author shinnlove.jinsheng
 * @version $Id: ThreadPoolUtil.java, v 0.1 2019-04-01 11:39 shinnlove.jinsheng Exp $$
 */
public class ThreadPoolUtil {

    /**
     * 创建一个下载线程池。
     *
     * 核心线程池30，最大可以增加到100，限制3秒回收，
     * 等待队列50（为了控制网卡下载速度，最多允许同时进行150个并发请求下载），队列满了就打日志丢弃下载请求。
     *
     * @param threadGroupName   自定义线程池线程组的名称
     * @param maximum           最大线程池数量
     * @return
     */
    public static ThreadPoolExecutor createPool(String threadGroupName, int maximum) {
        return new ThreadPoolExecutor(30, maximum, 300L, TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(50), new NamedThreadFactory(threadGroupName),
            new RejectedExecutionHandler() {
                // 自定义拒绝策略，一般打日志
                @Override
                public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                    System.out.println("下载请求被抛弃了");
                    // 可以选择抛出异常，一般选择吃掉
                    // throw new RejectedExecutionException("下载请求过多被拒绝");
                }
            });
    }

}