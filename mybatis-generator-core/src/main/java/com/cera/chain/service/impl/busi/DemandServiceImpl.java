package com.cera.chain.service.impl.busi;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cera.chain.cache.intf.busi.IDemandRedis;
import com.cera.chain.entity.busi.Demand;
import com.cera.chain.service.intf.busi.IDemandService;
import com.cera.chain.servutil.ServConst;
import com.cera.chain.status.CommStatus;

@Service("demandService")
public class DemandServiceImpl implements IDemandService{

	private static final Logger logger = Logger.getLogger(DemandServiceImpl.class);
	
	@Autowired
	IDemandRedis demandRedis;
	
	public void loadDemandRelate() {
		demandRedis.loadAllDemand();
	}
	
    public Long getDemandId(){
    	return demandRedis.getDemandId() ;
    }
    

	public Demand loadDemandByKey(Long demandId) {
		return demandRedis.getDemandByKey(demandId);
	}
	
    public Demand getDemandByNamestatus(String nickName, Byte status){
    	return demandRedis.getDemandByNamestatus(nickName, status) ;
    }
    
	public List<Demand> findListDemand(List<Long> listDemandId) {
		return demandRedis.getDemand(listDemandId) ;
	}
	
	@Transactional(rollbackFor = Exception.class)
	public Demand addDemand(Demand demand) {
		Date now = new Date() ;
		demand.setCreateTime(now) ;
		demand.setStatus(CommStatus.INUSE.getIndex()) ;
		demand.setStatusTime(now) ;
		return demandRedis.addDemand(demand) ;
	}

	@Transactional(rollbackFor = Exception.class)
	public int updateDemand(Demand demand) {
		//判断是不是状态修改了
		Demand oldDemand = demandRedis.getDemandByKey(demand.getDemandId()) ;
		if(!oldDemand.getDemandId().equals(demand.getDemandId())){
			Date now = new Date() ;
			demand.setStatusTime(now);
		}
		demandRedis.updateDemand(demand, true) ;
		
		return demandRedis.updateDemand(demand, true) ;
	}

	public Long findTotalPage(Demand demand){
		Long totalDemand = demandRedis.getTotalDemand(demand) ;
		Integer pageSize = (demand.getPageSize() == null || demand.getPageSize() == 0) ? ServConst.LoadPageSize : demand.getPageSize() ;
		Long totalPage = (long)Math.ceil((double)totalDemand / pageSize);
		return totalPage ;
	}
	
	
	public List<Demand> findDemand(Demand demand) {
		List<Demand> listDemand = demandRedis.getDemandByPage(demand) ;
		return listDemand ;
	}

	@Transactional(rollbackFor = Exception.class)
	public void deleteDemand(Long demandId) {
		demandRedis.deleteDemand(demandId);
	}

}