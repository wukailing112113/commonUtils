package com.cera.chain.service.intf.busi;

import java.util.List;

import com.cera.chain.entity.busi.Demand;


public interface IDemandService {

	/**
	 * 加载相关的到内存
	 */
    public void loadDemandRelate();
	
	/**
	 * 获取最大DemandID
	 */
    public Long getDemandId();
    
	/**
	 * 
	 * @param Long demandId
	 * @return
	 */
    public Demand loadDemandByKey(Long demandId);
	
    /**
    * @param 
    * @return
    */
    public Demand getDemandByNamestatus(String nickName, Byte status);
    
    
	/**
	 * 
	 * @param demand
	 */
    public Demand addDemand(Demand demand);
	
	/**
	 * 
	 * @param demand
	 */
    public int updateDemand(Demand demand);
	
	/**
	 * 根据条件查找
	 * @param brand
	 * @return
	 */
    public List<Demand> findDemand(Demand demand);
	
	
    /**
    * @param listDemandId
    * @return
    */
    public List<Demand> findListDemand(List<Long> listDemandId);
    
	/**
	 * 查找在当前条件下的页数
	 * @param demand
	 * @return
	 */
    public Long findTotalPage(Demand demand);

	/**
	 * 删除，一般不提供这样的方法
	 * @param Long demandId
	 */
	public void deleteDemand(Long demandId);
	
}
