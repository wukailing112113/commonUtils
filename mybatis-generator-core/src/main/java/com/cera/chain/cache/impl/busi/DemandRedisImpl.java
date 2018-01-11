package com.cera.chain.cache.impl.busi;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.io.Serializable;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.cera.chain.cache.BaseRedis;
import com.cera.chain.cache.intf.busi.IDemandRedis;
import com.cera.chain.dao.intf.busi.IDemandDao;
import com.cera.chain.entity.PageEntity;
import com.cera.chain.entity.busi.Demand;
import com.cera.chain.util.CommonUtil;

import com.cera.chain.util.StringUtil;

@Repository("demandRedis")
public class DemandRedisImpl extends BaseRedis<String, Demand>implements IDemandRedis {

	private static final Logger logger = Logger.getLogger(DemandRedisImpl.class);

	@Autowired IDemandDao demandDao;

	public final static String PRE_KEY = Demand.class.getSimpleName();
	private final static String SEQ_DEMAND_KEY = new StringBuffer("SEQ:").append(PRE_KEY).toString() ;
	public final static String SET_DEMAND_KEY = new StringBuffer("SET:").append(PRE_KEY).toString() ;
	public final static String SET_DEMAND_IDX_KEY = new StringBuffer("SET:").append(PRE_KEY).append(":IDX").toString() ;
	
	private final static Long START_DEMANDID = 1000000L;
	
	public Long getDemandId(){
		Long demandId = Long.valueOf(incr(SEQ_DEMAND_KEY).toString());
		if(demandId <= 1L){  // 缓存里面还没有，从数据中加载吧
			demandId = demandDao.selectMaxDemandId();
			if( demandId == null || demandId == 0 ){
				demandId = START_DEMANDID ;
			}else{
				demandId ++;
			}
			set(SEQ_DEMAND_KEY, demandId.toString(), 0L);
		}
		return demandId ;
	}

	@PostConstruct
	public void initMaxDemandId(){
		Long demandId = demandDao.selectMaxDemandId();
		if (demandId == null || demandId == 0) {
			demandId = START_DEMANDID ;
		}
		set(SEQ_DEMAND_KEY, demandId.toString(), 0L);
	}

	/**
	 * Set 里面没有元素，Key自动被删除
	*/
	private void clearAllDemand() {
		Set<String> setKey = smembers(SET_DEMAND_KEY, String.class) ;
		Set<String> setIdxKey = smembers(SET_DEMAND_IDX_KEY, String.class) ;
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
	 * 2. SET_DEMAND_KEY 1中对象的Key的Set;
	 * 3. 索引的对应关系，设计成Set,方便后续扩展成非索引。
	 */
	private void setDemand2Redis(Demand demand){
		String primaryKey = getDemandKey(demand.getDemandId());
		sadd(SET_DEMAND_KEY, primaryKey);
		set(primaryKey, demand, 0L);
		
		// 处理索引情况
		String IndexSetKey = null ;
		IndexSetKey = getDemandIdxSetKey(demand.getNickName(), demand.getStatus() );
		if(StringUtil.isNotEmptyString(IndexSetKey)){
			sadd(SET_DEMAND_IDX_KEY, IndexSetKey) ;
			sadd(IndexSetKey,primaryKey) ;
		}
	}
	
	
	public void loadAllDemand(){
		clearAllDemand() ;

		Demand demand = new Demand() ;
		List<Demand> listDemand = new ArrayList<Demand>() ;
		Long total = getTotalDemand(demand) ;
		Integer totalPage = (int)Math.ceil((double)total / LOAD_PAGE_SIZE);
		demand.setPageSize(LOAD_PAGE_SIZE);
		for(int i = 1 ; i <= totalPage; i++){
			demand.setPage(i);
			listDemand = demandDao.selectDemands(demand);
			for(Demand itDemand : listDemand){
				setDemand2Redis(itDemand) ;
			}
		}
	}

	/**
	 * 获取记录数，存缓存。
	 * 关于查询结果的缓存
	 * Hset 存储，Key 为 实体类:MD5(条件) total 1 List<Demand> 2 List<Demand> ....
	 * 过期时间为30秒
	 */
	public Long getTotalDemand(Demand demand) {
		String hsetKey = CommonUtil.getAttrVal4ForRedisKey(demand) ;
		Long total = hget(hsetKey,PageEntity.TOTAL,Long.class) ;
		if(total == null || total == 0L){ // 从数据库里面查一遍呗
			total = demandDao.getTotalDemand(demand) ;
			hset(hsetKey,PageEntity.TOTAL,total,PageEntity.EXPIRE) ;
		}
		return total ;
	}

	private String getDemandKey(final Long demandId){
		return new StringBuffer(PRE_KEY).append(":").append(demandId).toString() ;
	}

	public Demand getDemandByKey(Long demandId){
		String primaryKey = getDemandKey(demandId) ;
		Demand demand = get(primaryKey,Demand.class) ;
		if(demand == null){
			demand = demandDao.getDemandByKey(demandId) ;
			if(demand != null){
				setDemand2Redis(demand) ;
			}
		}
		return demand ;
	}
	
	
	private String getDemandIdxSetKey(String nickName, Byte status){
		return new StringBuffer(SET_DEMAND_KEY).append(":NickName:").append(nickName).append(":Status:").append(status).toString() ;
    }
    public Demand getDemandByNamestatus(String nickName, Byte status){
    	String indexSetKey = getDemandIdxSetKey(nickName, status) ;
    	Set<String> setDemandKey = smembers(indexSetKey, String.class) ;
    	Demand demand = null ;
		if(setDemandKey != null){
			List<String> listKey = new ArrayList<String>(setDemandKey) ;
			demand = get(listKey.get(0), Demand.class) ;
		}
		// 从数据库里面查
		if(demand == null){
			demand = new Demand() ;
			demand.setNickName(nickName) ;
			demand.setStatus(status) ;
			List<Demand> listDemand = demandDao.selectDemands(demand) ;
			if(listDemand != null && listDemand.size() == 1){
				demand = listDemand.get(0) ;
			}else{
				demand = null ;
			}
			if(demand != null){
				setDemand2Redis(demand) ;
			}
		}
		return demand;
    }
    
    
	// 执行分页查询的逻辑
	public List<Demand> getDemandByPage(Demand demand){
		String hsetKey = CommonUtil.getAttrVal4ForRedisKey(demand) ;
		if(demand.getPage() == null || demand.getPage() == 0){ //从第一页开始取
			demand.setPage(1);
		}
		demand.setIndex((demand.getPage() - 1) * demand.getPageSize()) ;
		@SuppressWarnings("unchecked")
		ArrayList<Demand> listDemand = (ArrayList<Demand>)hget(hsetKey, demand.getPage().toString(), ArrayList.class) ;
		if(listDemand == null || listDemand.size() == 0){
			logger.debug("=== fetch from database.....");
			listDemand = (ArrayList<Demand>)demandDao.selectDemands(demand) ;
			hset(hsetKey,demand.getPage().toString(),listDemand, PageEntity.EXPIRE) ;
		}
		return listDemand ;
	}

	public Demand addDemand(Demand demand) {
		if(demand.getDemandId() == null || demand.getDemandId() == 0L) {
			demand.setDemandId(getDemandId());
		}
		
		demand = demandDao.addDemand(demand);
		setDemand2Redis(demand) ;
		return demand;
	}
	
	public List<Demand> addListDemand(List<Demand> listDemand){
		for(Demand demand : listDemand){
			if(demand.getDemandId() == null || demand.getDemandId() == 0L) {
				demand.setDemandId(getDemandId());
				setDemand2Redis(demand) ;
			}
		}
		demandDao.addDemandList(listDemand);
		return listDemand;
	}
	
	public int updateDemand(Demand demand, boolean syncdb) {
		String primaryKey =  getDemandKey(demand.getDemandId());
		Demand oldDemand = get(primaryKey, Demand.class) ;
		if (oldDemand != null) {
			if (!(oldDemand.getNickName().equals(demand.getNickName()) && oldDemand.getStatus().equals(demand.getStatus()) )) {
				delFromDemandSet(oldDemand);
				setDemand2Redis(demand) ;
			} else {
				set(getDemandKey(demand.getDemandId()), demand, 0L);
			}
		} else {
			setDemand2Redis(demand) ;
		}
		
		if (syncdb) {
			return demandDao.updateDemand(demand) ;
		}

		return 1;
	}

	public int updateListDemand(List<Demand> demands, boolean syncdb) {
		for(Demand p : demands){
			updateDemand(p,syncdb) ;
		}
		return demands.size();
	}

	public List<Demand> getDemand(List<Long> listDemandId) {
		if(listDemandId == null || listDemandId.size() == 0 ){
			return new ArrayList<Demand>();
		}

		List<String> listDemandKey = new ArrayList<String>() ;
		for(Long i : listDemandId){
			listDemandKey.add(getDemandKey(i)) ;
		}
		return mget(listDemandKey,Demand.class) ;
	}

	private String delFromDemandSet(Demand demand){
		String primaryKey = getDemandKey(demand.getDemandId());
		
		//1.0 从 Key-Set 中删除Key
		srem(SET_DEMAND_KEY, primaryKey) ;
		
		//2.0 处理索引情况
		String IndexSetKey = null ;
		if(demand != null){
			IndexSetKey = getDemandIdxSetKey(demand.getNickName(), demand.getStatus() );
			if(StringUtil.isNotEmptyString(IndexSetKey)){
				srem(SET_DEMAND_IDX_KEY,IndexSetKey) ;
				srem(IndexSetKey,primaryKey) ;
			}
		}
		
		return primaryKey ;
	}
	
	public void deleteDemand(Long demandId) {
		Demand demand = getDemandByKey(demandId) ;
		
		List<String> key2Del = new ArrayList<String>() ;
		
		//从Redis中删除 KeyValue
		String primaryKey = delFromDemandSet(demand);
		key2Del.add(primaryKey) ;
		delete(key2Del) ;
		
		//从数据库中删除
		demandDao.deleteDemandByKey(demandId);
	}
	
	public void deleteListDemand(List<Demand> listDemand) {
		
		//1.0 从SET 中删除对象Key的关联
		List<String> key2Del = new ArrayList<String>() ;
		for(Demand demand : listDemand){
			demand = getDemandByKey(demand.getDemandId()) ;
			
			String primaryKey = delFromDemandSet(demand);
			key2Del.add(primaryKey) ;
		}
		//3.0 删除元素
		delete(key2Del) ;
		//4.0 先从数据库中删除
		demandDao.deleteDemandByList(listDemand);
	}
	
	public void setSomething(final String key, final Serializable value, final long seconds) {
		set(key, value, seconds) ;
	}
    
    public <T extends Serializable> T getSomething(final String key, final Class<T> clazz) {
    	return get(key, clazz) ;
    }
}