/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.shinnlove.wechatpay.sdk.config.impl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import com.shinnlove.wechatpay.sdk.config.WXPayConfig;
import com.shinnlove.wechatpay.sdk.domain.IWXPayDomain;
import com.shinnlove.wechatpay.sdk.domain.impl.WXPayDomainSimpleImpl;

/**
 * 微信配置实现类。
 *
 * @author shinnlove.jinsheng
 * @version $Id: WXPayConfigImpl.java, v 0.1 2018-07-08 上午10:35 shinnlove.jinsheng Exp $$
 */
public class WXPayConfigImpl extends WXPayConfig {

    /** 二进制证书信息 */
    private byte[]                 certData;
    private static WXPayConfigImpl INSTANCE;

    /**
     * 私有化构造函数，单例输出。
     *
     * @throws Exception
     */
    private WXPayConfigImpl() throws Exception {
        //        String certPath = "/Users/zhaochensheng/Downloads/apiclient_cert.p12";
        String certPath = "/Users/zhaochensheng/Documents/Shinnlove/我的资料/微动证书/apiclient_cert.p12";
        File file = new File(certPath);
        InputStream certStream = new FileInputStream(file);
        this.certData = new byte[(int) file.length()];
        certStream.read(this.certData);
        certStream.close();
    }

    /**
     * 懒汉式单例模式。
     *
     * @return
     * @throws Exception
     */
    public static WXPayConfigImpl getInstance() throws Exception {
        if (INSTANCE == null) {
            // 一锁
            synchronized (WXPayConfigImpl.class) {
                // 二判断
                if (INSTANCE == null) {
                    // 三处理
                    INSTANCE = new WXPayConfigImpl();
                }
            }
        }
        return INSTANCE;
    }

    @Override
    public String getAppID() {
        return "wxdb3bb7c95c0d5932";
    }

    @Override
    public String getMchID() {
        return "10029370";
    }

    /**
     * 4*8=32位密钥，weactpay正好8位，就重复4次
     * @return
     */
    @Override
    public String getKey() {
        return "weactpayweactpayweactpayweactpay";
    }

    @Override
    public InputStream getCertStream() {
        ByteArrayInputStream certBis;
        certBis = new ByteArrayInputStream(this.certData);
        return certBis;
    }

    @Override
    public int getHttpConnectTimeoutMs() {
        return 2000;
    }

    @Override
    public int getHttpReadTimeoutMs() {
        return 10000;
    }

    @Override
    public IWXPayDomain getWXPayDomain() {
        return WXPayDomainSimpleImpl.instance();
    }

    public String getPrimaryDomain() {
        return "api.mch.weixin.qq.com";
    }

    public String getAlternateDomain() {
        return "api2.mch.weixin.qq.com";
    }

    @Override
    public int getReportWorkerNum() {
        return 1;
    }

    @Override
    public int getReportBatchSize() {
        return 2;
    }

}