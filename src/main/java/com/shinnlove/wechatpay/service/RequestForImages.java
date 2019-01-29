/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2019 All Rights Reserved.
 */
package com.shinnlove.wechatpay.service;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.concurrent.*;

import org.apache.http.HttpEntity;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.shinnlove.wechatpay.http.diy.PoolingHttpClient;

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
            article = getFirstPage(article);
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
        int total = requestForPages(url, 1);

        // 提交下载任务（下载图片交给另外一个线程池）
        for (int i = 1; i <= total; i++) {
            final int pageNo = i;
            downloadExecutor.submit(() -> requestForImages(url, pageNo, "./miko/"));
        }
    }

    /**
     * 查看某个帖子最多有几页。
     *
     * @param url
     * @param current
     * @return
     */
    public static int requestForPages(String url, int current) {
        String theme = getTheme(url);
        String requestURL = getRealURL(url, current);

        System.out.println("扫描第" + current + "页请求url=" + requestURL + "获取最大页数");

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

            if (max > 50) {
                // 超过50页说明可能请求到主页去了，直接返回
                return 50;
            }

            // 检查是否有最后一页
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
            return 1;
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
                downImages(imageSrc, savePath + getTheme(url) + "/");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 工具类函数：获取帖子主题编号。
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
     * 工具类函数：获取每个帖子第一页。
     *
     * @param url
     * @return
     */
    private static String getFirstPage(String url) {
        int start = url.lastIndexOf('/');
        int end = url.lastIndexOf('.');

        // 要提取这个/
        String urlPrefix = url.substring(0, start + 1);
        String urlSuffix = url.substring(end);
        String firstPage = urlPrefix + getTheme(url) + urlSuffix;
        return firstPage;
    }

    /**
     * 工具类函数：请求真正url，注意.的位置。
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
     * 工具类函数：下载图片到指定目录。
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