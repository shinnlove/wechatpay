/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2019 All Rights Reserved.
 */
package com.shinnlove.wechatpay.service;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.shinnlove.wechatpay.model.PostPage;
import com.shinnlove.wechatpay.util.PostUtil;

/**
 * AI自学习下载图片。
 *
 * 搜索图片线程就1根，发现文章数量会指数级增长，不需要太多。
 *
 * @author shinnlove.jinsheng
 * @version $Id: AIDownload.java, v 0.1 2019-01-31 10:35 shinnlove.jinsheng Exp $$
 */
public class AIDownload {

    /** 默认域名 */
    private static final String                  DOMAIN_NAME      = "https://2019zfl.com";

    /** 搜索帖子线程池 */
    private static final ExecutorService         searchExecutor   = Executors.newCachedThreadPool();

    /** 消费帖子线程池 */
    private static final ExecutorService         consumeExecutor  = Executors.newCachedThreadPool();

    /** 读取帖子线程池 */
    private static final ExecutorService         readExecutor     = Executors
                                                                      .newFixedThreadPool(80);

    /** 下载图片线程池 */
    private static final ExecutorService         downloadExecutor = Executors
                                                                      .newFixedThreadPool(300);

    /** 发现文章队列 */
    private static final BlockingQueue<String>   searchQueue      = new LinkedBlockingDeque<>(2000);

    /** 已搜锁 */
    private static final ReentrantLock           searchedLock     = new ReentrantLock();

    /** 已搜文章队列（防止重复搜索） */
    private static final Map<String, Boolean>    searchedURL      = new ConcurrentHashMap<>();

    /** 已阅锁 */
    private static final ReentrantLock           readLock         = new ReentrantLock();

    /** 等待阅读帖子队列 */
    private static final BlockingQueue<String>   readQueue        = new LinkedBlockingDeque<>(2000);

    /** 已阅"文章有几页"列表（防止重复检索文章页数） */
    private static final Map<String, Boolean>    readPosts        = new ConcurrentHashMap<>();

    /** 阅读详情帖子队列 */
    private static final BlockingQueue<PostPage> detailQueue      = new LinkedBlockingQueue<>(2000);

    public static void main(String[] args) {

        // 要请求的图片首页
        String article = DOMAIN_NAME + "/luyilu/2018/0825/5701.html";

        if (args.length > 0) {
            article = args[0];
            if (article.indexOf(DOMAIN_NAME) < 0) {
                System.out.println("本程序仅针对网址：" + DOMAIN_NAME + "才能下载图片");
                return;
            }

            // 修正不是第一页
            article = PostUtil.getFirstPage(article);
        }

        // 初始化搜索列表
        searchQueue.offer(article);

        // 异步广度优先遍历
        for (int i = 0; i < 2; i++) {
            searchExecutor.submit(() -> BFSTravers(searchQueue));
        }

        // 同时消费帖子（消费者差不多是生产者8倍）
        for (int j = 0; j < 16; j++) {
            consumeExecutor.submit(() -> preHandlePost(readQueue));
        }

        // 开始读取帖子（10个阅读者并发读）
        for (int k = 0; k < 10; k++) {
            readExecutor.submit(() -> readPost());
        }

        // 优雅关闭
        searchExecutor.shutdown();
        consumeExecutor.shutdown();
    }

    /**
     * 从队列中读取一篇文章并搜索相关推荐。
     */
    public static void BFSTravers(final BlockingQueue queue) {
        while (true) {
            Object o = queue.poll();
            if (o == null) {
                // 没有更多文章了
                PostUtil.sleepForFun(5);
                continue;
            }

            String url = String.valueOf(o);
            if (searchedURL.containsKey(url)) {
                // 已搜过
                continue;
            }

            // 没搜过让阅读者去看看帖子有几页
            PostUtil.offerQueueOrWait(readQueue, url);

            searchMore(url, queue);
        }
    }

    /**
     * 通过一篇文章找更多相关文章链接。
     *
     * @param url
     * @param queue 队列带泛参，加入毫无违和感
     */
    public static void searchMore(String url, final BlockingQueue<String> queue) {
        // 利用JSoup获得连接
        Connection connect = Jsoup.connect(url);
        try {
            // 得到Document对象
            Document document = connect.get();

            // 找到推荐帖
            Elements navs = document.getElementsByClass("relates");
            Element nav = navs.get(0);
            Elements links = nav.getElementsByTag("a");

            // 本帖已搜过
            try {
                searchedLock.lock();
                searchedURL.put(url, true);
            } finally {
                searchedLock.unlock();
            }

            for (Element e : links) {
                String pageSuffix = e.attr("href");
                String fullURL = DOMAIN_NAME + pageSuffix;

                // 新推荐加入检索队列
                PostUtil.offerQueueOrWait(queue, fullURL);
            }

        } catch (IOException e) {
            System.out.println("请求帖子url=" + url + "发生了网络错误，原因ex=" + e.getMessage());
            //            e.printStackTrace();
            // 出错就默认页数无效
        }
    }

    /**
     * 消费者预处理帖子（重复标记、帖子页数），把帖子放到详情列表里。
     *
     * @param queue
     */
    private static void preHandlePost(final BlockingQueue queue) {
        while (true) {

            Object o = queue.poll();
            if (o == null) {
                PostUtil.sleepForFun(3);
                continue;
            }
            String url = String.valueOf(o);

            if (readPosts.containsKey(url)) {
                // 已阅
                continue;
            }

            // 先标记已阅
            try {
                readLock.lock();
                readPosts.put(url, true);
            } finally {
                readLock.unlock();
            }

            // 再获取帖子页数（耗时步骤）
            int pages = PostUtil.requestForPages(url, 1);
            PostPage p = new PostPage(url, pages);

            // 塞入图片详细队列
            PostUtil.offerQueueOrWait(detailQueue, p);
        }
    }

    /**
     * 阅读帖子并下载图片。
     */
    public static void readPost() {
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

}