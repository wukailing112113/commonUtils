/*
 * file comment: IDemandDao.java
 * Copyright(C) All rights reserved. 
 */
package com.cera.chain.dao.intf.busi;

import com.cera.chain.entity.busi.Demand;
import java.util.List;

public interface IDemandDao {
    Long selectMaxDemandId();

    Demand getDemandByKey(Long demandId);

    List<Demand> selectDemands(Demand demand);

    List<Demand> selectDemandByPage(Demand demand);

    Long getTotalDemand(Demand demand);

    int deleteDemandByKey(Long demandId);

    void deleteDemandByList(List<Demand> listDemand);

    Demand addDemand(Demand demand);

    List<Demand> addDemandList(List<Demand> listDemand);

    int updateDemand(Demand demand);

    int updateListDemand(List<Demand> listDemand);
}