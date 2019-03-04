/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2019 All Rights Reserved.
 */
package com.shinnlove.wechatpay.handler;

import org.jsoup.nodes.Document;

/**
 * 从文档中抽取一系列信息的lambda接口。
 * 为了让用户更好的定义抽取不同的代码逻辑，为了让请求后JSoup的对象尽早的GC掉。
 *
 * 如：
 * a) 从一片文章某一页抽取这一页的所有图片；
 * b) 从一片文章某一页抽取底部所有推荐文章；
 * c) 从目录页抽取列表中所有文章地址；
 *
 * @author shinnlove.jinsheng
 * @version $Id: DocumentExtract.java, v 0.1 2019-03-04 11:05 shinnlove.jinsheng Exp $$
 */
@FunctionalInterface
public interface DocumentExtract<T> {

    /**
     * 处理网页的Document，要提取的信息放入上下文传入的final List中，不用单独返回。
     *
     * @param document 
     * @return              如果要返回内容，在接口中返回。
     */
    T pickUp(Document document);

}
