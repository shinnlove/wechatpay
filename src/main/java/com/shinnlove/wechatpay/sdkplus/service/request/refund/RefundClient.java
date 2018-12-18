/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.shinnlove.wechatpay.sdkplus.service.request.refund;

import com.shinnlove.wechatpay.sdkplus.config.WXPayMchConfig;
import com.shinnlove.wechatpay.sdkplus.service.request.base.WXPayRequestClient;

/**
 * 微信退款客户端。
 *
 * @author shinnlove.jinsheng
 * @version $Id: RefundClient.java, v 0.1 2018-12-18 下午5:34 shinnlove.jinsheng Exp $$
 */
public class RefundClient extends WXPayRequestClient {

    /**
     * 构造函数。
     *
     * @param wxPayMchConfig
     */
    public RefundClient(WXPayMchConfig wxPayMchConfig) {
        super(wxPayMchConfig);
    }

}