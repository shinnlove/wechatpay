/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.shinnlove.wechatpay.sdkplus.service.paymode.strategy.impl;

import com.shinnlove.wechatpay.sdkplus.service.paymode.strategy.WXPayModeStrategy;

/**
 * 普通商户模式支付策略。
 *
 * @author shinnlove.jinsheng
 * @version $Id: OrdinaryPayModeStrategy.java, v 0.1 2018-12-18 下午5:03 shinnlove.jinsheng Exp $$
 */
public class OrdinaryPayModeStrategy implements WXPayModeStrategy {

    @Override
    public String createXml() {
        return null;
    }

}