/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.shinnlove.wechatpay.security;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;

/**
 * DES、AES密钥加解密。
 *
 * @author shinnlove.jinsheng
 * @version $Id: DESDemo.java, v 0.1 2018-07-09 下午9:52 shinnlove.jinsheng Exp $$
 */
public class DESDemo {

    /** DES和AES算法与位数 */
    private static Map<String, Integer> algorithmMap = new HashMap<>();

    static {
        algorithmMap.put("DES", 56);
        algorithmMap.put("AES", 128);
    }

    /**
     * 生成DES密钥。
     *
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static String genKeyDES(String algorithm) throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance(algorithm);
        keyGen.init(algorithmMap.get(algorithm));
        SecretKey key = keyGen.generateKey();
        String base64Str = Base64Demo.byte2base64(key.getEncoded());
        return base64Str;
    }

    /**
     * 将Base64的密码转为`SecretKey`。
     *
     * @param base64Key
     * @return
     * @throws IOException
     */
    public static SecretKey loadKeyDES(String base64Key, String algorithm) throws IOException {
        byte[] bytes = Base64Demo.base642byte(base64Key);
        SecretKey key = new SecretKeySpec(bytes, algorithm);
        return key;
    }

    /**
     * 使用DES加密。
     * 
     * @param source
     * @param key
     * @param algorithm
     * @return
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     */
    public static byte[] encryptDES(byte[] source, SecretKey key, String algorithm)
                                                                                   throws NoSuchPaddingException,
                                                                                   NoSuchAlgorithmException,
                                                                                   InvalidKeyException,
                                                                                   BadPaddingException,
                                                                                   IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] bytes = cipher.doFinal(source);
        return bytes;
    }

    /**
     * 使用DES或者AES解密。
     * 
     * @param source
     * @param key
     * @param algorithm
     * @return
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     */
    public static byte[] decryptDES(byte[] source, SecretKey key, String algorithm)
                                                                                   throws NoSuchPaddingException,
                                                                                   NoSuchAlgorithmException,
                                                                                   InvalidKeyException,
                                                                                   BadPaddingException,
                                                                                   IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] bytes = cipher.doFinal(source);
        return bytes;
    }

    public static void main(String[] args) throws NoSuchAlgorithmException, IOException,
                                          IllegalBlockSizeException, InvalidKeyException,
                                          BadPaddingException, NoSuchPaddingException {
        String str = "这是要加密的字符串，嘿嘿。";
        for (Map.Entry<String, Integer> entry : algorithmMap.entrySet()) {
            System.out.println("===========Begin===========");

            // 生成key与存储
            String algorithm = entry.getKey();
            String base64DES = genKeyDES(algorithm);
            System.out.println("=========base64DES=========");
            System.out.println(base64DES);

            // 加载key
            SecretKey secretKey = loadKeyDES(base64DES, algorithm);

            // 加密
            byte[] encrypt = encryptDES(str.getBytes(), secretKey, algorithm);
            System.out.println("=========base64DES Encrypt=========");
            System.out.println(new String(encrypt));

            // 解密
            System.out.println("=========base64DES Decrypt=========");
            byte[] decrypt = decryptDES(encrypt, secretKey, algorithm);
            System.out.println(new String(decrypt));

            System.out.println("===========End===========");
        }
    }

}