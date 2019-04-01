/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2019 All Rights Reserved.
 */
package com.shinnlove.wechatpay.participator;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;

import com.shinnlove.wechatpay.model.PostPage;
import com.shinnlove.wechatpay.util.PostUtil;
import com.shinnlove.wechatpay.util.ThreadPoolUtil;

/**
 * 图片下载者。
 *
 * @author shinnlove.jinsheng
 * @version $Id: PictureDownloader.java, v 0.1 2019-03-04 11:45 shinnlove.jinsheng Exp $$
 */
public class PictureDownloader {

    /** 自定义-读取帖子线程池 */
    private static final ExecutorService readExecutor     = ThreadPoolUtil.createPool(
                                                              "extract-pic", 100);

    /** 自定义-下载图片线程池 */
    private static final ExecutorService downloadExecutor = ThreadPoolUtil.createPool(
                                                              "download-pic", 360);

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

}