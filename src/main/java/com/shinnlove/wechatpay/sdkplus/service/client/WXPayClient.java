/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.shinnlove.wechatpay.sdkplus.service.client;

import java.util.Map;

/**
 * 微信支付基类。
 *
 * @author shinnlove.jinsheng
 * @version $Id: WXPayClient.java, v 0.1 2018-12-18 下午4:06 shinnlove.jinsheng Exp $$
 */
public interface WXPayClient {

    String createXml();

    String postXml();

    void setParameter(String key, String value);

    void setParameters(Map<String, String> keyPairs);

    Map<String, String> getParameters();

}
