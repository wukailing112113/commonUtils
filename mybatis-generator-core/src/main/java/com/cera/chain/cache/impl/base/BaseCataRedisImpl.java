package com.cera.chain.cache.impl.base;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.cera.chain.cache.BaseRedis;
import com.cera.chain.cache.intf.base.IBaseCataRedis;
import com.cera.chain.dao.intf.base.IBaseCataDao;
import com.cera.chain.entity.PageEntity;
import com.cera.chain.entity.base.BaseCata;
import com.cera.chain.util.CommonUtil;

import com.cera.chain.util.StringUtil;

@Repository("baseCataRedis")
public class BaseCataRedisImpl extends BaseRedis<String, BaseCata>implements IBaseCataRedis {

	private static final Logger logger = Logger.getLogger(BaseCataRedisImpl.class);

	@Autowired IBaseCataDao baseCataDao;

	public final static String PRE_KEY = BaseCata.class.getSimpleName();
	private final static String SEQ_BASECATA_KEY = new StringBuffer("SEQ:").append(PRE_KEY).toString() ;
	public final static String SET_BASECATA_KEY = new StringBuffer("SET:").append(PRE_KEY).toString() ;
	public final static String SET_BASECATA_IDX_KEY = new StringBuffer("SET:").append(PRE_KEY).append(":IDX").toString() ;
	
	private final static Integer START_BASECATAID = 1000;
	
	public Integer getBaseCataId(){
		Integer cataId = Integer.valueOf(incr(SEQ_BASECATA_KEY).toString());
		if(cataId <= 1){  // 缓存里面还没有，从数据中加载吧
			cataId = baseCataDao.selectMaxBaseCataId();
			if( cataId == null || cataId == 0 ){
				cataId = START_BASECATAID ;
			}else{
				cataId ++;
			}
			set(SEQ_BASECATA_KEY, cataId.toString(), 0L);
		}
		return cataId ;
	}

	@PostConstruct
	public void initMaxBaseCataId(){
		Integer cataId = baseCataDao.selectMaxBaseCataId();
		if (cataId == null || cataId == 0) {
			cataId = START_BASECATAID ;
		}
		set(SEQ_BASECATA_KEY, cataId.toString(), 0L);
	}

	/**
	 * Set 里面没有元素，Key自动被删除
	*/
	private void clearAllBaseCata() {
		Set<String> setKey = smembers(SET_BASECATA_KEY, String.class) ;
		Set<String> setIdxKey = smembers(SET_BASECATA_IDX_KEY, String.class) ;
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
	 * 2. SET_BASECATA_KEY 1中对象的Key的Set;
	 * 3. 索引的对应关系，设计成Set,方便后续扩展成非索引。
	 */
	private void setBaseCata2Redis(BaseCata baseCata){
		String primaryKey = getBaseCataKey(baseCata.getCataId());
		sadd(SET_BASECATA_KEY, primaryKey);
		set(primaryKey, baseCata, 0L);
		
		// 处理索引情况
	}
	
	
	public void loadAllBaseCata(){
		clearAllBaseCata() ;

		BaseCata baseCata = new BaseCata() ;
		List<BaseCata> listBaseCata = new ArrayList<BaseCata>() ;
		Long total = getTotalBaseCata(baseCata) ;
		Integer totalPage = (int)Math.ceil((double)total / LOAD_PAGE_SIZE);
		baseCata.setPageSize(LOAD_PAGE_SIZE);
		for(int i = 1 ; i <= totalPage; i++){
			baseCata.setPage(i);
			listBaseCata = baseCataDao.selectBaseCatas(baseCata);
			for(BaseCata itBaseCata : listBaseCata){
				setBaseCata2Redis(itBaseCata) ;
			}
		}
	}

	/**
	 * 获取记录数，存缓存。
	 * 关于查询结果的缓存
	 * Hset 存储，Key 为 实体类:MD5(条件) total 1 List<BaseCata> 2 List<BaseCata> ....
	 * 过期时间为30秒
	 */
	public Long getTotalBaseCata(BaseCata baseCata) {
		String hsetKey = CommonUtil.getAttrVal4ForRedisKey(baseCata) ;
		Long total = hget(hsetKey,PageEntity.TOTAL,Long.class) ;
		if(total == null || total == 0L){ // 从数据库里面查一遍呗
			total = baseCataDao.getTotalBaseCata(baseCata) ;
			hset(hsetKey,PageEntity.TOTAL,total,PageEntity.EXPIRE) ;
		}
		return total ;
	}

	private String getBaseCataKey(final Integer cataId){
		return new StringBuffer(PRE_KEY).append(":").append(cataId).toString() ;
	}

	public BaseCata getBaseCataByKey(Integer cataId){
		String baseCataKey = getBaseCataKey(cataId) ;
		BaseCata baseCata = get(baseCataKey,BaseCata.class) ;
		if(baseCata == null){
			baseCata = baseCataDao.getBaseCataByKey(cataId) ;
			if(baseCata != null){
				setBaseCata2Redis(baseCata) ;
			}
		}
		return baseCata ;
	}
	
	
    
    
	// 执行分页查询的逻辑
	public List<BaseCata> getBaseCataByPage(BaseCata baseCata){
		String hsetKey = CommonUtil.getAttrVal4ForRedisKey(baseCata) ;
		if(baseCata.getPage() == null || baseCata.getPage() == 0){ //从第一页开始取
			baseCata.setPage(1);
		}
		baseCata.setIndex((baseCata.getPage() - 1) * baseCata.getPageSize()) ;
		@SuppressWarnings("unchecked")
		ArrayList<BaseCata> listBaseCata = (ArrayList<BaseCata>)hget(hsetKey, baseCata.getPage().toString(), ArrayList.class) ;
		if(listBaseCata == null || listBaseCata.size() == 0){
			logger.debug("=== fetch from database.....");
			listBaseCata = (ArrayList<BaseCata>)baseCataDao.selectBaseCatas(baseCata) ;
			hset(hsetKey,baseCata.getPage().toString(),listBaseCata, PageEntity.EXPIRE) ;
		}
		return listBaseCata ;
	}

	public BaseCata addBaseCata(BaseCata baseCata) {
		if(baseCata.getCataId() == null || baseCata.getCataId() == 0L) {
			baseCata.setCataId(getBaseCataId());
		}
		
		baseCata = baseCataDao.addBaseCata(baseCata);
		setBaseCata2Redis(baseCata) ;
		return baseCata;
	}
	
	public List<BaseCata> addListBaseCata(List<BaseCata> listBaseCata){
		for(BaseCata baseCata : listBaseCata){
			if(baseCata.getCataId() == null || baseCata.getCataId() == 0L) {
				baseCata.setCataId(getBaseCataId());
				setBaseCata2Redis(baseCata) ;
			}
		}
		baseCataDao.addBaseCataList(listBaseCata);
		return listBaseCata;
	}
	
	public int updateBaseCata(BaseCata baseCata, boolean syncdb) {
		set(getBaseCataKey(baseCata.getCataId()), baseCata, 0L);
		if (syncdb) {
			return baseCataDao.updateBaseCata(baseCata) ;
		}

		return 1;
	}

	public int updateListBaseCata(List<BaseCata> baseCatas, boolean syncdb) {
		for(BaseCata p : baseCatas){
			updateBaseCata(p,syncdb) ;
		}
		return baseCatas.size();
	}

	public List<BaseCata> getBaseCata(List<Integer> listBaseCataId) {
		if(listBaseCataId == null || listBaseCataId.size() == 0 ){
			return new ArrayList<BaseCata>();
		}

		List<String> listBaseCataKey = new ArrayList<String>() ;
		for(Integer i : listBaseCataId){
			listBaseCataKey.add(getBaseCataKey(i)) ;
		}
		return mget(listBaseCataKey,BaseCata.class) ;
	}

	private String delFromBaseCataSet(BaseCata baseCata){
		String primaryKey = getBaseCataKey(baseCata.getCataId());
		
		//1.0 从 Key-Set 中删除Key
		srem(SET_BASECATA_KEY, primaryKey) ;
		
		//2.0 处理索引情况
		
		return primaryKey ;
	}
	
	public void deleteBaseCata(Integer cataId) {
		BaseCata baseCata = getBaseCataByKey(cataId) ;
		
		List<String> key2Del = new ArrayList<String>() ;
		
		//从Redis中删除 KeyValue
		String primaryKey = delFromBaseCataSet(baseCata);
		key2Del.add(primaryKey) ;
		delete(key2Del) ;
		
		//从数据库中删除
		baseCataDao.deleteBaseCataByKey(cataId);
	}
	
	public void deleteListBaseCata(List<BaseCata> listBaseCata) {
		
		//1.0 从SET 中删除对象Key的关联
		List<String> key2Del = new ArrayList<String>() ;
		for(BaseCata baseCata : listBaseCata){
			baseCata = getBaseCataByKey(baseCata.getCataId()) ;
			
			String primaryKey = delFromBaseCataSet(baseCata);
			key2Del.add(primaryKey) ;
		}
		//3.0 删除元素
		delete(key2Del) ;
		//4.0 先从数据库中删除
		baseCataDao.deleteBaseCataByList(listBaseCata);
	}
}