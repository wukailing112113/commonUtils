package com.cera.chain.cache.impl.base;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.cera.chain.cache.BaseRedis;
import com.cera.chain.cache.intf.base.IBaseAttrRedis;
import com.cera.chain.dao.intf.base.IBaseAttrDao;
import com.cera.chain.entity.PageEntity;
import com.cera.chain.entity.base.BaseAttr;
import com.cera.chain.util.CommonUtil;

import com.cera.chain.util.StringUtil;

@Repository("baseAttrRedis")
public class BaseAttrRedisImpl extends BaseRedis<String, BaseAttr>implements IBaseAttrRedis {

	private static final Logger logger = Logger.getLogger(BaseAttrRedisImpl.class);

	@Autowired IBaseAttrDao baseAttrDao;

	public final static String PRE_KEY = BaseAttr.class.getSimpleName();
	private final static String SEQ_BASEATTR_KEY = new StringBuffer("SEQ:").append(PRE_KEY).toString() ;
	public final static String SET_BASEATTR_KEY = new StringBuffer("SET:").append(PRE_KEY).toString() ;
	public final static String SET_BASEATTR_IDX_KEY = new StringBuffer("SET:").append(PRE_KEY).append(":IDX").toString() ;
	
	private final static Integer START_BASEATTRID = 1000;
	
	public Integer getBaseAttrId(){
		Integer attrId = Integer.valueOf(incr(SEQ_BASEATTR_KEY).toString());
		if(attrId <= 1){  // 缓存里面还没有，从数据中加载吧
			attrId = baseAttrDao.selectMaxBaseAttrId();
			if( attrId == null || attrId == 0 ){
				attrId = START_BASEATTRID ;
			}else{
				attrId ++;
			}
			set(SEQ_BASEATTR_KEY, attrId.toString(), 0L);
		}
		return attrId ;
	}

	@PostConstruct
	public void initMaxBaseAttrId(){
		Integer attrId = baseAttrDao.selectMaxBaseAttrId();
		if (attrId == null || attrId == 0) {
			attrId = START_BASEATTRID ;
		}
		set(SEQ_BASEATTR_KEY, attrId.toString(), 0L);
	}

	/**
	 * Set 里面没有元素，Key自动被删除
	*/
	private void clearAllBaseAttr() {
		Set<String> setKey = smembers(SET_BASEATTR_KEY, String.class) ;
		Set<String> setIdxKey = smembers(SET_BASEATTR_IDX_KEY, String.class) ;
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
	 * 2. SET_BASEATTR_KEY 1中对象的Key的Set;
	 * 3. 索引的对应关系，设计成Set,方便后续扩展成非索引。
	 */
	private void setBaseAttr2Redis(BaseAttr baseAttr){
		String primaryKey = getBaseAttrKey(baseAttr.getAttrId());
		sadd(SET_BASEATTR_KEY, primaryKey);
		set(primaryKey, baseAttr, 0L);
		
		// 处理索引情况
	}
	
	
	public void loadAllBaseAttr(){
		clearAllBaseAttr() ;

		BaseAttr baseAttr = new BaseAttr() ;
		List<BaseAttr> listBaseAttr = new ArrayList<BaseAttr>() ;
		Long total = getTotalBaseAttr(baseAttr) ;
		Integer totalPage = (int)Math.ceil((double)total / LOAD_PAGE_SIZE);
		baseAttr.setPageSize(LOAD_PAGE_SIZE);
		for(int i = 1 ; i <= totalPage; i++){
			baseAttr.setPage(i);
			listBaseAttr = baseAttrDao.selectBaseAttrs(baseAttr);
			for(BaseAttr itBaseAttr : listBaseAttr){
				setBaseAttr2Redis(itBaseAttr) ;
			}
		}
	}

	/**
	 * 获取记录数，存缓存。
	 * 关于查询结果的缓存
	 * Hset 存储，Key 为 实体类:MD5(条件) total 1 List<BaseAttr> 2 List<BaseAttr> ....
	 * 过期时间为30秒
	 */
	public Long getTotalBaseAttr(BaseAttr baseAttr) {
		String hsetKey = CommonUtil.getAttrVal4ForRedisKey(baseAttr) ;
		Long total = hget(hsetKey,PageEntity.TOTAL,Long.class) ;
		if(total == null || total == 0L){ // 从数据库里面查一遍呗
			total = baseAttrDao.getTotalBaseAttr(baseAttr) ;
			hset(hsetKey,PageEntity.TOTAL,total,PageEntity.EXPIRE) ;
		}
		return total ;
	}

	private String getBaseAttrKey(final Integer attrId){
		return new StringBuffer(PRE_KEY).append(":").append(attrId).toString() ;
	}

	public BaseAttr getBaseAttrByKey(Integer attrId){
		String baseAttrKey = getBaseAttrKey(attrId) ;
		BaseAttr baseAttr = get(baseAttrKey,BaseAttr.class) ;
		if(baseAttr == null){
			baseAttr = baseAttrDao.getBaseAttrByKey(attrId) ;
			if(baseAttr != null){
				setBaseAttr2Redis(baseAttr) ;
			}
		}
		return baseAttr ;
	}
	
	
    
    
	// 执行分页查询的逻辑
	public List<BaseAttr> getBaseAttrByPage(BaseAttr baseAttr){
		String hsetKey = CommonUtil.getAttrVal4ForRedisKey(baseAttr) ;
		if(baseAttr.getPage() == null || baseAttr.getPage() == 0){ //从第一页开始取
			baseAttr.setPage(1);
		}
		baseAttr.setIndex((baseAttr.getPage() - 1) * baseAttr.getPageSize()) ;
		@SuppressWarnings("unchecked")
		ArrayList<BaseAttr> listBaseAttr = (ArrayList<BaseAttr>)hget(hsetKey, baseAttr.getPage().toString(), ArrayList.class) ;
		if(listBaseAttr == null || listBaseAttr.size() == 0){
			logger.debug("=== fetch from database.....");
			listBaseAttr = (ArrayList<BaseAttr>)baseAttrDao.selectBaseAttrs(baseAttr) ;
			hset(hsetKey,baseAttr.getPage().toString(),listBaseAttr, PageEntity.EXPIRE) ;
		}
		return listBaseAttr ;
	}

	public BaseAttr addBaseAttr(BaseAttr baseAttr) {
		if(baseAttr.getAttrId() == null || baseAttr.getAttrId() == 0L) {
			baseAttr.setAttrId(getBaseAttrId());
		}
		
		baseAttr = baseAttrDao.addBaseAttr(baseAttr);
		setBaseAttr2Redis(baseAttr) ;
		return baseAttr;
	}
	
	public List<BaseAttr> addListBaseAttr(List<BaseAttr> listBaseAttr){
		for(BaseAttr baseAttr : listBaseAttr){
			if(baseAttr.getAttrId() == null || baseAttr.getAttrId() == 0L) {
				baseAttr.setAttrId(getBaseAttrId());
				setBaseAttr2Redis(baseAttr) ;
			}
		}
		baseAttrDao.addBaseAttrList(listBaseAttr);
		return listBaseAttr;
	}
	
	public int updateBaseAttr(BaseAttr baseAttr, boolean syncdb) {
		set(getBaseAttrKey(baseAttr.getAttrId()), baseAttr, 0L);
		if (syncdb) {
			return baseAttrDao.updateBaseAttr(baseAttr) ;
		}

		return 1;
	}

	public int updateListBaseAttr(List<BaseAttr> baseAttrs, boolean syncdb) {
		for(BaseAttr p : baseAttrs){
			updateBaseAttr(p,syncdb) ;
		}
		return baseAttrs.size();
	}

	public List<BaseAttr> getBaseAttr(List<Integer> listBaseAttrId) {
		if(listBaseAttrId == null || listBaseAttrId.size() == 0 ){
			return new ArrayList<BaseAttr>();
		}

		List<String> listBaseAttrKey = new ArrayList<String>() ;
		for(Integer i : listBaseAttrId){
			listBaseAttrKey.add(getBaseAttrKey(i)) ;
		}
		return mget(listBaseAttrKey,BaseAttr.class) ;
	}

	private String delFromBaseAttrSet(BaseAttr baseAttr){
		String primaryKey = getBaseAttrKey(baseAttr.getAttrId());
		
		//1.0 从 Key-Set 中删除Key
		srem(SET_BASEATTR_KEY, primaryKey) ;
		
		//2.0 处理索引情况
		
		return primaryKey ;
	}
	
	public void deleteBaseAttr(Integer attrId) {
		BaseAttr baseAttr = getBaseAttrByKey(attrId) ;
		
		List<String> key2Del = new ArrayList<String>() ;
		
		//从Redis中删除 KeyValue
		String primaryKey = delFromBaseAttrSet(baseAttr);
		key2Del.add(primaryKey) ;
		delete(key2Del) ;
		
		//从数据库中删除
		baseAttrDao.deleteBaseAttrByKey(attrId);
	}
	
	public void deleteListBaseAttr(List<BaseAttr> listBaseAttr) {
		
		//1.0 从SET 中删除对象Key的关联
		List<String> key2Del = new ArrayList<String>() ;
		for(BaseAttr baseAttr : listBaseAttr){
			baseAttr = getBaseAttrByKey(baseAttr.getAttrId()) ;
			
			String primaryKey = delFromBaseAttrSet(baseAttr);
			key2Del.add(primaryKey) ;
		}
		//3.0 删除元素
		delete(key2Del) ;
		//4.0 先从数据库中删除
		baseAttrDao.deleteBaseAttrByList(listBaseAttr);
	}
}