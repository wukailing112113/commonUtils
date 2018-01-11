/*
 * file comment: INewsDao.java
 * Copyright(C) All rights reserved. 
 */
package com.cera.chain.dao.intf.prod;

import com.cera.chain.entity.prod.News;
import java.util.List;

public interface INewsDao {
    Long selectMaxNewsId();

    News getNewsByKey(Long newsId);

    List<News> selectNewss(News news);

    List<News> selectNewsByPage(News news);

    Long getTotalNews(News news);

    int deleteNewsByKey(Long newsId);

    void deleteNewsByList(List<News> listNews);

    News addNews(News news);

    List<News> addNewsList(List<News> listNews);

    int updateNews(News news);

    int updateListNews(List<News> listNews);
}