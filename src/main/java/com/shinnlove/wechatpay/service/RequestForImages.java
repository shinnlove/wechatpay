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
 * 下载图片。
 *
 * @author shinnlove.jinsheng
 * @version $Id: RequestForImages.java, v 0.1 2019-01-29 16:03 shinnlove.jinsheng Exp $$
 */
public class RequestForImages {

    public static void main(String[] args) {
        // 要请求的图片首页
        final String url = "https://2019zfl.com/luyilu/2018/0825/5701.html";

        // 从第一页开始请求
        int total = requestForPages(url, 1);

        System.out.println(total);

        ExecutorService executor = Executors.newFixedThreadPool(total);
        for (int i = 1; i <= total; i++) {
            final int pageNo = i;
            executor.submit(() -> requestForImages(url, pageNo, "./miko/"));
        }

        executor.shutdown();
    }

    public static int requestForPages(String url, int current) {
        String theme = getTheme(url);
        String requestURL = getRealURL(url, current);

        System.out.println("第" + current + "次请求url=" + requestURL + "获取最大页数");

        // 利用JSoup获得连接
        Connection connect = Jsoup.connect(requestURL);
        try {
            // 得到Document对象
            Document document = connect.get();

            // 再处理下一页
            Elements navs = document.getElementsByClass("pagination");
            Element nav = navs.get(0);

            // 所有的image图标（特别注意：max从当前页开始的!!!）
            int max = current;
            Elements links = nav.getElementsByTag("a");
            for (Element e : links) {
                // 每一页路径
                String pageSuffix = e.attr("href");

                // 页数去掉后缀
                String ulNo = pageSuffix.replace(".html", "");
                if (ulNo.indexOf("_") > 1) {
                    // 大于1页去下划线
                    ulNo = ulNo.replace(theme + "_", "");
                }

                // 取最大页
                int temp = Integer.valueOf(ulNo);
                if (temp > max) {
                    max = temp;
                }
            }

            Elements nextList = document.getElementsByClass("next-page");
            if (nextList.size() == 0) {
                // 最后一页最大的page
                return max;
            }

            // 还有其他页，再递归来一次
            return requestForPages(url, max);

        } catch (IOException e) {
            e.printStackTrace();
            // 出错就默认页数无效
            return 0;
        }
    }

    public static List<String> requestForImages(String url, int pageNo, String savePath) {
        List<String> imageList = new ArrayList<>();

        String requestURL = getRealURL(url, pageNo);

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
                imageList.add(imageSrc);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        // 每根线程下载一页
        for (String s : imageList) {
            downImages(s, savePath);
        }

        return imageList;
    }

    /**
     * 获取帖子主题编号。
     *
     * @param url
     * @return
     */
    private static String getTheme(String url) {
        int start = url.lastIndexOf('/');
        int end = url.lastIndexOf('.');
        String str = url.substring(start + 1, end);
        if (str.indexOf("_") > 0) {
            int pos = str.lastIndexOf("_");
            str = str.substring(0, pos);
        }
        return str;
    }

    /**
     * 请求真正url，注意.的位置。
     *
     * @param url
     * @param pageNo
     * @return
     */
    private static String getRealURL(String url, int pageNo) {
        String requestURL = url;

        // 第二页开始加下划线
        if (pageNo > 1) {
            int end = url.lastIndexOf('.');

            String urlPrefix = url.substring(0, end);
            String urlSuffix = url.substring(end);
            requestURL = urlPrefix + "_" + pageNo + urlSuffix;
        }

        return requestURL;
    }

    /**
     * 下载图片到指定目录
     *
     * @param imgUrl   图片URL
     * @param filePath 文件路径
     */
    public static void downImages(String imgUrl, String filePath) {
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

}