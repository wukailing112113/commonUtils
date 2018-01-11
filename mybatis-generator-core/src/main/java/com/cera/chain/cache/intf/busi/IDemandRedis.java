package com.cera.chain.cache.intf.busi;

import java.util.List;

import com.cera.chain.entity.busi.Demand;
import java.io.Serializable;

public interface IDemandRedis {

    /**
    * 加载所有的数据到内存
    */
    public void loadAllDemand();

    /**
    * @return
    */
    public Long getDemandId();

    /**
    * @param params
    * @return
    */
    public Long getTotalDemand(Demand demand);

    /**
    *
    * @param id
    * @return
    */
    public Demand getDemandByKey(Long demandId);


    /**
    * @param listDemandId
    * @return
    */
    public List<Demand> getDemand(List<Long> listDemandId);

	
    /**
    * @param 
    * @return
    */
    public Demand getDemandByNamestatus(String nickName, Byte status);
    
   
    /**
    * @param prod
    * @return
    */
    public List<Demand> getDemandByPage(Demand demand);

    /**
    *
    * @param prod
    * @return
    */
    public Demand addDemand(Demand demand);

	/**
    *
    * @param prod
    * @return
    */
    public List<Demand> addListDemand(List<Demand> listDemand);
   
    /**
    *
    * @param prod
    * @return
    */
    public int updateDemand(Demand demand, boolean syncdb);

    /**
    *
    * @param prods
    * @return
    */
    public int updateListDemand(List<Demand> listDemand, boolean syncdb);

	/**
    * @param prodId
    */
    public void deleteDemand(Long demandId) ;
    
    /**
    * @param prodId
    */
    public void deleteListDemand(List<Demand> listDemand) ;
    
    /**
    * 暴露个上层直接操作Redis的接口
    */
    public void setSomething(final String key, final Serializable value, final long seconds) ;
    
    
    /**
    * 暴露个上层直接操作Redis的接口
    */
    public <T extends Serializable> T getSomething(final String key, final Class<T> clazz) ;
}
