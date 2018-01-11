package com.cera.chain.service.intf.prod;

import java.util.List;

import com.cera.chain.entity.prod.News;


public interface INewsService {

	/**
	 * 加载相关的到内存
	 */
    public void loadNewsRelate();
	
	/**
	 * 
	 * @param Long newsId
	 * @return
	 */
    public News loadNewsByKey(Long newsId);
	
	/**
	 * 
	 * @param news
	 */
    public News addNews(News news);
	
	/**
	 * 
	 * @param news
	 */
    public int updateNews(News news);
	
	/**
	 * 根据条件查找
	 * @param brand
	 * @return
	 */
    public List<News> findNews(News news);
	
	
    /**
    * @param listNewsId
    * @return
    */
    public List<News> findListNews(List<Long> listNewsId);
    
	/**
	 * 查找在当前条件下的页数
	 * @param news
	 * @return
	 */
    public Long findTotalPage(News news);

	/**
	 * 删除，一般不提供这样的方法
	 * @param Long newsId
	 */
	public void deleteNews(Long newsId);
	
}
