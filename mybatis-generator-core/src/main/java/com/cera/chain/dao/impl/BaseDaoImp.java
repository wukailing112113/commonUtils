package com.cera.chain.dao.impl;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;

import com.wins.shop.dao.intf.IBaseDao;

public abstract class BaseDaoImp<POJO> implements IBaseDao<POJO> {

	Logger log = Logger.getLogger(BaseDaoImp.class);

	protected SqlSessionFactory sqlSessionFactory;

	/**
	 * 获取表的最大ID，用来初始化话Redis用
	 * @param mapperName
	 * @return
	 */
	public Object getMaxId(String mapperName) {
		if ((mapperName == null) || "".equals(mapperName.trim())) {
			return 0;
		}
		Object result = null;
		SqlSession session = null;
		try {
			session = this.getSqlSessionFactory().openSession();
			result = session.selectOne(mapperName, null);
		} catch (DataAccessException e) {
			log.error("BaseDaoImpl's getMaxId() occur error : ", e);
		} finally {
			session.close();
		}
		log.debug("BaseDaoImpl's getMaxId() run successful.");
		return result;
	}

	
	/**
	 * 将pojo对象持久化到数据库
	 * 
	 * @param mapperName
	 *            映射的id
	 * @param pojo
	 *            需要新增的pojo，类型为泛型所指定类型
	 * @return void
	 */
	public POJO saveAndFetch(String mapperName, POJO pojo) {
		if (pojo == null) {
			return null;
		}
		SqlSession session = null;
		try {
			session = this.getSqlSessionFactory().openSession();
			session.insert(mapperName, pojo);

			session.commit();
			log.debug("BaseDaoImpl's save() run successful.");
			return pojo;
		} catch (DataAccessException e) {
			log.error("BaseDaoImpl's sava() occur error : ", e);
		} finally {
			session.close();
		}
		return pojo;
	}

	/**
	 * 将pojo对象持久化到数据库
	 * 
	 * @param mapperName
	 *            映射的id
	 * @param pojo
	 *            需要新增的pojo，类型为泛型所指定类型
	 * @return void
	 */
	public int save(String mapperName, POJO pojo) {
		if (pojo == null) {
			return 0;
		}
		SqlSession session = null;
		int result = 0;
		try {
			session = this.getSqlSessionFactory().openSession();
			result = session.insert(mapperName, pojo);

			session.commit();
			log.debug("BaseDaoImpl's save() run successful.");
			return result;
		} catch (DataAccessException e) {
			log.error("BaseDaoImpl's sava() occur error : ", e);
		} finally {
			session.close();
		}
		return result;
	}

	/**
	 * 将pojoList对象持久化到数据库
	 * 
	 * @param mapperName
	 *            映射的id
	 * @param pojoList
	 *            需要新增的pojoList，类型为泛型所指定类型
	 * @return void
	 */
	public int saveList(String mapperName, List<POJO> pojoList) {
		if (pojoList == null || pojoList.size() == 0) {
			return 0;
		}
		SqlSession session = null;
		int result = 0;
		try {
			session = this.getSqlSessionFactory().openSession();
			result = session.insert(mapperName, pojoList);

			session.commit();
			log.debug("BaseDaoImpl's save() run successful.");
			return result;
		} catch (DataAccessException e) {
			log.error("BaseDaoImpl's sava() occur error : ", e);
		} finally {
			session.close();
		}
		return result;
	}

	/**
	 * 将记录从数据库删除
	 * 
	 * @param mapperName
	 *            映射的id
	 * @param pojo
	 *            需要删除的pojo，类型为泛型所指定类型
	 * @return void
	 */
	public int delete(String mapperName, POJO pojo) {
		SqlSession session = null;
		int result = 0;
		try {
			session = this.getSqlSessionFactory().openSession();
			result = session.delete(mapperName, pojo);

			session.commit();
		} catch (DataAccessException e) {
			log.error("BaseDaoImpl's delete() occur error : ", e);
			return 0;
		} finally {
			session.close();
		}

		log.debug("BaseDaoImpl's delete() run successful.");
		return result;
	}

	/**
	 * 将记录从数据库中批量删除
	 * 
	 * @param mapperName
	 *            映射的id
	 * @param entities
	 *            需要删除实体集合
	 * @return void
	 */
	public void deleteAll(String mapperName, Collection<POJO> entities) {
		SqlSession session = null;
		try {
			session = this.getSqlSessionFactory().openSession();
			session.delete(mapperName, entities);
			session.commit();
		} catch (DataAccessException e) {
			log.error("BaseDaoImpl's deleteAll() occur error : ", e);
		} finally {
			session.close();
		}
		log.debug("BaseDaoImpl's deleteAll() run successful.");
	}

	public int deleteById(String mapperName, Serializable ID) {
		SqlSession session = null;
		int result = 0;
		try {
			session = this.getSqlSessionFactory().openSession();
			result = session.delete(mapperName, ID);

			session.commit();
		} catch (DataAccessException e) {
			log.error("BaseDaoImpl's delete() occur error : ", e);
			return 0;
		} finally {
			session.close();
		}

		log.debug("BaseDaoImpl's delete() run successful.");
		return result;
	}

	/**
	 * 更新数据库记录
	 * 
	 * @param mapperName
	 *            映射的id
	 * @param pojo
	 *            需要更新的pojo，类型为泛型所指定类型
	 * @return void
	 */
	public int update(String mapperName, POJO pojo) {
		SqlSession session = null;
		try {
			session = this.getSqlSessionFactory().openSession();
			int result = session.update(mapperName, pojo);

			session.commit();
			return result;
		} catch (DataAccessException e) {
			log.error("BaseDaoImpl's update() occur error : ", e);
			return -1;
		} finally {
			session.close();
		}
		// log.debug("BaseDaoImpl's update() run successful.");
	}

	/**
	 * 批量更新数据库记录
	 * 
	 * @param mapperName
	 *            映射的id
	 * @param pojo
	 *            需要更新的pojo，类型为泛型所指定类型
	 * @return void
	 */
	public int updateList(String mapperName, List<POJO> pojoList) {
		SqlSession session = null;
		try {
			session = this.getSqlSessionFactory().openSession();
			int result = session.update(mapperName, pojoList);

			session.commit();
			return result;
		} catch (DataAccessException e) {
			log.error("BaseDaoImpl's update() occur error : ", e);
			return -1;
		} finally {
			session.close();
		}
		// log.debug("BaseDaoImpl's update() run successful.");
	}

	/**
	 * 根据主键，获得数据库一条对应的记录,如果没有相应的实体，返回 null
	 * 
	 * @param mapperName
	 *            映射的id
	 * @param ID
	 *            主键类型可以是(Integer,Float,Double,Short,Byte,String)
	 * @return POJO 从数据库获得的相应记录，POJO的实例，类型为POJO泛型
	 */
	public POJO getPojoById(String mapperName, Serializable ID) {
		if ((mapperName == null) || "".equals(mapperName.trim())) {
			return null;
		}

		POJO pojo = null;

		SqlSession session = null;
		try {
			session = this.getSqlSessionFactory().openSession();
			pojo = session.selectOne(mapperName, ID);

		} catch (DataAccessException e) {
			log.error("BaseDaoImpl's getPojoById() occur error : ", e);
		} finally {
			session.close();
		}
		log.debug("BaseDaoImpl's getPojoById() run successful.");
		return pojo;
	}

	/**
	 * 根据POJO的属性获得数据库相应记录(相当于根据查询条件的字符串获得相应记录)
	 * 
	 * @param mapperName
	 *            映射的id
	 * @return List<POJO> 从数据库获得的相应记录的结果集，list的元素为POJO泛型
	 */
	public List<POJO> findByProperty(String mapperName, POJO pojo) {
		if (mapperName == null) {
			return null;
		}
		List<POJO> list = null;
		SqlSession session = null;
		try {
			session = this.getSqlSessionFactory().openSession();
			list = session.selectList(mapperName, pojo);
		} catch (DataAccessException e) {
			log.error("BaseDaoImpl's findByProperty() occur error : ", e);
		} finally {
			session.close();
		}
		log.debug("BaseDaoImpl's findByProperty() run successful.");
		return list;
	}

	public <T> List<T> execMapperByProp(String mapperName, POJO pojo,Class<T> clazz) {
		if (mapperName == null) {
			return null;
		}
		List<T> list = null;
		SqlSession session = null;
		try {
			session = this.getSqlSessionFactory().openSession();
			list = session.selectList(mapperName, pojo);
		} catch (DataAccessException e) {
			log.error("BaseDaoImpl's findByProperty() occur error : ", e);
		} finally {
			session.close();
		}
		log.debug("BaseDaoImpl's findByProperty() run successful.");
		return list;
	}
	
	public List<POJO> findByListProperty(String mapperName, List<POJO> pojo) {
		if (mapperName == null) {
			return null;
		}
		List<POJO> list = null;
		SqlSession session = null;
		try {
			session = this.getSqlSessionFactory().openSession();
			list = session.selectList(mapperName, pojo);
		} catch (DataAccessException e) {
			log.error("BaseDaoImpl's findByProperty() occur error : ", e);
		} finally {
			session.close();
		}
		log.debug("BaseDaoImpl's findByProperty() run successful.");
		return list;
	}

	
	/**
	 * 根据Map的参数获取数据库数据（用于连表查询传多个值的情况，单表查询请使用findByProperty）
	 * 
	 * @param mapperName
	 *            映射的id
	 * @param prams
	 *            从数据库获得的相应记录的结果集，list的元素为POJO泛型
	 * @return
	 */
	public List<POJO> findByMap(String mapperName, Map<String, Object> prams) {
		if (mapperName == null) {
			return null;
		}
		List<POJO> list = null;
		SqlSession session = null;
		try {
			session = this.getSqlSessionFactory().openSession();
			list = session.selectList(mapperName, prams);
		} catch (DataAccessException e) {
			log.error("BaseDaoImpl's findByProperty() occur error : ", e);
		} finally {
			session.close();
		}
		log.debug("BaseDaoImpl's findByProperty() run successful.");
		return list;
	}

	/**
	 * 获取查询的总数
	 * 
	 * @param mapperName
	 *            映射的id
	 * @return int 总数
	 */
	public Object getRowCount(String mapperName, POJO pojo) {
		if ((mapperName == null) || "".equals(mapperName.trim())) {
			return 0;
		}
		Object result = null;
		SqlSession session = null;
		try {
			session = this.getSqlSessionFactory().openSession();
			result = session.selectOne(mapperName, pojo);
		} catch (DataAccessException e) {
			log.error("BaseDaoImpl's getPojoById() occur error : ", e);
		} finally {
			session.close();
		}
		log.debug("BaseDaoImpl's getPojoById() run successful.");
		return result;
	}
	
	public Object getRowCountByMap(String mapperName, Map<String, Object> param) {
		if ((mapperName == null) || "".equals(mapperName.trim())) {
			return 0;
		}
		Object result = null;
		SqlSession session = null;
		try {
			session = this.getSqlSessionFactory().openSession();
			result = session.selectOne(mapperName, param);
		} catch (DataAccessException e) {
			log.error("BaseDaoImpl's getPojoById() occur error : ", e);
		} finally {
			session.close();
		}
		log.debug("BaseDaoImpl's getPojoById() run successful.");
		return result;
	}

	public SqlSessionFactory getSqlSessionFactory() {
		return this.sqlSessionFactory;
	}

	public abstract void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory);

}
