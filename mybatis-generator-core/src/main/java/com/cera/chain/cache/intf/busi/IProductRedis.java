package com.cera.chain.cache.intf.busi;

import java.util.List;

import com.cera.chain.entity.busi.Product;
import java.io.Serializable;

public interface IProductRedis {

    /**
    * 加载所有的数据到内存
    */
    public void loadAllProduct();

    /**
    * @return
    */
    public Long getProductId();

    /**
    * @param params
    * @return
    */
    public Long getTotalProduct(Product product);

    /**
    *
    * @param id
    * @return
    */
    public Product getProductByKey(Long prodId);


    /**
    * @param listProductId
    * @return
    */
    public List<Product> getProduct(List<Long> listProductId);

	
    /**
    * @param 
    * @return
    */
    public Product getProductByUserid(Integer uid);
    
    /**
    * @param 
    * @return
    */
    public Product getProductByBrandcata(Integer cataId, Integer brandId);
    
   
    /**
    * @param prod
    * @return
    */
    public List<Product> getProductByPage(Product product);

    /**
    *
    * @param prod
    * @return
    */
    public Product addProduct(Product product);

	/**
    *
    * @param prod
    * @return
    */
    public List<Product> addListProduct(List<Product> listProduct);
   
    /**
    *
    * @param prod
    * @return
    */
    public int updateProduct(Product product, boolean syncdb);

    /**
    *
    * @param prods
    * @return
    */
    public int updateListProduct(List<Product> listProduct, boolean syncdb);

	/**
    * @param prodId
    */
    public void deleteProduct(Long prodId) ;
    
    /**
    * @param prodId
    */
    public void deleteListProduct(List<Product> listProduct) ;
    
    /**
    * 暴露个上层直接操作Redis的接口
    */
    public void setSomething(final String key, final Serializable value, final long seconds) ;
    
    
    /**
    * 暴露个上层直接操作Redis的接口
    */
    public <T extends Serializable> T getSomething(final String key, final Class<T> clazz) ;
}
