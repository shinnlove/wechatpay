/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.shinnlove.wechatpay.sdkplus.service.request.base;

import java.util.HashMap;
import java.util.Map;

import com.shinnlove.wechatpay.sdkplus.config.WXPayMchConfig;
import com.shinnlove.wechatpay.sdkplus.service.client.AbstractWXPayClient;

/**
 * 微信支付主动请求类。
 *
 * @author shinnlove.jinsheng
 * @version $Id: WXPayRequestClient.java, v 0.1 2018-12-18 下午4:13 shinnlove.jinsheng Exp $$
 */
public abstract class WXPayRequestClient extends AbstractWXPayClient {

    /** 各个主动请求的地址 */
    protected String              requestURL;

    /** 请求超时时间 */
    protected int                 request_timeout;

    /** 支付请求参数 */
    protected Map<String, String> payParameters = new HashMap<>();

    /** 支付响应结果：XML格式 */
    protected String              response;

    /** 支付响应结果KeyPairs */
    protected Map<String, String> payResult     = new HashMap<>();

    /**
     * 构造函数。
     *
     * @param wxPayMchConfig
     */
    public WXPayRequestClient(WXPayMchConfig wxPayMchConfig) {
        super(wxPayMchConfig);
    }

    /**
     * 请求类型必定要postXML
     *
     * @return
     */
    @Override
    public String postXml() {
        return null;
    }

    /**
     * Getter method for property requestURL.
     *
     * @return property value of requestURL
     */
    public String getRequestURL() {
        return requestURL;
    }

    /**
     * Setter method for property requestURL.
     *
     * @param requestURL value to be assigned to property requestURL
     */
    public void setRequestURL(String requestURL) {
        this.requestURL = requestURL;
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

    /**
     * Getter method for property payParameters.
     *
     * @return property value of payParameters
     */
    public Map<String, String> getPayParameters() {
        return payParameters;
    }

    /**
     * Setter method for property payParameters.
     *
     * @param payParameters value to be assigned to property payParameters
     */
    public void setPayParameters(Map<String, String> payParameters) {
        this.payParameters = payParameters;
    }

    /**
     * Getter method for property response.
     *
     * @return property value of response
     */
    public String getResponse() {
        return response;
    }

    /**
     * Setter method for property response.
     *
     * @param response value to be assigned to property response
     */
    public void setResponse(String response) {
        this.response = response;
    }

    /**
     * Getter method for property payResult.
     *
     * @return property value of payResult
     */
    public Map<String, String> getPayResult() {
        return payResult;
    }

    /**
     * Setter method for property payResult.
     *
     * @param payResult value to be assigned to property payResult
     */
    public void setPayResult(Map<String, String> payResult) {
        this.payResult = payResult;
    }

}