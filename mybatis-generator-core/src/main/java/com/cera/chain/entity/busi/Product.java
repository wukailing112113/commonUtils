/*
 * file comment: Product.java
 * Copyright(C) All rights reserved. 
 */
package com.cera.chain.entity.busi;

import com.cera.chain.entity.PageEntity;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Date;

public class Product extends PageEntity implements Serializable {
    /**
     * field comment: 产品标识
     */
    private Long prodId;

    /**
     * field comment: 归属用户
     */
    private Integer uid;

    /**
     * field comment: 归属用户
     */
    private String nickName;

    /**
     * field comment: 产品名称
     */
    private String prodName;

    /**
     * field comment: 产品名称
     */
    private String prodCode;

    /**
     * field comment: 产品关键字，逗号分隔。
     */
    private String keyWords;

    /**
     * field comment: 归属类目 base_catalog.cata_id
     */
    private Integer cataId;

    /**
     * field comment: 产品名称
     */
    private String cataName;

    /**
     * field comment: 外键，base_brand.brand_id
     */
    private Integer brandId;

    /**
     * field comment: 产品名称
     */
    private String brandName;

    /**
     * field comment: 产品型号 {attrid,attrval}
     */
    private String model;

    /**
     * field comment: 产品规格 {attrid,attrval}
     */
    private String spec;

    /**
     * field comment: 产地
     */
    private String origin;

    /**
     * field comment: 等级
     */
    private String level;

    /**
     * field comment: 计量单位:   件， 包
     */
    private String unit;

    /**
     * field comment: 单价
     */
    private Float price;

    /**
     * field comment: 库存数量
     */
    private Integer stock;

    /**
     * field comment: 市场价
     */
    private Integer minsale;

    /**
     * field comment: 提货地址
     */
    private String pickAddr;

    /**
     * field comment: 展厅地址
     */
    private String exhbAddr;

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
     * field comment: 状态。
     */
    private Byte status;

    /**
     * field comment: 审核状态 0 - 不通过；1 - 审核通过
     */
    private Byte checkStatus;

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
     * field comment: 促销节省价格
     */
    private Float salesPrice;

    /**
     * field comment: 产品图片相册(多个用逗号分开)，存放在服务器上的相对路径。
     */
    private String album;

    /**
     * field comment: 是否新上市
     */
    private Byte fresh;

    /**
     * field comment: 销售类型 0 - 正常销售；1-特价清仓
     */
    private Byte saleType;

    /**
     * field comment: 有效天数
     */
    private Integer validDates;

    /**
     * field comment: 产品简述
     */
    private String brief;

    /**
     * field comment: 产品描述(静态化)
     */
    private String descp;

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

    public Long getProdId() {
        return prodId;
    }

    public void setProdId(Long prodId) {
        this.prodId = prodId;
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

    public String getProdName() {
        return prodName;
    }

    public void setProdName(String prodName) {
        this.prodName = prodName == null ? null : prodName.trim();
    }

    public String getProdCode() {
        return prodCode;
    }

    public void setProdCode(String prodCode) {
        this.prodCode = prodCode == null ? null : prodCode.trim();
    }

    public String getKeyWords() {
        return keyWords;
    }

    public void setKeyWords(String keyWords) {
        this.keyWords = keyWords == null ? null : keyWords.trim();
    }

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

    public Integer getBrandId() {
        return brandId;
    }

    public void setBrandId(Integer brandId) {
        this.brandId = brandId;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName == null ? null : brandName.trim();
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model == null ? null : model.trim();
    }

    public String getSpec() {
        return spec;
    }

    public void setSpec(String spec) {
        this.spec = spec == null ? null : spec.trim();
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin == null ? null : origin.trim();
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level == null ? null : level.trim();
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit == null ? null : unit.trim();
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Integer getMinsale() {
        return minsale;
    }

    public void setMinsale(Integer minsale) {
        this.minsale = minsale;
    }

    public String getPickAddr() {
        return pickAddr;
    }

    public void setPickAddr(String pickAddr) {
        this.pickAddr = pickAddr == null ? null : pickAddr.trim();
    }

    public String getExhbAddr() {
        return exhbAddr;
    }

    public void setExhbAddr(String exhbAddr) {
        this.exhbAddr = exhbAddr == null ? null : exhbAddr.trim();
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

    public Byte getCheckStatus() {
        return checkStatus;
    }

    public void setCheckStatus(Byte checkStatus) {
        this.checkStatus = checkStatus;
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

    public Float getSalesPrice() {
        return salesPrice;
    }

    public void setSalesPrice(Float salesPrice) {
        this.salesPrice = salesPrice;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album == null ? null : album.trim();
    }

    public Byte getFresh() {
        return fresh;
    }

    public void setFresh(Byte fresh) {
        this.fresh = fresh;
    }

    public Byte getSaleType() {
        return saleType;
    }

    public void setSaleType(Byte saleType) {
        this.saleType = saleType;
    }

    public Integer getValidDates() {
        return validDates;
    }

    public void setValidDates(Integer validDates) {
        this.validDates = validDates;
    }

    public String getBrief() {
        return brief;
    }

    public void setBrief(String brief) {
        this.brief = brief == null ? null : brief.trim();
    }

    public String getDescp() {
        return descp;
    }

    public void setDescp(String descp) {
        this.descp = descp == null ? null : descp.trim();
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