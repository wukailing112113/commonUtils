package com.cera.chain.service.impl.prod;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cera.chain.cache.intf.prod.IBrandRedis;
import com.cera.chain.entity.prod.Brand;
import com.cera.chain.service.intf.prod.IBrandService;
import com.cera.chain.servutil.ServConst;
import com.cera.chain.status.CommStatus;

@Service("brandService")
public class BrandServiceImpl implements IBrandService{

	@Autowired
	IBrandRedis brandRedis;
	
	public void loadBrandRelate() {
		brandRedis.loadAllBrand();
	}

	public Brand loadBrandByKey(Integer brandId) {
		return brandRedis.getBrandByKey(brandId);
	}

	public List<Brand> findListBrand(List<Integer> listBrandId) {
		return brandRedis.getBrand(listBrandId) ;
	}
	
	@Transactional(rollbackFor = Exception.class)
	public Brand addBrand(Brand brand) {
		Date now = new Date() ;
		brand.setCreateTime(now) ;
		brand.setStatus(CommStatus.INUSE.getIndex()) ;
		brand.setStatusTime(now) ;
		return brandRedis.addBrand(brand) ;
		
	}

	@Transactional(rollbackFor = Exception.class)
	public int updateBrand(Brand brand) {
		//判断是不是状态修改了
		Brand oldBrand = brandRedis.getBrandByKey(brand.getBrandId()) ;
		if(!oldBrand.getBrandId().equals(brand.getBrandId())){
			Date now = new Date() ;
			brand.setStatusTime(now);
		}
		brandRedis.updateBrand(brand, true) ;
		
		return brandRedis.updateBrand(brand, true) ;
	}

	public Long findTotalPage(Brand brand){
		Long totalBrand = brandRedis.getTotalBrand(brand) ;
		Integer pageSize = (brand.getPageSize() == null || brand.getPageSize() == 0) ? ServConst.LoadPageSize : brand.getPageSize() ;
		Long totalPage = (long)Math.ceil((double)totalBrand / pageSize);
		return totalPage ;
	}
	
	
	public List<Brand> findBrand(Brand brand) {
		List<Brand> listBrand = brandRedis.getBrandByPage(brand) ;
		return listBrand ;
	}

	@Transactional(rollbackFor = Exception.class)
	public void deleteBrand(Integer brandId) {
		brandRedis.deleteBrand(brandId);
	}

}