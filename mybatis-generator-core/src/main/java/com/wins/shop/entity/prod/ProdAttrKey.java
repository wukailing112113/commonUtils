/*
 * file comment: ProdAttrKey.java
 * Copyright(C) All rights reserved. 
 */
package com.wins.shop.entity.prod;

import com.wins.shop.entity.PageEntity;
import java.io.Serializable;

public class ProdAttrKey extends PageEntity implements Serializable {
    /**
     * field comment: 属性名称标识
     */
    private Integer attrId;

    /**
     * field comment: 商品标识，product.spu_id
     */
    private Long prodId;

    private static final long serialVersionUID = 1L;

    public Integer getAttrId() {
        return attrId;
    }

    public void setAttrId(Integer attrId) {
        this.attrId = attrId;
    }

    public Long getProdId() {
        return prodId;
    }

    public void setProdId(Long prodId) {
        this.prodId = prodId;
    }
}