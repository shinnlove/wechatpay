/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.shinnlove.wechatpay.sdkplus.service.handler;

/**
 * 微信支付——请求执行接口。
 *
 * @author shinnlove.jinsheng
 * @version $Id: WXPayRequestExecuteHandler.java, v 0.1 2018-12-19 上午10:40 shinnlove.jinsheng Exp $$
 */
public interface WXPayRequestExecuteHandler {

    /**
     * 请求是否需要使用证书：给各个微信支付请求客户端决定请求是否需要使用证书通信。
     *
     * 如：对退款申请需要使用证书双向认证，则RefundClient需要实现此接口并返回true。
     *
     * @return
     */
    boolean requestNeedCert();

    /**
     * 执行微信支付不同类型请求，返回HttpResponse流字符串（暂定）。
     *
     * @return
     */
    String executePayRequest();

}
