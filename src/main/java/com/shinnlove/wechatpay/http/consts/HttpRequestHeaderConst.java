/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.shinnlove.wechatpay.http.consts;

/**
 * Http请求中有的字段常量。
 *
 * 一个HTTP请求如：
 *
 * ==========================Request Headers==========================
 * Accept: application/json,text/plain;charset=UTF-8
 * Accept-Encoding:gzip,deflate
 * Accept-Language:zh-CN,zh;q=0.9,en;q=0.8
 * Connection:keep-alive
 * Content-Type:application/json;charset=UTF-8
 * Cookie:_b_s=chensheng.zcs;_b_n=113505;buservice_domain_id=KOUBEI_SALESCRM;IS_INNER_LOGIN=1;UM_distinctid=164174aef2039c-0768eb31a428b8-33667f07-13c680-164174aef21d29;zone=GZ00B;ZAUTH_REST_LOGIN_INFO=7b22666f7277617264223a302c226970223a2231302e3230392e31392e3832222c226c6f67696e4e616d65223a226368656e7368656e672e7a6373222c226c6f67696e54696d65223a313532393633393935323833312c22746f6b656e223a2232373565366562342d393938362d343336662d613261382d333665643635363939343435222c2275726c223a22687474703a2f2f31302e3230392e31392e38322f2f726573742f6765744c6f67696e5573657241757468732e6a736f6e227d;ALIPAYJSESSIONID=GZ00SlixdekHrX3dXUMtTPvAoxkvYFfindecisionGZ00;session.cookieNameId=ALIPAYBUMNGJSESSIONID;ALIPAYBUMNGJSESSIONID=GZ003NhJdOUJkeqSsET3RXdfvTXihrantbuserviceGZ00;ctoken=wfX1NpGsSe4xoaj8;sso.global.authtoken=sso.global.authtoken;_l_n=106809;JSESSIONID=3230D52FDEE91AFB6D16AA4943E6B806
 * Host:instasset-zth-2.gz00b.dev.alipay.net
 * Referer:http://instasset-zth-2.gz00b.dev.alipay.net/index.htm
 * User-Agent:Mozilla/5.0(Macintosh;Intel Mac OS X 10_12_5)AppleWebKit/537.36(KHTML,like Gecko)Chrome/66.0.3359.139Safari/537.36
 * ===================================================================
 *
 * @author shinnlove.jinsheng
 * @version $Id: HttpRequestHeaderConst.java, v 0.1 2018-07-04 下午2:07 shinnlove.jinsheng Exp $$
 */
public class HttpRequestHeaderConst {

    private HttpRequestHeaderConst() {
    }

    /** 客户端可以接收的类型，如：application/json,text/plain,charset=UTF-8 */
    public static final String ACCEPT              = "Accept";

    /** 支持的编码方式：如：gzip, deflate。可以支持压缩 */
    public static final String ACCEPT_ENCODING     = "Accept-Encoding";

    public static final String ACCEPT_CHARSET      = "Accept-Charset";

    /** 客户端能接收的语言类型，如：zh-CN,zh;q=0.9,en;q=0.8 */
    public static final String ACCEPT_LANGUAGE     = "Accept-Language";

    public static final String ACCEPT_RANGES       = "Accept-Ranges";

    public static final String AUTHORIZATION       = "Authorization";

    /** 可以设置为keep-alive */
    public static final String CONNECTION          = "Connection";

    /** 内容类型，如：application/json;charset=UTF-8 */
    public static final String CONTENT_TYPE        = "Content-Type";

    public static final String CONTENT_LENGTH      = "Content-Length";

    public static final String CACHE_CONTROL       = "Cache-Control";

    /** 请求头中携带的cookie，一般为浏览器产生于使用，key=value; key2=value2; 注意空格和分号，下划线优选 */
    public static final String COOKIE              = "Cookie";

    public static final String DATE                = "Date";

    public static final String EXPECT              = "Expect";

    public static final String FROM                = "From";

    public static final String HOST                = "Host";

    public static final String IF_MATCH            = "If-Match";

    public static final String IF_MODIFIED_SINCE   = "If-Modified-Since";

    public static final String IF_NONE_MATCH       = "If-None-Match";

    public static final String IF_RANGE            = "If-Range";

    public static final String IF_UNMODIFIED_SINCE = "If-Unmodified-Since";

    public static final String KEEP_ALIVE          = "Keep-Alive";

    public static final String MAX_FORWARDS        = "Max-Forwards";

    public static final String PRAGMA              = "Pragma";

    public static final String PROXY_AUTHORIZATION = "Proxy-Authorization";

    public static final String RANGE               = "Range";

    /** 从哪个来源发起的请求，一般用于跨域验证 */
    public static final String REFERER             = "Referer";

    public static final String TE                  = "TE";

    /** WebSocket类型使用 */
    public static final String UPGRADE             = "Upgrade";

    /** 当使用浏览器访问时，不同浏览器、版本会携带不同用户请求头 */
    public static final String USER_AGENT          = "User-Agent";

    public static final String VIA                 = "Via";

    public static final String WARNING             = "Warning";

}