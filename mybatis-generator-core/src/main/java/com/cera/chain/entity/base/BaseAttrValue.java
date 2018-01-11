/*
 * file comment: BaseAttrValue.java
 * Copyright(C) All rights reserved. 
 */
package com.cera.chain.entity.base;

import com.cera.chain.entity.PageEntity;
import java.io.Serializable;
import java.util.Date;

public class BaseAttrValue extends PageEntity implements Serializable {
    /**
     * field comment: 属性值标识
     */
    private Integer valId;

    /**
     * field comment: 属性值
     */
    private String attrVal;

    /**
     * field comment: 属性值创建时间
     */
    private Date createTime;

    /**
     * field comment: 属性值创建时间
     */
    private Date createTimeStart;

    /**
     * field comment: 属性值创建时间
     */
    private Date createTimeEnd;

    /**
     * field comment: 状态： 0 - 弃用；1-在用
     */
    private Byte status;

    /**
     * field comment: 属性值最后修改时间
     */
    private Date statusTime;

    /**
     * field comment: 属性值最后修改时间
     */
    private Date statusTimeStart;

    /**
     * field comment: 属性值最后修改时间
     */
    private Date statusTimeEnd;

    /**
     * field comment: 最后修改人的ID
     */
    private Long updateSuid;

    private static final long serialVersionUID = 1L;

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

    public Long getUpdateSuid() {
        return updateSuid;
    }

    public void setUpdateSuid(Long updateSuid) {
        this.updateSuid = updateSuid;
    }
}