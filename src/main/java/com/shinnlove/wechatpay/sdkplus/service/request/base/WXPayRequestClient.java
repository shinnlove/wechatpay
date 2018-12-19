/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.shinnlove.wechatpay.sdkplus.service.request.base;

import java.util.HashMap;
import java.util.Map;

import com.shinnlove.wechatpay.sdk.utils.WXPayUtil;
import com.shinnlove.wechatpay.sdkplus.config.WXPayMchConfig;
import com.shinnlove.wechatpay.sdkplus.http.WXPayRequestExecutor;
import com.shinnlove.wechatpay.sdkplus.invoker.WXPayInvoker;
import com.shinnlove.wechatpay.sdkplus.service.client.AbstractWXPayClient;
import com.shinnlove.wechatpay.sdkplus.service.handler.WXPayExecuteHandler;
import com.shinnlove.wechatpay.sdkplus.service.handler.WXPayParamsHandler;
import com.shinnlove.wechatpay.sdkplus.service.handler.WXPayService;

/**
 * 微信支付主动请求抽象类。
 *
 * 微信支付能力透出：{@link WXPayService}
 * 各类请求参数处理接口：{@link WXPayParamsHandler}
 * 各类请求公共执行接口：{@link WXPayExecuteHandler}
 *
 * @author shinnlove.jinsheng
 * @version $Id: WXPayRequestClient.java, v 0.1 2018-12-18 下午4:13 shinnlove.jinsheng Exp $$
 */
public abstract class WXPayRequestClient extends AbstractWXPayClient implements WXPayService,
                                                                    WXPayParamsHandler,
                                                                    WXPayExecuteHandler {

    /** 微信支付请求对象 */
    protected WXPayRequestExecutor wxPayRequestExecutor;

    /**
     * 构造函数。
     *
     * @param wxPayMchConfig
     */
    public WXPayRequestClient(WXPayMchConfig wxPayMchConfig) {
        super(wxPayMchConfig);
    }

    @Override
    public Map<String, String> doPayRequest(Map<String, String> keyPairs, WXPayInvoker invoker)
                                                                                               throws Exception {
        // 一次微信请求的payParameters可以作为这个函数的局部变量栈上分配优化...
        Map<String, String> payParams = new HashMap<>();

        // Step1：校验支付入参
        checkParameters(keyPairs);

        // Step2：填写请求入参
        fillRequestParams(keyPairs, payParams);

        // Step3：执行请求
        String respStr = executePayRequest(payParams);

        // Step4：解码请求结果
        final Map<String, String> response = processResponseXml(respStr);

        // Step5：回调外层
        invoker.doPayCallback(response);

        // PS：结果还是返回，如果外面想要...
        return response;
    }

    @Override
    public void fillRequestParams(Map<String, String> keyPairs, final Map<String, String> payParams)
                                                                                                    throws Exception {
        // 策略模式上下文填写请求主体信息
        wxPayModeContext.fillRequestMainBodyParams(wxPayMchConfig, payParams);

        // 交给具体的子类完成其他请求必填参数
        fillRequestDetailParams(keyPairs, payParams);
    }

    /**
     * 抽象填入请求需要的具体字段信息。
     * 
     * @param keyPairs  
     * @param payParams
     */
    public abstract void fillRequestDetailParams(Map<String, String> keyPairs,
                                                 final Map<String, String> payParams);

    @Override
    public String executePayRequest(final Map<String, String> payParams) throws Exception {
        // 请求地址
        String url = payRequestURL(wxPayMchConfig);
        // 组装支付xml报文
        String reqBody = WXPayUtil.mapToXml(payParams);
        // 是否需要证书
        boolean needCert = requestNeedCert();

        // 打印入参（打印出入参放到doRequest中?）

        // 执行
        String response = wxPayRequestExecutor.doRequest(wxPayMchConfig, url, reqBody, needCert);

        // 打印出参

        return response;
    }

    /**
     * Setter method for property wxPayRequestExecutor.
     *
     * @param wxPayRequestExecutor value to be assigned to property wxPayRequestExecutor
     */
    public void setWxPayRequestExecutor(WXPayRequestExecutor wxPayRequestExecutor) {
        this.wxPayRequestExecutor = wxPayRequestExecutor;
    }

}