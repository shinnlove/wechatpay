/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.shinnlove.wechatpay.sdk.pay;

import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.security.KeyStore;
import java.security.SecureRandom;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.DefaultHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;

import com.shinnlove.wechatpay.sdk.config.WXPayConfig;
import com.shinnlove.wechatpay.sdk.domain.IWXPayDomain;
import com.shinnlove.wechatpay.sdk.report.WXPayReport;
import com.shinnlove.wechatpay.sdk.utils.WXPayUtil;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;

/**
 * 微信支付请求对象。
 *
 * @author shinnlove.jinsheng
 * @version $Id: WXPayRequest.java, v 0.1 2018-07-08 上午10:36 shinnlove.jinsheng Exp $$
 */
public class WXPayRequest {

    /** 微信支付配置 */
    private WXPayConfig config;

    /**
     * 构造中传入配置。
     *
     * @param config
     * @throws Exception
     */
    public WXPayRequest(WXPayConfig config) throws Exception {
        this.config = config;
    }

    /**
     * 请求，只请求一次，不做重试。
     *
     * @param domain
     * @param urlSuffix
     * @param uuid
     * @param data
     * @param connectTimeoutMs
     * @param readTimeoutMs
     * @param useCert 是否使用证书，针对退款、撤销等操作
     * @return
     * @throws Exception
     */
    private String requestOnce(final String domain, String urlSuffix, String uuid, String data,
                               int connectTimeoutMs, int readTimeoutMs, boolean useCert)
                                                                                        throws Exception {
        BasicHttpClientConnectionManager connManager;
        if (useCert) {

            // 证书
            char[] password = config.getMchID().toCharArray();
            InputStream certStream = config.getCertStream();
            KeyStore ks = KeyStore.getInstance("PKCS12");
            ks.load(certStream, password);

            // 实例化密钥库 & 初始化密钥工厂
            // `KeyManagerFactory`->`KeyStore`->init
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory
                .getDefaultAlgorithm());
            kmf.init(ks, password);

            // 创建 SSLContext
            // `SSLContext`->`KeyManagerFactory`、`trustManagers`、`SecureRandom`->init
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(kmf.getKeyManagers(), null, new SecureRandom());

            // 带证书的`SSLConnectionSocketFactory`(第二个形参是协议supportedProtocols，注意与`SSLContext.getInstance("TLS")`呼应)
            // `SSLConnectionSocketFactory`->`SSLContext`
            SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(
                sslContext, new String[] { "TLSv1" }, null, new DefaultHostnameVerifier());

            // 基础连接池(一次只管理一个连接)
            // 特别注意：`ConnectionManager`不同协议的`SocketFactory`可以在构造中注入，也可以用`register()`函数注册
            connManager = new BasicHttpClientConnectionManager(RegistryBuilder
                .<ConnectionSocketFactory> create()
                // `PlainConnectionSocketFactory`是默认的创建、初始化明文socket（不加密）的工厂类
                .register("http", PlainConnectionSocketFactory.getSocketFactory())
                .register("https", sslConnectionSocketFactory).build(), null, null, null);

        } else {

            // http->`PlainConnectionSocketFactory.INSTANCE`等于`PlainConnectionSocketFactory.getSocketFactory()`
            // https->`SSLConnectionSocketFactory.getSocketFactory()`返回一个默认的SocketFactory
            connManager = new BasicHttpClientConnectionManager(RegistryBuilder
                .<ConnectionSocketFactory> create()
                .register("http", PlainConnectionSocketFactory.getSocketFactory())
                .register("https", SSLConnectionSocketFactory.getSocketFactory()).build(), null,
                null, null);
        }

        // 被`BasicHttpClientConnectionManager`管理的`HttpClient`在线程中创造，如果被多个线程共同调用，占用的时候就会抛出`IllegalStateException`
        // 在这里属于一个可栈上分配的局部变量
        HttpClient httpClient = HttpClientBuilder.create().setConnectionManager(connManager)
            .build();

        String url = "https://" + domain + urlSuffix;
        HttpPost httpPost = new HttpPost(url);

        // 使用`HttpClients.custom().setDefaultRequestConfig()`可以设置，不需要塞到
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(readTimeoutMs)
            .setConnectTimeout(connectTimeoutMs).build();
        httpPost.setConfig(requestConfig);

        // 将xml转成字符串，创建了一个字符串实体放入post中
        StringEntity postEntity = new StringEntity(data, "UTF-8");
        httpPost.addHeader("Content-Type", "text/xml");
        httpPost.addHeader("User-Agent", "wxpay sdk java v1.0 " + config.getMchID()); // TODO: 很重要，用来检测 sdk 的使用情况，要不要加上商户信息？
        httpPost.setEntity(postEntity);

        HttpResponse httpResponse = httpClient.execute(httpPost);
        HttpEntity httpEntity = httpResponse.getEntity();
        return EntityUtils.toString(httpEntity, "UTF-8");
    }

    /**
     * 调用requestOnce，拦截错误、上报结果的。
     *
     * @param urlSuffix
     * @param uuid
     * @param data
     * @param connectTimeoutMs
     * @param readTimeoutMs
     * @param useCert
     * @param autoReport
     * @return
     * @throws Exception
     */
    private String request(String urlSuffix, String uuid, String data, int connectTimeoutMs,
                           int readTimeoutMs, boolean useCert, boolean autoReport) throws Exception {
        Exception exception = null;
        long elapsedTimeMillis = 0;
        long startTimestampMs = WXPayUtil.getCurrentTimestampMs();
        boolean firstHasDnsErr = false;
        boolean firstHasConnectTimeout = false;
        boolean firstHasReadTimeout = false;
        IWXPayDomain.DomainInfo domainInfo = config.getWXPayDomain().getDomain(config);
        if (domainInfo == null) {
            throw new Exception("WXPayConfig.getWXPayDomain().getDomain() is empty or null");
        }
        try {
            String result = requestOnce(domainInfo.domain, urlSuffix, uuid, data, connectTimeoutMs,
                readTimeoutMs, useCert);
            elapsedTimeMillis = WXPayUtil.getCurrentTimestampMs() - startTimestampMs;
            config.getWXPayDomain().report(domainInfo.domain, elapsedTimeMillis, null);
            WXPayReport.getInstance(config).report(uuid, elapsedTimeMillis, domainInfo.domain,
                domainInfo.primaryDomain, connectTimeoutMs, readTimeoutMs, firstHasDnsErr,
                firstHasConnectTimeout, firstHasReadTimeout);
            return result;
        } catch (UnknownHostException ex) { // dns 解析错误，或域名不存在
            exception = ex;
            firstHasDnsErr = true;
            elapsedTimeMillis = WXPayUtil.getCurrentTimestampMs() - startTimestampMs;
            WXPayUtil.getLogger().warn("UnknownHostException for domainInfo {}", domainInfo);
            WXPayReport.getInstance(config).report(uuid, elapsedTimeMillis, domainInfo.domain,
                domainInfo.primaryDomain, connectTimeoutMs, readTimeoutMs, firstHasDnsErr,
                firstHasConnectTimeout, firstHasReadTimeout);
        } catch (ConnectTimeoutException ex) {
            exception = ex;
            firstHasConnectTimeout = true;
            elapsedTimeMillis = WXPayUtil.getCurrentTimestampMs() - startTimestampMs;
            WXPayUtil.getLogger().warn("connect timeout happened for domainInfo {}", domainInfo);
            WXPayReport.getInstance(config).report(uuid, elapsedTimeMillis, domainInfo.domain,
                domainInfo.primaryDomain, connectTimeoutMs, readTimeoutMs, firstHasDnsErr,
                firstHasConnectTimeout, firstHasReadTimeout);
        } catch (SocketTimeoutException ex) {
            exception = ex;
            firstHasReadTimeout = true;
            elapsedTimeMillis = WXPayUtil.getCurrentTimestampMs() - startTimestampMs;
            WXPayUtil.getLogger().warn("timeout happened for domainInfo {}", domainInfo);
            WXPayReport.getInstance(config).report(uuid, elapsedTimeMillis, domainInfo.domain,
                domainInfo.primaryDomain, connectTimeoutMs, readTimeoutMs, firstHasDnsErr,
                firstHasConnectTimeout, firstHasReadTimeout);
        } catch (Exception ex) {
            exception = ex;
            elapsedTimeMillis = WXPayUtil.getCurrentTimestampMs() - startTimestampMs;
            WXPayReport.getInstance(config).report(uuid, elapsedTimeMillis, domainInfo.domain,
                domainInfo.primaryDomain, connectTimeoutMs, readTimeoutMs, firstHasDnsErr,
                firstHasConnectTimeout, firstHasReadTimeout);
        }
        // 上报超时、错误数
        config.getWXPayDomain().report(domainInfo.domain, elapsedTimeMillis, exception);
        throw exception;
    }

    /**
     * 可重试的，非双向认证的请求
     * @param urlSuffix
     * @param uuid
     * @param data
     * @return
     */
    public String requestWithoutCert(String urlSuffix, String uuid, String data, boolean autoReport)
                                                                                                    throws Exception {
        return this.request(urlSuffix, uuid, data, config.getHttpConnectTimeoutMs(),
            config.getHttpReadTimeoutMs(), false, autoReport);
        //return requestWithoutCert(urlSuffix, uuid, data, config.getHttpConnectTimeoutMs(), config.getHttpReadTimeoutMs(), autoReport);
    }

    /**
     * 可重试的，非双向认证的请求
     * @param urlSuffix
     * @param uuid
     * @param data
     * @param connectTimeoutMs
     * @param readTimeoutMs
     * @return
     */
    public String requestWithoutCert(String urlSuffix, String uuid, String data,
                                     int connectTimeoutMs, int readTimeoutMs, boolean autoReport)
                                                                                                 throws Exception {
        return this.request(urlSuffix, uuid, data, connectTimeoutMs, readTimeoutMs, false,
            autoReport);

        /*
        String result;
        Exception exception;
        boolean shouldRetry = false;

        boolean useCert = false;
        try {
            result = requestOnce(domain, urlSuffix, uuid, data, connectTimeoutMs, readTimeoutMs, useCert);
            return result;
        }
        catch (UnknownHostException ex) {  // dns 解析错误，或域名不存在
            exception = ex;
            WXPayUtil.getLogger().warn("UnknownHostException for domain {}, try to use {}", domain, this.primaryDomain);
            shouldRetry = true;
        }
        catch (ConnectTimeoutException ex) {
            exception = ex;
            WXPayUtil.getLogger().warn("connect timeout happened for domain {}, try to use {}", domain, this.primaryDomain);
            shouldRetry = true;
        }
        catch (SocketTimeoutException ex) {
            exception = ex;
            shouldRetry = false;
        }
        catch (Exception ex) {
            exception = ex;
            shouldRetry = false;
        }

        if (shouldRetry) {
            result = requestOnce(this.primaryDomain, urlSuffix, uuid, data, connectTimeoutMs, readTimeoutMs, useCert);
            return result;
        }
        else {
            throw exception;
        }
        */
    }

    /**
     * 可重试的，双向认证的请求
     * @param urlSuffix
     * @param uuid
     * @param data
     * @return
     */
    public String requestWithCert(String urlSuffix, String uuid, String data, boolean autoReport)
                                                                                                 throws Exception {
        return this.request(urlSuffix, uuid, data, config.getHttpConnectTimeoutMs(),
            config.getHttpReadTimeoutMs(), true, autoReport);
        //return requestWithCert(urlSuffix, uuid, data, config.getHttpConnectTimeoutMs(), config.getHttpReadTimeoutMs(), autoReport);
    }

    /**
     * 可重试的，双向认证的请求
     * @param urlSuffix
     * @param uuid
     * @param data
     * @param connectTimeoutMs
     * @param readTimeoutMs
     * @return
     */
    public String requestWithCert(String urlSuffix, String uuid, String data, int connectTimeoutMs,
                                  int readTimeoutMs, boolean autoReport) throws Exception {
        return this.request(urlSuffix, uuid, data, connectTimeoutMs, readTimeoutMs, true,
            autoReport);

        /*
        String result;
        Exception exception;
        boolean shouldRetry = false;

        boolean useCert = true;
        try {
            result = requestOnce(domain, urlSuffix, uuid, data, connectTimeoutMs, readTimeoutMs, useCert);
            return result;
        }
        catch (ConnectTimeoutException ex) {
            exception = ex;
            WXPayUtil.getLogger().warn(String.format("connect timeout happened for domain {}, try to use {}", domain, this.primaryDomain));
            shouldRetry = true;
        }
        catch (SocketTimeoutException ex) {
            exception = ex;
            shouldRetry = false;
        }
        catch (Exception ex) {
            exception = ex;
            shouldRetry = false;
        }

        if (shouldRetry && this.primaryDomain != null) {
            result = requestOnce(this.primaryDomain, urlSuffix, uuid, data, connectTimeoutMs, readTimeoutMs, useCert, autoReport);
            return result;
        }
        else {
            throw exception;
        }
        */
    }

}