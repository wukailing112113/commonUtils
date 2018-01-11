package com.cera.chain.cache.intf.base;

import java.util.List;

import com.cera.chain.entity.base.BaseAttrValue;

public interface IBaseAttrValueRedis {

    /**
    * 加载所有的数据到内存
    */
    public void loadAllBaseAttrValue();

    /**
    * @return
    */
    public Integer getBaseAttrValueId();

    /**
    * @param params
    * @return
    */
    public Long getTotalBaseAttrValue(BaseAttrValue baseAttrValue);

    /**
    *
    * @param id
    * @return
    */
    public BaseAttrValue getBaseAttrValueByKey(Integer valId);


    /**
    * @param listBaseAttrValueId
    * @return
    */
    public List<BaseAttrValue> getBaseAttrValue(List<Integer> listBaseAttrValueId);

	
   
    /**
    * @param prod
    * @return
    */
    public List<BaseAttrValue> getBaseAttrValueByPage(BaseAttrValue baseAttrValue);

    /**
    *
    * @param prod
    * @return
    */
    public BaseAttrValue addBaseAttrValue(BaseAttrValue baseAttrValue);

	/**
    *
    * @param prod
    * @return
    */
    public List<BaseAttrValue> addListBaseAttrValue(List<BaseAttrValue> listBaseAttrValue);
   
    /**
    *
    * @param prod
    * @return
    */
    public int updateBaseAttrValue(BaseAttrValue baseAttrValue, boolean syncdb);

    /**
    *
    * @param prods
    * @return
    */
    public int updateListBaseAttrValue(List<BaseAttrValue> listBaseAttrValue, boolean syncdb);

	/**
    * @param prodId
    */
    public void deleteBaseAttrValue(Integer valId) ;
    
    /**
    * @param prodId
    */
    public void deleteListBaseAttrValue(List<BaseAttrValue> listBaseAttrValue) ;
}
