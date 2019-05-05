/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2019 All Rights Reserved.
 */
package com.shinnlove.wechatpay.service;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.shinnlove.wechatpay.model.PostPage;
import com.shinnlove.wechatpay.participator.PageReader;
import com.shinnlove.wechatpay.participator.PictureDownloader;
import com.shinnlove.wechatpay.participator.RecommendHunter;
import com.shinnlove.wechatpay.util.PostUtil;

/**
 * AI自学习下载图片。
 *
 * 搜索帖子线程一多，发现文章数量会指数级增长，队列满线程会挂起，内存小的电脑容易OOM。
 *
 * @author shinnlove.jinsheng
 * @version $Id: AIDownload.java, v 0.1 2019-01-31 10:35 shinnlove.jinsheng Exp $$
 */
public class AIDownload {

    /** 发现文章队列 */
    private static final BlockingQueue<String>   searchQueue = new LinkedBlockingQueue<>(5000);

    /** 等待阅读帖子队列 */
    private static final BlockingQueue<String>   readQueue   = new LinkedBlockingQueue<>(9000);

    /** 阅读详情帖子队列 */
    private static final BlockingQueue<PostPage> detailQueue = new LinkedBlockingQueue<>(3000);

    public static void main(String[] args) {

        final String domainName = PostUtil.getDomainName();

        // 要请求的图片首页
        String article = PostUtil.getDomainName() + "/meinv/36792.html";

        if (args.length > 0) {
            article = args[0];
            if (article.indexOf(domainName) < 0) {
                System.out.println("本程序仅针对网址：" + domainName + "才能下载图片");
                return;
            }

            // 修正不是第一页
            article = PostUtil.getFirstPage(article);
        }

        // 初始化搜索列表
        searchQueue.offer(article);

        startGracefully();
    }

    /**
     * 开启AI下载。
     */
    public static void startGracefully() {
        // 搜索
        RecommendHunter hunter = RecommendHunter.custom().setSearchQueue(searchQueue)
            .setReadQueue(readQueue).start();

        // 阅读
        PageReader reader = PageReader.custom().setReadQueue(readQueue).setDetailQueue(detailQueue)
            .start();

        // 下载
        PictureDownloader downloader = PictureDownloader.custom().setDetailQueue(detailQueue)
            .start();

        // 优雅关闭
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            hunter.stop();
            reader.stop();
            downloader.stop();
        }));
    }

}