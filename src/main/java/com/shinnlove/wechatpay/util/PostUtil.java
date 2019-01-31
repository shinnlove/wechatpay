/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2019 All Rights Reserved.
 */
package com.shinnlove.wechatpay.util;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.shinnlove.wechatpay.http.diy.PoolingHttpClient;
import com.shinnlove.wechatpay.model.PostPage;

/**
 * 帖子URL处理公共类。
 *
 * @author shinnlove.jinsheng
 * @version $Id: PostUtil.java, v 0.1 2019-01-31 11:35 shinnlove.jinsheng Exp $$
 */
public class PostUtil {

    /** 图片默认保存路径 */
    private static final String SAVE_PATH = "./miko/";

    /**
     * 抽取帖子一页中的图片。
     *
     * @param post
     * @param pageNo
     */
    public static void onePageImages(PostPage post, int pageNo) {
        String requestURL = PostUtil.getRealURL(post.getUrl(), pageNo);

        System.out.println("准备请求url=" + requestURL + " 获取图片。");

        Connection connect = Jsoup.connect(requestURL);
        try {
            // 得到Document对象
            Document document = connect.get();

            // 先处理图片路径
            Elements articles = document.getElementsByClass("article-content");
            Element article = articles.get(0);
            Elements images = article.getElementsByTag("img");

            // 先读取所有图片
            List<String> srcList = new ArrayList<>();
            for (Element e : images) {
                String imageSrc = e.attr("src");
                srcList.add(imageSrc);
            }

            // 再for...try...catch下载（健壮）
            for (String src : srcList) {
                try {
                    PostUtil.downImages(src, SAVE_PATH + PostUtil.getImagePath(requestURL) + "/");
                } catch (Exception e) {
                    System.out.println("某图片下载失败：src=" + src + "，失败原因是ex=" + e.getMessage());
                }
            }

        } catch (IOException e) {
            System.out.println("读取帖子url=" + requestURL + "，请求第" + pageNo + "页图片，但是发生网络错误，ex="
                               + e.getMessage());
            //            e.printStackTrace();
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
        String theme = PostUtil.getTheme(url);
        String requestURL = PostUtil.getRealURL(url, current);

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
            System.out.println("当前请求帖子页发生错误，原因是ex=" + e.getMessage());
            // 出错就默认上一次递归获得的页数是最大
            return current;
        }
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
            System.out.println("文件名乱码转换错误，ex=" + e.getMessage());
        }

        // 图片写出的路径
        File file = new File(filePath + File.separator + fileName);

        // 老图不下(除非手动删掉不完整的图片)
        if (file.exists()) {
            return;
        }

        try {
            // http文件流
            HttpEntity entity = PoolingHttpClient.getImagesDownload(imgUrl);
            InputStream in = entity.getContent();

            // 获得文件输出流
            BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file));

            // 构建缓冲区(10KB得读写)
            byte[] buf = new byte[20480];
            int size;
            // 写入到文件
            while (-1 != (size = in.read(buf))) {
                out.write(buf, 0, size);
            }
            out.close();
        } catch (MalformedURLException e) {
            System.out.println("下载帖子发生缓冲区错误，原因是ex=" + e.getMessage());
        } catch (IOException e) {
            System.out.println("下载帖子图片网络错误，原因是ex=" + e.getMessage());
        } catch (Exception e) {
            System.out.println("下载帖子图片发生错误，原因是ex=" + e.getMessage());
        }

    }

    /**
     * 工具类函数：获取帖子主题编号。
     *
     * @param url
     * @return
     */
    public static String getTheme(String url) {
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
     * 按帖子年月帖子编号来命名文件夹，防止重复。
     *
     * @param url 
     * @return
     */
    public static String getImagePath(String url) {
        String theme = getTheme(url);

        try {
            int lastPath = url.lastIndexOf('/');
            String prefix = url.substring(0, lastPath);

            int lastPath2nd = prefix.lastIndexOf('/');
            String month = prefix.substring(lastPath2nd + 1);
            String prefix2nd = prefix.substring(0, lastPath2nd);

            int lastPath3rd = prefix2nd.lastIndexOf('/');
            String year = prefix2nd.substring(lastPath3rd + 1);

            String picPath = year + "_" + month + "_" + theme;

            return picPath;
        } catch (Exception e) {
            System.out.println("某帖子路径不合规范，直接用theme代替");
            return theme;
        }
    }

    /**
     * 工具类函数：获取每个帖子第一页。
     *
     * @param url
     * @return
     */
    public static String getFirstPage(String url) {
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
    public static String getRealURL(String url, int pageNo) {
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

}