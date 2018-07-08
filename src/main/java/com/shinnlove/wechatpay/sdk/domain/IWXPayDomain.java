/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.shinnlove.wechatpay.sdk.domain;

import com.shinnlove.wechatpay.sdk.config.WXPayConfig;

/**
 * 域名管理，实现主备域名自动切换接口。
 *
 * @author shinnlove.jinsheng
 * @version $Id: IWXPayDomain.java, v 0.1 2018-07-08 上午10:32 shinnlove.jinsheng Exp $$
 */
public interface IWXPayDomain {

    /**
     * 上报域名网络状况
     * @param domain 域名。 比如：api.mch.weixin.qq.com
     * @param elapsedTimeMillis 耗时
     * @param ex 网络请求中出现的异常。
     *           null表示没有异常
     *           ConnectTimeoutException，表示建立网络连接异常
     *           UnknownHostException， 表示dns解析异常
     */
    void report(final String domain, long elapsedTimeMillis, final Exception ex);

    /**
     * 获取域名
     *
     * 本函数可以根据微信配置和请求域名的次数最大化保证微信支付域名能使用。
     *
     * @param config 配置
     * @return 域名
     */
    DomainInfo getDomain(final WXPayConfig config);

    /**
     * 域名信息键值对(放入接口中的类是static的)。
     */
    class DomainInfo {

        /** 域名 */
        public String  domain;
        /** 该域名是否为主域名。例如:api.mch.weixin.qq.com为主域名 */
        public boolean primaryDomain;

        public DomainInfo(String domain, boolean primaryDomain) {
            this.domain = domain;
            this.primaryDomain = primaryDomain;
        }

        @Override
        public String toString() {
            return "DomainInfo{" + "domain='" + domain + '\'' + ", primaryDomain=" + primaryDomain
                   + '}';
        }
    }

}
