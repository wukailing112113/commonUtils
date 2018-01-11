/*
 * file comment: NewsDaoImpl.java
 * Copyright(C) All rights reserved. 
 */
package com.cera.chain.dao.impl.prod;

import com.cera.chain.dao.impl.BaseDaoImp;
import com.cera.chain.dao.intf.prod.INewsDao;
import com.cera.chain.entity.prod.News;
import java.util.List;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository("newsDao")
public class NewsDaoImpl extends BaseDaoImp<News> implements INewsDao {

    public Long selectMaxNewsId() {
        return (Long)getMaxId("selectMaxNewsId"); 
    }

    public News getNewsByKey(Long newsId) {
        return getPojoById("getNewsByKey", newsId); 
    }

    public List<News> selectNewss(News news) {
        return findByProperty("selectNewss", news);
    }

    public List<News> selectNewsByPage(News news) {
        return findByProperty("selectNewsByPage", news);
    }

    public Long getTotalNews(News news) {
        return (Long) getRowCount("getTotalNews", news); 
    }

    public int deleteNewsByKey(Long newsId) {
        return deleteById("deleteNewsByKey", newsId); 
    }

    public void deleteNewsByList(List<News> listNews) {
        deleteAll("deleteNewsByList", listNews); 
    }

    public News addNews(News news) {
        return saveAndFetch("addNews", news);
    }

    public List<News> addNewsList(List<News> listNews) {
        saveList("addNewsList", listNews);
        return listNews;
    }

    public int updateNews(News news) {
        return update("updateNewsSelective", news); 
    }

    public int updateListNews(List<News> listNews) {
        int result = 0;
        for (News news : listNews){
            result = updateNews(news);
            if (result == -1) {
                break;
            }
        }
        return result;
    }

    @Autowired
    @Qualifier("prodSqlSessionFactory")
    public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
         super.sqlSessionFactory = sqlSessionFactory;
    }
}