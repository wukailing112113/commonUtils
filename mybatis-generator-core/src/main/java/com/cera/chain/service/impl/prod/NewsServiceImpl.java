package com.cera.chain.service.impl.prod;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cera.chain.cache.intf.prod.INewsRedis;
import com.cera.chain.entity.prod.News;
import com.cera.chain.service.intf.prod.INewsService;
import com.cera.chain.servutil.ServConst;
import com.cera.chain.status.CommStatus;

@Service("newsService")
public class NewsServiceImpl implements INewsService{

	@Autowired
	INewsRedis newsRedis;
	
	public void loadNewsRelate() {
		newsRedis.loadAllNews();
	}

	public News loadNewsByKey(Long newsId) {
		return newsRedis.getNewsByKey(newsId);
	}

	public List<News> findListNews(List<Long> listNewsId) {
		return newsRedis.getNews(listNewsId) ;
	}
	
	@Transactional(rollbackFor = Exception.class)
	public News addNews(News news) {
		Date now = new Date() ;
		news.setCreateTime(now) ;
		news.setStatus(CommStatus.INUSE.getIndex()) ;
		news.setStatusTime(now) ;
		return newsRedis.addNews(news) ;
		
	}

	@Transactional(rollbackFor = Exception.class)
	public int updateNews(News news) {
		//判断是不是状态修改了
		News oldNews = newsRedis.getNewsByKey(news.getNewsId()) ;
		if(!oldNews.getNewsId().equals(news.getNewsId())){
			Date now = new Date() ;
			news.setStatusTime(now);
		}
		newsRedis.updateNews(news, true) ;
		
		return newsRedis.updateNews(news, true) ;
	}

	public Long findTotalPage(News news){
		Long totalNews = newsRedis.getTotalNews(news) ;
		Integer pageSize = (news.getPageSize() == null || news.getPageSize() == 0) ? ServConst.LoadPageSize : news.getPageSize() ;
		Long totalPage = (long)Math.ceil((double)totalNews / pageSize);
		return totalPage ;
	}
	
	
	public List<News> findNews(News news) {
		List<News> listNews = newsRedis.getNewsByPage(news) ;
		return listNews ;
	}

	@Transactional(rollbackFor = Exception.class)
	public void deleteNews(Long newsId) {
		newsRedis.deleteNews(newsId);
	}

}