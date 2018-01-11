package com.cera.chain.service.impl.base;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cera.chain.cache.intf.base.IBaseCataRedis;
import com.cera.chain.entity.base.BaseCata;
import com.cera.chain.service.intf.base.IBaseCataService;
import com.cera.chain.servutil.ServConst;
import com.cera.chain.status.CommStatus;

@Service("baseCataService")
public class BaseCataServiceImpl implements IBaseCataService{

	@Autowired
	IBaseCataRedis baseCataRedis;
	
	public void loadBaseCataRelate() {
		baseCataRedis.loadAllBaseCata();
	}

	public BaseCata loadBaseCataByKey(Integer cataId) {
		return baseCataRedis.getBaseCataByKey(cataId);
	}

	public List<BaseCata> findListBaseCata(List<Integer> listBaseCataId) {
		return baseCataRedis.getBaseCata(listBaseCataId) ;
	}
	
	@Transactional(rollbackFor = Exception.class)
	public BaseCata addBaseCata(BaseCata baseCata) {
		Date now = new Date() ;
		baseCata.setCreateTime(now) ;
		baseCata.setStatus(CommStatus.INUSE.getIndex()) ;
		baseCata.setStatusTime(now) ;
		return baseCataRedis.addBaseCata(baseCata) ;
		
	}

	@Transactional(rollbackFor = Exception.class)
	public int updateBaseCata(BaseCata baseCata) {
		//判断是不是状态修改了
		BaseCata oldBaseCata = baseCataRedis.getBaseCataByKey(baseCata.getCataId()) ;
		if(!oldBaseCata.getCataId().equals(baseCata.getCataId())){
			Date now = new Date() ;
			baseCata.setStatusTime(now);
		}
		baseCataRedis.updateBaseCata(baseCata, true) ;
		
		return baseCataRedis.updateBaseCata(baseCata, true) ;
	}

	public Long findTotalPage(BaseCata baseCata){
		Long totalBaseCata = baseCataRedis.getTotalBaseCata(baseCata) ;
		Integer pageSize = (baseCata.getPageSize() == null || baseCata.getPageSize() == 0) ? ServConst.LoadPageSize : baseCata.getPageSize() ;
		Long totalPage = (long)Math.ceil((double)totalBaseCata / pageSize);
		return totalPage ;
	}
	
	
	public List<BaseCata> findBaseCata(BaseCata baseCata) {
		List<BaseCata> listBaseCata = baseCataRedis.getBaseCataByPage(baseCata) ;
		return listBaseCata ;
	}

	@Transactional(rollbackFor = Exception.class)
	public void deleteBaseCata(Integer cataId) {
		baseCataRedis.deleteBaseCata(cataId);
	}

}