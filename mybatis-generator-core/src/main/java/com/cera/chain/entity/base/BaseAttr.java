/*
 * file comment: BaseAttr.java
 * Copyright(C) All rights reserved. 
 */
package com.cera.chain.entity.base;

import com.cera.chain.entity.PageEntity;
import java.io.Serializable;
import java.util.Date;

public class BaseAttr extends PageEntity implements Serializable {
    /**
     * field comment: 属性名称标识
     */
    private Integer attrId;

    /**
     * field comment: 属性名称
     */
    private String attrName;

    /**
     * field comment: 属性关键字，以逗号分隔
     */
    private String attrKeywords;

    /**
     * field comment: 组合类型：1-原子属性；2-简单组合；3-限制组合
     */
    private Byte groupType;

    /**
     * field comment: 简单组合属性时的属性成员id列表。英文逗号分隔。

复杂组合时候[{attr_id,attr_id2}]

     */
    private String attrMembers;

    /**
     * field comment: 属性类型：1-关键属性(SPU);2-销售属性(SKU),3-其他属性； 其中SKU影响库存。
     */
    private Byte attrType;

    /**
     * field comment: 属性展示方式：0-输入框，1-单选框，2-复选框，3-单选下拉
     */
    private Byte attrDisplay;

    /**
     * field comment: 属性权重
     */
    private Integer attrWeight;

    /**
     * field comment: 属性值ID，多个用,分开
     */
    private String valIds;

    /**
     * field comment: 属性创建时间
     */
    private Date createTime;

    /**
     * field comment: 属性创建时间
     */
    private Date createTimeStart;

    /**
     * field comment: 属性创建时间
     */
    private Date createTimeEnd;

    /**
     * field comment: 状态 0 - 弃用； 1 - 在用
     */
    private Byte status;

    /**
     * field comment: 属性最后修改时间
     */
    private Date statusTime;

    /**
     * field comment: 属性最后修改时间
     */
    private Date statusTimeStart;

    /**
     * field comment: 属性最后修改时间
     */
    private Date statusTimeEnd;

    /**
     * field comment: 最后修改人的ID
     */
    private Long updateSuid;

    private static final long serialVersionUID = 1L;

    public Integer getAttrId() {
        return attrId;
    }

    public void setAttrId(Integer attrId) {
        this.attrId = attrId;
    }

    public String getAttrName() {
        return attrName;
    }

    public void setAttrName(String attrName) {
        this.attrName = attrName == null ? null : attrName.trim();
    }

    public String getAttrKeywords() {
        return attrKeywords;
    }

    public void setAttrKeywords(String attrKeywords) {
        this.attrKeywords = attrKeywords == null ? null : attrKeywords.trim();
    }

    public Byte getGroupType() {
        return groupType;
    }

    public void setGroupType(Byte groupType) {
        this.groupType = groupType;
    }

    public String getAttrMembers() {
        return attrMembers;
    }

    public void setAttrMembers(String attrMembers) {
        this.attrMembers = attrMembers == null ? null : attrMembers.trim();
    }

    public Byte getAttrType() {
        return attrType;
    }

    public void setAttrType(Byte attrType) {
        this.attrType = attrType;
    }

    public Byte getAttrDisplay() {
        return attrDisplay;
    }

    public void setAttrDisplay(Byte attrDisplay) {
        this.attrDisplay = attrDisplay;
    }

    public Integer getAttrWeight() {
        return attrWeight;
    }

    public void setAttrWeight(Integer attrWeight) {
        this.attrWeight = attrWeight;
    }

    public String getValIds() {
        return valIds;
    }

    public void setValIds(String valIds) {
        this.valIds = valIds == null ? null : valIds.trim();
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