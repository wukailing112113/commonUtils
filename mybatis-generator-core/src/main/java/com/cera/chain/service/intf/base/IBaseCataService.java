package com.cera.chain.service.intf.base;

import java.util.List;

import com.cera.chain.entity.base.BaseCata;


public interface IBaseCataService {

	/**
	 * 加载相关的到内存
	 */
    public void loadBaseCataRelate();
	
	/**
	 * 
	 * @param Integer cataId
	 * @return
	 */
    public BaseCata loadBaseCataByKey(Integer cataId);
	
	/**
	 * 
	 * @param baseCata
	 */
    public BaseCata addBaseCata(BaseCata baseCata);
	
	/**
	 * 
	 * @param baseCata
	 */
    public int updateBaseCata(BaseCata baseCata);
	
	/**
	 * 根据条件查找
	 * @param brand
	 * @return
	 */
    public List<BaseCata> findBaseCata(BaseCata baseCata);
	
	
    /**
    * @param listBaseCataId
    * @return
    */
    public List<BaseCata> findListBaseCata(List<Integer> listBaseCataId);
    
	/**
	 * 查找在当前条件下的页数
	 * @param baseCata
	 * @return
	 */
    public Long findTotalPage(BaseCata baseCata);

	/**
	 * 删除，一般不提供这样的方法
	 * @param Integer cataId
	 */
	public void deleteBaseCata(Integer cataId);
	
}
