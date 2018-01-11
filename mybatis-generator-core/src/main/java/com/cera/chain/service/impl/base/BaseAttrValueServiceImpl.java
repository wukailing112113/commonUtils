package com.cera.chain.service.impl.base;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cera.chain.cache.intf.base.IBaseAttrValueRedis;
import com.cera.chain.entity.base.BaseAttrValue;
import com.cera.chain.service.intf.base.IBaseAttrValueService;
import com.cera.chain.servutil.ServConst;
import com.cera.chain.status.CommStatus;

@Service("baseAttrValueService")
public class BaseAttrValueServiceImpl implements IBaseAttrValueService{

	@Autowired
	IBaseAttrValueRedis baseAttrValueRedis;
	
	public void loadBaseAttrValueRelate() {
		baseAttrValueRedis.loadAllBaseAttrValue();
	}

	public BaseAttrValue loadBaseAttrValueByKey(Integer valId) {
		return baseAttrValueRedis.getBaseAttrValueByKey(valId);
	}

	public List<BaseAttrValue> findListBaseAttrValue(List<Integer> listBaseAttrValueId) {
		return baseAttrValueRedis.getBaseAttrValue(listBaseAttrValueId) ;
	}
	
	@Transactional(rollbackFor = Exception.class)
	public BaseAttrValue addBaseAttrValue(BaseAttrValue baseAttrValue) {
		Date now = new Date() ;
		baseAttrValue.setCreateTime(now) ;
		baseAttrValue.setStatus(CommStatus.INUSE.getIndex()) ;
		baseAttrValue.setStatusTime(now) ;
		return baseAttrValueRedis.addBaseAttrValue(baseAttrValue) ;
		
	}

	@Transactional(rollbackFor = Exception.class)
	public int updateBaseAttrValue(BaseAttrValue baseAttrValue) {
		//判断是不是状态修改了
		BaseAttrValue oldBaseAttrValue = baseAttrValueRedis.getBaseAttrValueByKey(baseAttrValue.getValId()) ;
		if(!oldBaseAttrValue.getValId().equals(baseAttrValue.getValId())){
			Date now = new Date() ;
			baseAttrValue.setStatusTime(now);
		}
		baseAttrValueRedis.updateBaseAttrValue(baseAttrValue, true) ;
		
		return baseAttrValueRedis.updateBaseAttrValue(baseAttrValue, true) ;
	}

	public Long findTotalPage(BaseAttrValue baseAttrValue){
		Long totalBaseAttrValue = baseAttrValueRedis.getTotalBaseAttrValue(baseAttrValue) ;
		Integer pageSize = (baseAttrValue.getPageSize() == null || baseAttrValue.getPageSize() == 0) ? ServConst.LoadPageSize : baseAttrValue.getPageSize() ;
		Long totalPage = (long)Math.ceil((double)totalBaseAttrValue / pageSize);
		return totalPage ;
	}
	
	
	public List<BaseAttrValue> findBaseAttrValue(BaseAttrValue baseAttrValue) {
		List<BaseAttrValue> listBaseAttrValue = baseAttrValueRedis.getBaseAttrValueByPage(baseAttrValue) ;
		return listBaseAttrValue ;
	}

	@Transactional(rollbackFor = Exception.class)
	public void deleteBaseAttrValue(Integer valId) {
		baseAttrValueRedis.deleteBaseAttrValue(valId);
	}

}