package com.cera.chain.service.intf.prod;

import java.util.List;

import com.cera.chain.entity.prod.Brand;


public interface IBrandService {

	/**
	 * 加载相关的到内存
	 */
    public void loadBrandRelate();
	
	/**
	 * 
	 * @param Integer brandId
	 * @return
	 */
    public Brand loadBrandByKey(Integer brandId);
	
	/**
	 * 
	 * @param brand
	 */
    public Brand addBrand(Brand brand);
	
	/**
	 * 
	 * @param brand
	 */
    public int updateBrand(Brand brand);
	
	/**
	 * 根据条件查找
	 * @param brand
	 * @return
	 */
    public List<Brand> findBrand(Brand brand);
	
	
    /**
    * @param listBrandId
    * @return
    */
    public List<Brand> findListBrand(List<Integer> listBrandId);
    
	/**
	 * 查找在当前条件下的页数
	 * @param brand
	 * @return
	 */
    public Long findTotalPage(Brand brand);

	/**
	 * 删除，一般不提供这样的方法
	 * @param Integer brandId
	 */
	public void deleteBrand(Integer brandId);
	
}
