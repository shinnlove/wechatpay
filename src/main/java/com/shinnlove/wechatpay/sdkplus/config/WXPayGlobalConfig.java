/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.shinnlove.wechatpay.sdkplus.config;

import com.shinnlove.wechatpay.sdk.domain.IWXPayDomain;

/**
 * 微信支付全局配置。
 *
 * 本类包含主备域名切换、超时时间控制、重试次数等微信支付通信的全局配置（后续做成动态可切换配置）。
 *
 * @author shinnlove.jinsheng
 * @version $Id: WXPayGlobalConfig.java, v 0.1 2018-12-19 下午4:33 shinnlove.jinsheng Exp $$
 */
public class WXPayGlobalConfig {

    private int          httpConnectionTimeoutMs;

    private int          httpReadTimeoutMs;

    private int          reportWorkerNum;

    private int          reportBatchSize;

    private boolean      autoReport;

    private IWXPayDomain domain;

    public WXPayGlobalConfig(int httpConnectionTimeoutMs, int httpReadTimeoutMs,
                             int reportWorkerNum, int reportBatchSize, boolean autoReport,
                             IWXPayDomain domain) {
        this.httpConnectionTimeoutMs = httpConnectionTimeoutMs;
        this.httpReadTimeoutMs = httpReadTimeoutMs;
        this.reportWorkerNum = reportWorkerNum;
        this.reportBatchSize = reportBatchSize;
        this.autoReport = autoReport;
        this.domain = domain;
    }

    /**
     * Getter method for property httpConnectionTimeoutMs.
     *
     * @return property value of httpConnectionTimeoutMs
     */
    public int getHttpConnectionTimeoutMs() {
        return httpConnectionTimeoutMs;
    }

    /**
     * Setter method for property httpConnectionTimeoutMs.
     *
     * @param httpConnectionTimeoutMs value to be assigned to property httpConnectionTimeoutMs
     */
    public void setHttpConnectionTimeoutMs(int httpConnectionTimeoutMs) {
        this.httpConnectionTimeoutMs = httpConnectionTimeoutMs;
    }

    /**
     * Getter method for property httpReadTimeoutMs.
     *
     * @return property value of httpReadTimeoutMs
     */
    public int getHttpReadTimeoutMs() {
        return httpReadTimeoutMs;
    }

    /**
     * Setter method for property httpReadTimeoutMs.
     *
     * @param httpReadTimeoutMs value to be assigned to property httpReadTimeoutMs
     */
    public void setHttpReadTimeoutMs(int httpReadTimeoutMs) {
        this.httpReadTimeoutMs = httpReadTimeoutMs;
    }

    /**
     * Getter method for property reportWorkerNum.
     *
     * @return property value of reportWorkerNum
     */
    public int getReportWorkerNum() {
        return reportWorkerNum;
    }

    /**
     * Setter method for property reportWorkerNum.
     *
     * @param reportWorkerNum value to be assigned to property reportWorkerNum
     */
    public void setReportWorkerNum(int reportWorkerNum) {
        this.reportWorkerNum = reportWorkerNum;
    }

    /**
     * Getter method for property reportBatchSize.
     *
     * @return property value of reportBatchSize
     */
    public int getReportBatchSize() {
        return reportBatchSize;
    }

    /**
     * Setter method for property reportBatchSize.
     *
     * @param reportBatchSize value to be assigned to property reportBatchSize
     */
    public void setReportBatchSize(int reportBatchSize) {
        this.reportBatchSize = reportBatchSize;
    }

    public boolean isAutoReport() {
        return autoReport;
    }

    /**
     * Setter method for property autoReport.
     *
     * @param autoReport value to be assigned to property autoReport
     */
    public void setAutoReport(boolean autoReport) {
        this.autoReport = autoReport;
    }

    /**
     * Getter method for property domain.
     *
     * @return property value of domain
     */
    public IWXPayDomain getDomain() {
        return domain;
    }

    /**
     * Setter method for property domain.
     *
     * @param domain value to be assigned to property domain
     */
    public void setDomain(IWXPayDomain domain) {
        this.domain = domain;
    }

}