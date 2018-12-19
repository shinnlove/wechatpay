/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.shinnlove.wechatpay.sdkplus.service.request.bill;

import java.util.Map;

import com.shinnlove.wechatpay.sdkplus.config.WXPayMchConfig;
import com.shinnlove.wechatpay.sdkplus.consts.WXPayConstants;
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
        this.requestURL = WXPayConstants.HTTPS + WXPayConstants.DOMAIN_API
                          + WXPayConstants.DOWNLOADBILL_URL_SUFFIX;
    }

    @Override
    public void checkParameters(Map<String, String> keyPairs) throws Exception {
        // 公共校验
        if (keyPairs == null || keyPairs.size() == 0) {
            // 这里引入spring的jar替换成CollectionUtils.isEmpty()方法!RuntimeException改成具体的Exception
            throw new Exception("对账单下载入参不能为空");
        }

        // 微信对账单必填字段
        if (!keyPairs.containsKey(WXPayConstants.BILL_DATE)) {
            throw new Exception("对账单接口对账日期不能为空");
        }
    }

    @Override
    public void fillRequestDetailParams(Map<String, String> keyPairs) {
        // 对账单需要的参数
        // 对账日期
        payParameters.put(WXPayConstants.BILL_DATE, keyPairs.get(WXPayConstants.BILL_DATE));
    }

}