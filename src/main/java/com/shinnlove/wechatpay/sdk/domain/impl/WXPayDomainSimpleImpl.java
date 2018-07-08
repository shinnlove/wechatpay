/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.shinnlove.wechatpay.sdk.domain.impl;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.conn.ConnectTimeoutException;

import com.shinnlove.wechatpay.sdk.config.WXPayConfig;
import com.shinnlove.wechatpay.sdk.consts.WXPayConstants;
import com.shinnlove.wechatpay.sdk.domain.IWXPayDomain;

/**
 * 主、备域名统计及切换策略。
 *
 * @author shinnlove.jinsheng
 * @version $Id: WXPayDomainSimpleImpl.java, v 0.1 2018-07-08 上午10:35 shinnlove.jinsheng Exp $$
 */
public class WXPayDomainSimpleImpl implements IWXPayDomain {

    /** 上次切换到备用域名的时间(只有一个实例，所以只保存一份) */
    private long                       switchToAlternateDomainTime = 0;

    /** 使用备用域名最多时间上限(3分钟后固定切换回主域名尝试) */
    private final int                  MIN_SWITCH_PRIMARY_MSEC     = 3 * 60 * 1000;                       //3 minutes

    /** 主备域名统计数据的缓存 */
    private Map<String, DomainStatics> domainData                  = new HashMap<String, DomainStatics>();

    /**
     * 私有化构造函数。
     */
    private WXPayDomainSimpleImpl() {
    }

    /**
     * 静态初始化单例。
     */
    private static class WxpayDomainHolder {
        private static IWXPayDomain holder = new WXPayDomainSimpleImpl();
    }

    /**
     * 返回一个实例对象。
     *
     * @return
     */
    public static IWXPayDomain instance() {
        return WxpayDomainHolder.holder;
    }

    /**
     * @see IWXPayDomain#report(java.lang.String, long, java.lang.Exception)
     */
    @Override
    public synchronized void report(final String domain, long elapsedTimeMillis, final Exception ex) {
        DomainStatics info = domainData.get(domain);
        if (info == null) {
            info = new DomainStatics(domain);
            domainData.put(domain, info);
        }

        if (ex == null) { //success
            if (info.succCount >= 2) { //continue succ, clear error count
                info.connectTimeoutCount = info.dnsErrorCount = info.otherErrorCount = 0;
            } else {
                ++info.succCount;
            }
        } else if (ex instanceof ConnectTimeoutException) {
            info.succCount = info.dnsErrorCount = 0;
            ++info.connectTimeoutCount;
        } else if (ex instanceof UnknownHostException) {
            info.succCount = 0;
            ++info.dnsErrorCount;
        } else {
            info.succCount = 0;
            ++info.otherErrorCount;
        }
    }

    /**
     * @see IWXPayDomain#getDomain(com.github.wxpay.sdk.WXPayConfig)
     */
    @Override
    public synchronized DomainInfo getDomain(final WXPayConfig config) {
        // 主要域名
        DomainStatics primaryDomain = domainData.get(WXPayConstants.DOMAIN_API);
        // 超时数量和dns解析错误数量小于2
        if (primaryDomain == null || primaryDomain.isGood()) {
            return new DomainInfo(WXPayConstants.DOMAIN_API, true);
        }

        // 如果主域名经常超时，尝试采用备用域名
        long now = System.currentTimeMillis();
        if (switchToAlternateDomainTime == 0) {
            // 第一次切换成备用域名，直接使用备用域名
            switchToAlternateDomainTime = now;
            return new DomainInfo(WXPayConstants.DOMAIN_API2, false);
        } else if (now - switchToAlternateDomainTime < MIN_SWITCH_PRIMARY_MSEC) {
            // 第二次及以上使用备用域名、还在3分钟内
            DomainStatics alternateDomain = domainData.get(WXPayConstants.DOMAIN_API2);
            if (alternateDomain == null || alternateDomain.isGood()
                || alternateDomain.badCount() < primaryDomain.badCount()) {
                // 备用域名OK且超时、失败数量小于主域名，则继续用
                return new DomainInfo(WXPayConstants.DOMAIN_API2, false);
            } else {
                // 否则切换回主域名
                return new DomainInfo(WXPayConstants.DOMAIN_API, true);
            }
        } else {
            // 备用域名撑了3分钟以上，强制切换回主域名
            // 重置切换时间、两个域名的失败、超时数(如果主域名还起不来，超时2次后，继续路由到备用域名上)
            switchToAlternateDomainTime = 0;
            primaryDomain.resetCount();
            DomainStatics alternateDomain = domainData.get(WXPayConstants.DOMAIN_API2);
            if (alternateDomain != null) {
                alternateDomain.resetCount();
            }

            return new DomainInfo(WXPayConstants.DOMAIN_API, true);
        }
    }

    /**
     * 域名统计数据，包含4类：域名、成功数量、连接超时数量、dns解析错误数量和其他错误数量。
     */
    static class DomainStatics {
        final String domain;
        int          succCount           = 0;
        int          connectTimeoutCount = 0;
        int          dnsErrorCount       = 0;
        int          otherErrorCount     = 0;

        DomainStatics(String domain) {
            this.domain = domain;
        }

        void resetCount() {
            succCount = connectTimeoutCount = dnsErrorCount = otherErrorCount = 0;
        }

        boolean isGood() {
            return connectTimeoutCount <= 2 && dnsErrorCount <= 2;
        }

        int badCount() {
            return connectTimeoutCount + dnsErrorCount * 5 + otherErrorCount / 4;
        }
    }

}