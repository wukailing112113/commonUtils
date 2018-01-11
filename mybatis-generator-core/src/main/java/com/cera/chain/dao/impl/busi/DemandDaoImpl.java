/*
 * file comment: DemandDaoImpl.java
 * Copyright(C) All rights reserved. 
 */
package com.cera.chain.dao.impl.busi;

import com.cera.chain.dao.impl.BaseDaoImp;
import com.cera.chain.dao.intf.busi.IDemandDao;
import com.cera.chain.entity.busi.Demand;
import java.util.List;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository("demandDao")
public class DemandDaoImpl extends BaseDaoImp<Demand> implements IDemandDao {

    public Long selectMaxDemandId() {
        return (Long)getMaxId("selectMaxDemandId"); 
    }

    public Demand getDemandByKey(Long demandId) {
        return getPojoById("getDemandByKey", demandId); 
    }

    public List<Demand> selectDemands(Demand demand) {
        return findByProperty("selectDemands", demand);
    }

    public List<Demand> selectDemandByPage(Demand demand) {
        return findByProperty("selectDemandByPage", demand);
    }

    public Long getTotalDemand(Demand demand) {
        return (Long) getRowCount("getTotalDemand", demand); 
    }

    public int deleteDemandByKey(Long demandId) {
        return deleteById("deleteDemandByKey", demandId); 
    }

    public void deleteDemandByList(List<Demand> listDemand) {
        deleteAll("deleteDemandByList", listDemand); 
    }

    public Demand addDemand(Demand demand) {
        return saveAndFetch("addDemand", demand);
    }

    public List<Demand> addDemandList(List<Demand> listDemand) {
        saveList("addDemandList", listDemand);
        return listDemand;
    }

    public int updateDemand(Demand demand) {
        return update("updateDemandSelective", demand); 
    }

    public int updateListDemand(List<Demand> listDemand) {
        int result = 0;
        for (Demand demand : listDemand){
            result = updateDemand(demand);
            if (result == -1) {
                break;
            }
        }
        return result;
    }

    @Autowired
    @Qualifier("busiSqlSessionFactory")
    public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
         super.sqlSessionFactory = sqlSessionFactory;
    }
}