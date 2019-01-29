/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.shinnlove.wechatpay.sdkplus.service.response.scan;

import com.shinnlove.wechatpay.sdkplus.config.WXPayMchConfig;
import com.shinnlove.wechatpay.sdkplus.service.client.AbstractWXPayClient;

/**
 * 微信原生扫码支付响应类(接收商品id下单并回应)。
 *
 * @author shinnlove.jinsheng
 * @version $Id: WXNativePayClient.java, v 0.1 2018-12-18 下午5:29 shinnlove.jinsheng Exp $$
 */
public class WXNativePayClient extends AbstractWXPayClient {

    /**
     * 构造函数。
     *
     * @param wxPayMchConfig
     */
    public WXNativePayClient(WXPayMchConfig wxPayMchConfig) {
        super(wxPayMchConfig);
    }

    //    @Override
    //    public void doBizSuccess(Map<String, String> responseData) throws Exception {
    //        // 原生扫码业务成功
    //    }
    //
    //    @Override
    //    public void doBizFail(Map<String, String> responseData) throws Exception {
    //        // 原生扫码业务失败
    //    }

}