package com.cera.chain.service.intf.prod;

import java.util.List;

import com.cera.chain.entity.prod.Banner;


public interface IBannerService {

	/**
	 * 加载相关的到内存
	 */
    public void loadBannerRelate();
	
	/**
	 * 
	 * @param Long bannerId
	 * @return
	 */
    public Banner loadBannerByKey(Long bannerId);
	
	/**
	 * 
	 * @param banner
	 */
    public Banner addBanner(Banner banner);
	
	/**
	 * 
	 * @param banner
	 */
    public int updateBanner(Banner banner);
	
	/**
	 * 根据条件查找
	 * @param brand
	 * @return
	 */
    public List<Banner> findBanner(Banner banner);
	
	
    /**
    * @param listBannerId
    * @return
    */
    public List<Banner> findListBanner(List<Long> listBannerId);
    
	/**
	 * 查找在当前条件下的页数
	 * @param banner
	 * @return
	 */
    public Long findTotalPage(Banner banner);

	/**
	 * 删除，一般不提供这样的方法
	 * @param Long bannerId
	 */
	public void deleteBanner(Long bannerId);
	
}
