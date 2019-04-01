/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2019 All Rights Reserved.
 */
package com.shinnlove.wechatpay.participator;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.shinnlove.wechatpay.model.PostPage;
import com.shinnlove.wechatpay.util.NamedThreadFactory;
import com.shinnlove.wechatpay.util.PostUtil;

/**
 * 每页读取器，消费阅读队列里的数据。
 *
 * @author shinnlove.jinsheng
 * @version $Id: PageReader.java, v 0.1 2019-03-04 11:28 shinnlove.jinsheng Exp $$
 */
public class PageReader {

    /** 阅读帖子线程池 */
    private final ExecutorService   readExecutor = Executors
                                                     .newCachedThreadPool(new NamedThreadFactory(
                                                         "read-post"));

    /** 等待下载帖子队列 */
    private BlockingQueue<PostPage> detailQueue;

    /** 等待阅读帖子队列 */
    private BlockingQueue<String>   readQueue;

    /**
     * 每次自定义都返回一个新的阅读器。
     *
     * @return
     */
    public static PageReader custom() {
        return new PageReader();
    }

    /**
     * 数据入口待阅读队列。
     *
     * @param queue
     * @return
     */
    public PageReader setReadQueue(final BlockingQueue queue) {
        readQueue = queue;
        return this;
    }

    /**
     * 数据出口帖子详情队列。
     *
     * @param queue
     * @return
     */
    public PageReader setDetailQueue(final BlockingQueue queue) {
        detailQueue = queue;
        return this;
    }

    /**
     * 启动阅读。
     *
     * @return
     */
    public PageReader start() {
        // 同时消费帖子（起初消费者差不多是生产者8倍，但是后来差不多1:3，重复帖子越来越多）
        for (int j = 0; j < 24; j++) {
            readExecutor.submit(() -> preHandlePost(readQueue));
        }
        return this;
    }

    /**
     * 关闭阅读线程池。
     */
    public void stop() {
        readExecutor.shutdown();
    }

    /**
     * 消费者预处理帖子（重复标记、帖子页数），把帖子放到详情列表里。
     *
     * @param queue
     */
    private void preHandlePost(final BlockingQueue queue) {
        while (true) {

            Object o = queue.poll();
            if (o == null) {
                PostUtil.sleepForFun(3);
                continue;
            }
            String url = String.valueOf(o);

            // 再获取帖子页数（耗时步骤）
            int pages = PostUtil.requestForPages(url, 1);
            PostPage p = new PostPage(url, pages);

            // 塞入图片详细队列
            PostUtil.offerQueueOrWait(detailQueue, p);
        }
    }

}