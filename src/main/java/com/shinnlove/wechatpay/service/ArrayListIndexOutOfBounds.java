/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2019 All Rights Reserved.
 */
package com.shinnlove.wechatpay.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 线程断点方式模拟ArrayList并发数组超出的问题。
 *
 * @author shinnlove.jinsheng
 * @version $Id: ArrayListIndexOutOfBounds.java, v 0.1 2019-03-31 23:09 shinnlove.jinsheng Exp $$
 */
public class ArrayListIndexOutOfBounds {

    private static List list = new ArrayList();

    public static void main(String[] args) {

        Runnable r = new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    list.add(new Object());
                }
            }
        };

        Thread t1 = new Thread(r, "t1");
        Thread t2 = new Thread(r, "t2");

        t1.start();
        t2.start();

        try {
            TimeUnit.SECONDS.sleep(999999999);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}