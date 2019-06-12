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
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpEntity;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.shinnlove.wechatpay.handler.DocumentExtract;
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
    private static final String SAVE_PATH                = "./miko/";

    /** 默认域名 */
    private static final String DOMAIN_NAME              = "https://ts2dh.com";

    /** 秀人主页目录 */
    private static final String XIUREN_CATALOG_PREFIX    = DOMAIN_NAME + "/xiurenwang/list_14_";

    /** 尤果网主页目录 */
    private static final String UGIRLS_CATALOG_PREFIX    = DOMAIN_NAME + "/youguowang/list_9_";

    /** 推荐主页目录前缀 */
    private static final String RECOMMEND_CATALOG_PREFIX = DOMAIN_NAME + "/page/";

    /** 页面后缀 */
    private static final String URL_SUFFIX               = ".html";

    /**
     * 获取域名。
     *
     * @return
     */
    public static String getDomainName() {
        return DOMAIN_NAME;
    }

    /**
     * 推荐目录50页。
     *
     * @param page
     * @return
     */
    public static String getRecommendCatelog(int page) {
        return RECOMMEND_CATALOG_PREFIX + page + URL_SUFFIX;
    }

    /**
     * 秀人网目录128页。
     *
     * @param page
     * @return
     */
    public static String getXiuRenCatelog(int page) {
        return XIUREN_CATALOG_PREFIX + page + URL_SUFFIX;
    }

    /**
     * 尤果网目录24页。
     *
     * @param page
     * @return
     */
    public static String getUGirlsCatelog(int page) {
        return UGIRLS_CATALOG_PREFIX + page + URL_SUFFIX;
    }

    /**
     * 找出某个目录下的帖子地址。
     *
     * @param domainName    域名
     * @param navURL        要搜索的目录地址
     * @param queue         要放入的队列
     */
    public static void searchCataLog(String domainName, String navURL,
                                     final BlockingQueue<String> queue) {
        final List<String> urls = new ArrayList<>();

        // 请求并抽取
        request(navURL, doc -> {
            // 找到推荐帖
            Elements navs = doc.getElementsByClass("content-wrap");
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

                urls.add(domainName + urlSuffix);
            }

            return null;
        });

        for (String url : urls) {
            try {
                queue.put(url);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 努力尝试10秒放入队列，或者沉睡3秒。
     *
     * @param queue
     * @param data
     * @param <T>
     */
    public static <T> void offerQueueOrWait(BlockingQueue<T> queue, T data) {
        try {
            boolean addResult = queue.offer(data, 10, TimeUnit.SECONDS);
            if (!addResult) {
                // 加不进去就等待10秒
                TimeUnit.SECONDS.sleep(3);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 持有锁尝试1秒加入队列数据。
     *
     * @param queue
     * @param data
     * @param <T>
     * @return
     */
    public static <T> boolean tryOfferWithLock(BlockingQueue<T> queue, T data) {
        try {
            return queue.offer(data, 1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            System.out.println("尝试加入队列data=" + data + "，被打断，加入未成功");
        }
        return false;
    }

    /**
     * 睡睡更健康。
     *
     * @param seconds
     */
    public static void sleepForFun(int seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 抽取帖子一页中的图片。
     *
     * @param post
     * @param pageNo
     */
    public static void onePageImages(PostPage post, int pageNo) {
        String requestURL = PostUtil.getRealURL(post.getUrl(), pageNo);

        System.out.println("准备请求帖子url=" + requestURL + " 获取图片。");

        final List<String> pictures = new ArrayList<>();

        String name = request(requestURL, doc -> {
            // 新增抓取帖子名称
            String postName = "miko美图";
            Elements wraps = doc.getElementsByClass("content-wrap");
            Element wrap = wraps.get(0);
            Elements titles = wrap.getElementsByClass("article-title");
            Element title = titles.get(0);
            postName = title.text();

            // 先处理图片路径
            Elements articles = doc.getElementsByClass("article-content");
            Element article = articles.get(0);
            Elements images = article.getElementsByTag("img");

            // 先读取所有图片
            for (Element e : images) {
                String imageSrc = e.attr("src");
                pictures.add(imageSrc);
            }

            return postName;
        });

        // 再for...try...catch下载（健壮）
        for (String src : pictures) {
            try {
                PostUtil.downImages(src,
                    SAVE_PATH + PostUtil.getImagePath(requestURL, name, pageNo) + "/");
            } catch (Exception e) {
                System.out.println("某图片下载失败：src=" + src + "，失败原因是ex=" + e.getMessage());
            }
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
            Document document = connect.timeout(30000).get();

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

            // 获得文件输出流，带buffer缓冲，避免系统态过多
            BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file));

            // 构建缓冲区(20KB得读写)
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
            System.out.println("下载帖子图片磁盘文件读写错误，原因是ex=" + e.getMessage());
        } catch (Exception e) {
            System.out.println("下载帖子图片网络错误，原因是ex=" + e.getMessage());
        }

    }

    /**
     * 工具类函数：获取帖子主题编号。
     *
     * 类似：https://zfl2019.com/xiurenwang/2018/0303/4769_9.html这样的网址，带上了下标，得到的帖子id是4769。
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
     * 特别注意：后来发现这个网站的帖子不仅只有`luyilu`这样的分类，还有`xiurenwang`、`youguowang`等分类。
     * 所以存储图片的时候先提取分类、然后是年、月、帖子id，这样存储比较合适。
     *
     * 如：https://zfl2019.com/xiurenwang/2018/0303/4769_9.html
     *
     * 存储为：xiurenwang/2018_0303_4769，其中xiurenwang是秀人文件夹、后边是整体的帖子信息
     *
     * @param url      
     * @param postName
     * @param pageNo
     * @return
     */
    public static String getImagePath(String url, String postName, int pageNo) {
        // 帖子id复用函数
        String theme = getTheme(url);

        try {
            // 然后截取前面部分提取月份
            int lastPath = url.lastIndexOf('/');
            String prefix = url.substring(0, lastPath);

            int lastPathMonth = prefix.lastIndexOf('/');
            String month = prefix.substring(lastPathMonth + 1);

            // 余下的提取年份
            String prefix2nd = prefix.substring(0, lastPathMonth);

            int lastPathYear = prefix2nd.lastIndexOf('/');
            String year = prefix2nd.substring(lastPathYear + 1);

            // 余下的提取分类
            String prefix3rd = prefix2nd.substring(0, lastPathYear);

            int lastPathCategory = prefix3rd.lastIndexOf('/');
            String category = prefix3rd.substring(lastPathCategory + 1);

            // 帖子title加上了页数，特殊处理
            String name = postName == null ? "miko美图" : postName;
            if (postName.length() >= 3) {
                if (pageNo > 1 && pageNo < 10) {
                    name = postName.substring(0, postName.length() - 3);
                } else if (pageNo >= 10 && pageNo <= 99) {
                    name = postName.substring(0, postName.length() - 4);
                } else if (pageNo >= 100 && pageNo <= 999) {
                    name = postName.substring(0, postName.length() - 5);
                }
            }

            String picPath = category + "/" + year + "_" + month + "_" + theme + "_" + name;

            return picPath;
        } catch (Exception e) {
            System.out.println("某帖子路径不合规范，直接用theme代替，theme=" + theme + "_" + postName);
            return theme + postName;
        }
    }

    /**
     * 工具类函数：获取每个帖子第一页。
     *
     * 类似：https://zfl2019.com/xiurenwang/2018/0303/4769_9.html，这样的网址，第一页就是：
     * https://zfl2019.com/xiurenwang/2018/0303/4769.html，去掉了下划线。
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
     * 类似这样的：https://zfl2019.com/xiurenwang/2018/0303/4769_9.html，
     * 传入pageNo=3会变成：https://zfl2019.com/xiurenwang/2018/0303/4769_9_3.html
     * 这里升级版选择先对url进行一个getFirstPage处理。
     *
     * @param url
     * @param pageNo
     * @return
     */
    public static String getRealURL(String url, int pageNo) {
        String requestURL = getFirstPage(url);

        // 第二页开始加下划线
        if (pageNo > 1) {
            int end = requestURL.lastIndexOf('.');

            String urlPrefix = requestURL.substring(0, end);
            String urlSuffix = requestURL.substring(end);
            requestURL = urlPrefix + "_" + pageNo + urlSuffix;
        }

        return requestURL;
    }

    /**
     * 公共请求网络的函数，旨在尽早GC掉JSoup对象。
     *
     * @param url
     * @param extract
     * @param <T>
     * @return
     */
    public static <T> T request(String url, DocumentExtract<T> extract) {
        // 连接这一步只是设置私有变量，不会出错
        Connection connect = Jsoup.connect(url);
        try {
            // 得到Document对象
            Document document = connect.timeout(30000).get();

            // 处理文档
            return extract.pickUp(document);

        } catch (IOException e) {
            // 出错就默认页数无效
            System.out.println("请求连接url=" + url + "，发生IO异常，ex=" + e.getMessage());
        } catch (Exception e) {
            System.out.println("请求连接url=" + url + "，发生异常，ex=" + e.getMessage());
        }
        return null;
    }

}