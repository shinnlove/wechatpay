/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2019 All Rights Reserved.
 */
package com.shinnlove.wechatpay.service;

import java.io.IOException;
import java.util.concurrent.*;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.shinnlove.wechatpay.util.PostUtil;

/**
 * 自学习并发下载图片。
 *
 * @author shinnlove.jinsheng
 * @version $Id: RequestForImages.java, v 0.1 2019-01-29 16:03 shinnlove.jinsheng Exp $$
 */
public class RequestForImages {

    /** 默认域名 */
    private static final String                DOMAIN_NAME      = "https://2019zfl.com";

    /** 搜索图片线程池 */
    private static final ExecutorService       commonExecutor   = Executors.newCachedThreadPool();

    /** 下载图片线程池 */
    private static final ExecutorService       downloadExecutor = Executors.newFixedThreadPool(300);

    /** 下载文章队列 */
    private static final BlockingQueue<String> downloadQueue    = new LinkedBlockingQueue<>(800);

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

        // 所有文章
        pickUp();

        // 启动检索消费者
        startUpRequest();

        // 最后优雅的关闭
        //        commonExecutor.shutdown();
        //        downloadExecutor.shutdown();
    }

    public static void pickUp() {
        commonExecutor.submit(() -> {
            for (int i = 1; i <= 50; i++) {
                String url = DOMAIN_NAME + "/page/" + i + ".html";
                searchOneNav(url);
            }
        });
    }

    public static void searchOneNav(String navURL) {
        Connection connect = Jsoup.connect(navURL);
        try {
            // 得到Document对象
            Document document = connect.get();

            // 找到推荐帖
            Elements navs = document.getElementsByClass("content-wrap");
            Element wrap = navs.get(0);
            Elements contents = wrap.getElementsByClass("content");
            Element content = contents.get(0);

            // 每一篇文章
            Elements excerpts = content.getElementsByClass("excerpt-one");
            for (Element one : excerpts) {
                Elements h2s = one.getElementsByTag("h2");
                Element h2 = h2s.get(0);

                Elements links = h2.getElementsByTag("a");
                Element a = links.get(0);

                String urlSuffix = a.attr("href");

                String fullURL = DOMAIN_NAME + urlSuffix;

                try {
                    downloadQueue.put(fullURL);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
            // 出错就默认页数无效
        }

    }

    /**
     * 启动检索消费者。
     */
    public static void startUpRequest() {
        // 20个消费者开始消费队列中文章
        for (int i = 0; i < 30; i++) {
            commonExecutor.submit(() -> downloadTask());
        }
    }

    /**
     * 下载任务。
     */
    public static void downloadTask() {
        while (true) {
            // 从队列中拿
            String url = "";
            try {
                url = downloadQueue.poll(3, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // 没有相关文章睡3秒
            if ("".equalsIgnoreCase(url)) {
                try {
                    TimeUnit.SECONDS.sleep(3);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                continue;
            }

            // 有文章开始请求
            requestForArticle(url);
        }
    }

    /**
     * 找文章的相关链接。
     *
     */
    public static void searchRelateAndPut(String url) {
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
                    boolean downloadResult = downloadQueue.offer(fullURL, 10, TimeUnit.SECONDS);
                    if (!downloadResult) {
                        // 加不进去就等待10秒
                        TimeUnit.SECONDS.sleep(10);
                    }
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            // 出错就默认页数无效
        }
    }

    /**
     * 并发请求某篇文章的图片。
     *
     * @param url
     */
    public static void requestForArticle(String url) {
        // 从第一页开始检索帖子有多少页
        int total = PostUtil.requestForPages(url, 1);

        // 提交下载任务（下载图片交给另外一个线程池）
        for (int i = 1; i <= total; i++) {
            final int pageNo = i;
            downloadExecutor.submit(() -> requestForImages(url, pageNo, "./miko/"));
        }
    }

    /**
     * 抽取帖子一页中的图片。
     *
     * @param url
     * @param pageNo
     * @param savePath
     * @return
     */
    public static void requestForImages(String url, int pageNo, String savePath) {
        String requestURL = PostUtil.getRealURL(url, pageNo);

        System.out.println("准备请求url=" + requestURL + " 获取图片。");

        Connection connect = Jsoup.connect(requestURL);
        try {
            // 得到Document对象
            Document document = connect.get();

            // 先处理图片路径
            Elements articles = document.getElementsByClass("article-content");
            Element article = articles.get(0);
            Elements images = article.getElementsByTag("img");
            for (Element e : images) {
                String imageSrc = e.attr("src");
                PostUtil.downImages(imageSrc, savePath + PostUtil.getTheme(url) + "/");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}