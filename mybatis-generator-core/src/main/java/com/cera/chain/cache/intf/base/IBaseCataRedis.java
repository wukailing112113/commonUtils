package com.cera.chain.cache.intf.base;

import java.util.List;

import com.cera.chain.entity.base.BaseCata;

public interface IBaseCataRedis {

    /**
    * 加载所有的数据到内存
    */
    public void loadAllBaseCata();

    /**
    * @return
    */
    public Integer getBaseCataId();

    /**
    * @param params
    * @return
    */
    public Long getTotalBaseCata(BaseCata baseCata);

    /**
    *
    * @param id
    * @return
    */
    public BaseCata getBaseCataByKey(Integer cataId);


    /**
    * @param listBaseCataId
    * @return
    */
    public List<BaseCata> getBaseCata(List<Integer> listBaseCataId);

	
   
    /**
    * @param prod
    * @return
    */
    public List<BaseCata> getBaseCataByPage(BaseCata baseCata);

    /**
    *
    * @param prod
    * @return
    */
    public BaseCata addBaseCata(BaseCata baseCata);

	/**
    *
    * @param prod
    * @return
    */
    public List<BaseCata> addListBaseCata(List<BaseCata> listBaseCata);
   
    /**
    *
    * @param prod
    * @return
    */
    public int updateBaseCata(BaseCata baseCata, boolean syncdb);

    /**
    *
    * @param prods
    * @return
    */
    public int updateListBaseCata(List<BaseCata> listBaseCata, boolean syncdb);

	/**
    * @param prodId
    */
    public void deleteBaseCata(Integer cataId) ;
    
    /**
    * @param prodId
    */
    public void deleteListBaseCata(List<BaseCata> listBaseCata) ;
}
