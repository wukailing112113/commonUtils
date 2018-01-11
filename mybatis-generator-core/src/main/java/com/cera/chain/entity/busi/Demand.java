/*
 * file comment: Demand.java
 * Copyright(C) All rights reserved. 
 */
package com.cera.chain.entity.busi;

import com.cera.chain.entity.PageEntity;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Date;

public class Demand extends PageEntity implements Serializable {
    /**
     * field comment: 需求ID
     */
    private Long demandId;

    /**
     * field comment: 归属用户
     */
    private Integer uid;

    /**
     * field comment: 归属用户
     */
    private String nickName;

    /**
     * field comment: 需求标题
     */
    private String title;

    /**
     * field comment: 需求关键字
     */
    private String keyWords;

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
     * field comment: 报价方式：1,按商品数量报价;2,按销售属性报价
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
     * field comment: 需求的产品图册
     */
    private String album;

    /**
     * field comment: 计量单位:   件， 包
     */
    private String unit;

    /**
     * field comment: 产品简述
     */
    private String brief;

    /**
     * field comment: 需求产品属性{[{attrname:value}]}
     */
    private String attrs;

    /**
     * field comment: 产地要求
     */
    private String origin;

    /**
     * field comment: 最后修改时间
     */
    private Date lastUpdate;

    /**
     * field comment: 最后修改时间
     */
    private Date lastUpdateStart;

    /**
     * field comment: 最后修改时间
     */
    private Date lastUpdateEnd;

    /**
     * field comment: 最后修改人的ID
     */
    private String updates;

    private static final long serialVersionUID = 1L;

    private String orderBy;

    public Long getDemandId() {
        return demandId;
    }

    public void setDemandId(Long demandId) {
        this.demandId = demandId;
    }

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName == null ? null : nickName.trim();
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

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album == null ? null : album.trim();
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit == null ? null : unit.trim();
    }

    public String getBrief() {
        return brief;
    }

    public void setBrief(String brief) {
        this.brief = brief == null ? null : brief.trim();
    }

    public String getAttrs() {
        return attrs;
    }

    public void setAttrs(String attrs) {
        this.attrs = attrs == null ? null : attrs.trim();
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin == null ? null : origin.trim();
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public Date getLastUpdateStart() {
        return lastUpdateStart;
    }

    public void setLastUpdateStart(Date lastUpdateStart) {
        this.lastUpdateStart = lastUpdateStart;
    }

    public Date getLastUpdateEnd() {
        return lastUpdateEnd;
    }

    public void setLastUpdateEnd(Date lastUpdateEnd) {
        this.lastUpdateEnd = lastUpdateEnd;
    }

    public String getUpdates() {
        return updates;
    }

    public void setUpdates(String updates) {
        this.updates = updates == null ? null : updates.trim();
    }

    public void addOrderBy(String fieldName, boolean bIsDesc) {
        if(orderBy == null){
            orderBy = "" ;
        }
        fieldName = toCamel(fieldName) ;
        StringBuffer sb = new StringBuffer(orderBy) ;
        Field[] fields = this.getClass().getDeclaredFields() ;  
        for (Field field : fields) {
            if (field.getName().equals(fieldName)) {
                if (sb.length() == 0)  {
                    sb.append(" ORDER BY " ).append(toUnderLine(fieldName)) ;
                    if (bIsDesc) {
                        sb.append(" DESC") ;
                    }
                } else {
                    sb.append("," ).append(toUnderLine(fieldName)) ;
                    if (bIsDesc) {
                        sb.append(" DESC") ;
                    }
                }
                break;
            }
        }
        orderBy = sb.toString() ;
    }
}