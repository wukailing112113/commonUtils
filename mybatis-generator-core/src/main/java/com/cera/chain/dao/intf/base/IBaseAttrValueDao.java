/*
 * file comment: IBaseAttrValueDao.java
 * Copyright(C) All rights reserved. 
 */
package com.cera.chain.dao.intf.base;

import com.cera.chain.entity.base.BaseAttrValue;
import java.util.List;

public interface IBaseAttrValueDao {
    Integer selectMaxBaseAttrValueId();

    BaseAttrValue getBaseAttrValueByKey(Integer valId);

    List<BaseAttrValue> selectBaseAttrValues(BaseAttrValue baseAttrValue);

    List<BaseAttrValue> selectBaseAttrValueByPage(BaseAttrValue baseAttrValue);

    Long getTotalBaseAttrValue(BaseAttrValue baseAttrValue);

    int deleteBaseAttrValueByKey(Integer valId);

    void deleteBaseAttrValueByList(List<BaseAttrValue> listBaseAttrValue);

    BaseAttrValue addBaseAttrValue(BaseAttrValue baseAttrValue);

    List<BaseAttrValue> addBaseAttrValueList(List<BaseAttrValue> listBaseAttrValue);

    int updateBaseAttrValue(BaseAttrValue baseAttrValue);

    int updateListBaseAttrValue(List<BaseAttrValue> listBaseAttrValue);
}