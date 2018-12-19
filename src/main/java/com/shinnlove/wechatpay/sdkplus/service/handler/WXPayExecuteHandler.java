/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.shinnlove.wechatpay.sdkplus.service.handler;

import java.util.Map;

import com.shinnlove.wechatpay.sdkplus.config.WXPayMchConfig;

/**
 * 微信支付——请求执行接口。
 *
 * 具体网络请求执行委托给请求服务执行。
 *
 * @author shinnlove.jinsheng
 * @version $Id: WXPayExecuteHandler.java, v 0.1 2018-12-19 上午10:40 shinnlove.jinsheng Exp $$
 */
public interface WXPayExecuteHandler {

    /**
     * 请求是否需要使用证书：给各个微信支付请求客户端决定请求是否需要使用证书通信。
     *
     * 如：对退款申请需要使用证书双向认证，则RefundClient需要实现此接口并返回true。
     *
     * @return
     */
    boolean requestNeedCert();

    /**
     * 微信支付请求具体地址，这里可以设置主备域、沙箱与正式环境。
     *
     * @param config
     * @return
     */
    String payRequestURL(WXPayMchConfig config);

    /**
     * 执行微信支付不同类型请求，返回HttpResponse流字符串（暂定）。
     *
     * @param payParams 
     * @return
     * @throws Exception
     */
    String executePayRequest(final Map<String, String> payParams) throws Exception;

}
