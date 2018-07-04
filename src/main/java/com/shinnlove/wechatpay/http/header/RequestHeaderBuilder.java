/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.shinnlove.wechatpay.http.header;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;

import com.shinnlove.wechatpay.http.consts.HttpRequestHeaderConst;

/**
 * Http请求头。
 *
 * @author shinnlove.jinsheng
 * @version $Id: RequestHeaderBuilder.java, v 0.1 2018-07-04 下午2:29 shinnlove.jinsheng Exp $$
 */
public class RequestHeaderBuilder {

    /** 保存请求头内容的Map */
    private Map<String, Header> headerMap = new HashMap<>();

    /**
     * 禁用构造函数。
     */
    private RequestHeaderBuilder() {

    }

    /**
     * 每次调用custom()函数都返回一个新的Map。
     *
     * @return
     */
    public static RequestHeaderBuilder custom() {
        return new RequestHeaderBuilder();
    }

    /**
     * 添加请求referer。
     *
     * @param referer
     * @return
     */
    public RequestHeaderBuilder referer(String referer) {
        return addHeader(HttpRequestHeaderConst.REFERER, referer);
    }

    /**
     * 添加cookie。
     *
     * @param cookie
     * @return
     */
    public RequestHeaderBuilder cookie(String cookie) {
        return addHeader(HttpRequestHeaderConst.COOKIE, cookie);
    }

    /**
     * 添加用户请求头。
     *
     * @param userAgent
     * @return
     */
    public RequestHeaderBuilder userAgent(String userAgent) {
        return addHeader(HttpRequestHeaderConst.USER_AGENT, userAgent);
    }

    /**
     * 将每一个Header添加到Map映射中。
     *
     * @param key
     * @param value
     * @return
     */
    private RequestHeaderBuilder addHeader(String key, String value) {
        headerMap.put(key, new BasicHeader(key, value));
        return this;
    }

    /**
     * 最后组装所有Header成Header[]数组的函数。
     *
     * @return
     */
    public Header[] build() {
        int len = headerMap.size();
        Header[] headers = new Header[len];
        int i = 0;
        for (Map.Entry<String, Header> entry : headerMap.entrySet()) {
            headers[i++] = entry.getValue();
        }
        headerMap.clear();
        headerMap = null;
        return headers;
    }

}