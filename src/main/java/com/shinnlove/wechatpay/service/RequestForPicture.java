/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2019 All Rights Reserved.
 */
package com.shinnlove.wechatpay.service;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.HttpEntity;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.shinnlove.wechatpay.http.diy.PoolingHttpClient;

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
            executor.submit(() -> downImages("./miko/", url));
        }
        executor.shutdown();
    }

    /**
     * 下载图片到指定目录
     *
     * @param filePath 文件路径
     * @param imgUrl   图片URL
     */
    public static void downImages(String filePath, String imgUrl) {
        // 若指定文件夹没有，则先创建
        File dir = new File(filePath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        // 截取图片文件名
        String fileName = imgUrl.substring(imgUrl.lastIndexOf('/') + 1, imgUrl.length());

        try {
            // 文件名里面可能有中文或者空格，所以这里要进行处理。但空格又会被URLEncoder转义为加号
            String urlTail = URLEncoder.encode(fileName, "UTF-8");
            // 因此要将加号转化为UTF-8格式的%20
            imgUrl = imgUrl.substring(0, imgUrl.lastIndexOf('/') + 1)
                     + urlTail.replaceAll("\\+", "\\%20");

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        // 写出的路径
        File file = new File(filePath + File.separator + fileName);

        try {
            // http文件流
            HttpEntity entity = PoolingHttpClient.getImagesDownload(imgUrl);
            InputStream in = entity.getContent();

            // 获得文件输出流
            BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file));
            // 构建缓冲区(1KB得读写)
            byte[] buf = new byte[1024];
            int size;
            // 写入到文件
            while (-1 != (size = in.read(buf))) {
                out.write(buf, 0, size);
            }
            out.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

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