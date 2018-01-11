/*
 * file comment: IProdAttrDao.java
 * Copyright(C) All rights reserved. 
 */
package com.wins.shop.dao.intf.prod;

import com.wins.shop.entity.prod.ProdAttr;
import java.util.List;

public interface IProdAttrDao {
    ProdAttr getProdAttrByKey(Integer attrId, Long prodId);

    List<ProdAttr> selectProdAttrs(ProdAttr prodAttr);

    List<ProdAttr> selectProdAttrByPage(ProdAttr prodAttr);

    Long getTotalProdAttr(ProdAttr prodAttr);

    int deleteProdAttrByKey(Integer attrId, Long prodId);

    void deleteProdAttrByList(List<ProdAttr> listProdAttr);

    ProdAttr addProdAttr(ProdAttr prodAttr);

    List<ProdAttr> addProdAttrList(List<ProdAttr> listProdAttr);

    int updateProdAttr(ProdAttr prodAttr);

    int updateListProdAttr(List<ProdAttr> listProdAttr);
}