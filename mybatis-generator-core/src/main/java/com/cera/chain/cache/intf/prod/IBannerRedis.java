package com.cera.chain.cache.intf.prod;

import java.util.List;

import com.cera.chain.entity.prod.Banner;

public interface IBannerRedis {

    /**
    * 加载所有的数据到内存
    */
    public void loadAllBanner();

    /**
    * @return
    */
    public Long getBannerId();

    /**
    * @param params
    * @return
    */
    public Long getTotalBanner(Banner banner);

    /**
    *
    * @param id
    * @return
    */
    public Banner getBannerByKey(Long bannerId);


    /**
    * @param listBannerId
    * @return
    */
    public List<Banner> getBanner(List<Long> listBannerId);

	
    /**
    * @param 
    * @return
    */
    public Banner getBannerByUrl(String url);
    
    /**
    * @param 
    * @return
    */
    public Banner getBannerByOther(Short type, String icon);
    
   
    /**
    * @param prod
    * @return
    */
    public List<Banner> getBannerByPage(Banner banner);

    /**
    *
    * @param prod
    * @return
    */
    public Banner addBanner(Banner banner);

	/**
    *
    * @param prod
    * @return
    */
    public List<Banner> addListBanner(List<Banner> listBanner);
   
    /**
    *
    * @param prod
    * @return
    */
    public int updateBanner(Banner banner, boolean syncdb);

    /**
    *
    * @param prods
    * @return
    */
    public int updateListBanner(List<Banner> listBanner, boolean syncdb);

	/**
    * @param prodId
    */
    public void deleteBanner(Long bannerId) ;
    
    /**
    * @param prodId
    */
    public void deleteListBanner(List<Banner> listBanner) ;
}
