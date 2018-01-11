/*
 * file comment: ProdAttr.java
 * Copyright(C) All rights reserved. 
 */
package com.wins.shop.entity.prod;

import java.io.Serializable;
import java.util.Date;

public class ProdAttr extends ProdAttrKey implements Serializable {
    /**
     * field comment: 属性名称
     */
    private String attrName;

    /**
     * field comment: 属性关键字，以逗号分隔
     */
    private String keywords;

    /**
     * field comment: 属性名称标识
     */
    private Integer valId;

    /**
     * field comment: 属性值, 如果是输入框的，直接值值，如果是非输入框的按[val_id:attr_val]方式保存，多个用","分开
     */
    private String attrVal;

    /**
     * field comment: 属性最后修改时间
     */
    private Date createTime;

    /**
     * field comment: 属性最后修改时间
     */
    private Date createTimeStart;

    /**
     * field comment: 属性最后修改时间
     */
    private Date createTimeEnd;

    /**
     * field comment: 状态： 0 - 弃用；1-启用
     */
    private Byte status;

    private Date statusTime;

    private Date statusTimeStart;

    private Date statusTimeEnd;

    /**
     * field comment: 最后修改人的ID
     */
    private String updates;

    private static final long serialVersionUID = 1L;

    public String getAttrName() {
        return attrName;
    }

    public void setAttrName(String attrName) {
        this.attrName = attrName == null ? null : attrName.trim();
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords == null ? null : keywords.trim();
    }

    public Integer getValId() {
        return valId;
    }

    public void setValId(Integer valId) {
        this.valId = valId;
    }

    public String getAttrVal() {
        return attrVal;
    }

    public void setAttrVal(String attrVal) {
        this.attrVal = attrVal == null ? null : attrVal.trim();
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