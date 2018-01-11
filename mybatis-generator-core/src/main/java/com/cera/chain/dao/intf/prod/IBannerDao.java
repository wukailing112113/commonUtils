/*
 * file comment: IBannerDao.java
 * Copyright(C) All rights reserved. 
 */
package com.cera.chain.dao.intf.prod;

import com.cera.chain.entity.prod.Banner;
import java.util.List;

public interface IBannerDao {
    Long selectMaxBannerId();

    Banner getBannerByKey(Long bannerId);

    List<Banner> selectBanners(Banner banner);

    List<Banner> selectBannerByPage(Banner banner);

    Long getTotalBanner(Banner banner);

    int deleteBannerByKey(Long bannerId);

    void deleteBannerByList(List<Banner> listBanner);

    Banner addBanner(Banner banner);

    List<Banner> addBannerList(List<Banner> listBanner);

    int updateBanner(Banner banner);

    int updateListBanner(List<Banner> listBanner);
}