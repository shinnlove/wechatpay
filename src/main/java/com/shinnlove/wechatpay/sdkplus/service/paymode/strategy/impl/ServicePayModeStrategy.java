/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.shinnlove.wechatpay.sdkplus.service.paymode.strategy.impl;

import com.shinnlove.wechatpay.sdkplus.service.paymode.strategy.WXPayModeStrategy;

/**
 * 服务商模式支付策略。
 *
 * @author shinnlove.jinsheng
 * @version $Id: ServicePayModeStrategy.java, v 0.1 2018-12-18 下午5:03 shinnlove.jinsheng Exp $$
 */
public class ServicePayModeStrategy implements WXPayModeStrategy {

    @Override
    public String createXml() {
        // 记得多塞子商户的信息
        return null;
    }

}