package com.cera.chain.service.impl.base;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cera.chain.cache.intf.base.IBaseAttrRedis;
import com.cera.chain.entity.base.BaseAttr;
import com.cera.chain.service.intf.base.IBaseAttrService;
import com.cera.chain.servutil.ServConst;
import com.cera.chain.status.CommStatus;

@Service("baseAttrService")
public class BaseAttrServiceImpl implements IBaseAttrService{

	@Autowired
	IBaseAttrRedis baseAttrRedis;
	
	public void loadBaseAttrRelate() {
		baseAttrRedis.loadAllBaseAttr();
	}

	public BaseAttr loadBaseAttrByKey(Integer attrId) {
		return baseAttrRedis.getBaseAttrByKey(attrId);
	}

	public List<BaseAttr> findListBaseAttr(List<Integer> listBaseAttrId) {
		return baseAttrRedis.getBaseAttr(listBaseAttrId) ;
	}
	
	@Transactional(rollbackFor = Exception.class)
	public BaseAttr addBaseAttr(BaseAttr baseAttr) {
		Date now = new Date() ;
		baseAttr.setCreateTime(now) ;
		baseAttr.setStatus(CommStatus.INUSE.getIndex()) ;
		baseAttr.setStatusTime(now) ;
		return baseAttrRedis.addBaseAttr(baseAttr) ;
		
	}

	@Transactional(rollbackFor = Exception.class)
	public int updateBaseAttr(BaseAttr baseAttr) {
		//判断是不是状态修改了
		BaseAttr oldBaseAttr = baseAttrRedis.getBaseAttrByKey(baseAttr.getAttrId()) ;
		if(!oldBaseAttr.getAttrId().equals(baseAttr.getAttrId())){
			Date now = new Date() ;
			baseAttr.setStatusTime(now);
		}
		baseAttrRedis.updateBaseAttr(baseAttr, true) ;
		
		return baseAttrRedis.updateBaseAttr(baseAttr, true) ;
	}

	public Long findTotalPage(BaseAttr baseAttr){
		Long totalBaseAttr = baseAttrRedis.getTotalBaseAttr(baseAttr) ;
		Integer pageSize = (baseAttr.getPageSize() == null || baseAttr.getPageSize() == 0) ? ServConst.LoadPageSize : baseAttr.getPageSize() ;
		Long totalPage = (long)Math.ceil((double)totalBaseAttr / pageSize);
		return totalPage ;
	}
	
	
	public List<BaseAttr> findBaseAttr(BaseAttr baseAttr) {
		List<BaseAttr> listBaseAttr = baseAttrRedis.getBaseAttrByPage(baseAttr) ;
		return listBaseAttr ;
	}

	@Transactional(rollbackFor = Exception.class)
	public void deleteBaseAttr(Integer attrId) {
		baseAttrRedis.deleteBaseAttr(attrId);
	}

}