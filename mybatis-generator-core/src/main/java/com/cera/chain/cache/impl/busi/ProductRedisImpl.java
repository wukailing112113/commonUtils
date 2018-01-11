package com.cera.chain.cache.impl.busi;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.io.Serializable;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.cera.chain.cache.BaseRedis;
import com.cera.chain.cache.intf.busi.IProductRedis;
import com.cera.chain.dao.intf.busi.IProductDao;
import com.cera.chain.entity.PageEntity;
import com.cera.chain.entity.busi.Product;
import com.cera.chain.util.CommonUtil;

import com.cera.chain.util.StringUtil;

@Repository("productRedis")
public class ProductRedisImpl extends BaseRedis<String, Product>implements IProductRedis {

	private static final Logger logger = Logger.getLogger(ProductRedisImpl.class);

	@Autowired IProductDao productDao;

	public final static String PRE_KEY = Product.class.getSimpleName();
	private final static String SEQ_PRODUCT_KEY = new StringBuffer("SEQ:").append(PRE_KEY).toString() ;
	public final static String SET_PRODUCT_KEY = new StringBuffer("SET:").append(PRE_KEY).toString() ;
	public final static String SET_PRODUCT_IDX_KEY = new StringBuffer("SET:").append(PRE_KEY).append(":IDX").toString() ;
	
	private final static Long START_PRODUCTID = 1000000L;
	
	public Long getProductId(){
		Long prodId = Long.valueOf(incr(SEQ_PRODUCT_KEY).toString());
		if(prodId <= 1L){  // 缓存里面还没有，从数据中加载吧
			prodId = productDao.selectMaxProductId();
			if( prodId == null || prodId == 0 ){
				prodId = START_PRODUCTID ;
			}else{
				prodId ++;
			}
			set(SEQ_PRODUCT_KEY, prodId.toString(), 0L);
		}
		return prodId ;
	}

	@PostConstruct
	public void initMaxProductId(){
		Long prodId = productDao.selectMaxProductId();
		if (prodId == null || prodId == 0) {
			prodId = START_PRODUCTID ;
		}
		set(SEQ_PRODUCT_KEY, prodId.toString(), 0L);
	}

	/**
	 * Set 里面没有元素，Key自动被删除
	*/
	private void clearAllProduct() {
		Set<String> setKey = smembers(SET_PRODUCT_KEY, String.class) ;
		Set<String> setIdxKey = smembers(SET_PRODUCT_IDX_KEY, String.class) ;
		List<String> listKey = new ArrayList<String>() ;
		if(setKey != null){
			listKey.addAll(setKey) ;
		}
		if(setIdxKey != null){
			listKey.addAll(setIdxKey) ;
		}
		if(listKey.size() > 0){
			delete(listKey) ;
		}
	}

	/**
	 * 把对象存入Redis
	 * 1. Key-Value 对象;
	 * 2. SET_PRODUCT_KEY 1中对象的Key的Set;
	 * 3. 索引的对应关系，设计成Set,方便后续扩展成非索引。
	 */
	private void setProduct2Redis(Product product){
		String primaryKey = getProductKey(product.getProdId());
		sadd(SET_PRODUCT_KEY, primaryKey);
		set(primaryKey, product, 0L);
		
		// 处理索引情况
		String IndexSetKey = null ;
		IndexSetKey = getProductIdxSetKey(product.getUid() );
		if(StringUtil.isNotEmptyString(IndexSetKey)){
			sadd(SET_PRODUCT_IDX_KEY, IndexSetKey) ;
			sadd(IndexSetKey,primaryKey) ;
		}
		IndexSetKey = getProductIdxSetKey(product.getCataId(), product.getBrandId() );
		if(StringUtil.isNotEmptyString(IndexSetKey)){
			sadd(SET_PRODUCT_IDX_KEY, IndexSetKey) ;
			sadd(IndexSetKey,primaryKey) ;
		}
	}
	
	
	public void loadAllProduct(){
		clearAllProduct() ;

		Product product = new Product() ;
		List<Product> listProduct = new ArrayList<Product>() ;
		Long total = getTotalProduct(product) ;
		Integer totalPage = (int)Math.ceil((double)total / LOAD_PAGE_SIZE);
		product.setPageSize(LOAD_PAGE_SIZE);
		for(int i = 1 ; i <= totalPage; i++){
			product.setPage(i);
			listProduct = productDao.selectProducts(product);
			for(Product itProduct : listProduct){
				setProduct2Redis(itProduct) ;
			}
		}
	}

	/**
	 * 获取记录数，存缓存。
	 * 关于查询结果的缓存
	 * Hset 存储，Key 为 实体类:MD5(条件) total 1 List<Product> 2 List<Product> ....
	 * 过期时间为30秒
	 */
	public Long getTotalProduct(Product product) {
		String hsetKey = CommonUtil.getAttrVal4ForRedisKey(product) ;
		Long total = hget(hsetKey,PageEntity.TOTAL,Long.class) ;
		if(total == null || total == 0L){ // 从数据库里面查一遍呗
			total = productDao.getTotalProduct(product) ;
			hset(hsetKey,PageEntity.TOTAL,total,PageEntity.EXPIRE) ;
		}
		return total ;
	}

	private String getProductKey(final Long prodId){
		return new StringBuffer(PRE_KEY).append(":").append(prodId).toString() ;
	}

	public Product getProductByKey(Long prodId){
		String primaryKey = getProductKey(prodId) ;
		Product product = get(primaryKey,Product.class) ;
		if(product == null){
			product = productDao.getProductByKey(prodId) ;
			if(product != null){
				setProduct2Redis(product) ;
			}
		}
		return product ;
	}
	
	
	private String getProductIdxSetKey(Integer uid){
		return new StringBuffer(SET_PRODUCT_KEY).append(":Uid:").append(uid).toString() ;
    }
    public Product getProductByUserid(Integer uid){
    	String indexSetKey = getProductIdxSetKey(uid) ;
    	Set<String> setProductKey = smembers(indexSetKey, String.class) ;
    	Product product = null ;
		if(setProductKey != null){
			List<String> listKey = new ArrayList<String>(setProductKey) ;
			product = get(listKey.get(0), Product.class) ;
		}
		// 从数据库里面查
		if(product == null){
			product = new Product() ;
			product.setUid(uid) ;
			List<Product> listProduct = productDao.selectProducts(product) ;
			if(listProduct != null && listProduct.size() == 1){
				product = listProduct.get(0) ;
			}else{
				product = null ;
			}
			if(product != null){
				setProduct2Redis(product) ;
			}
		}
		return product;
    }
	private String getProductIdxSetKey(Integer cataId, Integer brandId){
		return new StringBuffer(SET_PRODUCT_KEY).append(":CataId:").append(cataId).append(":BrandId:").append(brandId).toString() ;
    }
    public Product getProductByBrandcata(Integer cataId, Integer brandId){
    	String indexSetKey = getProductIdxSetKey(cataId, brandId) ;
    	Set<String> setProductKey = smembers(indexSetKey, String.class) ;
    	Product product = null ;
		if(setProductKey != null){
			List<String> listKey = new ArrayList<String>(setProductKey) ;
			product = get(listKey.get(0), Product.class) ;
		}
		// 从数据库里面查
		if(product == null){
			product = new Product() ;
			product.setCataId(cataId) ;
			product.setBrandId(brandId) ;
			List<Product> listProduct = productDao.selectProducts(product) ;
			if(listProduct != null && listProduct.size() == 1){
				product = listProduct.get(0) ;
			}else{
				product = null ;
			}
			if(product != null){
				setProduct2Redis(product) ;
			}
		}
		return product;
    }
    
    
	// 执行分页查询的逻辑
	public List<Product> getProductByPage(Product product){
		String hsetKey = CommonUtil.getAttrVal4ForRedisKey(product) ;
		if(product.getPage() == null || product.getPage() == 0){ //从第一页开始取
			product.setPage(1);
		}
		product.setIndex((product.getPage() - 1) * product.getPageSize()) ;
		@SuppressWarnings("unchecked")
		ArrayList<Product> listProduct = (ArrayList<Product>)hget(hsetKey, product.getPage().toString(), ArrayList.class) ;
		if(listProduct == null || listProduct.size() == 0){
			logger.debug("=== fetch from database.....");
			listProduct = (ArrayList<Product>)productDao.selectProducts(product) ;
			hset(hsetKey,product.getPage().toString(),listProduct, PageEntity.EXPIRE) ;
		}
		return listProduct ;
	}

	public Product addProduct(Product product) {
		if(product.getProdId() == null || product.getProdId() == 0L) {
			product.setProdId(getProductId());
		}
		
		product = productDao.addProduct(product);
		setProduct2Redis(product) ;
		return product;
	}
	
	public List<Product> addListProduct(List<Product> listProduct){
		for(Product product : listProduct){
			if(product.getProdId() == null || product.getProdId() == 0L) {
				product.setProdId(getProductId());
				setProduct2Redis(product) ;
			}
		}
		productDao.addProductList(listProduct);
		return listProduct;
	}
	
	public int updateProduct(Product product, boolean syncdb) {
		String primaryKey =  getProductKey(product.getProdId());
		Product oldProduct = get(primaryKey, Product.class) ;
		if (oldProduct != null) {
			if (!(oldProduct.getUid().equals(product.getUid()) && oldProduct.getCataId().equals(product.getCataId()) && oldProduct.getBrandId().equals(product.getBrandId()) )) {
				delFromProductSet(oldProduct);
				setProduct2Redis(product) ;
			} else {
				set(getProductKey(product.getProdId()), product, 0L);
			}
		} else {
			setProduct2Redis(product) ;
		}
		
		if (syncdb) {
			return productDao.updateProduct(product) ;
		}

		return 1;
	}

	public int updateListProduct(List<Product> products, boolean syncdb) {
		for(Product p : products){
			updateProduct(p,syncdb) ;
		}
		return products.size();
	}

	public List<Product> getProduct(List<Long> listProductId) {
		if(listProductId == null || listProductId.size() == 0 ){
			return new ArrayList<Product>();
		}

		List<String> listProductKey = new ArrayList<String>() ;
		for(Long i : listProductId){
			listProductKey.add(getProductKey(i)) ;
		}
		return mget(listProductKey,Product.class) ;
	}

	private String delFromProductSet(Product product){
		String primaryKey = getProductKey(product.getProdId());
		
		//1.0 从 Key-Set 中删除Key
		srem(SET_PRODUCT_KEY, primaryKey) ;
		
		//2.0 处理索引情况
		String IndexSetKey = null ;
		if(product != null){
			IndexSetKey = getProductIdxSetKey(product.getUid() );
			if(StringUtil.isNotEmptyString(IndexSetKey)){
				srem(SET_PRODUCT_IDX_KEY,IndexSetKey) ;
				srem(IndexSetKey,primaryKey) ;
			}
		}
		if(product != null){
			IndexSetKey = getProductIdxSetKey(product.getCataId(), product.getBrandId() );
			if(StringUtil.isNotEmptyString(IndexSetKey)){
				srem(SET_PRODUCT_IDX_KEY,IndexSetKey) ;
				srem(IndexSetKey,primaryKey) ;
			}
		}
		
		return primaryKey ;
	}
	
	public void deleteProduct(Long prodId) {
		Product product = getProductByKey(prodId) ;
		
		List<String> key2Del = new ArrayList<String>() ;
		
		//从Redis中删除 KeyValue
		String primaryKey = delFromProductSet(product);
		key2Del.add(primaryKey) ;
		delete(key2Del) ;
		
		//从数据库中删除
		productDao.deleteProductByKey(prodId);
	}
	
	public void deleteListProduct(List<Product> listProduct) {
		
		//1.0 从SET 中删除对象Key的关联
		List<String> key2Del = new ArrayList<String>() ;
		for(Product product : listProduct){
			product = getProductByKey(product.getProdId()) ;
			
			String primaryKey = delFromProductSet(product);
			key2Del.add(primaryKey) ;
		}
		//3.0 删除元素
		delete(key2Del) ;
		//4.0 先从数据库中删除
		productDao.deleteProductByList(listProduct);
	}
	
	public void setSomething(final String key, final Serializable value, final long seconds) {
		set(key, value, seconds) ;
	}
    
    public <T extends Serializable> T getSomething(final String key, final Class<T> clazz) {
    	return get(key, clazz) ;
    }
}