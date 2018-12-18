/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.shinnlove.wechatpay.sdkplus.service.request.order;

import com.shinnlove.wechatpay.sdk.consts.WXPayConstants;
import com.shinnlove.wechatpay.sdkplus.config.WXPayMchConfig;
import com.shinnlove.wechatpay.sdkplus.service.request.base.WXPayRequestClient;

/**
 * 订单类客户端。
 *
 * @author shinnlove.jinsheng
 * @version $Id: UnifiedOrderClient.java, v 0.1 2018-12-18 下午4:27 shinnlove.jinsheng Exp $$
 */
public class UnifiedOrderClient extends WXPayRequestClient {

    /** 统一下单预支付id字段 */
    public final String PREPAY_ID = "prepay_id";

    /**
     * 构造函数。
     *
     * @param wxPayMchConfig 
     * @param url
     */
    public UnifiedOrderClient(WXPayMchConfig wxPayMchConfig, String url) {
        super(wxPayMchConfig);
        this.requestURL = WXPayConstants.HTTPS + WXPayConstants.DOMAIN_API
                          + WXPayConstants.UNIFIEDORDER_URL_SUFFIX;
    }

    /**
     * 获取支付id。
     *
     * @return
     */
    public String getPrepayId() {
        return payResult.get(PREPAY_ID);
    }

}