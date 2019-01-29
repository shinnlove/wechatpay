/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2019 All Rights Reserved.
 */
package com.shinnlove.wechatpay.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * 利用JSoup请求图片。
 *
 * @author shinnlove.jinsheng
 * @version $Id: RequestForPicture.java, v 0.1 2019-01-29 14:45 shinnlove.jinsheng Exp $$
 */
public class RequestForPicture {

    private static ExecutorService executor = Executors.newFixedThreadPool(50);

    public static void main(String[] args) {
        List<String> images = getImages();
        for (String url : images) {
            executor.submit(() -> RequestForImages.downImages(url, "./miko/"));
        }
        executor.shutdown();
    }

    /**
     * 采集图片。
     *
     * @return
     */
    public static List<String> getImages() {
        List<String> imageList = new ArrayList<>();
        // 利用Jsoup获得连接
        Connection connect = Jsoup.connect("https://www.haorenka.cc/mikojiang.html");
        try {
            // 得到Document对象
            Document document = connect.get();

            // 只要第一篇帖子内容
            Elements contents = document.getElementsByClass("entry-content");
            Element content = contents.get(0);

            // 所有的image图标
            Elements images = content.getElementsByTag("img");
            for (Element e : images) {
                //获取每个img标签URL "abs:"表示绝对路径
                String imgSrc = e.attr("data-original");
                // 打印URL
                System.out.println(imgSrc);

                imageList.add(imgSrc);
            }

            System.out.println("下载完成");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return imageList;
    }

}