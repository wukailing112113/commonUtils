/*
 * file comment: BaseAttrValueDaoImpl.java
 * Copyright(C) All rights reserved. 
 */
package com.cera.chain.dao.impl.base;

import com.cera.chain.dao.impl.BaseDaoImp;
import com.cera.chain.dao.intf.base.IBaseAttrValueDao;
import com.cera.chain.entity.base.BaseAttrValue;
import java.util.List;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository("baseAttrValueDao")
public class BaseAttrValueDaoImpl extends BaseDaoImp<BaseAttrValue> implements IBaseAttrValueDao {

    public Integer selectMaxBaseAttrValueId() {
        return (Integer)getMaxId("selectMaxBaseAttrValueId"); 
    }

    public BaseAttrValue getBaseAttrValueByKey(Integer valId) {
        return getPojoById("getBaseAttrValueByKey", valId); 
    }

    public List<BaseAttrValue> selectBaseAttrValues(BaseAttrValue baseAttrValue) {
        return findByProperty("selectBaseAttrValues", baseAttrValue);
    }

    public List<BaseAttrValue> selectBaseAttrValueByPage(BaseAttrValue baseAttrValue) {
        return findByProperty("selectBaseAttrValueByPage", baseAttrValue);
    }

    public Long getTotalBaseAttrValue(BaseAttrValue baseAttrValue) {
        return (Long) getRowCount("getTotalBaseAttrValue", baseAttrValue); 
    }

    public int deleteBaseAttrValueByKey(Integer valId) {
        return deleteById("deleteBaseAttrValueByKey", valId); 
    }

    public void deleteBaseAttrValueByList(List<BaseAttrValue> listBaseAttrValue) {
        deleteAll("deleteBaseAttrValueByList", listBaseAttrValue); 
    }

    public BaseAttrValue addBaseAttrValue(BaseAttrValue baseAttrValue) {
        return saveAndFetch("addBaseAttrValue", baseAttrValue);
    }

    public List<BaseAttrValue> addBaseAttrValueList(List<BaseAttrValue> listBaseAttrValue) {
        saveList("addBaseAttrValueList", listBaseAttrValue);
        return listBaseAttrValue;
    }

    public int updateBaseAttrValue(BaseAttrValue baseAttrValue) {
        return update("updateBaseAttrValueSelective", baseAttrValue); 
    }

    public int updateListBaseAttrValue(List<BaseAttrValue> listBaseAttrValue) {
        int result = 0;
        for (BaseAttrValue baseAttrValue : listBaseAttrValue){
            result = updateBaseAttrValue(baseAttrValue);
            if (result == -1) {
                break;
            }
        }
        return result;
    }

    @Autowired
    @Qualifier("baseSqlSessionFactory")
    public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
         super.sqlSessionFactory = sqlSessionFactory;
    }
}