/*
 * file comment: IBaseCataDao.java
 * Copyright(C) All rights reserved. 
 */
package com.cera.chain.dao.intf.base;

import com.cera.chain.entity.base.BaseCata;
import java.util.List;

public interface IBaseCataDao {
    Integer selectMaxBaseCataId();

    BaseCata getBaseCataByKey(Integer cataId);

    List<BaseCata> selectBaseCatas(BaseCata baseCata);

    List<BaseCata> selectBaseCataByPage(BaseCata baseCata);

    Long getTotalBaseCata(BaseCata baseCata);

    int deleteBaseCataByKey(Integer cataId);

    void deleteBaseCataByList(List<BaseCata> listBaseCata);

    BaseCata addBaseCata(BaseCata baseCata);

    List<BaseCata> addBaseCataList(List<BaseCata> listBaseCata);

    int updateBaseCata(BaseCata baseCata);

    int updateListBaseCata(List<BaseCata> listBaseCata);
}