/*
 * file comment: ProductDaoImpl.java
 * Copyright(C) All rights reserved. 
 */
package com.cera.chain.dao.impl.busi;

import com.cera.chain.dao.impl.BaseDaoImp;
import com.cera.chain.dao.intf.busi.IProductDao;
import com.cera.chain.entity.busi.Product;
import java.util.List;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository("productDao")
public class ProductDaoImpl extends BaseDaoImp<Product> implements IProductDao {

    public Long selectMaxProductId() {
        return (Long)getMaxId("selectMaxProductId"); 
    }

    public Product getProductByKey(Long prodId) {
        return getPojoById("getProductByKey", prodId); 
    }

    public List<Product> selectProducts(Product product) {
        return findByProperty("selectProducts", product);
    }

    public List<Product> selectProductByPage(Product product) {
        return findByProperty("selectProductByPage", product);
    }

    public Long getTotalProduct(Product product) {
        return (Long) getRowCount("getTotalProduct", product); 
    }

    public int deleteProductByKey(Long prodId) {
        return deleteById("deleteProductByKey", prodId); 
    }

    public void deleteProductByList(List<Product> listProduct) {
        deleteAll("deleteProductByList", listProduct); 
    }

    public Product addProduct(Product product) {
        return saveAndFetch("addProduct", product);
    }

    public List<Product> addProductList(List<Product> listProduct) {
        saveList("addProductList", listProduct);
        return listProduct;
    }

    public int updateProduct(Product product) {
        return update("updateProductSelective", product); 
    }

    public int updateListProduct(List<Product> listProduct) {
        int result = 0;
        for (Product product : listProduct){
            result = updateProduct(product);
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