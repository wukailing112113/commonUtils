/*
 * file comment: IProductDao.java
 * Copyright(C) All rights reserved. 
 */
package com.cera.chain.dao.intf.busi;

import com.cera.chain.entity.busi.Product;
import java.util.List;

public interface IProductDao {
    Long selectMaxProductId();

    Product getProductByKey(Long prodId);

    List<Product> selectProducts(Product product);

    List<Product> selectProductByPage(Product product);

    Long getTotalProduct(Product product);

    int deleteProductByKey(Long prodId);

    void deleteProductByList(List<Product> listProduct);

    Product addProduct(Product product);

    List<Product> addProductList(List<Product> listProduct);

    int updateProduct(Product product);

    int updateListProduct(List<Product> listProduct);
}