/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.shinnlove.wechatpay.sdkplus.http;

/**
 * 执行微信支付网络请求服务。
 *
 * 可以考虑配置成一个spring服务。
 *
 * @author shinnlove.jinsheng
 * @version $Id: WXPayRequestExecutor.java, v 0.1 2018-12-19 下午1:55 shinnlove.jinsheng Exp $$
 */
public class WXPayRequestExecutor {

    /** 请求超时时间 */
    protected int request_timeout;

    /**
     * 执行https请求。
     *
     * @param url           请求地址
     * @param requestBody   请求体内容
     * @param needCert      需要证书
     * @return
     */
    public String doRequest(String url, String requestBody, boolean needCert) {
        return null;
    }

    /**
     * Getter method for property request_timeout.
     *
     * @return property value of request_timeout
     */
    public int getRequest_timeout() {
        return request_timeout;
    }

    /**
     * Setter method for property request_timeout.
     *
     * @param request_timeout value to be assigned to property request_timeout
     */
    public void setRequest_timeout(int request_timeout) {
        this.request_timeout = request_timeout;
    }

}