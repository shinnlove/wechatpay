/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2019 All Rights Reserved.
 */
package com.shinnlove.wechatpay.participator;

import java.util.concurrent.*;

import com.shinnlove.wechatpay.model.PostPage;
import com.shinnlove.wechatpay.util.NamedThreadFactory;
import com.shinnlove.wechatpay.util.PostUtil;

/**
 * 图片下载者。
 *
 * @author shinnlove.jinsheng
 * @version $Id: PictureDownloader.java, v 0.1 2019-03-04 11:45 shinnlove.jinsheng Exp $$
 */
public class PictureDownloader {

    /** 自定义-读取帖子线程池 */
    private static final ExecutorService readExecutor     = createPool("read-post", 100);

    /** 自定义-下载图片线程池 */
    private static final ExecutorService downloadExecutor = createPool("download-pic", 360);

    /** 帖子详情队列 */
    private BlockingQueue<PostPage>      detailQueue;

    /**
     * 每次自定义都返回一个新的下载对象。
     *
     * @return
     */
    public static PictureDownloader custom() {
        return new PictureDownloader();
    }

    /**
     * 已经读完多少页的帖子队列。
     *
     * @param queue
     * @return
     */
    public PictureDownloader setDetailQueue(final BlockingQueue queue) {
        detailQueue = queue;
        return this;
    }

    /**
     * 启动读每页帖子图片。
     *
     * @return
     */
    public PictureDownloader start() {
        // 开始读取帖子（10个阅读者并发读）
        for (int k = 0; k < 20; k++) {
            readExecutor.submit(() -> readPost());
        }
        return this;
    }

    /**
     * 阅读帖子并下载图片。
     */
    public void readPost() {
        while (true) {

            final PostPage post = detailQueue.poll();
            if (post == null) {
                PostUtil.sleepForFun(3);
                continue;
            }

            int pages = post.getPages();

            // 并发读帖子（特别注意：帖子第一页开始）
            for (int i = 1; i <= pages; i++) {
                final int pageNo = i;
                downloadExecutor.submit(() -> PostUtil.onePageImages(post, pageNo));
            }

        }
    }

    /**
     * 停止图片下载。
     */
    public void stop() {
        readExecutor.shutdown();
        downloadExecutor.shutdown();
    }

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
    private static ThreadPoolExecutor createPool(String threadGroupName, int maximum) {
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