package com.cera.chain.cache.impl.prod;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.cera.chain.cache.BaseRedis;
import com.cera.chain.cache.intf.prod.INewsRedis;
import com.cera.chain.dao.intf.prod.INewsDao;
import com.cera.chain.entity.PageEntity;
import com.cera.chain.entity.prod.News;
import com.cera.chain.util.CommonUtil;

import com.cera.chain.util.StringUtil;

@Repository("newsRedis")
public class NewsRedisImpl extends BaseRedis<String, News>implements INewsRedis {

	private static final Logger logger = Logger.getLogger(NewsRedisImpl.class);

	@Autowired INewsDao newsDao;

	public final static String PRE_KEY = News.class.getSimpleName();
	private final static String SEQ_NEWS_KEY = new StringBuffer("SEQ:").append(PRE_KEY).toString() ;
	public final static String SET_NEWS_KEY = new StringBuffer("SET:").append(PRE_KEY).toString() ;
	public final static String SET_NEWS_IDX_KEY = new StringBuffer("SET:").append(PRE_KEY).append(":IDX").toString() ;
	
	private final static Long START_NEWSID = 1000000L;
	
	public Long getNewsId(){
		Long newsId = Long.valueOf(incr(SEQ_NEWS_KEY).toString());
		if(newsId <= 1L){  // 缓存里面还没有，从数据中加载吧
			newsId = newsDao.selectMaxNewsId();
			if( newsId == null || newsId == 0 ){
				newsId = START_NEWSID ;
			}else{
				newsId ++;
			}
			set(SEQ_NEWS_KEY, newsId.toString(), 0L);
		}
		return newsId ;
	}

	@PostConstruct
	public void initMaxNewsId(){
		Long newsId = newsDao.selectMaxNewsId();
		if (newsId == null || newsId == 0) {
			newsId = START_NEWSID ;
		}
		set(SEQ_NEWS_KEY, newsId.toString(), 0L);
	}

	/**
	 * Set 里面没有元素，Key自动被删除
	*/
	private void clearAllNews() {
		Set<String> setKey = smembers(SET_NEWS_KEY, String.class) ;
		Set<String> setIdxKey = smembers(SET_NEWS_IDX_KEY, String.class) ;
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
	 * 2. SET_NEWS_KEY 1中对象的Key的Set;
	 * 3. 索引的对应关系，设计成Set,方便后续扩展成非索引。
	 */
	private void setNews2Redis(News news){
		String primaryKey = getNewsKey(news.getNewsId());
		sadd(SET_NEWS_KEY, primaryKey);
		set(primaryKey, news, 0L);
		
		// 处理索引情况
	}
	
	
	public void loadAllNews(){
		clearAllNews() ;

		News news = new News() ;
		List<News> listNews = new ArrayList<News>() ;
		Long total = getTotalNews(news) ;
		Integer totalPage = (int)Math.ceil((double)total / LOAD_PAGE_SIZE);
		news.setPageSize(LOAD_PAGE_SIZE);
		for(int i = 1 ; i <= totalPage; i++){
			news.setPage(i);
			listNews = newsDao.selectNewss(news);
			for(News itNews : listNews){
				setNews2Redis(itNews) ;
			}
		}
	}

	/**
	 * 获取记录数，存缓存。
	 * 关于查询结果的缓存
	 * Hset 存储，Key 为 实体类:MD5(条件) total 1 List<News> 2 List<News> ....
	 * 过期时间为30秒
	 */
	public Long getTotalNews(News news) {
		String hsetKey = CommonUtil.getAttrVal4ForRedisKey(news) ;
		Long total = hget(hsetKey,PageEntity.TOTAL,Long.class) ;
		if(total == null || total == 0L){ // 从数据库里面查一遍呗
			total = newsDao.getTotalNews(news) ;
			hset(hsetKey,PageEntity.TOTAL,total,PageEntity.EXPIRE) ;
		}
		return total ;
	}

	private String getNewsKey(final Long newsId){
		return new StringBuffer(PRE_KEY).append(":").append(newsId).toString() ;
	}

	public News getNewsByKey(Long newsId){
		String newsKey = getNewsKey(newsId) ;
		News news = get(newsKey,News.class) ;
		if(news == null){
			news = newsDao.getNewsByKey(newsId) ;
			if(news != null){
				setNews2Redis(news) ;
			}
		}
		return news ;
	}
	
	
    
    
	// 执行分页查询的逻辑
	public List<News> getNewsByPage(News news){
		String hsetKey = CommonUtil.getAttrVal4ForRedisKey(news) ;
		if(news.getPage() == null || news.getPage() == 0){ //从第一页开始取
			news.setPage(1);
		}
		news.setIndex((news.getPage() - 1) * news.getPageSize()) ;
		@SuppressWarnings("unchecked")
		ArrayList<News> listNews = (ArrayList<News>)hget(hsetKey, news.getPage().toString(), ArrayList.class) ;
		if(listNews == null || listNews.size() == 0){
			logger.debug("=== fetch from database.....");
			listNews = (ArrayList<News>)newsDao.selectNewss(news) ;
			hset(hsetKey,news.getPage().toString(),listNews, PageEntity.EXPIRE) ;
		}
		return listNews ;
	}

	public News addNews(News news) {
		if(news.getNewsId() == null || news.getNewsId() == 0L) {
			news.setNewsId(getNewsId());
		}
		
		news = newsDao.addNews(news);
		setNews2Redis(news) ;
		return news;
	}
	
	public List<News> addListNews(List<News> listNews){
		for(News news : listNews){
			if(news.getNewsId() == null || news.getNewsId() == 0L) {
				news.setNewsId(getNewsId());
				setNews2Redis(news) ;
			}
		}
		newsDao.addNewsList(listNews);
		return listNews;
	}
	
	public int updateNews(News news, boolean syncdb) {
		set(getNewsKey(news.getNewsId()), news, 0L);
		if (syncdb) {
			return newsDao.updateNews(news) ;
		}

		return 1;
	}

	public int updateListNews(List<News> newss, boolean syncdb) {
		for(News p : newss){
			updateNews(p,syncdb) ;
		}
		return newss.size();
	}

	public List<News> getNews(List<Long> listNewsId) {
		if(listNewsId == null || listNewsId.size() == 0 ){
			return new ArrayList<News>();
		}

		List<String> listNewsKey = new ArrayList<String>() ;
		for(Long i : listNewsId){
			listNewsKey.add(getNewsKey(i)) ;
		}
		return mget(listNewsKey,News.class) ;
	}

	private String delFromNewsSet(News news){
		String primaryKey = getNewsKey(news.getNewsId());
		
		//1.0 从 Key-Set 中删除Key
		srem(SET_NEWS_KEY, primaryKey) ;
		
		//2.0 处理索引情况
		
		return primaryKey ;
	}
	
	public void deleteNews(Long newsId) {
		News news = getNewsByKey(newsId) ;
		
		List<String> key2Del = new ArrayList<String>() ;
		
		//从Redis中删除 KeyValue
		String primaryKey = delFromNewsSet(news);
		key2Del.add(primaryKey) ;
		delete(key2Del) ;
		
		//从数据库中删除
		newsDao.deleteNewsByKey(newsId);
	}
	
	public void deleteListNews(List<News> listNews) {
		
		//1.0 从SET 中删除对象Key的关联
		List<String> key2Del = new ArrayList<String>() ;
		for(News news : listNews){
			news = getNewsByKey(news.getNewsId()) ;
			
			String primaryKey = delFromNewsSet(news);
			key2Del.add(primaryKey) ;
		}
		//3.0 删除元素
		delete(key2Del) ;
		//4.0 先从数据库中删除
		newsDao.deleteNewsByList(listNews);
	}
}