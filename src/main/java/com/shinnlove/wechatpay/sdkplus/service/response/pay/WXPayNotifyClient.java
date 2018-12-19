/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.shinnlove.wechatpay.sdkplus.service.response.pay;

import java.util.Map;

import com.shinnlove.wechatpay.sdkplus.config.WXPayMchConfig;
import com.shinnlove.wechatpay.sdkplus.service.client.AbstractWXPayClient;

/**
 * 微信支付结果通知客户端。
 *
 * @author shinnlove.jinsheng
 * @version $Id: WXPayNotifyClient.java, v 0.1 2018-12-18 下午5:28 shinnlove.jinsheng Exp $$
 */
public class WXPayNotifyClient extends AbstractWXPayClient {

    /**
     * 构造函数。
     *
     * @param wxPayMchConfig
     */
    public WXPayNotifyClient(WXPayMchConfig wxPayMchConfig) {
        super(wxPayMchConfig);
    }

    @Override
    public void doBizSuccess(Map<String, String> responseData) throws Exception {
        // 支付业务成功
    }

    @Override
    public void doBizFail(Map<String, String> responseData) throws Exception {
        // 支付业务失败
    }

}