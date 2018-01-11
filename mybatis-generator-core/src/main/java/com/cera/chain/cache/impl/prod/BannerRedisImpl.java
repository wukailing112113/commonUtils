package com.cera.chain.cache.impl.prod;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.cera.chain.cache.BaseRedis;
import com.cera.chain.cache.intf.prod.IBannerRedis;
import com.cera.chain.dao.intf.prod.IBannerDao;
import com.cera.chain.entity.PageEntity;
import com.cera.chain.entity.prod.Banner;
import com.cera.chain.util.CommonUtil;

import com.cera.chain.util.StringUtil;

@Repository("bannerRedis")
public class BannerRedisImpl extends BaseRedis<String, Banner>implements IBannerRedis {

	private static final Logger logger = Logger.getLogger(BannerRedisImpl.class);

	@Autowired IBannerDao bannerDao;

	public final static String PRE_KEY = Banner.class.getSimpleName();
	private final static String SEQ_BANNER_KEY = new StringBuffer("SEQ:").append(PRE_KEY).toString() ;
	public final static String SET_BANNER_KEY = new StringBuffer("SET:").append(PRE_KEY).toString() ;
	public final static String SET_BANNER_IDX_KEY = new StringBuffer("SET:").append(PRE_KEY).append(":IDX").toString() ;
	
	private final static Long START_BANNERID = 1000000L;
	
	public Long getBannerId(){
		Long bannerId = Long.valueOf(incr(SEQ_BANNER_KEY).toString());
		if(bannerId <= 1L){  // 缓存里面还没有，从数据中加载吧
			bannerId = bannerDao.selectMaxBannerId();
			if( bannerId == null || bannerId == 0 ){
				bannerId = START_BANNERID ;
			}else{
				bannerId ++;
			}
			set(SEQ_BANNER_KEY, bannerId.toString(), 0L);
		}
		return bannerId ;
	}

	@PostConstruct
	public void initMaxBannerId(){
		Long bannerId = bannerDao.selectMaxBannerId();
		if (bannerId == null || bannerId == 0) {
			bannerId = START_BANNERID ;
		}
		set(SEQ_BANNER_KEY, bannerId.toString(), 0L);
	}

	/**
	 * Set 里面没有元素，Key自动被删除
	*/
	private void clearAllBanner() {
		Set<String> setKey = smembers(SET_BANNER_KEY, String.class) ;
		Set<String> setIdxKey = smembers(SET_BANNER_IDX_KEY, String.class) ;
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
	 * 2. SET_BANNER_KEY 1中对象的Key的Set;
	 * 3. 索引的对应关系，设计成Set,方便后续扩展成非索引。
	 */
	private void setBanner2Redis(Banner banner){
		String primaryKey = getBannerKey(banner.getBannerId());
		sadd(SET_BANNER_KEY, primaryKey);
		set(primaryKey, banner, 0L);
		
		// 处理索引情况
		String IndexSetKey = null ;
		IndexSetKey = getBannerIdxSetKey(banner.getUrl() );
		if(StringUtil.isNotEmptyString(IndexSetKey)){
			sadd(SET_BANNER_IDX_KEY, IndexSetKey) ;
			sadd(IndexSetKey,primaryKey) ;
		}
		IndexSetKey = getBannerIdxSetKey(banner.getType(), banner.getIcon() );
		if(StringUtil.isNotEmptyString(IndexSetKey)){
			sadd(SET_BANNER_IDX_KEY, IndexSetKey) ;
			sadd(IndexSetKey,primaryKey) ;
		}
	}
	
	
	public void loadAllBanner(){
		clearAllBanner() ;

		Banner banner = new Banner() ;
		List<Banner> listBanner = new ArrayList<Banner>() ;
		Long total = getTotalBanner(banner) ;
		Integer totalPage = (int)Math.ceil((double)total / LOAD_PAGE_SIZE);
		banner.setPageSize(LOAD_PAGE_SIZE);
		for(int i = 1 ; i <= totalPage; i++){
			banner.setPage(i);
			listBanner = bannerDao.selectBanners(banner);
			for(Banner itBanner : listBanner){
				setBanner2Redis(itBanner) ;
			}
		}
	}

	/**
	 * 获取记录数，存缓存。
	 * 关于查询结果的缓存
	 * Hset 存储，Key 为 实体类:MD5(条件) total 1 List<Banner> 2 List<Banner> ....
	 * 过期时间为30秒
	 */
	public Long getTotalBanner(Banner banner) {
		String hsetKey = CommonUtil.getAttrVal4ForRedisKey(banner) ;
		Long total = hget(hsetKey,PageEntity.TOTAL,Long.class) ;
		if(total == null || total == 0L){ // 从数据库里面查一遍呗
			total = bannerDao.getTotalBanner(banner) ;
			hset(hsetKey,PageEntity.TOTAL,total,PageEntity.EXPIRE) ;
		}
		return total ;
	}

	private String getBannerKey(final Long bannerId){
		return new StringBuffer(PRE_KEY).append(":").append(bannerId).toString() ;
	}

	public Banner getBannerByKey(Long bannerId){
		String bannerKey = getBannerKey(bannerId) ;
		Banner banner = get(bannerKey,Banner.class) ;
		if(banner == null){
			banner = bannerDao.getBannerByKey(bannerId) ;
			if(banner != null){
				setBanner2Redis(banner) ;
			}
		}
		return banner ;
	}
	
	
	private String getBannerIdxSetKey(String url){
		return new StringBuffer(SET_BANNER_KEY).append(":Url:").append(url).toString() ;
    }
    public Banner getBannerByUrl(String url){
    	String setKey = getBannerIdxSetKey(url) ;
    	Set<String> setBannerKey = smembers(setKey, String.class) ;
    	Banner banner = null ;
		if(setBannerKey != null){
			List<String> listKey = new ArrayList<String>(setBannerKey) ;
			banner = get(listKey.get(0), Banner.class) ;
		}
		// 从数据库里面查
		if(banner == null){
			banner = new Banner() ;
			banner.setUrl(url) ;
			List<Banner> listBanner = bannerDao.selectBanners(banner) ;
			if(listBanner != null && listBanner.size() == 1){
				banner = listBanner.get(0) ;
			}
			
			if(banner != null){
				setBanner2Redis(banner) ;
			}
		}
		return banner;
    }
	private String getBannerIdxSetKey(Short type, String icon){
		return new StringBuffer(SET_BANNER_KEY).append(":Type:").append(type).append(":Icon:").append(icon).toString() ;
    }
    public Banner getBannerByOther(Short type, String icon){
    	String setKey = getBannerIdxSetKey(type, icon) ;
    	Set<String> setBannerKey = smembers(setKey, String.class) ;
    	Banner banner = null ;
		if(setBannerKey != null){
			List<String> listKey = new ArrayList<String>(setBannerKey) ;
			banner = get(listKey.get(0), Banner.class) ;
		}
		// 从数据库里面查
		if(banner == null){
			banner = new Banner() ;
			banner.setType(type) ;
			banner.setIcon(icon) ;
			List<Banner> listBanner = bannerDao.selectBanners(banner) ;
			if(listBanner != null && listBanner.size() == 1){
				banner = listBanner.get(0) ;
			}
			
			if(banner != null){
				setBanner2Redis(banner) ;
			}
		}
		return banner;
    }
    
    
	// 执行分页查询的逻辑
	public List<Banner> getBannerByPage(Banner banner){
		String hsetKey = CommonUtil.getAttrVal4ForRedisKey(banner) ;
		if(banner.getPage() == null || banner.getPage() == 0){ //从第一页开始取
			banner.setPage(1);
		}
		banner.setIndex((banner.getPage() - 1) * banner.getPageSize()) ;
		@SuppressWarnings("unchecked")
		ArrayList<Banner> listBanner = (ArrayList<Banner>)hget(hsetKey, banner.getPage().toString(), ArrayList.class) ;
		if(listBanner == null || listBanner.size() == 0){
			logger.debug("=== fetch from database.....");
			listBanner = (ArrayList<Banner>)bannerDao.selectBanners(banner) ;
			hset(hsetKey,banner.getPage().toString(),listBanner, PageEntity.EXPIRE) ;
		}
		return listBanner ;
	}

	public Banner addBanner(Banner banner) {
		if(banner.getBannerId() == null || banner.getBannerId() == 0L) {
			banner.setBannerId(getBannerId());
		}
		
		banner = bannerDao.addBanner(banner);
		setBanner2Redis(banner) ;
		return banner;
	}
	
	public List<Banner> addListBanner(List<Banner> listBanner){
		for(Banner banner : listBanner){
			if(banner.getBannerId() == null || banner.getBannerId() == 0L) {
				banner.setBannerId(getBannerId());
				setBanner2Redis(banner) ;
			}
		}
		bannerDao.addBannerList(listBanner);
		return listBanner;
	}
	
	public int updateBanner(Banner banner, boolean syncdb) {
		set(getBannerKey(banner.getBannerId()), banner, 0L);
		if (syncdb) {
			return bannerDao.updateBanner(banner) ;
		}

		return 1;
	}

	public int updateListBanner(List<Banner> banners, boolean syncdb) {
		for(Banner p : banners){
			updateBanner(p,syncdb) ;
		}
		return banners.size();
	}

	public List<Banner> getBanner(List<Long> listBannerId) {
		if(listBannerId == null || listBannerId.size() == 0 ){
			return new ArrayList<Banner>();
		}

		List<String> listBannerKey = new ArrayList<String>() ;
		for(Long i : listBannerId){
			listBannerKey.add(getBannerKey(i)) ;
		}
		return mget(listBannerKey,Banner.class) ;
	}

	private String delFromBannerSet(Banner banner){
		String primaryKey = getBannerKey(banner.getBannerId());
		
		//1.0 从 Key-Set 中删除Key
		srem(SET_BANNER_KEY, primaryKey) ;
		
		//2.0 处理索引情况
		String IndexSetKey = null ;
		if(banner != null){
			IndexSetKey = getBannerIdxSetKey(banner.getUrl() );
			if(StringUtil.isNotEmptyString(IndexSetKey)){
				srem(IndexSetKey,primaryKey) ;
			}
		}
		if(banner != null){
			IndexSetKey = getBannerIdxSetKey(banner.getType(), banner.getIcon() );
			if(StringUtil.isNotEmptyString(IndexSetKey)){
				srem(IndexSetKey,primaryKey) ;
			}
		}
		
		return primaryKey ;
	}
	
	public void deleteBanner(Long bannerId) {
		Banner banner = getBannerByKey(bannerId) ;
		
		List<String> key2Del = new ArrayList<String>() ;
		
		//从Redis中删除 KeyValue
		String primaryKey = delFromBannerSet(banner);
		key2Del.add(primaryKey) ;
		delete(key2Del) ;
		
		//从数据库中删除
		bannerDao.deleteBannerByKey(bannerId);
	}
	
	public void deleteListBanner(List<Banner> listBanner) {
		
		//1.0 从SET 中删除对象Key的关联
		List<String> key2Del = new ArrayList<String>() ;
		for(Banner banner : listBanner){
			banner = getBannerByKey(banner.getBannerId()) ;
			
			String primaryKey = delFromBannerSet(banner);
			key2Del.add(primaryKey) ;
		}
		//3.0 删除元素
		delete(key2Del) ;
		//4.0 先从数据库中删除
		bannerDao.deleteBannerByList(listBanner);
	}
}