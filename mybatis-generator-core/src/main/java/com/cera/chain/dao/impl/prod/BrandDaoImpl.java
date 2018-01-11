/*
 * file comment: BrandDaoImpl.java
 * Copyright(C) All rights reserved. 
 */
package com.cera.chain.dao.impl.prod;

import com.cera.chain.dao.impl.BaseDaoImp;
import com.cera.chain.dao.intf.prod.IBrandDao;
import com.cera.chain.entity.prod.Brand;
import java.util.List;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository("brandDao")
public class BrandDaoImpl extends BaseDaoImp<Brand> implements IBrandDao {

    public Integer selectMaxBrandId() {
        return (Integer)getMaxId("selectMaxBrandId"); 
    }

    public Brand getBrandByKey(Integer brandId) {
        return getPojoById("getBrandByKey", brandId); 
    }

    public List<Brand> selectBrands(Brand brand) {
        return findByProperty("selectBrands", brand);
    }

    public List<Brand> selectBrandByPage(Brand brand) {
        return findByProperty("selectBrandByPage", brand);
    }

    public Long getTotalBrand(Brand brand) {
        return (Long) getRowCount("getTotalBrand", brand); 
    }

    public int deleteBrandByKey(Integer brandId) {
        return deleteById("deleteBrandByKey", brandId); 
    }

    public void deleteBrandByList(List<Brand> listBrand) {
        deleteAll("deleteBrandByList", listBrand); 
    }

    public Brand addBrand(Brand brand) {
        return saveAndFetch("addBrand", brand);
    }

    public List<Brand> addBrandList(List<Brand> listBrand) {
        saveList("addBrandList", listBrand);
        return listBrand;
    }

    public int updateBrand(Brand brand) {
        return update("updateBrandSelective", brand); 
    }

    public int updateListBrand(List<Brand> listBrand) {
        int result = 0;
        for (Brand brand : listBrand){
            result = updateBrand(brand);
            if (result == -1) {
                break;
            }
        }
        return result;
    }

    @Autowired
    @Qualifier("prodSqlSessionFactory")
    public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
         super.sqlSessionFactory = sqlSessionFactory;
    }
}