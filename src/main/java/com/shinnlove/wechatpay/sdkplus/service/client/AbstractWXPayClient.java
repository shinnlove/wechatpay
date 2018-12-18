/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.shinnlove.wechatpay.sdkplus.service.client;

import java.util.Map;

import com.shinnlove.wechatpay.sdkplus.config.WXPayMchConfig;
import com.shinnlove.wechatpay.sdkplus.service.paymode.context.WXPayModeContext;

/**
 * 微信支付抽象公共类。
 *
 * 这个类会派生出两大类：主动请求类和被动通知类。这个类不持有通信结果的私有数据！
 * 其中主动请求类持有请求所需的数据和请求结果；被动通知类有原始的微信通知和解析后数据以及回应数据。
 *
 * @author shinnlove.jinsheng
 * @version $Id: AbstractWXPayClient.java, v 0.1 2018-12-18 下午4:21 shinnlove.jinsheng Exp $$
 */
public abstract class AbstractWXPayClient implements WXPayClient {

    /** 微信支付全局配置 */
    protected final WXPayMchConfig wxPayMchConfig;

    /** 微信支付模式（0为普通商户模式、1为服务商模式） */
    protected WXPayModeContext     wxPayModeContext;

    /**
     * 必定要调用的构造函数。
     *
     * @param wxPayMchConfig
     */
    public AbstractWXPayClient(WXPayMchConfig wxPayMchConfig) {
        this.wxPayMchConfig = wxPayMchConfig;
        this.wxPayModeContext = new WXPayModeContext(wxPayMchConfig.getPayMode());
    }

    @Override
    public void setParameter(String key, String value) {

    }

    @Override
    public void setParameters(Map<String, String> keyPairs) {

    }

    @Override
    public Map<String, String> getParameters() {
        return null;
    }

    @Override
    public String createXml() {
        // 使用上下文策略：创建通信XML
        return wxPayModeContext.createXml();
    }

}