/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.shinnlove.wechatpay.sdkplus.service.request.order;

import java.util.Map;

import com.shinnlove.wechatpay.sdkplus.config.WXPayMchConfig;
import com.shinnlove.wechatpay.sdkplus.consts.WXPayConstants;
import com.shinnlove.wechatpay.sdkplus.service.request.base.WXPayRequestClient;

/**
 * 微信支付-订单查询客户端。
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

    @Override
    public void checkParameters(Map<String, String> keyPairs) throws Exception {
        // 做订单查询的校验参数
        if (keyPairs == null || keyPairs.size() == 0) {
            // 这里引入spring的jar替换成CollectionUtils.isEmpty()方法!RuntimeException改成具体的Exception
            throw new Exception("订单查询入参不能为空");
        }
        // 订单号至少有一个不能为空
        if (!keyPairs.containsKey(WXPayConstants.OUT_TRADE_NO)
            && !keyPairs.containsKey(WXPayConstants.TRANSACTION_ID)) {
            throw new Exception("订单查询接口out_trade_no和transaction_id至少填一个");
        }
    }

    @Override
    public void fillRequestDetailParams(Map<String, String> keyPairs) {
        // 订单查询需要的参数
        if (keyPairs.containsKey(WXPayConstants.OUT_TRADE_NO)) {
            // 商户侧订单号
            payParameters.put(WXPayConstants.OUT_TRADE_NO,
                keyPairs.get(WXPayConstants.OUT_TRADE_NO));
        }
        if (keyPairs.containsKey(WXPayConstants.TRANSACTION_ID)) {
            // 微信侧订单号
            payParameters.put(WXPayConstants.TRANSACTION_ID,
                keyPairs.get(WXPayConstants.TRANSACTION_ID));
        }
    }

}