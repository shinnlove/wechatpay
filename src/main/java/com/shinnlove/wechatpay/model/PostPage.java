/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2019 All Rights Reserved.
 */
package com.shinnlove.wechatpay.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 帖子页对象。
 *
 * @author shinnlove.jinsheng
 * @version $Id: PostPage.java, v 0.1 2019-01-31 10:38 shinnlove.jinsheng Exp $$
 */
public class PostPage implements Serializable {

    /** uuid */
    private static final long serialVersionUID = -7182514722081110849L;

    /** 帖子id，以完整分类-年-月-帖子主题编号来确定一篇帖子 */
    private String            id;

    /** 帖子名 */
    private String            name;

    /** 帖子首页地址 */
    private String            url;

    /** 帖子总共几页 */
    private int               pages;

    /** 帖子包含图片地址列表 */
    private List<ImageMark>   imageList        = new ArrayList<>();

    /** 帖子是否下载完成 */
    private boolean           finished         = false;

    /** 帖子重试次数 */
    private int               retryCounts      = 0;

    public PostPage() {
    }

    /**
     * 创建帖子构造函数。
     *
     * @param url
     * @param pages
     */
    public PostPage(String url, int pages) {
        this.url = url;
        this.pages = pages;
    }

    /**
     * Getter method for property id.
     *
     * @return property value of id
     */
    public String getId() {
        return id;
    }

    /**
     * Setter method for property id.
     *
     * @param id value to be assigned to property id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Getter method for property name.
     *
     * @return property value of name
     */
    public String getName() {
        return name;
    }

    /**
     * Setter method for property name.
     *
     * @param name value to be assigned to property name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Getter method for property url.
     *
     * @return property value of url
     */
    public String getUrl() {
        return url;
    }

    /**
     * Setter method for property url.
     *
     * @param url value to be assigned to property url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * Getter method for property pages.
     *
     * @return property value of pages
     */
    public int getPages() {
        return pages;
    }

    /**
     * Setter method for property pages.
     *
     * @param pages value to be assigned to property pages
     */
    public void setPages(int pages) {
        this.pages = pages;
    }

    /**
     * Getter method for property imageList.
     *
     * @return property value of imageList
     */
    public List<ImageMark> getImageList() {
        return imageList;
    }

    /**
     * Setter method for property imageList.
     *
     * @param imageList value to be assigned to property imageList
     */
    public void setImageList(List<ImageMark> imageList) {
        this.imageList = imageList;
    }

    public boolean isFinished() {
        return finished;
    }

    /**
     * Setter method for property finished.
     *
     * @param finished value to be assigned to property finished
     */
    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    /**
     * Getter method for property retryCounts.
     *
     * @return property value of retryCounts
     */
    public int getRetryCounts() {
        return retryCounts;
    }

    /**
     * Setter method for property retryCounts.
     *
     * @param retryCounts value to be assigned to property retryCounts
     */
    public void setRetryCounts(int retryCounts) {
        this.retryCounts = retryCounts;
    }

    /**
     * @see Object#toString() 
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(ToStringStyle.SHORT_PREFIX_STYLE);
    }

}