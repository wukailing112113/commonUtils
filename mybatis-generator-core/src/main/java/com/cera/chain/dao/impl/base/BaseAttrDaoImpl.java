/*
 * file comment: BaseAttrDaoImpl.java
 * Copyright(C) All rights reserved. 
 */
package com.cera.chain.dao.impl.base;

import com.cera.chain.dao.impl.BaseDaoImp;
import com.cera.chain.dao.intf.base.IBaseAttrDao;
import com.cera.chain.entity.base.BaseAttr;
import java.util.List;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository("baseAttrDao")
public class BaseAttrDaoImpl extends BaseDaoImp<BaseAttr> implements IBaseAttrDao {

    public Integer selectMaxBaseAttrId() {
        return (Integer)getMaxId("selectMaxBaseAttrId"); 
    }

    public BaseAttr getBaseAttrByKey(Integer attrId) {
        return getPojoById("getBaseAttrByKey", attrId); 
    }

    public List<BaseAttr> selectBaseAttrs(BaseAttr baseAttr) {
        return findByProperty("selectBaseAttrs", baseAttr);
    }

    public List<BaseAttr> selectBaseAttrByPage(BaseAttr baseAttr) {
        return findByProperty("selectBaseAttrByPage", baseAttr);
    }

    public Long getTotalBaseAttr(BaseAttr baseAttr) {
        return (Long) getRowCount("getTotalBaseAttr", baseAttr); 
    }

    public int deleteBaseAttrByKey(Integer attrId) {
        return deleteById("deleteBaseAttrByKey", attrId); 
    }

    public void deleteBaseAttrByList(List<BaseAttr> listBaseAttr) {
        deleteAll("deleteBaseAttrByList", listBaseAttr); 
    }

    public BaseAttr addBaseAttr(BaseAttr baseAttr) {
        return saveAndFetch("addBaseAttr", baseAttr);
    }

    public List<BaseAttr> addBaseAttrList(List<BaseAttr> listBaseAttr) {
        saveList("addBaseAttrList", listBaseAttr);
        return listBaseAttr;
    }

    public int updateBaseAttr(BaseAttr baseAttr) {
        return update("updateBaseAttrSelective", baseAttr); 
    }

    public int updateListBaseAttr(List<BaseAttr> listBaseAttr) {
        int result = 0;
        for (BaseAttr baseAttr : listBaseAttr){
            result = updateBaseAttr(baseAttr);
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