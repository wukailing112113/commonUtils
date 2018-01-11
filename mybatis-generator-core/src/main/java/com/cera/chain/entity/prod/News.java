/*
 * file comment: News.java
 * Copyright(C) All rights reserved. 
 */
package com.cera.chain.entity.prod;

import com.cera.chain.entity.PageEntity;
import java.io.Serializable;
import java.util.Date;

public class News extends PageEntity implements Serializable {
    /**
     * field comment: 新闻ID
     */
    private Long newsId;

    /**
     * field comment: 新闻title
     */
    private String title;

    /**
     * field comment: 新闻关键字
     */
    private String keyWords;

    /**
     * field comment: 新闻类别 1. 行业；2.活动；3.腕表；4.珠宝；5.知识。
     */
    private Short clazz;

    /**
     * field comment: 产品图片相册(多个用逗号分开)，存放在服务器上的相对路径。
     */
    private String album;

    /**
     * field comment: 产品简述
     */
    private String brief;

    /**
     * field comment: 是否推荐上首页： 0 - 否；1-是
     */
    private Byte isFirst;

    /**
     * field comment: 创建日期
     */
    private Date createTime;

    /**
     * field comment: 创建日期
     */
    private Date createTimeStart;

    /**
     * field comment: 创建日期
     */
    private Date createTimeEnd;

    /**
     * field comment: 状态 0. 编辑(前端不可见)；1.发布(前端可见)；
     */
    private Byte status;

    /**
     * field comment: 状态变换日期 (上架日期下架日期)
     */
    private Date statusTime;

    /**
     * field comment: 状态变换日期 (上架日期下架日期)
     */
    private Date statusTimeStart;

    /**
     * field comment: 状态变换日期 (上架日期下架日期)
     */
    private Date statusTimeEnd;

    /**
     * field comment: 修改列表，JSON格式{{suid,time}}
     */
    private String updates;

    /**
     * field comment: 新闻页数
     */
    private Short pages;

    private String page1;

    private String page2;

    private String page3;

    private String page4;

    private String page5;

    private String page6;

    private String page7;

    private String page8;

    private String page9;

    private String page10;

    private static final long serialVersionUID = 1L;

    public Long getNewsId() {
        return newsId;
    }

    public void setNewsId(Long newsId) {
        this.newsId = newsId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title == null ? null : title.trim();
    }

    public String getKeyWords() {
        return keyWords;
    }

    public void setKeyWords(String keyWords) {
        this.keyWords = keyWords == null ? null : keyWords.trim();
    }

    public Short getClazz() {
        return clazz;
    }

    public void setClazz(Short clazz) {
        this.clazz = clazz;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album == null ? null : album.trim();
    }

    public String getBrief() {
        return brief;
    }

    public void setBrief(String brief) {
        this.brief = brief == null ? null : brief.trim();
    }

    public Byte getIsFirst() {
        return isFirst;
    }

    public void setIsFirst(Byte isFirst) {
        this.isFirst = isFirst;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getCreateTimeStart() {
        return createTimeStart;
    }

    public void setCreateTimeStart(Date createTimeStart) {
        this.createTimeStart = createTimeStart;
    }

    public Date getCreateTimeEnd() {
        return createTimeEnd;
    }

    public void setCreateTimeEnd(Date createTimeEnd) {
        this.createTimeEnd = createTimeEnd;
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }

    public Date getStatusTime() {
        return statusTime;
    }

    public void setStatusTime(Date statusTime) {
        this.statusTime = statusTime;
    }

    public Date getStatusTimeStart() {
        return statusTimeStart;
    }

    public void setStatusTimeStart(Date statusTimeStart) {
        this.statusTimeStart = statusTimeStart;
    }

    public Date getStatusTimeEnd() {
        return statusTimeEnd;
    }

    public void setStatusTimeEnd(Date statusTimeEnd) {
        this.statusTimeEnd = statusTimeEnd;
    }

    public String getUpdates() {
        return updates;
    }

    public void setUpdates(String updates) {
        this.updates = updates == null ? null : updates.trim();
    }

    public Short getPages() {
        return pages;
    }

    public void setPages(Short pages) {
        this.pages = pages;
    }

    public String getPage1() {
        return page1;
    }

    public void setPage1(String page1) {
        this.page1 = page1 == null ? null : page1.trim();
    }

    public String getPage2() {
        return page2;
    }

    public void setPage2(String page2) {
        this.page2 = page2 == null ? null : page2.trim();
    }

    public String getPage3() {
        return page3;
    }

    public void setPage3(String page3) {
        this.page3 = page3 == null ? null : page3.trim();
    }

    public String getPage4() {
        return page4;
    }

    public void setPage4(String page4) {
        this.page4 = page4 == null ? null : page4.trim();
    }

    public String getPage5() {
        return page5;
    }

    public void setPage5(String page5) {
        this.page5 = page5 == null ? null : page5.trim();
    }

    public String getPage6() {
        return page6;
    }

    public void setPage6(String page6) {
        this.page6 = page6 == null ? null : page6.trim();
    }

    public String getPage7() {
        return page7;
    }

    public void setPage7(String page7) {
        this.page7 = page7 == null ? null : page7.trim();
    }

    public String getPage8() {
        return page8;
    }

    public void setPage8(String page8) {
        this.page8 = page8 == null ? null : page8.trim();
    }

    public String getPage9() {
        return page9;
    }

    public void setPage9(String page9) {
        this.page9 = page9 == null ? null : page9.trim();
    }

    public String getPage10() {
        return page10;
    }

    public void setPage10(String page10) {
        this.page10 = page10 == null ? null : page10.trim();
    }
}