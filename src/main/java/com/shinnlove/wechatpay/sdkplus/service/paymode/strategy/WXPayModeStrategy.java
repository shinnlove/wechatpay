/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.shinnlove.wechatpay.sdkplus.service.paymode.strategy;

/**
 * 微信支付策略模式，针对普通商户模式和服务商模式的不同所做的策略。
 *
 * @author shinnlove.jinsheng
 * @version $Id: WXPayModeStrategy.java, v 0.1 2018-12-18 下午4:43 shinnlove.jinsheng Exp $$
 */
public interface WXPayModeStrategy {

    /**
     * 根据不同的微信支付模式创建通信XML。
     *
     * @return
     */
    String createXml();

}
