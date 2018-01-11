package com.cera.chain.service.intf.busi;

import java.util.List;

import com.cera.chain.entity.busi.Product;


public interface IProductService {

	/**
	 * 加载相关的到内存
	 */
    public void loadProductRelate();
	
	/**
	 * 获取最大ProductID
	 */
    public Long getProductId();
    
	/**
	 * 
	 * @param Long prodId
	 * @return
	 */
    public Product loadProductByKey(Long prodId);
	
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
	 * 
	 * @param product
	 */
    public Product addProduct(Product product);
	
	/**
	 * 
	 * @param product
	 */
    public int updateProduct(Product product);
	
	/**
	 * 根据条件查找
	 * @param brand
	 * @return
	 */
    public List<Product> findProduct(Product product);
	
	
    /**
    * @param listProductId
    * @return
    */
    public List<Product> findListProduct(List<Long> listProductId);
    
	/**
	 * 查找在当前条件下的页数
	 * @param product
	 * @return
	 */
    public Long findTotalPage(Product product);

	/**
	 * 删除，一般不提供这样的方法
	 * @param Long prodId
	 */
	public void deleteProduct(Long prodId);
	
}
