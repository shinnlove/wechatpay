/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.shinnlove.wechatpay.sdkplus.service.request.redpackage;

import com.shinnlove.wechatpay.sdkplus.config.WXPayMchConfig;
import com.shinnlove.wechatpay.sdkplus.service.request.base.WXPayRequestClient;

/**
 * 微信红包请求客户端。
 *
 * @author shinnlove.jinsheng
 * @version $Id: RedPackageClient.java, v 0.1 2018-12-18 下午5:35 shinnlove.jinsheng Exp $$
 */
public class RedPackageClient extends WXPayRequestClient {

    /**
     * 构造函数。
     *
     * @param wxPayMchConfig
     */
    public RedPackageClient(WXPayMchConfig wxPayMchConfig) {
        super(wxPayMchConfig);
    }

}