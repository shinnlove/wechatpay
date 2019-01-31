/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.shinnlove.wechatpay.http.diy;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.DnsResolver;
import org.apache.http.conn.HttpConnectionFactory;
import org.apache.http.conn.ManagedHttpClientConnection;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultHttpResponseParserFactory;
import org.apache.http.impl.conn.ManagedHttpClientConnectionFactory;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.conn.SystemDefaultDnsResolver;
import org.apache.http.impl.io.DefaultHttpRequestWriterFactory;
import org.apache.http.util.EntityUtils;

import com.shinnlove.wechatpay.http.consts.DefaultHeaders;
import com.shinnlove.wechatpay.http.header.RequestHeaderBuilder;

/**
 * 带有连接池的自定义HttpClient。
 *
 * @author shinnlove.jinsheng
 * @version $Id: PoolingHttpClient.java, v 0.1 2018-07-04 上午11:31 shinnlove.jinsheng Exp $$
 */
public class PoolingHttpClient {

    /** 默认字符集 */
    private static final String               CHARSET    = "UTF-8";

    /** http池化连接 */
    static PoolingHttpClientConnectionManager manager    = null;

    /** 线程安全的httpClient */
    static CloseableHttpClient                httpClient = null;

    public static synchronized CloseableHttpClient getHttpClient() {
        // 懒初始化
        if (httpClient == null) {

            // 如果https使用自己的证书，可以初始化`KeyStore`(PKCS12, load)、`KeyManagerFactory(pwd, init)`、`SSLContext(TLS、init)`
            // 这里使用默认的`SSLConnectionSocketFactory.getSystemSocketFactory()`来创建

            // 注册访问协议相关的SocketFactory，支持http/https
            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder
                .<ConnectionSocketFactory> create()
                // `PlainConnectionSocketFactory`是默认的创建、初始化明文socket（不加密）的工厂类
                .register("http", PlainConnectionSocketFactory.INSTANCE)
                .register("https", SSLConnectionSocketFactory.getSystemSocketFactory()).build();

            // 配置请求响应处理器
            HttpConnectionFactory<HttpRoute, ManagedHttpClientConnection> connectionFactory = new ManagedHttpClientConnectionFactory(
                DefaultHttpRequestWriterFactory.INSTANCE, DefaultHttpResponseParserFactory.INSTANCE);

            // 配置DNS解析器
            DnsResolver dnsResolver = SystemDefaultDnsResolver.INSTANCE;

            // 创建池化连接管理器
            manager = new PoolingHttpClientConnectionManager(socketFactoryRegistry,
                connectionFactory, dnsResolver);

            // 默认的TCP套接字配置
            SocketConfig defaultSocketConfig = SocketConfig.custom().setTcpNoDelay(true).build();
            manager.setDefaultSocketConfig(defaultSocketConfig);

            // 整个池子最大连接
            manager.setMaxTotal(2000);
            // 每个路由最大连接数
            manager.setDefaultMaxPerRoute(2000);
            // 从池子中获取连接时，连接不活跃多长时间需要进行一次验证(Keep-Alive再次请求服务器)
            manager.setValidateAfterInactivity(5 * 1000);

            // 默认请求配置
            RequestConfig defaultRequestConfig = RequestConfig.custom()
            // 连接超时时间
                .setConnectTimeout(5 * 1000)
                // 等待数据读取(SocketReadTimeOut)时间
                .setSocketTimeout(10 * 1000)
                // 从池子中获取连接等待超时时间
                .setConnectionRequestTimeout(3000).build();

            httpClient = HttpClients
            // 自定义创建
                .custom()
                // 自定义连接池——PoolingManager
                .setConnectionManager(manager)
                // 不共享连接池(决定是否共用空闲回收)
                .setConnectionManagerShared(false)
                // 60秒一次回收空闲连接
                .evictIdleConnections(60, TimeUnit.SECONDS)
                // 开启定期回收(HttpClientBuild创建后台线程定期回收空闲)
                .evictExpiredConnections()
                // 连接存活时间，如果不设置，根据长连接信息决定
                .setConnectionTimeToLive(60, TimeUnit.SECONDS)
                // 默认请求配置
                .setDefaultRequestConfig(defaultRequestConfig)
                // 连接重用策略
                .setConnectionReuseStrategy(DefaultConnectionReuseStrategy.INSTANCE)
                // 长连接配置，长连接生存时间(这里使用默认，可以自己实现接口`ConnectionKeepAliveStrategy`)
                .setKeepAliveStrategy(DefaultConnectionKeepAliveStrategy.INSTANCE)
                // 重试次数，可以自定义是否发起重试
                .setRetryHandler(new DefaultHttpRequestRetryHandler(0, false)).build();

            // 增加JVM钩子，优雅关闭连接池释放连接
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    httpClient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }));

        }
        return httpClient;
    }

    public static HttpEntity getImagesDownload(String imageSrc) {
        CloseableHttpClient httpClient = getHttpClient();

        HttpGet hGet = new HttpGet(imageSrc);

        // 设置请求头
        Header[] headers = RequestHeaderBuilder.custom()
        // accept
            .accept(DefaultHeaders.ACCEPT)
            // 可接受编码集
            .acceptEncoding(DefaultHeaders.ACCEPT_ENCODING)
            // 可接受语言
            .acceptLanguage(DefaultHeaders.ACCEPT_LANGUAGE)
            // 添加连接类型
            //            .connection(DefaultHeaders.KEEP_ALIVE)
            // 增加content-type
            .contentType(DefaultHeaders.CONTENT_TYPE)
            // Cookie
            //            .cookie(DefaultHeaders.COOKIE)
            // 增加主机地址
            //            .host(DefaultHeaders.HOST)
            // 请求来源
            //            .referer(DefaultHeaders.REFERER)
            // 用户代理
            .userAgent(DefaultHeaders.USER_AGENT).build();

        hGet.setHeaders(headers);

        HttpEntity entity = null;
        try {
            //执行请求
            HttpResponse response = httpClient.execute(hGet);
            entity = response.getEntity();
        } catch (SocketTimeoutException e) {
            System.out.println("pooling连接池发生套接字读取错误，ex=" + e.getMessage());
        } catch (IOException e) {
            System.out.println("pooling连接池发生套接字IO读取错误，ex=" + e.getMessage());
        } catch (Exception e) {
            System.out.println("pooling连接池发生系统错误，ex=" + e.getMessage());
        }

        return entity;
    }

    public static String doHttpGet(String url) {
        String resp = "";

        CloseableHttpClient httpClient = getHttpClient();

        //        HttpGet hGet = new HttpGet(
        //            "http://instasset-zth-2.gz00b.dev.alipay.net/resultcode/query.json?_input_charset=UTF-8&ctoken=bK1E6Uba3BGcrNb-");

        HttpGet hGet = new HttpGet(url);

        // 设置请求头
        Header[] headers = RequestHeaderBuilder.custom()
        // accept
            .accept(DefaultHeaders.ACCEPT)
            // 可接受编码集
            .acceptEncoding(DefaultHeaders.ACCEPT_ENCODING)
            // 可接受语言
            .acceptLanguage(DefaultHeaders.ACCEPT_LANGUAGE)
            // 添加连接类型
            //            .connection(DefaultHeaders.KEEP_ALIVE)
            // 增加content-type
            .contentType(DefaultHeaders.CONTENT_TYPE)
            // Cookie
            //            .cookie(DefaultHeaders.COOKIE)
            // 增加主机地址
            //            .host(DefaultHeaders.HOST)
            // 请求来源
            //            .referer(DefaultHeaders.REFERER)
            // 用户代理
            .userAgent(DefaultHeaders.USER_AGENT).build();

        hGet.setHeaders(headers);

        try {
            //执行请求
            HttpResponse response = httpClient.execute(hGet);

            HttpEntity entity = response.getEntity();
            entity.getContent();

            resp = EntityUtils.toString(response.getEntity(), Charset.forName(CHARSET));
            System.out.println(resp);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return resp;
    }

    public static void main(String[] args) {
        // 做get请求
        String getUrl = "http://10.8.160.227:21237/v/api/user/credit_customers/?star_ids=12390380912472,123149780&timestamp=1541830265&nonce=7434030276&token=791886dd15dffb56b5d60070a475259018a363a3";
        doHttpGet(getUrl);

        // 做post请求
        String host = "https://xgk.microyan.com";
        String url = "https://xgk.microyan.com/api/course/2949/vote";
        String refer = "https://xgk.microyan.com/wx/pay/article?articleId=2949";
        String origin = "https://xgk.microyan.com";
        String userAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36";
        String cookie = "CNZZDATA1260640839=1047289734-1484787657-%7C1484787657; JSESSIONID=c5074d84-eb06-4e49-884d-6973469b9495; spring-rm=MTUwMjEyMzc1NTE6MTU0NTU1Mjc3MTAyNzoxNGY5OTAxYWM5N2Q1ZDljNTZiYWFjYmE4ODA4NmZmMw";

        CloseableHttpClient httpClient = getHttpClient();
        HttpPost httpPost = new HttpPost(url);

        // 设置请求头
        Header[] headers = RequestHeaderBuilder.custom()
        // accept
            .accept(DefaultHeaders.ACCEPT)
            // 可接受编码集
            .acceptEncoding(DefaultHeaders.ACCEPT_ENCODING)
            // 可接受语言
            .acceptLanguage(DefaultHeaders.ACCEPT_LANGUAGE)
            // 添加连接类型
            //                .connection(DefaultHeaders.KEEP_ALIVE)
            // 增加content-type
            //                .contentType(DefaultHeaders.CONTENT_TYPE)
            // Cookie
            .cookie(cookie)
            // 增加主机地址
            .host(host)
            // 源头
            .origin(origin)
            // 请求来源
            .referer(refer)
            // 用户代理
            .userAgent(userAgent).build();

        httpPost.setHeaders(headers);

        HttpResponse response = null;
        try {
            response = httpClient.execute(httpPost);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 检验返回码
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode != HttpStatus.SC_OK) {
            System.out.println("请求出错");
        }

        System.out.println(response);

    }

}