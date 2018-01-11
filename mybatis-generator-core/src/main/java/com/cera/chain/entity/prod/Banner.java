/*
 * file comment: Banner.java
 * Copyright(C) All rights reserved. 
 */
package com.cera.chain.entity.prod;

import com.cera.chain.entity.PageEntity;
import java.io.Serializable;
import java.util.Date;

public class Banner extends PageEntity implements Serializable {
    /**
     * field comment: 新闻ID
     */
    private Long bannerId;

    /**
     * field comment: 适用目标页面 1 - 首页；2-产品页；3-详情页。
     */
    private Short type;

    /**
     * field comment: 需要跳转到的URL
     */
    private String url;

    /**
     * field comment: 首页的小图标，商品列表页和资讯页没有。
     */
    private String icon;

    /**
     * field comment: banner简短说明。首页没有，商品列表页和资讯页有。
     */
    private String brief;

    /**
     * field comment: 产品图片相册(多个用逗号分开)，存放在服务器上的相对路径。
     */
    private String picture;

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

    private static final long serialVersionUID = 1L;

    public Long getBannerId() {
        return bannerId;
    }

    public void setBannerId(Long bannerId) {
        this.bannerId = bannerId;
    }

    public Short getType() {
        return type;
    }

    public void setType(Short type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url == null ? null : url.trim();
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon == null ? null : icon.trim();
    }

    public String getBrief() {
        return brief;
    }

    public void setBrief(String brief) {
        this.brief = brief == null ? null : brief.trim();
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture == null ? null : picture.trim();
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
}