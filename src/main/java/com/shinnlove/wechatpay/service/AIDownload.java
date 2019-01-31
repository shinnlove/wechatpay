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

    /** 锁机制 */
    private static final ReentrantLock           lock             = new ReentrantLock();

    /** 发现文章列表（防止重复下载） */
    private static final Map<String, Boolean>    findPosts        = new ConcurrentHashMap<>();

    /** 需要阅读帖子队列 */
    private static final BlockingQueue<PostPage> readQueue        = new LinkedBlockingQueue<>(2000);

    /** 下载文章队列 */
    private static final BlockingQueue<PostPage> downloadQueue    = new LinkedBlockingQueue<>(2000);

    public static void main(String[] args) {

        // 要请求的图片首页
        String article = DOMAIN_NAME + "/luyilu/2018/0825/5701.html";

        // 初始化文章列表
        searchQueue.offer(article);

        // 异步广度优先遍历
        for (int i = 0; i < 2; i++) {
            searchExecutor.submit(() -> BFSTravers());
        }

        // 同时消费帖子（消费者差不多是生产者8倍）
        for (int j = 0; j < 16; j++) {
            consumeExecutor.submit(() -> preHandlePost(searchQueue));
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
    public static void BFSTravers() {
        while (true) {
            String url = searchQueue.poll();
            if (url == null) {
                // 没有更多文章了
                try {
                    TimeUnit.SECONDS.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                continue;
            }

            searchMore(url);
        }
    }

    /**
     * 通过一篇文章找更多相关文章链接。
     *
     * @param url
     */
    public static void searchMore(String url) {
        // 利用JSoup获得连接
        Connection connect = Jsoup.connect(url);
        try {
            // 得到Document对象
            Document document = connect.get();

            // 找到推荐帖
            Elements navs = document.getElementsByClass("relates");
            Element nav = navs.get(0);
            Elements links = nav.getElementsByTag("a");

            for (Element e : links) {
                String pageSuffix = e.attr("href");
                String fullURL = DOMAIN_NAME + pageSuffix;

                try {
                    // 加入搜索队列
                    boolean downloadResult = searchQueue.offer(fullURL, 10, TimeUnit.SECONDS);
                    if (!downloadResult) {
                        // 加不进去就等待10秒
                        TimeUnit.SECONDS.sleep(10);
                    }
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }

        } catch (IOException e) {
            System.out.println("请求帖子url=" + url + "发生了网络错误，原因ex=" + e.getMessage());
            //            e.printStackTrace();
            // 出错就默认页数无效
        }
    }

    /**
     * 消费者预处理帖子（重复标记、帖子页数）。
     *
     * @param queue
     */
    private static void preHandlePost(BlockingQueue queue) {
        while (true) {

            Object o = queue.poll();

            if (o == null) {
                try {
                    TimeUnit.SECONDS.sleep(3);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                continue;
            }

            System.out.println("线程id=" + Thread.currentThread().getId() + "发现文章" + o);

            String url = String.valueOf(o);
            if (findPosts.containsKey(url)) {
                // 已抓过
                continue;
            }

            // 先打标
            try {
                lock.lock();
                findPosts.put(url, true);
            } finally {
                lock.unlock();
            }

            // 再获取帖子页数（耗时步骤）
            int pages = PostUtil.requestForPages(url, 1);
            // 构造帖子
            PostPage p = new PostPage(url, pages);

            // 塞入下载队列
            try {
                boolean addResult = readQueue.offer(p, 10, TimeUnit.SECONDS);
                if (!addResult) {
                    // 加不进去就等待10秒
                    TimeUnit.SECONDS.sleep(10);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * 阅读帖子并下载图片。
     */
    public static void readPost() {
        while (true) {

            final PostPage post = readQueue.poll();

            if (post == null) {
                try {
                    TimeUnit.SECONDS.sleep(3);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                continue;
            }

            System.out.println("线程id=" + Thread.currentThread().getId() + "阅读文章" + post);

            int pages = post.getPages();

            // 并发读帖子（特别注意帖子第一页开始）
            for (int i = 1; i <= pages; i++) {
                final int pageNo = i;
                downloadExecutor.submit(() -> PostUtil.onePageImages(post, pageNo));
            }

        }
    }

}