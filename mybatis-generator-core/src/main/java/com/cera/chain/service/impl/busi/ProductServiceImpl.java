package com.cera.chain.service.impl.busi;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cera.chain.cache.intf.busi.IProductRedis;
import com.cera.chain.entity.busi.Product;
import com.cera.chain.service.intf.busi.IProductService;
import com.cera.chain.servutil.ServConst;
import com.cera.chain.status.CommStatus;

@Service("productService")
public class ProductServiceImpl implements IProductService{

	private static final Logger logger = Logger.getLogger(ProductServiceImpl.class);
	
	@Autowired
	IProductRedis productRedis;
	
	public void loadProductRelate() {
		productRedis.loadAllProduct();
	}
	
    public Long getProductId(){
    	return productRedis.getProductId() ;
    }
    

	public Product loadProductByKey(Long prodId) {
		return productRedis.getProductByKey(prodId);
	}
	
    public Product getProductByUserid(Integer uid){
    	return productRedis.getProductByUserid(uid) ;
    }
    public Product getProductByBrandcata(Integer cataId, Integer brandId){
    	return productRedis.getProductByBrandcata(cataId, brandId) ;
    }
    
	public List<Product> findListProduct(List<Long> listProductId) {
		return productRedis.getProduct(listProductId) ;
	}
	
	@Transactional(rollbackFor = Exception.class)
	public Product addProduct(Product product) {
		Date now = new Date() ;
		product.setCreateTime(now) ;
		product.setStatus(CommStatus.INUSE.getIndex()) ;
		product.setStatusTime(now) ;
		return productRedis.addProduct(product) ;
	}

	@Transactional(rollbackFor = Exception.class)
	public int updateProduct(Product product) {
		//判断是不是状态修改了
		Product oldProduct = productRedis.getProductByKey(product.getProdId()) ;
		if(!oldProduct.getProdId().equals(product.getProdId())){
			Date now = new Date() ;
			product.setStatusTime(now);
		}
		productRedis.updateProduct(product, true) ;
		
		return productRedis.updateProduct(product, true) ;
	}

	public Long findTotalPage(Product product){
		Long totalProduct = productRedis.getTotalProduct(product) ;
		Integer pageSize = (product.getPageSize() == null || product.getPageSize() == 0) ? ServConst.LoadPageSize : product.getPageSize() ;
		Long totalPage = (long)Math.ceil((double)totalProduct / pageSize);
		return totalPage ;
	}
	
	
	public List<Product> findProduct(Product product) {
		List<Product> listProduct = productRedis.getProductByPage(product) ;
		return listProduct ;
	}

	@Transactional(rollbackFor = Exception.class)
	public void deleteProduct(Long prodId) {
		productRedis.deleteProduct(prodId);
	}

}