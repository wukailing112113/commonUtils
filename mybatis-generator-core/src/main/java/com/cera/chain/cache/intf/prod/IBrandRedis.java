package com.cera.chain.cache.intf.prod;

import java.util.List;

import com.cera.chain.entity.prod.Brand;

public interface IBrandRedis {

    /**
    * 加载所有的数据到内存
    */
    public void loadAllBrand();

    /**
    * @return
    */
    public Integer getBrandId();

    /**
    * @param params
    * @return
    */
    public Long getTotalBrand(Brand brand);

    /**
    *
    * @param id
    * @return
    */
    public Brand getBrandByKey(Integer brandId);


    /**
    * @param listBrandId
    * @return
    */
    public List<Brand> getBrand(List<Integer> listBrandId);

	
   
    /**
    * @param prod
    * @return
    */
    public List<Brand> getBrandByPage(Brand brand);

    /**
    *
    * @param prod
    * @return
    */
    public Brand addBrand(Brand brand);

	/**
    *
    * @param prod
    * @return
    */
    public List<Brand> addListBrand(List<Brand> listBrand);
   
    /**
    *
    * @param prod
    * @return
    */
    public int updateBrand(Brand brand, boolean syncdb);

    /**
    *
    * @param prods
    * @return
    */
    public int updateListBrand(List<Brand> listBrand, boolean syncdb);

	/**
    * @param prodId
    */
    public void deleteBrand(Integer brandId) ;
    
    /**
    * @param prodId
    */
    public void deleteListBrand(List<Brand> listBrand) ;
}
