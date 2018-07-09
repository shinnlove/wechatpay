/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.shinnlove.wechatpay.security;

import java.io.IOException;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * 将二进制数据以Base64形式加密。
 *
 * @author shinnlove.jinsheng
 * @version $Id: Base64Demo.java, v 0.1 2018-07-09 下午4:25 shinnlove.jinsheng Exp $$
 */
public class Base64Demo {

    /**
     * 将二进制数组转换为Base64编码。
     *
     * @param bytes
     * @return
     */
    public static String byte2base64(byte[] bytes) {
        BASE64Encoder base64Encoder = new BASE64Encoder();
        return base64Encoder.encode(bytes);
    }

    /**
     * 将Base64格式的字符串转换成二进制数组。
     *
     * @param base64
     * @return
     * @throws IOException
     */
    public static byte[] base642byte(String base64) throws IOException {
        BASE64Decoder base64Decoder = new BASE64Decoder();
        return base64Decoder.decodeBuffer(base64);
    }

    public static void main(String[] args) throws IOException {
        String str = "fuck, 阿里巴巴里太多杠精了，要想脱颖而出，必定要先练就一身铁嘴本领，否则别人把你喷成筛子。";

        String strEncrypt = byte2base64(str.getBytes());

        System.out.println(strEncrypt);

        String strOrigin = new String(base642byte(strEncrypt));

        System.out.println(strOrigin);
    }

}