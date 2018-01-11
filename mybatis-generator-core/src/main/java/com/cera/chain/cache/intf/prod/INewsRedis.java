package com.cera.chain.cache.intf.prod;

import java.util.List;

import com.cera.chain.entity.prod.News;

public interface INewsRedis {

    /**
    * 加载所有的数据到内存
    */
    public void loadAllNews();

    /**
    * @return
    */
    public Long getNewsId();

    /**
    * @param params
    * @return
    */
    public Long getTotalNews(News news);

    /**
    *
    * @param id
    * @return
    */
    public News getNewsByKey(Long newsId);


    /**
    * @param listNewsId
    * @return
    */
    public List<News> getNews(List<Long> listNewsId);

	
   
    /**
    * @param prod
    * @return
    */
    public List<News> getNewsByPage(News news);

    /**
    *
    * @param prod
    * @return
    */
    public News addNews(News news);

	/**
    *
    * @param prod
    * @return
    */
    public List<News> addListNews(List<News> listNews);
   
    /**
    *
    * @param prod
    * @return
    */
    public int updateNews(News news, boolean syncdb);

    /**
    *
    * @param prods
    * @return
    */
    public int updateListNews(List<News> listNews, boolean syncdb);

	/**
    * @param prodId
    */
    public void deleteNews(Long newsId) ;
    
    /**
    * @param prodId
    */
    public void deleteListNews(List<News> listNews) ;
}
