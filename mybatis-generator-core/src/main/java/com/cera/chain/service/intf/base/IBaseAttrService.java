package com.cera.chain.service.intf.base;

import java.util.List;

import com.cera.chain.entity.base.BaseAttr;


public interface IBaseAttrService {

	/**
	 * 加载相关的到内存
	 */
    public void loadBaseAttrRelate();
	
	/**
	 * 
	 * @param Integer attrId
	 * @return
	 */
    public BaseAttr loadBaseAttrByKey(Integer attrId);
	
	/**
	 * 
	 * @param baseAttr
	 */
    public BaseAttr addBaseAttr(BaseAttr baseAttr);
	
	/**
	 * 
	 * @param baseAttr
	 */
    public int updateBaseAttr(BaseAttr baseAttr);
	
	/**
	 * 根据条件查找
	 * @param brand
	 * @return
	 */
    public List<BaseAttr> findBaseAttr(BaseAttr baseAttr);
	
	
    /**
    * @param listBaseAttrId
    * @return
    */
    public List<BaseAttr> findListBaseAttr(List<Integer> listBaseAttrId);
    
	/**
	 * 查找在当前条件下的页数
	 * @param baseAttr
	 * @return
	 */
    public Long findTotalPage(BaseAttr baseAttr);

	/**
	 * 删除，一般不提供这样的方法
	 * @param Integer attrId
	 */
	public void deleteBaseAttr(Integer attrId);
	
}
