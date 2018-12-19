/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.shinnlove.wechatpay.sdkplus;

import java.util.HashMap;
import java.util.Map;

import com.shinnlove.wechatpay.sdkplus.config.WXPayMchConfig;
import com.shinnlove.wechatpay.sdkplus.consts.WXPayConstants;
import com.shinnlove.wechatpay.sdkplus.service.request.order.UnifiedOrderClient;

/**
 * 新微信支付SDK测试。
 *
 * @author shinnlove.jinsheng
 * @version $Id: SDKPlusTest.java, v 0.1 2018-12-19 下午3:43 shinnlove.jinsheng Exp $$
 */
public class SDKPlusTest {

    public static void main(String[] args) throws Exception {
        // 微信支付配置领域对象，一般读DB
        WXPayMchConfig config = new WXPayMchConfig();

        // 统一支付下单客户端
        UnifiedOrderClient client = new UnifiedOrderClient(config);

        // 支付入参
        Map<String, String> payParams = new HashMap<>();

        // 支付请求
        client.doPayRequest(payParams, (resp) -> {
            String result_code = resp.get(WXPayConstants.RESULT_CODE);
            if (result_code.equals(WXPayConstants.SUCCESS)) {
                // biz成功回调

            } else {
                // biz失败收尾

            }
        });
    }

}