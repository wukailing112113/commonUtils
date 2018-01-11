/*
 * file comment: BaseCata.java
 * Copyright(C) All rights reserved. 
 */
package com.cera.chain.entity.base;

import com.cera.chain.entity.PageEntity;
import java.io.Serializable;
import java.util.Date;

public class BaseCata extends PageEntity implements Serializable {
    /**
     * field comment: 类目标识，一级类目以1开头的两位数字；二级类目以一级类目开头再加二位数字的枚举；三级类目以二级类目开头，加二位数字的枚举；

如：有父子关系的三个类目
1级： 11
2级： 1101~1199
3级： 110101~110199



     */
    private Integer cataId;

    /**
     * field comment: 类目名称
     */
    private String cataName;

    /**
     * field comment: 类目类别：1-一级类目，2-二级类目，3-三级类目
     */
    private Byte cataType;

    /**
     * field comment: 所属父类目id
     */
    private Integer cataParentId;

    /**
     * field comment: 类目关键字，逗号分隔
     */
    private String cataKeywords;

    /**
     * field comment: 类目权重
     */
    private Integer cataWeight;

    /**
     * field comment: 类目创建时间
     */
    private Date createTime;

    /**
     * field comment: 类目创建时间
     */
    private Date createTimeStart;

    /**
     * field comment: 类目创建时间
     */
    private Date createTimeEnd;

    /**
     * field comment: 状态： 0 - 弃用；1-在用
     */
    private Byte status;

    /**
     * field comment: 类目最后修改时间
     */
    private Date statusTime;

    /**
     * field comment: 类目最后修改时间
     */
    private Date statusTimeStart;

    /**
     * field comment: 类目最后修改时间
     */
    private Date statusTimeEnd;

    /**
     * field comment: 最后修改人的suid
     */
    private Long updateSuid;

    /**
     * field comment: 目录索引：一级目录则为目录ID，二级目录为一级目录ID+二级目录ID；三级目录则为一级目录ID+二级目录ID+三级目录ID。\r\n\r\n 比如有如下父子目录\r\n一级： 11\r\n二级： 21\r\n三级： 31\r\n\r\n则三级目录的索引为 112131\r\n\r\n
     */
    private Integer cataIndex;

    private static final long serialVersionUID = 1L;

    public Integer getCataId() {
        return cataId;
    }

    public void setCataId(Integer cataId) {
        this.cataId = cataId;
    }

    public String getCataName() {
        return cataName;
    }

    public void setCataName(String cataName) {
        this.cataName = cataName == null ? null : cataName.trim();
    }

    public Byte getCataType() {
        return cataType;
    }

    public void setCataType(Byte cataType) {
        this.cataType = cataType;
    }

    public Integer getCataParentId() {
        return cataParentId;
    }

    public void setCataParentId(Integer cataParentId) {
        this.cataParentId = cataParentId;
    }

    public String getCataKeywords() {
        return cataKeywords;
    }

    public void setCataKeywords(String cataKeywords) {
        this.cataKeywords = cataKeywords == null ? null : cataKeywords.trim();
    }

    public Integer getCataWeight() {
        return cataWeight;
    }

    public void setCataWeight(Integer cataWeight) {
        this.cataWeight = cataWeight;
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

    public Integer getCataIndex() {
        return cataIndex;
    }

    public void setCataIndex(Integer cataIndex) {
        this.cataIndex = cataIndex;
    }
}