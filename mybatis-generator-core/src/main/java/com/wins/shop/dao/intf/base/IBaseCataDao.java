/*
 * file comment: IBaseCataDao.java
 * Copyright(C) All rights reserved. 
 */
package com.wins.shop.dao.intf.base;

import com.wins.shop.entity.base.BaseCata;
import java.util.List;

public interface IBaseCataDao {
    Integer selectMaxBaseCataId();

    BaseCata selectBaseCataById(Integer cataId);

    List<BaseCata> selectBaseCatas(BaseCata baseCata);

    List<BaseCata> selectBaseCataByPage(BaseCata baseCata);

    Long getTotalBaseCata(BaseCata baseCata);

    int deleteBaseCataById(Integer cataId);

    void deleteBaseCataByList(List<BaseCata> listBaseCata);

    BaseCata insertBaseCata(BaseCata baseCata);

    List<BaseCata> insertBaseCataList(List<BaseCata> listBaseCata);

    int updateBaseCata(BaseCata baseCata);

    int updateListBaseCata(List<BaseCata> listBaseCata);
}