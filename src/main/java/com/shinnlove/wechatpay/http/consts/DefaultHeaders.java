/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.shinnlove.wechatpay.http.consts;

import org.apache.http.Consts;

/**
 * 默认常用HTTP请求头配置。
 *
 * @author shinnlove.jinsheng
 * @version $Id: DefaultHeaders.java, v 0.1 2018-07-04 下午2:19 shinnlove.jinsheng Exp $$
 */
public class DefaultHeaders {

    /** 其他默认 */
    public static final String APP_FORM_URLENCODED        = "application/x-www-form-urlencoded";
    public static final String TEXT_PLAIN                 = "text/plain";
    public static final String TEXT_HTML                  = "text/html";
    public static final String TEXT_XML                   = "text/xml";
    public static final String TEXT_JSON                  = "text/json";
    public static final String CONTENT_CHARSET_ISO_8859_1 = Consts.ISO_8859_1.name();
    public static final String CONTENT_CHARSET_UTF8       = Consts.UTF_8.name();
    public static final String DEF_PROTOCOL_CHARSET       = Consts.ASCII.name();
    public static final String CONN_CLOSE                 = "close";
    public static final String KEEP_ALIVE                 = "keep-alive";
    public static final String EXPECT_CONTINUE            = "100-continue";

    /** 可接受返回结果 */
    public static final String ACCEPT                     = "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3";

    /** 可接受类型 */
    public static final String ACCEPT_ENCODING            = "gzip, deflate, br";

    /** 可接受的语言 */
    public static final String ACCEPT_LANGUAGE            = "zh-CN,zh;q=0.9,en;q=0.8";

    /** 传输内容类型 */
    public static final String CONTENT_TYPE               = "application/json;charset=UTF-8";

    /** 请求来源 */
    public static final String REFERER                    = "https://www.aitaotu.com/gangtai/35216_4.html";

    /** 请求主机 */
    public static final String HOST                       = "instasset-zth-2.gz00b.dev.alipay.net";

    /** 模拟MacOSX的Chrome请求头 */
    public static final String USER_AGENT                 = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.103 Safari/537.36";

    /** 默认模拟的Cookie */
    public static final String COOKIE                     = "_uupv=28; UM_distinctid=1680979f769280-041de7c567e76-10346655-13c680-1680979f76a719; Hm_lvt_3b19253d112290a9184293cf68a02346=1555054302; _uupv=34; CNZZDATA1255139604=69273573-1497769960-%7C1557021275; Hm_lpvt_3b19253d112290a9184293cf68a02346=1557022490";

}