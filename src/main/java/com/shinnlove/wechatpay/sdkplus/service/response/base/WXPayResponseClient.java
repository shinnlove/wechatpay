/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.shinnlove.wechatpay.sdkplus.service.response.base;

import java.util.Map;

import com.shinnlove.wechatpay.sdkplus.config.WXPayMchConfig;
import com.shinnlove.wechatpay.sdkplus.service.client.AbstractWXPayClient;

/**
 * 微信支付通知响应类客户端。
 *
 * @author shinnlove.jinsheng
 * @version $Id: WXPayResponseClient.java, v 0.1 2018-12-18 下午5:20 shinnlove.jinsheng Exp $$
 */
public abstract class WXPayResponseClient extends AbstractWXPayClient {

    /** 微信支付结果XML报文 */
    protected String              payResponse;

    /** 微信支付结果Map */
    protected Map<String, String> payResult;

    /** 平台响应微信支付结果 */
    protected Map<String, String> answerResponse;

    /**
     * 构造函数。
     *
     * @param wxPayMchConfig
     */
    public WXPayResponseClient(WXPayMchConfig wxPayMchConfig) {
        super(wxPayMchConfig);
    }

    /**
     * 通知响应类接口必须进行签名验签。
     */
    public void checkSign() {

    }

    /**
     * 解析原始字符串变成Map对象。
     */
    public void resolveAndSave() {

    }

}