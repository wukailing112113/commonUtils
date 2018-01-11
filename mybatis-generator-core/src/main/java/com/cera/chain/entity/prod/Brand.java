/*
 * file comment: Brand.java
 * Copyright(C) All rights reserved. 
 */
package com.cera.chain.entity.prod;

import com.cera.chain.entity.PageEntity;
import java.io.Serializable;
import java.util.Date;

public class Brand extends PageEntity implements Serializable {
    /**
     * field comment: 品牌标识
     */
    private Integer brandId;

    /**
     * field comment: 品牌名首字母，大写
     */
    private String firstChar;

    /**
     * field comment: 品牌中文名
     */
    private String nameCn;

    /**
     * field comment: 品牌英文名
     */
    private String nameEn;

    /**
     * field comment: 品牌关键字，以逗号分隔
     */
    private String keywords;

    /**
     * field comment: 父级品牌标识
     */
    private Integer parentBrandId;

    /**
     * field comment: 品牌图标url
     */
    private String icon;

    /**
     * field comment: 品牌故事
     */
    private String story;

    /**
     * field comment: 品牌创建时间
     */
    private Date createTime;

    /**
     * field comment: 品牌创建时间
     */
    private Date createTimeStart;

    /**
     * field comment: 品牌创建时间
     */
    private Date createTimeEnd;

    /**
     * field comment: 状态： 0 - 弃用；1-在用
     */
    private Byte status;

    /**
     * field comment: 品牌创建时间
     */
    private Date statusTime;

    /**
     * field comment: 品牌创建时间
     */
    private Date statusTimeStart;

    /**
     * field comment: 品牌创建时间
     */
    private Date statusTimeEnd;

    /**
     * field comment: 修改列表，JSON格式。[{suid,time}]
     */
    private String updates;

    private static final long serialVersionUID = 1L;

    public Integer getBrandId() {
        return brandId;
    }

    public void setBrandId(Integer brandId) {
        this.brandId = brandId;
    }

    public String getFirstChar() {
        return firstChar;
    }

    public void setFirstChar(String firstChar) {
        this.firstChar = firstChar == null ? null : firstChar.trim();
    }

    public String getNameCn() {
        return nameCn;
    }

    public void setNameCn(String nameCn) {
        this.nameCn = nameCn == null ? null : nameCn.trim();
    }

    public String getNameEn() {
        return nameEn;
    }

    public void setNameEn(String nameEn) {
        this.nameEn = nameEn == null ? null : nameEn.trim();
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords == null ? null : keywords.trim();
    }

    public Integer getParentBrandId() {
        return parentBrandId;
    }

    public void setParentBrandId(Integer parentBrandId) {
        this.parentBrandId = parentBrandId;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon == null ? null : icon.trim();
    }

    public String getStory() {
        return story;
    }

    public void setStory(String story) {
        this.story = story == null ? null : story.trim();
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