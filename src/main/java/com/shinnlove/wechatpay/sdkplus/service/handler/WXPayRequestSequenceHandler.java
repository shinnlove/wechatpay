/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.shinnlove.wechatpay.sdkplus.service.handler;

import java.util.Map;

/**
 * 微信支付请求流程接口，本接口负责对外透出SDK的支付能力。
 *
 * @author shinnlove.jinsheng
 * @version $Id: WXPayRequestSequenceHandler.java, v 0.1 2018-12-19 上午10:49 shinnlove.jinsheng Exp $$
 */
public interface WXPayRequestSequenceHandler {

    /**
     * 做微信支付的对外流程。
     *
     * 请求链...模型转换...
     *
     * Step1：...
     *
     * @param keyPairs  请求入参。
     * @throws Exception
     */
    void doPayRequest(Map<String, String> keyPairs) throws Exception;

}
