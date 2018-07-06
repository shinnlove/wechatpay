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
    public static final String ACCEPT                     = "application/json,text/plain,*/*;charset=UTF-8";

    /** 可接受类型 */
    public static final String ACCEPT_ENCODING            = "gzip, deflate";

    /** 可接受的语言 */
    public static final String ACCEPT_LANGUAGE            = "zh-CN,zh;q=0.9,en;q=0.8";

    /** 传输内容类型 */
    public static final String CONTENT_TYPE               = "application/json;charset=UTF-8";

    /** 请求来源 */
    public static final String REFERER                    = "http://instasset-zth-2.gz00b.dev.alipay.net/index.htm";

    /** 请求主机 */
    public static final String HOST                       = "instasset-zth-2.gz00b.dev.alipay.net";

    /** 模拟MacOSX的Chrome请求头 */
    public static final String USER_AGENT                 = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.139 Safari/537.36";

    /** 默认模拟的Cookie */
    public static final String COOKIE                     = "_b_s=chensheng.zcs; _b_n=113505; buservice_domain_id=KOUBEI_SALESCRM; IS_INNER_LOGIN=1; UM_distinctid=164174aef2039c-0768eb31a428b8-33667f07-13c680-164174aef21d29; zone=GZ00B; ZAUTH_REST_LOGIN_INFO=7b22666f7277617264223a302c226970223a2231302e3230392e31392e3832222c226c6f67696e4e616d65223a226368656e7368656e672e7a6373222c226c6f67696e54696d65223a313532393633393935323833312c22746f6b656e223a2232373565366562342d393938362d343336662d613261382d333665643635363939343435222c2275726c223a22687474703a2f2f31302e3230392e31392e38322f2f726573742f6765744c6f67696e5573657241757468732e6a736f6e227d; ALIPAYJSESSIONID=GZ00SlixdekHrX3dXUMtTPvAoxkvYFfindecisionGZ00; JSESSIONID=E5AFFE1CB79CD5EA69230BDFD76667D4; ALIPAYBUMNGJSESSIONID=GZ00GKeFLRwC95TjqyFWbxplrqEtmNinstassetGZ00; ctoken=bK1E6Uba3BGcrNb-; session.cookieNameId=ALIPAYBUMNGJSESSIONID; sso.global.authtoken=sso.global.authtoken; _l_n=106809";

}