/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.shinnlove.wechatpay.sdkplus.service.request.bill;

import com.shinnlove.wechatpay.sdkplus.config.WXPayMchConfig;
import com.shinnlove.wechatpay.sdkplus.service.request.base.WXPayRequestClient;

/**
 * 微信支付对账单客户端。
 *
 * @author shinnlove.jinsheng
 * @version $Id: DownloadBillClient.java, v 0.1 2018-12-18 下午5:35 shinnlove.jinsheng Exp $$
 */
public class DownloadBillClient extends WXPayRequestClient {

    /**
     * 构造函数。
     *
     * @param wxPayMchConfig
     */
    public DownloadBillClient(WXPayMchConfig wxPayMchConfig) {
        super(wxPayMchConfig);
    }

}