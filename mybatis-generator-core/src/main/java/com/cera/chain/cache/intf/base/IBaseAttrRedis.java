package com.cera.chain.cache.intf.base;

import java.util.List;

import com.cera.chain.entity.base.BaseAttr;

public interface IBaseAttrRedis {

    /**
    * 加载所有的数据到内存
    */
    public void loadAllBaseAttr();

    /**
    * @return
    */
    public Integer getBaseAttrId();

    /**
    * @param params
    * @return
    */
    public Long getTotalBaseAttr(BaseAttr baseAttr);

    /**
    *
    * @param id
    * @return
    */
    public BaseAttr getBaseAttrByKey(Integer attrId);


    /**
    * @param listBaseAttrId
    * @return
    */
    public List<BaseAttr> getBaseAttr(List<Integer> listBaseAttrId);

	
   
    /**
    * @param prod
    * @return
    */
    public List<BaseAttr> getBaseAttrByPage(BaseAttr baseAttr);

    /**
    *
    * @param prod
    * @return
    */
    public BaseAttr addBaseAttr(BaseAttr baseAttr);

	/**
    *
    * @param prod
    * @return
    */
    public List<BaseAttr> addListBaseAttr(List<BaseAttr> listBaseAttr);
   
    /**
    *
    * @param prod
    * @return
    */
    public int updateBaseAttr(BaseAttr baseAttr, boolean syncdb);

    /**
    *
    * @param prods
    * @return
    */
    public int updateListBaseAttr(List<BaseAttr> listBaseAttr, boolean syncdb);

	/**
    * @param prodId
    */
    public void deleteBaseAttr(Integer attrId) ;
    
    /**
    * @param prodId
    */
    public void deleteListBaseAttr(List<BaseAttr> listBaseAttr) ;
}
