/*
 * file comment: BaseCataDaoImpl.java
 * Copyright(C) All rights reserved. 
 */
package com.wins.shop.dao.impl.base;

import com.wins.shop.dao.impl.BaseDaoImp;
import com.wins.shop.dao.intf.base.IBaseCataDao;
import com.wins.shop.entity.base.BaseCata;
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

    public BaseCata selectBaseCataById(Integer cataId) {
        return getPojoById("selectByPrimaryKey", cataId); 
    }

    public List<BaseCata> selectBaseCatas(BaseCata baseCata) {
        return findByProperty("selectBaseCatas", baseCata);
    }

    public List<BaseCata> selectBaseCataByPage(BaseCata baseCata) {
        return findByProperty("selectBaseCatas", baseCata);
    }

    public Long getTotalBaseCata(BaseCata baseCata) {
        return (Long) getRowCount("getTotalBaseCata", baseCata); 
    }

    public int deleteBaseCataById(Integer cataId) {
        return deleteById("deleteBaseCataById", cataId); 
    }

    public void deleteBaseCataByList(List<BaseCata> listBaseCata) {
        deleteAll("deleteBaseCataByList", listBaseCata); 
    }

    public BaseCata insertBaseCata(BaseCata baseCata) {
        return saveAndFetch("insertBaseCata", baseCata);
    }

    public List<BaseCata> insertBaseCataList(List<BaseCata> listBaseCata) {
        saveList("insertBaseCataList", listBaseCata);
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