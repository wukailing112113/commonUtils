/*
 * file comment: Prod.java
 * Copyright(C) All rights reserved. 
 */
package com.wins.shop.entity.prod;

import com.wins.shop.entity.PageEntity;
import java.io.Serializable;
import java.util.Date;

public class Prod extends PageEntity implements Serializable {
    /**
     * field comment: 产品标识
     */
    private Long prodId;

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
     * field comment: 外键，base_brand.brand_id
     */
    private Integer brandId;

    /**
     * field comment: 产品名称
     */
    private String brandName;

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
     * field comment: 库存
     */
    private Integer stock;

    /**
     * field comment: 市场价
     */
    private Float marketPrice;

    /**
     * field comment: 问时价
     */
    private Float winsPrice;

    /**
     * field comment: 促销节省折扣百分比分子
     */
    private Integer salesRatio;

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
     * field comment: 可入的仓库id，逗号分隔。值是 base_repository.repos_id 的组合
     */
    private String repose;

    /**
     * field comment: 计量单位:   件， 包
     */
    private String unit;

    /**
     * field comment: 产品简述
     */
    private String brief;

    /**
     * field comment: 产品描述(静态化)
     */
    private String descp;

    /**
     * field comment: 发货地
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

    public Long getProdId() {
        return prodId;
    }

    public void setProdId(Long prodId) {
        this.prodId = prodId;
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

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Float getMarketPrice() {
        return marketPrice;
    }

    public void setMarketPrice(Float marketPrice) {
        this.marketPrice = marketPrice;
    }

    public Float getWinsPrice() {
        return winsPrice;
    }

    public void setWinsPrice(Float winsPrice) {
        this.winsPrice = winsPrice;
    }

    public Integer getSalesRatio() {
        return salesRatio;
    }

    public void setSalesRatio(Integer salesRatio) {
        this.salesRatio = salesRatio;
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

    public String getRepose() {
        return repose;
    }

    public void setRepose(String repose) {
        this.repose = repose == null ? null : repose.trim();
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

    public String getDescp() {
        return descp;
    }

    public void setDescp(String descp) {
        this.descp = descp == null ? null : descp.trim();
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
}