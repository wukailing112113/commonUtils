package com.cera.chain.cache.impl.base;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.cera.chain.cache.BaseRedis;
import com.cera.chain.cache.intf.base.IBaseAttrValueRedis;
import com.cera.chain.dao.intf.base.IBaseAttrValueDao;
import com.cera.chain.entity.PageEntity;
import com.cera.chain.entity.base.BaseAttrValue;
import com.cera.chain.util.CommonUtil;

import com.cera.chain.util.StringUtil;

@Repository("baseAttrValueRedis")
public class BaseAttrValueRedisImpl extends BaseRedis<String, BaseAttrValue>implements IBaseAttrValueRedis {

	private static final Logger logger = Logger.getLogger(BaseAttrValueRedisImpl.class);

	@Autowired IBaseAttrValueDao baseAttrValueDao;

	public final static String PRE_KEY = BaseAttrValue.class.getSimpleName();
	private final static String SEQ_BASEATTRVALUE_KEY = new StringBuffer("SEQ:").append(PRE_KEY).toString() ;
	public final static String SET_BASEATTRVALUE_KEY = new StringBuffer("SET:").append(PRE_KEY).toString() ;
	public final static String SET_BASEATTRVALUE_IDX_KEY = new StringBuffer("SET:").append(PRE_KEY).append(":IDX").toString() ;
	
	private final static Integer START_BASEATTRVALUEID = 1000;
	
	public Integer getBaseAttrValueId(){
		Integer valId = Integer.valueOf(incr(SEQ_BASEATTRVALUE_KEY).toString());
		if(valId <= 1){  // 缓存里面还没有，从数据中加载吧
			valId = baseAttrValueDao.selectMaxBaseAttrValueId();
			if( valId == null || valId == 0 ){
				valId = START_BASEATTRVALUEID ;
			}else{
				valId ++;
			}
			set(SEQ_BASEATTRVALUE_KEY, valId.toString(), 0L);
		}
		return valId ;
	}

	@PostConstruct
	public void initMaxBaseAttrValueId(){
		Integer valId = baseAttrValueDao.selectMaxBaseAttrValueId();
		if (valId == null || valId == 0) {
			valId = START_BASEATTRVALUEID ;
		}
		set(SEQ_BASEATTRVALUE_KEY, valId.toString(), 0L);
	}

	/**
	 * Set 里面没有元素，Key自动被删除
	*/
	private void clearAllBaseAttrValue() {
		Set<String> setKey = smembers(SET_BASEATTRVALUE_KEY, String.class) ;
		Set<String> setIdxKey = smembers(SET_BASEATTRVALUE_IDX_KEY, String.class) ;
		List<String> listKey = new ArrayList<String>() ;
		if(setKey != null){
			listKey.addAll(setKey) ;
		}
		if(setIdxKey != null){
			listKey.addAll(setIdxKey) ;
		}
		if(listKey.size() > 0){
			delete(listKey) ;
		}
	}

	/**
	 * 把对象存入Redis
	 * 1. Key-Value 对象;
	 * 2. SET_BASEATTRVALUE_KEY 1中对象的Key的Set;
	 * 3. 索引的对应关系，设计成Set,方便后续扩展成非索引。
	 */
	private void setBaseAttrValue2Redis(BaseAttrValue baseAttrValue){
		String primaryKey = getBaseAttrValueKey(baseAttrValue.getValId());
		sadd(SET_BASEATTRVALUE_KEY, primaryKey);
		set(primaryKey, baseAttrValue, 0L);
		
		// 处理索引情况
	}
	
	
	public void loadAllBaseAttrValue(){
		clearAllBaseAttrValue() ;

		BaseAttrValue baseAttrValue = new BaseAttrValue() ;
		List<BaseAttrValue> listBaseAttrValue = new ArrayList<BaseAttrValue>() ;
		Long total = getTotalBaseAttrValue(baseAttrValue) ;
		Integer totalPage = (int)Math.ceil((double)total / LOAD_PAGE_SIZE);
		baseAttrValue.setPageSize(LOAD_PAGE_SIZE);
		for(int i = 1 ; i <= totalPage; i++){
			baseAttrValue.setPage(i);
			listBaseAttrValue = baseAttrValueDao.selectBaseAttrValues(baseAttrValue);
			for(BaseAttrValue itBaseAttrValue : listBaseAttrValue){
				setBaseAttrValue2Redis(itBaseAttrValue) ;
			}
		}
	}

	/**
	 * 获取记录数，存缓存。
	 * 关于查询结果的缓存
	 * Hset 存储，Key 为 实体类:MD5(条件) total 1 List<BaseAttrValue> 2 List<BaseAttrValue> ....
	 * 过期时间为30秒
	 */
	public Long getTotalBaseAttrValue(BaseAttrValue baseAttrValue) {
		String hsetKey = CommonUtil.getAttrVal4ForRedisKey(baseAttrValue) ;
		Long total = hget(hsetKey,PageEntity.TOTAL,Long.class) ;
		if(total == null || total == 0L){ // 从数据库里面查一遍呗
			total = baseAttrValueDao.getTotalBaseAttrValue(baseAttrValue) ;
			hset(hsetKey,PageEntity.TOTAL,total,PageEntity.EXPIRE) ;
		}
		return total ;
	}

	private String getBaseAttrValueKey(final Integer valId){
		return new StringBuffer(PRE_KEY).append(":").append(valId).toString() ;
	}

	public BaseAttrValue getBaseAttrValueByKey(Integer valId){
		String baseAttrValueKey = getBaseAttrValueKey(valId) ;
		BaseAttrValue baseAttrValue = get(baseAttrValueKey,BaseAttrValue.class) ;
		if(baseAttrValue == null){
			baseAttrValue = baseAttrValueDao.getBaseAttrValueByKey(valId) ;
			if(baseAttrValue != null){
				setBaseAttrValue2Redis(baseAttrValue) ;
			}
		}
		return baseAttrValue ;
	}
	
	
    
    
	// 执行分页查询的逻辑
	public List<BaseAttrValue> getBaseAttrValueByPage(BaseAttrValue baseAttrValue){
		String hsetKey = CommonUtil.getAttrVal4ForRedisKey(baseAttrValue) ;
		if(baseAttrValue.getPage() == null || baseAttrValue.getPage() == 0){ //从第一页开始取
			baseAttrValue.setPage(1);
		}
		baseAttrValue.setIndex((baseAttrValue.getPage() - 1) * baseAttrValue.getPageSize()) ;
		@SuppressWarnings("unchecked")
		ArrayList<BaseAttrValue> listBaseAttrValue = (ArrayList<BaseAttrValue>)hget(hsetKey, baseAttrValue.getPage().toString(), ArrayList.class) ;
		if(listBaseAttrValue == null || listBaseAttrValue.size() == 0){
			logger.debug("=== fetch from database.....");
			listBaseAttrValue = (ArrayList<BaseAttrValue>)baseAttrValueDao.selectBaseAttrValues(baseAttrValue) ;
			hset(hsetKey,baseAttrValue.getPage().toString(),listBaseAttrValue, PageEntity.EXPIRE) ;
		}
		return listBaseAttrValue ;
	}

	public BaseAttrValue addBaseAttrValue(BaseAttrValue baseAttrValue) {
		if(baseAttrValue.getValId() == null || baseAttrValue.getValId() == 0L) {
			baseAttrValue.setValId(getBaseAttrValueId());
		}
		
		baseAttrValue = baseAttrValueDao.addBaseAttrValue(baseAttrValue);
		setBaseAttrValue2Redis(baseAttrValue) ;
		return baseAttrValue;
	}
	
	public List<BaseAttrValue> addListBaseAttrValue(List<BaseAttrValue> listBaseAttrValue){
		for(BaseAttrValue baseAttrValue : listBaseAttrValue){
			if(baseAttrValue.getValId() == null || baseAttrValue.getValId() == 0L) {
				baseAttrValue.setValId(getBaseAttrValueId());
				setBaseAttrValue2Redis(baseAttrValue) ;
			}
		}
		baseAttrValueDao.addBaseAttrValueList(listBaseAttrValue);
		return listBaseAttrValue;
	}
	
	public int updateBaseAttrValue(BaseAttrValue baseAttrValue, boolean syncdb) {
		set(getBaseAttrValueKey(baseAttrValue.getValId()), baseAttrValue, 0L);
		if (syncdb) {
			return baseAttrValueDao.updateBaseAttrValue(baseAttrValue) ;
		}

		return 1;
	}

	public int updateListBaseAttrValue(List<BaseAttrValue> baseAttrValues, boolean syncdb) {
		for(BaseAttrValue p : baseAttrValues){
			updateBaseAttrValue(p,syncdb) ;
		}
		return baseAttrValues.size();
	}

	public List<BaseAttrValue> getBaseAttrValue(List<Integer> listBaseAttrValueId) {
		if(listBaseAttrValueId == null || listBaseAttrValueId.size() == 0 ){
			return new ArrayList<BaseAttrValue>();
		}

		List<String> listBaseAttrValueKey = new ArrayList<String>() ;
		for(Integer i : listBaseAttrValueId){
			listBaseAttrValueKey.add(getBaseAttrValueKey(i)) ;
		}
		return mget(listBaseAttrValueKey,BaseAttrValue.class) ;
	}

	private String delFromBaseAttrValueSet(BaseAttrValue baseAttrValue){
		String primaryKey = getBaseAttrValueKey(baseAttrValue.getValId());
		
		//1.0 从 Key-Set 中删除Key
		srem(SET_BASEATTRVALUE_KEY, primaryKey) ;
		
		//2.0 处理索引情况
		
		return primaryKey ;
	}
	
	public void deleteBaseAttrValue(Integer valId) {
		BaseAttrValue baseAttrValue = getBaseAttrValueByKey(valId) ;
		
		List<String> key2Del = new ArrayList<String>() ;
		
		//从Redis中删除 KeyValue
		String primaryKey = delFromBaseAttrValueSet(baseAttrValue);
		key2Del.add(primaryKey) ;
		delete(key2Del) ;
		
		//从数据库中删除
		baseAttrValueDao.deleteBaseAttrValueByKey(valId);
	}
	
	public void deleteListBaseAttrValue(List<BaseAttrValue> listBaseAttrValue) {
		
		//1.0 从SET 中删除对象Key的关联
		List<String> key2Del = new ArrayList<String>() ;
		for(BaseAttrValue baseAttrValue : listBaseAttrValue){
			baseAttrValue = getBaseAttrValueByKey(baseAttrValue.getValId()) ;
			
			String primaryKey = delFromBaseAttrValueSet(baseAttrValue);
			key2Del.add(primaryKey) ;
		}
		//3.0 删除元素
		delete(key2Del) ;
		//4.0 先从数据库中删除
		baseAttrValueDao.deleteBaseAttrValueByList(listBaseAttrValue);
	}
}