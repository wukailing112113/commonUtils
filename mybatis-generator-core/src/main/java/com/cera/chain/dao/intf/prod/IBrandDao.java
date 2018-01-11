/*
 * file comment: IBrandDao.java
 * Copyright(C) All rights reserved. 
 */
package com.cera.chain.dao.intf.prod;

import com.cera.chain.entity.prod.Brand;
import java.util.List;

public interface IBrandDao {
    Integer selectMaxBrandId();

    Brand getBrandByKey(Integer brandId);

    List<Brand> selectBrands(Brand brand);

    List<Brand> selectBrandByPage(Brand brand);

    Long getTotalBrand(Brand brand);

    int deleteBrandByKey(Integer brandId);

    void deleteBrandByList(List<Brand> listBrand);

    Brand addBrand(Brand brand);

    List<Brand> addBrandList(List<Brand> listBrand);

    int updateBrand(Brand brand);

    int updateListBrand(List<Brand> listBrand);
}