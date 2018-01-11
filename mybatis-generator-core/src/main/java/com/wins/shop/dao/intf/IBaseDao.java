package com.wins.shop.dao.intf;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author aone
 *
 * @param <POJO>
 */
public interface IBaseDao<POJO> {

	/**
	 * 将pojo对象持久化到数据库
	 * 
	 * @param mapperName
	 *            映射的id
	 * @param pojo
	 *            需要新增的pojo，类型为泛型所指定类型
	 * @return POJO
	 */
	public POJO saveAndFetch(String mapperName, POJO pojo);

	/**
	 * 将pojo对象持久化到数据库
	 * 
	 * @param mapperName
	 *            映射的id
	 * @param pojo
	 *            需要新增的pojo，类型为泛型所指定类型
	 * @return void
	 */
	public int save(String mapperName, POJO pojo);

	/**
	 * 将记录从数据库删除
	 * 
	 * @param mapperName
	 *            映射的id
	 * @param pojo
	 *            需要删除的pojo，类型为泛型所指定类型
	 * @return void
	 */
	public int delete(String mapperName, POJO pojo);

	/**
	 * @param mapperName
	 * @param ID
	 *            主键类型可以是(Integer,Float,Double,Short,Byte,String)
	 * @return
	 */
	public int deleteById(String mapperName, Serializable ID);

	/**
	 * 将记录从数据库中批量删除
	 * 
	 * @param mapperName
	 *            映射的id
	 * @param entities
	 *            需要删除实体集合
	 * @return void
	 */
	public void deleteAll(String mapperName, Collection<POJO> entities);

	/**
	 * 更新数据库记录
	 * 
	 * @param mapperName
	 *            映射的id
	 * @param pojo
	 *            需要更新的pojo，类型为泛型所指定类型
	 * @return void
	 */
	public int update(String mapperName, POJO pojo);

	/**
	 * 根据主键，获得数据库一条对应的记录
	 * 
	 * @param mapperName
	 *            映射的id
	 * @param ID
	 *            主键类型可以是(Integer,Float,Double,Short,Byte,String)
	 * @return POJO 从数据库获得的相应记录，POJO的实例，类型为POJO泛型
	 */
	public POJO getPojoById(String mapperName, Serializable ID);

	/**
	 * 根据POJO的属性获得数据库相应记录(相当于根据查询条件的字符串获得相应记录)
	 * 
	 * @param mapperName
	 *            映射的id
	 * @return List<POJO> 从数据库获得的相应记录的结果集，list的元素为POJO泛型
	 */
	public List<POJO> findByProperty(String mapperName, POJO pojo);

	/**
	 * 根据Map的参数获取数据库数据（用于连表查询传多个值的情况，单表查询请使用findByProperty）
	 * 
	 * @param mapperName
	 *            映射的id
	 * @param prams
	 *            从数据库获得的相应记录的结果集，list的元素为POJO泛型
	 * @return List<POJO> 从数据库获得的相应记录的结果集，list的元素为POJO泛型
	 */
	public List<POJO> findByMap(String mapperName, Map<String, Object> prams);

	/**
	 * 获得数据库表的总记录数
	 * 
	 * @param mapperName
	 *            映射的id
	 * @return int 数据库表的总记录数
	 */
	public Object getRowCount(String mapperName, POJO pojo);

	/**
	 * 获得数据库表的总记录数（连表查询时使用，单表查询请使用getRowCount）
	 * 
	 * @param mapperName
	 * @param param
	 * @return
	 */
	public Object getRowCountByMap(String mapperName, Map<String, Object> param);
	
}
