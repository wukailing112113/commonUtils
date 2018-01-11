/*
 * file comment: IBaseAttrDao.java
 * Copyright(C) All rights reserved. 
 */
package com.cera.chain.dao.intf.base;

import com.cera.chain.entity.base.BaseAttr;
import java.util.List;

public interface IBaseAttrDao {
    Integer selectMaxBaseAttrId();

    BaseAttr getBaseAttrByKey(Integer attrId);

    List<BaseAttr> selectBaseAttrs(BaseAttr baseAttr);

    List<BaseAttr> selectBaseAttrByPage(BaseAttr baseAttr);

    Long getTotalBaseAttr(BaseAttr baseAttr);

    int deleteBaseAttrByKey(Integer attrId);

    void deleteBaseAttrByList(List<BaseAttr> listBaseAttr);

    BaseAttr addBaseAttr(BaseAttr baseAttr);

    List<BaseAttr> addBaseAttrList(List<BaseAttr> listBaseAttr);

    int updateBaseAttr(BaseAttr baseAttr);

    int updateListBaseAttr(List<BaseAttr> listBaseAttr);
}