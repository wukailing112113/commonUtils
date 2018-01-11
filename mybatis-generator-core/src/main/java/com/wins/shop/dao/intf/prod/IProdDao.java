/*
 * file comment: IProdDao.java
 * Copyright(C) All rights reserved. 
 */
package com.wins.shop.dao.intf.prod;

import com.wins.shop.entity.prod.Prod;
import java.util.List;

public interface IProdDao {
    Long selectMaxProdId();

    Prod getProdByKey(Long prodId);

    List<Prod> selectProds(Prod prod);

    List<Prod> selectProdByPage(Prod prod);

    Long getTotalProd(Prod prod);

    int deleteProdByKey(Long prodId);

    void deleteProdByList(List<Prod> listProd);

    Prod addProd(Prod prod);

    List<Prod> addProdList(List<Prod> listProd);

    int updateProd(Prod prod);

    int updateListProd(List<Prod> listProd);
}