/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2019 All Rights Reserved.
 */
package com.shinnlove.wechatpay.model;

import java.io.Serializable;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 图片下载完成标记。
 *
 * @author shinnlove.jinsheng
 * @version $Id: ImageMark.java, v 0.1 2019-01-31 10:47 shinnlove.jinsheng Exp $$
 */
public class ImageMark implements Serializable {

    /** uuid */
    private static final long serialVersionUID = -4702095596995123903L;

    /** 图片地址 */
    private String            src;

    /** 是否下载完成 */
    private boolean           downloaded;

    public ImageMark() {
    }

    public ImageMark(String src, boolean downloaded) {
        this.src = src;
        this.downloaded = downloaded;
    }

    /**
     * Getter method for property src.
     *
     * @return property value of src
     */
    public String getSrc() {
        return src;
    }

    /**
     * Setter method for property src.
     *
     * @param src value to be assigned to property src
     */
    public void setSrc(String src) {
        this.src = src;
    }

    public boolean isDownloaded() {
        return downloaded;
    }

    /**
     * Setter method for property downloaded.
     *
     * @param downloaded value to be assigned to property downloaded
     */
    public void setDownloaded(boolean downloaded) {
        this.downloaded = downloaded;
    }

    /**
     * @see Object#toString()
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(ToStringStyle.SHORT_PREFIX_STYLE);
    }

}