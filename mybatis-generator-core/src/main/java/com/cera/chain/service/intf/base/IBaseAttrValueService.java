package com.cera.chain.service.intf.base;

import java.util.List;

import com.cera.chain.entity.base.BaseAttrValue;


public interface IBaseAttrValueService {

	/**
	 * 加载相关的到内存
	 */
    public void loadBaseAttrValueRelate();
	
	/**
	 * 
	 * @param Integer valId
	 * @return
	 */
    public BaseAttrValue loadBaseAttrValueByKey(Integer valId);
	
	/**
	 * 
	 * @param baseAttrValue
	 */
    public BaseAttrValue addBaseAttrValue(BaseAttrValue baseAttrValue);
	
	/**
	 * 
	 * @param baseAttrValue
	 */
    public int updateBaseAttrValue(BaseAttrValue baseAttrValue);
	
	/**
	 * 根据条件查找
	 * @param brand
	 * @return
	 */
    public List<BaseAttrValue> findBaseAttrValue(BaseAttrValue baseAttrValue);
	
	
    /**
    * @param listBaseAttrValueId
    * @return
    */
    public List<BaseAttrValue> findListBaseAttrValue(List<Integer> listBaseAttrValueId);
    
	/**
	 * 查找在当前条件下的页数
	 * @param baseAttrValue
	 * @return
	 */
    public Long findTotalPage(BaseAttrValue baseAttrValue);

	/**
	 * 删除，一般不提供这样的方法
	 * @param Integer valId
	 */
	public void deleteBaseAttrValue(Integer valId);
	
}
