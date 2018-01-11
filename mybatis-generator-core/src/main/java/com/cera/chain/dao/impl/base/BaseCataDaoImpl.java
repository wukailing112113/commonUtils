/*
 * file comment: BaseCataDaoImpl.java
 * Copyright(C) All rights reserved. 
 */
package com.cera.chain.dao.impl.base;

import com.cera.chain.dao.impl.BaseDaoImp;
import com.cera.chain.dao.intf.base.IBaseCataDao;
import com.cera.chain.entity.base.BaseCata;
import java.util.List;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository("baseCataDao")
public class BaseCataDaoImpl extends BaseDaoImp<BaseCata> implements IBaseCataDao {

    public Integer selectMaxBaseCataId() {
        return (Integer)getMaxId("selectMaxBaseCataId"); 
    }

    public BaseCata getBaseCataByKey(Integer cataId) {
        return getPojoById("getBaseCataByKey", cataId); 
    }

    public List<BaseCata> selectBaseCatas(BaseCata baseCata) {
        return findByProperty("selectBaseCatas", baseCata);
    }

    public List<BaseCata> selectBaseCataByPage(BaseCata baseCata) {
        return findByProperty("selectBaseCataByPage", baseCata);
    }

    public Long getTotalBaseCata(BaseCata baseCata) {
        return (Long) getRowCount("getTotalBaseCata", baseCata); 
    }

    public int deleteBaseCataByKey(Integer cataId) {
        return deleteById("deleteBaseCataByKey", cataId); 
    }

    public void deleteBaseCataByList(List<BaseCata> listBaseCata) {
        deleteAll("deleteBaseCataByList", listBaseCata); 
    }

    public BaseCata addBaseCata(BaseCata baseCata) {
        return saveAndFetch("addBaseCata", baseCata);
    }

    public List<BaseCata> addBaseCataList(List<BaseCata> listBaseCata) {
        saveList("addBaseCataList", listBaseCata);
        return listBaseCata;
    }

    public int updateBaseCata(BaseCata baseCata) {
        return update("updateBaseCataSelective", baseCata); 
    }

    public int updateListBaseCata(List<BaseCata> listBaseCata) {
        int result = 0;
        for (BaseCata baseCata : listBaseCata){
            result = updateBaseCata(baseCata);
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