/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.shinnlove.wechatpay.http.diy;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
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

            // 注册访问协议相关的SocketFactory，支持http/https
            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder
                .<ConnectionSocketFactory> create()
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
            manager.setMaxTotal(300);
            // 每个路由最大连接数
            manager.setDefaultMaxPerRoute(200);
            // 从池子中获取连接时，连接不活跃多长时间需要进行一次验证(Keep-Alive再次请求服务器)
            manager.setValidateAfterInactivity(5 * 1000);

            // 默认请求配置
            RequestConfig defaultRequestConfig = RequestConfig.custom()
            // 连接超时时间
                .setConnectTimeout(2 * 1000)
                // 等待数据读取(SocketReadTimeOut)时间
                .setSocketTimeout(5 * 1000)
                // 从池子中获取连接等待超时时间
                .setConnectionRequestTimeout(2000).build();

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
                // 长连接配置，长连接生存时间
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

    public static void main(String[] args) {

        CloseableHttpClient httpClient = getHttpClient();

        HttpGet hGet = new HttpGet(
            "http://instasset-zth-2.gz00b.dev.alipay.net/resultcode/query.json?_input_charset=UTF-8&ctoken=bK1E6Uba3BGcrNb-");

        // 设置请求头
        Header[] headers = RequestHeaderBuilder.custom()
        // accept
            .accept(DefaultHeaders.ACCEPT)
            // 可接受编码集
            .acceptEncoding(DefaultHeaders.ACCEPT_ENCODING)
            // 可接受语言
            .acceptLanguage(DefaultHeaders.ACCEPT_LANGUAGE)
            // 添加连接类型
            .connection(DefaultHeaders.KEEP_ALIVE)
            // 增加content-type
            .contentType(DefaultHeaders.CONTENT_TYPE)
            // Cookie
            .cookie(DefaultHeaders.COOKIE)
            // 增加主机地址
            .host(DefaultHeaders.HOST)
            // 请求来源
            .referer(DefaultHeaders.REFERER)
            // 用户代理
            .userAgent(DefaultHeaders.USER_AGENT).build();

        hGet.setHeaders(headers);

        try {
            //执行请求
            HttpResponse response = httpClient.execute(hGet);
            String str = EntityUtils.toString(response.getEntity(), Charset.forName(CHARSET));
            System.out.println(str);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}