package com.cera.chain.service.impl.prod;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cera.chain.cache.intf.prod.IBannerRedis;
import com.cera.chain.entity.prod.Banner;
import com.cera.chain.service.intf.prod.IBannerService;
import com.cera.chain.servutil.ServConst;
import com.cera.chain.status.CommStatus;

@Service("bannerService")
public class BannerServiceImpl implements IBannerService{

	@Autowired
	IBannerRedis bannerRedis;
	
	public void loadBannerRelate() {
		bannerRedis.loadAllBanner();
	}

	public Banner loadBannerByKey(Long bannerId) {
		return bannerRedis.getBannerByKey(bannerId);
	}

	public List<Banner> findListBanner(List<Long> listBannerId) {
		return bannerRedis.getBanner(listBannerId) ;
	}
	
	@Transactional(rollbackFor = Exception.class)
	public Banner addBanner(Banner banner) {
		Date now = new Date() ;
		banner.setCreateTime(now) ;
		banner.setStatus(CommStatus.INUSE.getIndex()) ;
		banner.setStatusTime(now) ;
		return bannerRedis.addBanner(banner) ;
		
	}

	@Transactional(rollbackFor = Exception.class)
	public int updateBanner(Banner banner) {
		//判断是不是状态修改了
		Banner oldBanner = bannerRedis.getBannerByKey(banner.getBannerId()) ;
		if(!oldBanner.getBannerId().equals(banner.getBannerId())){
			Date now = new Date() ;
			banner.setStatusTime(now);
		}
		bannerRedis.updateBanner(banner, true) ;
		
		return bannerRedis.updateBanner(banner, true) ;
	}

	public Long findTotalPage(Banner banner){
		Long totalBanner = bannerRedis.getTotalBanner(banner) ;
		Integer pageSize = (banner.getPageSize() == null || banner.getPageSize() == 0) ? ServConst.LoadPageSize : banner.getPageSize() ;
		Long totalPage = (long)Math.ceil((double)totalBanner / pageSize);
		return totalPage ;
	}
	
	
	public List<Banner> findBanner(Banner banner) {
		List<Banner> listBanner = bannerRedis.getBannerByPage(banner) ;
		return listBanner ;
	}

	@Transactional(rollbackFor = Exception.class)
	public void deleteBanner(Long bannerId) {
		bannerRedis.deleteBanner(bannerId);
	}

}