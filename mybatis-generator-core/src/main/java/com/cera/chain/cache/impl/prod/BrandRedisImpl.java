package com.cera.chain.cache.impl.prod;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.cera.chain.cache.BaseRedis;
import com.cera.chain.cache.intf.prod.IBrandRedis;
import com.cera.chain.dao.intf.prod.IBrandDao;
import com.cera.chain.entity.PageEntity;
import com.cera.chain.entity.prod.Brand;
import com.cera.chain.util.CommonUtil;

import com.cera.chain.util.StringUtil;

@Repository("brandRedis")
public class BrandRedisImpl extends BaseRedis<String, Brand>implements IBrandRedis {

	private static final Logger logger = Logger.getLogger(BrandRedisImpl.class);

	@Autowired IBrandDao brandDao;

	public final static String PRE_KEY = Brand.class.getSimpleName();
	private final static String SEQ_BRAND_KEY = new StringBuffer("SEQ:").append(PRE_KEY).toString() ;
	public final static String SET_BRAND_KEY = new StringBuffer("SET:").append(PRE_KEY).toString() ;
	public final static String SET_BRAND_IDX_KEY = new StringBuffer("SET:").append(PRE_KEY).append(":IDX").toString() ;
	
	private final static Integer START_BRANDID = 1000;
	
	public Integer getBrandId(){
		Integer brandId = Integer.valueOf(incr(SEQ_BRAND_KEY).toString());
		if(brandId <= 1){  // 缓存里面还没有，从数据中加载吧
			brandId = brandDao.selectMaxBrandId();
			if( brandId == null || brandId == 0 ){
				brandId = START_BRANDID ;
			}else{
				brandId ++;
			}
			set(SEQ_BRAND_KEY, brandId.toString(), 0L);
		}
		return brandId ;
	}

	@PostConstruct
	public void initMaxBrandId(){
		Integer brandId = brandDao.selectMaxBrandId();
		if (brandId == null || brandId == 0) {
			brandId = START_BRANDID ;
		}
		set(SEQ_BRAND_KEY, brandId.toString(), 0L);
	}

	/**
	 * Set 里面没有元素，Key自动被删除
	*/
	private void clearAllBrand() {
		Set<String> setKey = smembers(SET_BRAND_KEY, String.class) ;
		Set<String> setIdxKey = smembers(SET_BRAND_IDX_KEY, String.class) ;
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
	 * 2. SET_BRAND_KEY 1中对象的Key的Set;
	 * 3. 索引的对应关系，设计成Set,方便后续扩展成非索引。
	 */
	private void setBrand2Redis(Brand brand){
		String primaryKey = getBrandKey(brand.getBrandId());
		sadd(SET_BRAND_KEY, primaryKey);
		set(primaryKey, brand, 0L);
		
		// 处理索引情况
	}
	
	
	public void loadAllBrand(){
		clearAllBrand() ;

		Brand brand = new Brand() ;
		List<Brand> listBrand = new ArrayList<Brand>() ;
		Long total = getTotalBrand(brand) ;
		Integer totalPage = (int)Math.ceil((double)total / LOAD_PAGE_SIZE);
		brand.setPageSize(LOAD_PAGE_SIZE);
		for(int i = 1 ; i <= totalPage; i++){
			brand.setPage(i);
			listBrand = brandDao.selectBrands(brand);
			for(Brand itBrand : listBrand){
				setBrand2Redis(itBrand) ;
			}
		}
	}

	/**
	 * 获取记录数，存缓存。
	 * 关于查询结果的缓存
	 * Hset 存储，Key 为 实体类:MD5(条件) total 1 List<Brand> 2 List<Brand> ....
	 * 过期时间为30秒
	 */
	public Long getTotalBrand(Brand brand) {
		String hsetKey = CommonUtil.getAttrVal4ForRedisKey(brand) ;
		Long total = hget(hsetKey,PageEntity.TOTAL,Long.class) ;
		if(total == null || total == 0L){ // 从数据库里面查一遍呗
			total = brandDao.getTotalBrand(brand) ;
			hset(hsetKey,PageEntity.TOTAL,total,PageEntity.EXPIRE) ;
		}
		return total ;
	}

	private String getBrandKey(final Integer brandId){
		return new StringBuffer(PRE_KEY).append(":").append(brandId).toString() ;
	}

	public Brand getBrandByKey(Integer brandId){
		String brandKey = getBrandKey(brandId) ;
		Brand brand = get(brandKey,Brand.class) ;
		if(brand == null){
			brand = brandDao.getBrandByKey(brandId) ;
			if(brand != null){
				setBrand2Redis(brand) ;
			}
		}
		return brand ;
	}
	
	
    
    
	// 执行分页查询的逻辑
	public List<Brand> getBrandByPage(Brand brand){
		String hsetKey = CommonUtil.getAttrVal4ForRedisKey(brand) ;
		if(brand.getPage() == null || brand.getPage() == 0){ //从第一页开始取
			brand.setPage(1);
		}
		brand.setIndex((brand.getPage() - 1) * brand.getPageSize()) ;
		@SuppressWarnings("unchecked")
		ArrayList<Brand> listBrand = (ArrayList<Brand>)hget(hsetKey, brand.getPage().toString(), ArrayList.class) ;
		if(listBrand == null || listBrand.size() == 0){
			logger.debug("=== fetch from database.....");
			listBrand = (ArrayList<Brand>)brandDao.selectBrands(brand) ;
			hset(hsetKey,brand.getPage().toString(),listBrand, PageEntity.EXPIRE) ;
		}
		return listBrand ;
	}

	public Brand addBrand(Brand brand) {
		if(brand.getBrandId() == null || brand.getBrandId() == 0L) {
			brand.setBrandId(getBrandId());
		}
		
		brand = brandDao.addBrand(brand);
		setBrand2Redis(brand) ;
		return brand;
	}
	
	public List<Brand> addListBrand(List<Brand> listBrand){
		for(Brand brand : listBrand){
			if(brand.getBrandId() == null || brand.getBrandId() == 0L) {
				brand.setBrandId(getBrandId());
				setBrand2Redis(brand) ;
			}
		}
		brandDao.addBrandList(listBrand);
		return listBrand;
	}
	
	public int updateBrand(Brand brand, boolean syncdb) {
		set(getBrandKey(brand.getBrandId()), brand, 0L);
		if (syncdb) {
			return brandDao.updateBrand(brand) ;
		}

		return 1;
	}

	public int updateListBrand(List<Brand> brands, boolean syncdb) {
		for(Brand p : brands){
			updateBrand(p,syncdb) ;
		}
		return brands.size();
	}

	public List<Brand> getBrand(List<Integer> listBrandId) {
		if(listBrandId == null || listBrandId.size() == 0 ){
			return new ArrayList<Brand>();
		}

		List<String> listBrandKey = new ArrayList<String>() ;
		for(Integer i : listBrandId){
			listBrandKey.add(getBrandKey(i)) ;
		}
		return mget(listBrandKey,Brand.class) ;
	}

	private String delFromBrandSet(Brand brand){
		String primaryKey = getBrandKey(brand.getBrandId());
		
		//1.0 从 Key-Set 中删除Key
		srem(SET_BRAND_KEY, primaryKey) ;
		
		//2.0 处理索引情况
		
		return primaryKey ;
	}
	
	public void deleteBrand(Integer brandId) {
		Brand brand = getBrandByKey(brandId) ;
		
		List<String> key2Del = new ArrayList<String>() ;
		
		//从Redis中删除 KeyValue
		String primaryKey = delFromBrandSet(brand);
		key2Del.add(primaryKey) ;
		delete(key2Del) ;
		
		//从数据库中删除
		brandDao.deleteBrandByKey(brandId);
	}
	
	public void deleteListBrand(List<Brand> listBrand) {
		
		//1.0 从SET 中删除对象Key的关联
		List<String> key2Del = new ArrayList<String>() ;
		for(Brand brand : listBrand){
			brand = getBrandByKey(brand.getBrandId()) ;
			
			String primaryKey = delFromBrandSet(brand);
			key2Del.add(primaryKey) ;
		}
		//3.0 删除元素
		delete(key2Del) ;
		//4.0 先从数据库中删除
		brandDao.deleteBrandByList(listBrand);
	}
}