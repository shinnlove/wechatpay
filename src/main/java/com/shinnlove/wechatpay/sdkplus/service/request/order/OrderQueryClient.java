/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.shinnlove.wechatpay.sdkplus.service.request.order;

import com.shinnlove.wechatpay.sdk.consts.WXPayConstants;
import com.shinnlove.wechatpay.sdkplus.config.WXPayMchConfig;
import com.shinnlove.wechatpay.sdkplus.service.request.base.WXPayRequestClient;

/**
 * 订单查询客户端。
 *
 * @author shinnlove.jinsheng
 * @version $Id: OrderQueryClient.java, v 0.1 2018-12-18 下午5:16 shinnlove.jinsheng Exp $$
 */
public class OrderQueryClient extends WXPayRequestClient {

    /**
     * 构造函数。
     *
     * @param wxPayMchConfig
     */
    public OrderQueryClient(WXPayMchConfig wxPayMchConfig) {
        super(wxPayMchConfig);
        this.requestURL = WXPayConstants.HTTPS + WXPayConstants.DOMAIN_API
                          + WXPayConstants.ORDERQUERY_URL_SUFFIX;
    }

}