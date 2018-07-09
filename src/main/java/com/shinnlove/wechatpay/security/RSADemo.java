/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.shinnlove.wechatpay.security;

import java.io.IOException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 * RSA算法例子。
 *
 * @author shinnlove.jinsheng
 * @version $Id: RSADemo.java, v 0.1 2018-07-09 下午4:22 shinnlove.jinsheng Exp $$
 */
public class RSADemo {

    /**
     * 使用RSA算法初始化512位的键值对。
     *
     * @return
     * @throws Exception
     */
    public static KeyPair getKeyPair() throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(512);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        return keyPair;
    }

    /**
     * 生成公钥并转成Base64的字符串以持久化。
     *
     * @param keyPair
     * @return
     */
    public static String getPublicKey(KeyPair keyPair) {
        PublicKey publicKey = keyPair.getPublic();
        byte[] bytes = publicKey.getEncoded();
        return Base64Demo.byte2base64(bytes);
    }

    /**
     * 生成私钥并转成Base64的字符串以持久化。
     *
     * @param keyPair
     * @return
     */
    public static String getPrivateKey(KeyPair keyPair) {
        PrivateKey privateKey = keyPair.getPrivate();
        byte[] bytes = privateKey.getEncoded();
        return Base64Demo.byte2base64(bytes);
    }

    /**
     * 将保存的base64的钥匙转成公钥。
     *
     * 特别注意：公钥转换采用`X509EncodedKeySpec`对象。
     *
     * @param pubStr
     * @return
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    public static PublicKey string2PublicKey(String pubStr) throws IOException,
                                                           NoSuchAlgorithmException,
                                                           InvalidKeySpecException {
        byte[] keyBytes = Base64Demo.base642byte(pubStr);

        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);

        // NoSuchAlgorithm
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        // invalidKeySpec

        PublicKey publicKey = keyFactory.generatePublic(keySpec);
        return publicKey;
    }

    /**
     * 将保存的base64的钥匙转成私钥。
     *
     * 特别注意：公钥转换采用`PKCS8EncodedKeySpec`对象。
     *
     * @param priStr
     * @return
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    public static PrivateKey string2PrivateKey(String priStr) throws IOException,
                                                             NoSuchAlgorithmException,
                                                             InvalidKeySpecException {
        byte[] keyBytes = Base64Demo.base642byte(priStr);

        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);

        // NoSuchAlgorithm
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        // InvalidKeySpec
        PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
        return privateKey;
    }

    /**
     * 使用公钥对字节流数据进行加密。
     *
     * @param content
     * @param publicKey
     * @return
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     */
    public static byte[] publicEncrypt(byte[] content, PublicKey publicKey)
                                                                           throws NoSuchPaddingException,
                                                                           NoSuchAlgorithmException,
                                                                           InvalidKeyException,
                                                                           BadPaddingException,
                                                                           IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] bytes = cipher.doFinal(content);
        return bytes;
    }

    /**
     * 使用私钥对字节流数据进行解密。
     *
     * @param content
     * @param privateKey
     * @return
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     */
    public static byte[] privateDecrypt(byte[] content, PrivateKey privateKey)
                                                                              throws NoSuchPaddingException,
                                                                              NoSuchAlgorithmException,
                                                                              InvalidKeyException,
                                                                              BadPaddingException,
                                                                              IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] bytes = cipher.doFinal(content);
        return bytes;
    }

    public static void main(String[] args) throws Exception {
        String str = "I am shinnlove.";
        KeyPair keyPair = getKeyPair();
        // 公钥字符串
        String base64PubKey = getPublicKey(keyPair);
        // 私钥字符串
        String base64priKey = getPrivateKey(keyPair);

        // 公钥
        PublicKey publicKey = string2PublicKey(base64PubKey);
        // 私钥
        PrivateKey privateKey = string2PrivateKey(base64priKey);

        // 原来的信息
        System.out.println(str);

        System.out.println("========Encrypt=========");

        // 加密
        byte[] encrypt = publicEncrypt(str.getBytes(), publicKey);
        System.out.println(new String(encrypt));

        System.out.println("========Decrypt=========");

        // 解密
        byte[] decrypt = privateDecrypt(encrypt, privateKey);
        System.out.println(new String(decrypt));
    }

}