/*
 * file comment: BannerDaoImpl.java
 * Copyright(C) All rights reserved. 
 */
package com.cera.chain.dao.impl.prod;

import com.cera.chain.dao.impl.BaseDaoImp;
import com.cera.chain.dao.intf.prod.IBannerDao;
import com.cera.chain.entity.prod.Banner;
import java.util.List;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository("bannerDao")
public class BannerDaoImpl extends BaseDaoImp<Banner> implements IBannerDao {

    public Long selectMaxBannerId() {
        return (Long)getMaxId("selectMaxBannerId"); 
    }

    public Banner getBannerByKey(Long bannerId) {
        return getPojoById("getBannerByKey", bannerId); 
    }

    public List<Banner> selectBanners(Banner banner) {
        return findByProperty("selectBanners", banner);
    }

    public List<Banner> selectBannerByPage(Banner banner) {
        return findByProperty("selectBannerByPage", banner);
    }

    public Long getTotalBanner(Banner banner) {
        return (Long) getRowCount("getTotalBanner", banner); 
    }

    public int deleteBannerByKey(Long bannerId) {
        return deleteById("deleteBannerByKey", bannerId); 
    }

    public void deleteBannerByList(List<Banner> listBanner) {
        deleteAll("deleteBannerByList", listBanner); 
    }

    public Banner addBanner(Banner banner) {
        return saveAndFetch("addBanner", banner);
    }

    public List<Banner> addBannerList(List<Banner> listBanner) {
        saveList("addBannerList", listBanner);
        return listBanner;
    }

    public int updateBanner(Banner banner) {
        return update("updateBannerSelective", banner); 
    }

    public int updateListBanner(List<Banner> listBanner) {
        int result = 0;
        for (Banner banner : listBanner){
            result = updateBanner(banner);
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