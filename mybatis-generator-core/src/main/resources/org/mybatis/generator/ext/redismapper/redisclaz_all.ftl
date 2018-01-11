package ${basePackage}.cache.impl.${schema};

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.Collections;
import java.io.Serializable;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import ${basePackage}.cache.BaseRedis;
import ${basePackage}.cache.intf.${schema}.I${Bean}Redis;
import ${basePackage}.dao.intf.${schema}.I${Bean}Dao;
import ${basePackage}.entity.PageEntity;
import ${basePackage}.entity.${schema}.${Bean};
import ${basePackage}.util.CommonUtil;

import ${basePackage}.util.StringUtil;

/**
	 *Redis的模型设计：
	 * 一、KEY-VALUE 模型：
	 * 1.1 Key-Value, 其中 Key由 ${upperBean}:{id} 组成，Value则是 ${upperBean}的对象；
	 * 1.2 Set(Key),  1中Key组成的集合，方便列举全部对象， Set 里面没有元素，SetKey自动被删除；
	 * 二、IDX-SET模型，主要是针对唯一索引，方便根据索引字段值获取唯一的对象
	 * 2.1 为每一个索引值都建一个SETKEY，以兼容非唯一索引的情况，里面存所有索引包含的Key；
	 * 2.2 有一个总的SETKEY，方便用来删除；
	 *  例如： student 表有主键 id， 唯一索引 学号 code 
	 *  有2条记录  1 Code00001 | 2 Code00002
	 *  那么我们redis里面有如下数据
	 *  key-value：  Stundent:1 stuObj1 ;Stundent:2 stuObj2 ;
	 *  学生主键的SET SET:Stundent{ Stundent:1,Stundent:2} ;
	 *  学号的 SETKey SET:Stundent:Code:Code00001{ Stundent:1} , SET:Stundent:Code:Code00002{ Stundent:2} ;
	 *  所有学号SetKey的Set  SET:Stundent:IDX{SET:Stundent:Code:Code00001,SET:Stundent:Code:Code00002} ;
	*/
	
@Repository("${bean}Redis")
public class ${Bean}RedisImpl extends BaseRedis<String, ${Bean}>implements I${Bean}Redis {

	private static final Logger logger = Logger.getLogger(${Bean}RedisImpl.class);

	@Autowired I${Bean}Dao ${bean}Dao;

	public final static String PRE_KEY = ${Bean}.class.getSimpleName();
	public final static String SEQ_${upperBean}_KEY = new StringBuffer("SEQ:").append(PRE_KEY).toString() ;
	public final static String SET_${upperBean}_KEY = new StringBuffer("SET:").append(PRE_KEY).toString() ;
	<#if pColumRelate?? >
	public final static String SET_${upperBean}_PARENT_KEY = new StringBuffer("SET:").append(PRE_KEY).append(":PARENT").toString() ;	
	</#if>
	public final static String SET_${upperBean}_IDX_KEY = new StringBuffer("SET:").append(PRE_KEY).append(":IDX").toString() ;
	
	<#if listPkCol?size == 1 && listPkCol[0].type != "String">
	public final static ${listPkCol[0].type} START_${upperBean}ID = <#if listPkCol[0].type == "Long">1000000L<#else>1000</#if>;
	</#if>
	
	<#if listPkCol?size == 1 && listPkCol[0].type != "String">
	public ${listPkCol[0].type} ${getBeanIdMethod}(<#if maxIdCond?? >${maxIdCond.type} ${maxIdCond.name}</#if>){
		<#if maxIdCond?? > 
		String seqKey = new StringBuilder(SEQ_${upperBean}_KEY).append(":").append(<#if maxIdCond?? >${maxIdCond.name}</#if>).toString() ;
		${listPkCol[0].type} ${listPkCol[0].name} = ${listPkCol[0].type}.valueOf(incr(seqKey).toString());
		if(${listPkCol[0].name} <= 1<#if listPkCol[0].type == "Long">L</#if>){  // 缓存里面还没有，从数据中加载吧
			${listPkCol[0].name} = ${beanDao}.${findMaxBeanIdDaoMethod}(<#if maxIdCond?? >${maxIdCond.name}</#if>);
			if( ${listPkCol[0].name} == null || ${listPkCol[0].name} == 0){
				<#if maxIdPrefix != '0' >
				StringBuilder sbMaxId = new StringBuilder("${maxIdPrefix}") ;
				sbMaxId.append(${maxIdCond.name}) ;
				<#if maxIdCond?? >${maxIdCond.type}</#if> baseMaxId = <#if maxIdCond?? >${maxIdCond.type}</#if>.valueOf(sbMaxId.toString()) ;
				<#else>
				<#if maxIdCond?? >${maxIdCond.type}</#if> baseMaxId = <#if maxIdCond?? >${maxIdCond.type}</#if>.valueOf(${maxIdCond.name}) ;
				</#if>
				${listPkCol[0].name} = baseMaxId * ${maxIdPostfix} + ${listPkCol[0].type}.valueOf("1") ;
			}else{
				${listPkCol[0].name} ++;
			}
			set(seqKey, ${listPkCol[0].name}.toString(), 0L, true);
		}
		return ${listPkCol[0].name} ;
		<#else>
		${listPkCol[0].type} ${listPkCol[0].name} = ${listPkCol[0].type}.valueOf(incr(SEQ_${upperBean}_KEY).toString());
		if(${listPkCol[0].name} <= 1<#if listPkCol[0].type == "Long">L</#if>){  // 缓存里面还没有，从数据中加载吧
			${listPkCol[0].name} = ${beanDao}.${findMaxBeanIdDaoMethod}(<#if maxIdCond?? >${bean}</#if>);
			if( ${listPkCol[0].name} == null || ${listPkCol[0].name} == 0 ){
				${listPkCol[0].name} = START_${upperBean}ID ;
			}else{
				${listPkCol[0].name} ++;
			}
			set(SEQ_${upperBean}_KEY, ${listPkCol[0].name}.toString(), 0L, true);
		}
		return ${listPkCol[0].name} ;
		</#if>
	}

	<#if maxIdCond?? >
	// 这个ID比较负责,除了Group外，暂时没想到好的方法，所以暂时不提供。
	/*@PostConstruct
	public void ${initMaxBeanIdMethod}(){
		${listPkCol[0].type} ${listPkCol[0].name} = ${beanDao}.${findMaxBeanIdDaoMethod}();
		if (${listPkCol[0].name} == null || ${listPkCol[0].name} == 0) {
			${listPkCol[0].name} = START_${upperBean}ID ;
		}
		set(SEQ_${upperBean}_KEY, ${listPkCol[0].name}.toString(), 0L);
	}
	*/
	<#else>
	@PostConstruct
	public void ${initMaxBeanIdMethod}(){
		${listPkCol[0].type} ${listPkCol[0].name} = ${beanDao}.${findMaxBeanIdDaoMethod}();
		if (${listPkCol[0].name} == null || ${listPkCol[0].name} == 0) {
			${listPkCol[0].name} = START_${upperBean}ID ;
		}
		set(SEQ_${upperBean}_KEY, ${listPkCol[0].name}.toString(), 0L);
	}
	</#if>
	</#if>

	private void ${clearAllBeanMethod}() {
		
		@SuppressWarnings("unchecked")
		List<String> listKey = (List<String>)smembers(SET_${upperBean}_KEY) ;
		listKey.add(SET_${upperBean}_KEY) ;
		@SuppressWarnings("unchecked")
		List<String> listIdxKey = (List<String>)smembers(SET_${upperBean}_IDX_KEY) ;
		listKey.addAll(listIdxKey) ;
		listKey.add(SET_${upperBean}_IDX_KEY) ;
		
		<#if pColumRelate?? >
		@SuppressWarnings("unchecked")
		List<String> listParentSetKey = (List<String>)smembers(SET_${upperBean}_PARENT_KEY) ;
		listKey.addAll(listParentSetKey) ;
		listKey.add(SET_${upperBean}_PARENT_KEY) ;
		</#if>
		
		if(listKey.size() > 0){
			delete(listKey) ;
		}
	}

	/**
	 * 把对象存入Redis
	 * 1. Key-Value 对象;
	 * 2. SET_${upperBean}_KEY 1中对象的Key的Set;
	 * 3. 索引的对应关系，设计成Set,方便后续扩展成非索引。
	 */
	private void set${Bean}2Redis(${Bean} ${bean}){
		String primaryKey = ${getBeanPKIdKeyMethod}(<#list listPkCol as pkPara>${bean}.get${pkPara.name?cap_first}()<#if (pkPara_has_next)>, </#if></#list>);
		sadd(SET_${upperBean}_KEY, primaryKey);
		set(primaryKey, ${bean}, 0L);
		<#if pColumRelate?? >
		// 处理字段之间有父子关系的情况
		String parentSetKey = getParent${Bean}SetKey(${bean}.${getParentIdMethod}()) ;
		sadd(SET_${upperBean}_PARENT_KEY, parentSetKey); 
		sadd(parentSetKey, primaryKey); 
		</#if>
		
		<#if listUniqueIdxCol?size gt 0>
		// 处理唯一索引情况
		String uniqueIndexSetKey = null ;
		</#if>
		<#list listUniqueIdxCol as idx>
		uniqueIndexSetKey = get${Bean}UnqIdx4${idx.key}SetKey(<#list idx.litsPKBean as pkBean>${bean}.get${pkBean.name?cap_first}()<#if pkBean_has_next >, </#if></#list> );
		if(StringUtil.isNotEmptyString(uniqueIndexSetKey)){
			sadd(SET_${upperBean}_IDX_KEY, uniqueIndexSetKey) ;
			sadd(uniqueIndexSetKey,primaryKey) ;
		}
   	 	</#list>
   	 	
   	 	<#if listOtherIdxCol?size gt 0>
		// 处理非唯一索引索引情况
		String otherIndexSetKey = null ;
		</#if>
		<#list listOtherIdxCol as idx>
		otherIndexSetKey = get${Bean}Idx4${idx.key}SetKey(<#list idx.litsPKBean as pkBean>${bean}.get${pkBean.name?cap_first}()<#if pkBean_has_next >, </#if></#list> );
		if(StringUtil.isNotEmptyString(otherIndexSetKey)){
			sadd(SET_${upperBean}_IDX_KEY, otherIndexSetKey) ;
			sadd(otherIndexSetKey,primaryKey) ;
		}
   	 	</#list>
	   	 	
	}

		
	/**
	 * 把对象存入Redis
	 * 1. Key-Value 对象;
	 * 2. SET_${upperBean}_KEY 1中对象的Key的Set;
	 * 3. 索引的对应关系，设计成Set,方便后续扩展成非索引。
	 */
	private void set${Bean}2Redis(${Bean} ${bean}, long expire){
		String primaryKey = ${getBeanPKIdKeyMethod}(<#list listPkCol as pkPara>${bean}.get${pkPara.name?cap_first}()<#if (pkPara_has_next)>, </#if></#list>);
		sadd(SET_${upperBean}_KEY, primaryKey);
		set(primaryKey, ${bean}, expire);
		
		<#if pColumRelate?? >
		// 处理字段之间有父子关系的情况
		String parentSetKey = getParent${Bean}SetKey(${bean}.${getParentIdMethod}()) ;
		sadd(SET_${upperBean}_PARENT_KEY, parentSetKey); 
		sadd(parentSetKey, primaryKey); 
		</#if>
		
		<#if listUniqueIdxCol?size gt 0>
		// 处理索引情况
		String uniqueIndexSetKey = null ;
		</#if>
		<#list listUniqueIdxCol as idx>
		uniqueIndexSetKey = get${Bean}UnqIdx4${idx.key}SetKey(<#list idx.litsPKBean as pkBean>${bean}.get${pkBean.name?cap_first}()<#if pkBean_has_next >, </#if></#list> );
		if(StringUtil.isNotEmptyString(uniqueIndexSetKey)){
			sadd(SET_${upperBean}_IDX_KEY, uniqueIndexSetKey) ;
			sadd(uniqueIndexSetKey,primaryKey) ;
		}
   	 	</#list>
   	 	
   	 	<#if listOtherIdxCol?size gt 0>
		// 处理非唯一索引索引情况
		String otherIndexSetKey = null ;
		</#if>
		<#list listOtherIdxCol as idx>
		otherIndexSetKey = get${Bean}Idx4${idx.key}SetKey(<#list idx.litsPKBean as pkBean>${bean}.get${pkBean.name?cap_first}()<#if pkBean_has_next >, </#if></#list> );
		if(StringUtil.isNotEmptyString(otherIndexSetKey)){
			sadd(SET_${upperBean}_IDX_KEY, otherIndexSetKey) ;
			sadd(otherIndexSetKey,primaryKey) ;
		}
   	 	</#list>
	}
	
	public void ${loadAllBeanMethod}(){
		${clearAllBeanMethod}() ;

		${Bean} ${bean} = new ${Bean}() ;
		List<${Bean}> list${Bean} = new ArrayList<${Bean}>() ;
		Long total = ${getBeanTotalMethod}(${bean}) ;
		Integer totalPage = (int)Math.ceil((double)total / LOAD_PAGE_SIZE);
		${bean}.setPageSize(LOAD_PAGE_SIZE);
		for(int i = 1 ; i <= totalPage; i++){
			${bean}.setPage(i);
			${bean}.setIndex((i - 1) * ${bean}.getPageSize());
			list${Bean} = ${beanDao}.${findBeansDaoByPageMethod}(${bean});
			for(${Bean} it${Bean} : list${Bean}){
				set${Bean}2Redis(it${Bean}) ;
			}
		}
	}

	/**
	 *  加载到缓存， 提供超时时间
	 */
	public void ${loadAllBeanMethod}(long expire){
		${clearAllBeanMethod}() ;

		${Bean} ${bean} = new ${Bean}() ;
		List<${Bean}> list${Bean} = new ArrayList<${Bean}>() ;
		Long total = ${getBeanTotalMethod}(${bean}) ;
		Integer totalPage = (int)Math.ceil((double)total / LOAD_PAGE_SIZE);
		${bean}.setPageSize(LOAD_PAGE_SIZE);
		for(int i = 1 ; i <= totalPage; i++){
			${bean}.setPage(i);
			list${Bean} = ${beanDao}.${findBeansDaoByPageMethod}(${bean});
			for(${Bean} it${Bean} : list${Bean}){
				set${Bean}2Redis(it${Bean}, expire) ;
			}
		}
	}

	/**
	 * 获取记录数，存缓存。
	 * 关于查询结果的缓存
	 * Hset 存储，Key 为 实体类:MD5(条件) total 1 List<${Bean}> 2 List<${Bean}> ....
	 * 过期时间为30秒
	 */
	public Long ${getBeanTotalMethod}(${Bean} ${bean}) {
		String hsetKey = CommonUtil.getAttrVal4ForRedisKey(${bean}, false) ;
		Long total = hget(hsetKey,PageEntity.TOTAL,Long.class) ;
		if(total == null || total == 0L){ // 从数据库里面查一遍呗
			total = ${beanDao}.${getBeanTotalCountDaoMethod}(${bean}) ;
			hset(hsetKey,PageEntity.TOTAL,total,PageEntity.EXPIRE) ;
		}
		return total ;
	}

	/**
	 * 获取记录数，useCache为true的时候存缓存。 过期时间为30秒
	 */
	public Long ${getBeanTotalMethod}(${Bean} ${bean}, boolean useCache) {
		if (useCache) {
			return ${getBeanTotalMethod}(${bean});
		}
		
		return ${beanDao}.${getBeanTotalCountDaoMethod}(${bean});
	}

	private String ${getBeanPKIdKeyMethod}(<#list listPkCol as pkPara>final ${pkPara.type} ${pkPara.name}<#if (pkPara_has_next)>, </#if></#list>){
		return new StringBuffer(PRE_KEY)<#list listPkCol as pkPara>.append(":").append(${pkPara.name})</#list>.toString() ;
	}

	<#if pColumRelate?? >
	private String getParent${Bean}SetKey(${relateColumn.type} ${relateColumn.name}){
		return new StringBuffer(SET_${upperBean}_PARENT_KEY).append(":").append(${relateColumn.name}).toString() ;
	}
	</#if>
	
	public ${Bean} ${getBeanByKeyMethod}(<#list listPkCol as pkPara>${pkPara.type} ${pkPara.name}<#if (pkPara_has_next)>, </#if></#list>){
		String primaryKey = ${getBeanPKIdKeyMethod}(<#list listPkCol as pkPara>${pkPara.name}<#if (pkPara_has_next)>, </#if></#list>) ;
		${Bean} ${bean} = get(primaryKey,${Bean}.class) ;
		if(${bean} == null){
			${bean} = ${beanDao}.${findBeanByIdDaoMethod}(<#list listPkCol as pkPara>${pkPara.name}<#if (pkPara_has_next)>, </#if></#list>) ;
			if(${bean} != null){
				set${Bean}2Redis(${bean}) ;
			}
		}
		return ${bean} ;
	}

	/**
	 * 提供多一个超时时间
	 */
	public ${Bean} ${getBeanByKeyMethod}(<#list listPkCol as pkPara>${pkPara.type} ${pkPara.name}<#if (pkPara_has_next)>, </#if></#list>, long expire){
		String primaryKey = ${getBeanPKIdKeyMethod}(<#list listPkCol as pkPara>${pkPara.name}<#if (pkPara_has_next)>, </#if></#list>) ;
		${Bean} ${bean} = get(primaryKey,${Bean}.class) ;
		if(${bean} == null){
			${bean} = ${beanDao}.${findBeanByIdDaoMethod}(<#list listPkCol as pkPara>${pkPara.name}<#if (pkPara_has_next)>, </#if></#list>) ;
			if(${bean} != null){
				set${Bean}2Redis(${bean}, expire) ;
			}
		}
		return ${bean} ;
	}

	public ${Bean} ${getBeanByKeyMethod}ForUpdate(<#list listPkCol as pkPara>${pkPara.type} ${pkPara.name}<#if (pkPara_has_next)>, </#if></#list>){
		${Bean} ${bean} = ${beanDao}.${findBeanByIdDaoMethod}ForUpdate(<#list listPkCol as pkPara>${pkPara.name}<#if (pkPara_has_next)>, </#if></#list>) ;
		return ${bean} ;
	}

	<#list listUniqueIdxCol as idx>
	private String get${Bean}UnqIdx4${idx.key}SetKey(<#list idx.litsPKBean as pkBean>${pkBean.type} ${pkBean.name}<#if pkBean_has_next >, </#if></#list>){
		return new StringBuffer(SET_${upperBean}_KEY)<#list idx.litsPKBean as pkBean>.append(":${pkBean.name?cap_first}:".toUpperCase()).append(${pkBean.name}.toString())</#list>.toString() ;
    }
    public ${Bean} get${Bean}By${idx.key}(<#list idx.litsPKBean as pkBean>${pkBean.type} ${pkBean.name}<#if pkBean_has_next >, </#if></#list>){
    	String uniqueIndexSetKey = get${Bean}UnqIdx4${idx.key}SetKey(<#list idx.litsPKBean as pkBean>${pkBean.name}<#if pkBean_has_next >, </#if></#list>) ;
    	${Bean} ${bean} = null ;

		@SuppressWarnings("unchecked")
		List<String> listKey = (List<String>)smembers(uniqueIndexSetKey) ;
    	if(listKey != null && listKey.size() > 0){
    		${bean} = get(listKey.get(0), ${Bean}.class) ;
    	}
    	
		// 从数据库里面查
		if(${bean} == null){
			${bean} = new ${Bean}() ;
			<#list idx.litsPKBean as pkBean>
			${bean}.set${pkBean.name?cap_first}(${pkBean.name}) ;
			<#if pkBean.type == "String" > 
			${bean}.set${pkBean.name?cap_first}Equal(Boolean.TRUE) ;
			</#if>
			</#list>
			List<${Bean}> list${Bean} = ${beanDao}.select${Bean}s(${bean}) ;
			if(list${Bean} != null && list${Bean}.size() == 1){
				${bean} = list${Bean}.get(0) ;
			}else{
				${bean} = null ;
			}
			if(${bean} != null){
				set${Bean}2Redis(${bean}) ;
			}
		}
		return ${bean};
    }

    public ${Bean} get${Bean}By${idx.key}(<#list idx.litsPKBean as pkBean>${pkBean.type} ${pkBean.name}<#if pkBean_has_next >, </#if></#list>, long expire) {
    	String uniqueIndexSetKey = get${Bean}UnqIdx4${idx.key}SetKey(<#list idx.litsPKBean as pkBean>${pkBean.name}<#if pkBean_has_next >, </#if></#list>) ;
    	${Bean} ${bean} = null ;
		@SuppressWarnings("unchecked")
		List<String> listKey = (List<String>)smembers(uniqueIndexSetKey) ;
    	if(listKey != null && listKey.size() > 0){
    		${bean} = get(listKey.get(0), ${Bean}.class) ;
    	}
    	
		// 从数据库里面查
		if(${bean} == null){
			${bean} = new ${Bean}() ;
			<#list idx.litsPKBean as pkBean>
			${bean}.set${pkBean.name?cap_first}(${pkBean.name}) ;
			<#if pkBean.type == "String" > 
			${bean}.set${pkBean.name?cap_first}Equal(Boolean.TRUE) ;
			</#if>
			</#list>
			List<${Bean}> list${Bean} = ${beanDao}.select${Bean}s(${bean}) ;
			if(list${Bean} != null && list${Bean}.size() == 1){
				${bean} = list${Bean}.get(0) ;
			}else{
				${bean} = null ;
			}
			if(${bean} != null){
				set${Bean}2Redis(${bean}, expire) ;
			}
		}
		return ${bean};
    }
    </#list>
    
    <#list listOtherIdxCol as idx>
	private String get${Bean}Idx4${idx.key}SetKey(<#list idx.litsPKBean as pkBean>${pkBean.type} ${pkBean.name}<#if pkBean_has_next >, </#if></#list>){
		return new StringBuffer(SET_${upperBean}_KEY)<#list idx.litsPKBean as pkBean>.append(":${pkBean.name?cap_first}:".toUpperCase()).append(${pkBean.name})</#list>.toString() ;
    }
    public List<${Bean}> get${Bean}By${idx.key}(<#list idx.litsPKBean as pkBean>${pkBean.type} ${pkBean.name}<#if pkBean_has_next >, </#if></#list>){
    	String otherIndexSetKey = get${Bean}Idx4${idx.key}SetKey(<#list idx.litsPKBean as pkBean>${pkBean.name}<#if pkBean_has_next >, </#if></#list>) ;
    	List<${Bean}> list${Bean} = null ;

		@SuppressWarnings("unchecked")
		List<String> listKey = (List<String>)smembers(otherIndexSetKey) ;
    	if(listKey != null && listKey.size() > 0){
    		list${Bean} = mget(listKey, ${Bean}.class) ;
    	}
    	
		// 从数据库里面查
		if(list${Bean} == null || list${Bean}.size() == 0){
			${Bean} ${bean} = new ${Bean}() ;
			<#list idx.litsPKBean as pkBean>
			${bean}.set${pkBean.name?cap_first}(${pkBean.name}) ;
			<#if pkBean.type == "String" > 
			${bean}.set${pkBean.name?cap_first}Equal(Boolean.TRUE) ;
			</#if>
			</#list>
			list${Bean} = ${beanDao}.select${Bean}s(${bean}) ;
			if(list${Bean} == null){
				list${Bean} = Collections.emptyList() ;
			}
			for(${Bean} item : list${Bean}){
				set${Bean}2Redis(item) ;
			}
		}
		return list${Bean};
    }

    public List<${Bean}> get${Bean}By${idx.key}(<#list idx.litsPKBean as pkBean>${pkBean.type} ${pkBean.name}<#if pkBean_has_next >, </#if></#list>, long expire) {
    	String otherIndexSetKey = get${Bean}Idx4${idx.key}SetKey(<#list idx.litsPKBean as pkBean>${pkBean.name}<#if pkBean_has_next >, </#if></#list>) ;
    	List<${Bean}> list${Bean} = null ;

		@SuppressWarnings("unchecked")
		List<String> listKey = (List<String>)smembers(otherIndexSetKey) ;
    	if(listKey != null && listKey.size() > 0){
    		list${Bean} = mget(listKey, ${Bean}.class) ;
    	}
    	
		// 从数据库里面查
		if(list${Bean} == null || list${Bean}.size() == 0){
			${Bean} ${bean} = new ${Bean}() ;
			<#list idx.litsPKBean as pkBean>
			${bean}.set${pkBean.name?cap_first}(${pkBean.name}) ;
			<#if pkBean.type == "String" > 
			${bean}.set${pkBean.name?cap_first}Equal(Boolean.TRUE) ;
			</#if>
			</#list>
			list${Bean} = ${beanDao}.select${Bean}s(${bean}) ;
			if(list${Bean} == null){
				list${Bean} = Collections.emptyList() ;
			}
			for(${Bean} item : list${Bean}){
				set${Bean}2Redis(item, expire) ;
			}
		}
		return list${Bean};
    }
    </#list>
    
	// 执行分页查询的逻辑
	@SuppressWarnings("unchecked")
	public List<${Bean}> ${getBeanByPageMethod}(${Bean} ${bean}, Boolean bFromCache, long expire) {
		String hsetKey = CommonUtil.getAttrVal4ForRedisKey(${bean}, false) ;
		ArrayList<${Bean}> list${Bean} = null ;
		if (${bean}.getPage() == null || ${bean}.getPage() == 0){ //从第一页开始取
			${bean}.setPage(1);
		}
		${bean}.setIndex((${bean}.getPage() - 1) * ${bean}.getPageSize()) ;
		if(bFromCache) {
			list${Bean} = (ArrayList<${Bean}>)hget(hsetKey, ${bean}.getPage().toString(), ArrayList.class) ;
		}
		if(list${Bean} == null || list${Bean}.size() == 0){
			logger.debug("=== fetch from database.....");
			list${Bean} = (ArrayList<${Bean}>)${beanDao}.${findBeansDaoByPageMethod}(${bean}) ;
			hset(hsetKey,${bean}.getPage().toString(),list${Bean}, expire) ;
		}
		return list${Bean} ;
	}

	// 执行分页查询的逻辑
	@SuppressWarnings("unchecked")
	public List<${Bean}> ${getBeanByPageMethod}(${Bean} ${bean}, Boolean bFromCache){
		return ${getBeanByPageMethod}(${bean}, bFromCache, PageEntity.EXPIRE);
	}

	public ${Bean} ${addBeanMethod}(${Bean} ${bean}) {
		<#if listPkCol?size == 1 && listPkCol[0].type != "String">
		if(${bean}.get${listPkCol[0].name?cap_first}() == null || ${bean}.get${listPkCol[0].name?cap_first}() == 0L) {
			${bean}.set${listPkCol[0].name?cap_first}(${getBeanIdMethod}(<#if maxIdCond?? >${bean}.${maxIdRelateMethod}()</#if>));
		}
		</#if>
		
		${bean} = ${beanDao}.${addBeanDaoMethod}(${bean});
		set${Bean}2Redis(${bean}) ;
		return ${bean};
	}
	
	<#if pColumRelate?? >
	public List<${Bean}> getSub${Bean}sBy${relateColumn.name}(${relateColumn.type} ${relateColumn.name}){
		String parentSetKey = getParent${Bean}SetKey(${relateColumn.name}) ;
		@SuppressWarnings("unchecked")
		List<String> listKeys = (List<String>)smembers(parentSetKey) ; 
		if(listKeys != null && listKeys.size() > 0){
			return mget(listKeys, ${Bean}.class) ;
		}
		else{
			return new ArrayList<${Bean}>() ;
		}
	}
	</#if>
	
	
	public ${Bean} ${addBeanMethod}(${Bean} ${bean}, long expire) {
		<#if listPkCol?size == 1 && listPkCol[0].type != "String">
		if(${bean}.get${listPkCol[0].name?cap_first}() == null || ${bean}.get${listPkCol[0].name?cap_first}() == 0L) {
			${bean}.set${listPkCol[0].name?cap_first}(${getBeanIdMethod}(<#if maxIdCond?? >${bean}.${maxIdRelateMethod}()</#if>));
		}
		</#if>
		
		${bean} = ${beanDao}.${addBeanDaoMethod}(${bean});
		set${Bean}2Redis(${bean}, expire) ;
		return ${bean};
	}

	public List<${Bean}> ${addListBeanMethod}(List<${Bean}> list${Bean}){
		<#if listPkCol?size == 1 && listPkCol[0].type != "String">
		for(${Bean} ${bean} : list${Bean}){
			if(${bean}.get${listPkCol[0].name?cap_first}() == null || ${bean}.get${listPkCol[0].name?cap_first}() == 0L) {
				${bean}.set${listPkCol[0].name?cap_first}(${getBeanIdMethod}(<#if maxIdCond?? >${bean}.${maxIdRelateMethod}()</#if>));
			}
			set${Bean}2Redis(${bean}) ;
		}
		<#else>
		for(${Bean} ${bean} : list${Bean}){
			set${Bean}2Redis(${bean}) ;
		}
		</#if>
		${beanDao}.${addListDaoMethod}(list${Bean});
		return list${Bean};
	}
	
	public List<${Bean}> ${addListBeanMethod}(List<${Bean}> list${Bean}, long expire){
		<#if listPkCol?size == 1 && listPkCol[0].type != "String">
		for(${Bean} ${bean} : list${Bean}){
			if(${bean}.get${listPkCol[0].name?cap_first}() == null || ${bean}.get${listPkCol[0].name?cap_first}() == 0L) {
				${bean}.set${listPkCol[0].name?cap_first}(${getBeanIdMethod}(<#if maxIdCond?? >${bean}.${maxIdRelateMethod}()</#if>));
			}
			set${Bean}2Redis(${bean}, expire) ;
		}
		<#else>
		for(${Bean} ${bean} : list${Bean}){
			set${Bean}2Redis(${bean}, expire) ;
		}
		</#if>
		${beanDao}.${addListDaoMethod}(list${Bean});
		return list${Bean};
	}

	public int ${updateBeanMethod}(${Bean} ${bean}, boolean syncdb) {
		String primaryKey =  ${getBeanPKIdKeyMethod}(<#list listPkCol as pkPara>${bean}.get${pkPara.name?cap_first}()<#if (pkPara_has_next)>, </#if></#list>);
		${Bean} old${Bean} = get(primaryKey, ${Bean}.class) ;
		// 尝试数据库中再查询一遍
		if ( old${Bean} == null){
			old${Bean} = ${beanDao}.${getBeanByKeyMethod}(${bean}.get${listPkCol[0].name?cap_first}()) ;
		}
		if( old${Bean} == null){
			return 0 ;
		}
		<#if listUniqueIdxCol?size gt 0 || listOtherIdxCol?size gt 0>
			<#if listUniqueIdxCol?size gt 0>
			if (old${Bean} != null) {
				if (!(<#list listUniqueIdxCol as idx><#list idx.litsPKBean as pkBean>old${Bean}.get${pkBean.name?cap_first}().equals(${bean}.get${pkBean.name?cap_first}())<#if pkBean_has_next> && </#if></#list><#if idx_has_next> && </#if></#list> )) {
					delFrom${Bean}Set(old${Bean});
					CommonUtil.copyPropertiesIgnoreNull(${bean}, old${Bean}) ;
					set${Bean}2Redis(old${Bean}) ;
				} else {
					CommonUtil.copyPropertiesIgnoreNull(${bean}, old${Bean}) ;
					// 索引没变，只修改Key-Value 就好了。
					set(${getBeanPKIdKeyMethod}(<#list listPkCol as pkPara>${bean}.get${pkPara.name?cap_first}()<#if (pkPara_has_next)>, </#if></#list>), old${Bean}, 0L);
				}
			} 
			</#if>
		
			<#if listOtherIdxCol?size gt 0>
			if (old${Bean} != null) {
				if (!(<#list listOtherIdxCol as idx><#list idx.litsPKBean as pkBean>old${Bean}.get${pkBean.name?cap_first}().equals(${bean}.get${pkBean.name?cap_first}())<#if pkBean_has_next> && </#if></#list><#if idx_has_next> && </#if></#list> )) {
					delFrom${Bean}Set(old${Bean});
					CommonUtil.copyPropertiesIgnoreNull(${bean}, old${Bean}) ;
					set${Bean}2Redis(old${Bean}) ;
				} else {
					CommonUtil.copyPropertiesIgnoreNull(${bean}, old${Bean}) ;
					// 索引没变，只修改Key-Value 就好了。
					set(${getBeanPKIdKeyMethod}(<#list listPkCol as pkPara>${bean}.get${pkPara.name?cap_first}()<#if (pkPara_has_next)>, </#if></#list>), old${Bean}, 0L);
				}
			} 
			</#if>
		<#else>
		CommonUtil.copyPropertiesIgnoreNull(${bean}, old${Bean}) ;
		set${Bean}2Redis(old${Bean}) ;
		</#if>

		if (syncdb) {
			return ${beanDao}.${updateBeanDaoMethod}(old${Bean}) ;
		}
		return 1;
	}

	public int ${updateBeanMethod}(${Bean} ${bean}, boolean syncdb, long expire) {
		String primaryKey =  ${getBeanPKIdKeyMethod}(<#list listPkCol as pkPara>${bean}.get${pkPara.name?cap_first}()<#if (pkPara_has_next)>, </#if></#list>);
		${Bean} old${Bean} = get(primaryKey, ${Bean}.class) ;
		// 尝试数据库中再查询一遍
		if ( old${Bean} == null){
			old${Bean} = ${beanDao}.${getBeanByKeyMethod}(${bean}.get${listPkCol[0].name?cap_first}()) ;
		}
		if( old${Bean} == null){
			return 0 ;
		}
		
		<#if listUniqueIdxCol?size gt 0 || listOtherIdxCol?size gt 0>
			<#if listUniqueIdxCol?size gt 0>
			if (old${Bean} != null) {
				if (!(<#list listUniqueIdxCol as idx><#list idx.litsPKBean as pkBean>old${Bean}.get${pkBean.name?cap_first}().equals(${bean}.get${pkBean.name?cap_first}())<#if pkBean_has_next> && </#if></#list><#if idx_has_next> && </#if></#list> )) {
					delFrom${Bean}Set(old${Bean});
					CommonUtil.copyPropertiesIgnoreNull(${bean}, old${Bean}) ;
					set${Bean}2Redis(old${Bean}, expire) ;
				} else {
					CommonUtil.copyPropertiesIgnoreNull(${bean}, old${Bean}) ;
					// 索引没变，只修改Key-Value 就好了。
					set(${getBeanPKIdKeyMethod}(<#list listPkCol as pkPara>${bean}.get${pkPara.name?cap_first}()<#if (pkPara_has_next)>, </#if></#list>), old${Bean}, 0L);
				}
			}
			</#if>
			<#if listOtherIdxCol?size gt 0>
			if (old${Bean} != null) {
				if (!(<#list listOtherIdxCol as idx><#list idx.litsPKBean as pkBean>old${Bean}.get${pkBean.name?cap_first}().equals(${bean}.get${pkBean.name?cap_first}())<#if pkBean_has_next> && </#if></#list><#if idx_has_next> && </#if></#list> )) {
					delFrom${Bean}Set(old${Bean});
					CommonUtil.copyPropertiesIgnoreNull(${bean}, old${Bean}) ;
					set${Bean}2Redis(old${Bean}, expire) ;
				} else {
					CommonUtil.copyPropertiesIgnoreNull(${bean}, old${Bean}) ;
					// 索引没变，只修改Key-Value 就好了。
					set(${getBeanPKIdKeyMethod}(<#list listPkCol as pkPara>${bean}.get${pkPara.name?cap_first}()<#if (pkPara_has_next)>, </#if></#list>), old${Bean}, 0L);
				}
			} 
			</#if>
		<#else>
		CommonUtil.copyPropertiesIgnoreNull(${bean}, old${Bean}) ;
		set${Bean}2Redis(old${Bean}, expire) ;
		</#if>
		
		if (syncdb) {
			return ${beanDao}.${updateBeanDaoMethod}(old${Bean}) ;
		}
		return 1;
	}

	public int ${updateListBeanMethod}(List<${Bean}> ${bean}s, boolean syncdb) {
		for(${Bean} p : ${bean}s){
			${updateBeanMethod}(p,syncdb) ;
		}
		return ${bean}s.size();
	}

	public int ${updateListBeanMethod}(List<${Bean}> ${bean}s, boolean syncdb, long expire) {
		for(${Bean} p : ${bean}s){
			${updateBeanMethod}(p,syncdb, expire) ;
		}
		return ${bean}s.size();
	}

	<#if listPkCol?size == 1>
	public List<${Bean}> ${getListBeanMethod}(List<${listPkCol[0].type}> list${Bean}Id) {
		if(list${Bean}Id == null || list${Bean}Id.size() == 0 ){
			return new ArrayList<${Bean}>();
		}

		List<String> list${Bean}Key = new ArrayList<String>() ;
		for(${listPkCol[0].type} i : list${Bean}Id){
			list${Bean}Key.add(${getBeanPKIdKeyMethod}(i)) ;
		}
		return mget(list${Bean}Key,${Bean}.class) ;
	}
	</#if>

	private String delFrom${Bean}Set(${Bean} ${bean}){
		String primaryKey = ${getBeanPKIdKeyMethod}(<#list listPkCol as pkPara>${bean}.get${pkPara.name?cap_first}()<#if (pkPara_has_next)>, </#if></#list>);
		//1.0 从 Key-Set 中删除Key
		srem(SET_${upperBean}_KEY, primaryKey) ;
		
		<#if pColumRelate?? >
		//2.0 处理父子字段的删除情况
		String parentSetKey = getParent${Bean}SetKey(${bean}.${getParentIdMethod}()) ;
		srem(parentSetKey, primaryKey); 
		@SuppressWarnings("unchecked")
		List<String> listKey = (List<String>)smembers(SET_${upperBean}_KEY) ;
		if(listKey.size() <= 0 ){
			srem(SET_${upperBean}_PARENT_KEY, parentSetKey); 
		}
		</#if>
		
		<#if listUniqueIdxCol?size gt 0>
		//3.0 处理唯一索引情况
		String uniqueIndexSetKey = null ;
		</#if>
		<#list listUniqueIdxCol as idx>
		if(${bean} != null){
			uniqueIndexSetKey = get${Bean}UnqIdx4${idx.key}SetKey(<#list idx.litsPKBean as pkBean>${bean}.get${pkBean.name?cap_first}()<#if pkBean_has_next >, </#if></#list> );
			if(StringUtil.isNotEmptyString(uniqueIndexSetKey)){
				srem(SET_${upperBean}_IDX_KEY,uniqueIndexSetKey) ;
				srem(uniqueIndexSetKey,primaryKey) ;
			}
		}
   	 	</#list>
		
		<#if listOtherIdxCol?size gt 0>
		//4.0 处理非唯一索引情况
		String otherIndexSetKey = null ;
		</#if>
		<#list listOtherIdxCol as idx>
		if(${bean} != null){
			otherIndexSetKey = get${Bean}Idx4${idx.key}SetKey(<#list idx.litsPKBean as pkBean>${bean}.get${pkBean.name?cap_first}()<#if pkBean_has_next >, </#if></#list> );
			if(StringUtil.isNotEmptyString(otherIndexSetKey)){
				srem(SET_${upperBean}_IDX_KEY, otherIndexSetKey) ;
				srem(otherIndexSetKey, primaryKey) ;
			}
		}
   	 	</#list>
   	 	
		return primaryKey ;
	}
	
	public void ${deleteBeanByKeyMethod}(<#list listPkCol as pkPara>${pkPara.type} ${pkPara.name}<#if (pkPara_has_next)>, </#if></#list>) {
		${Bean} ${bean} = ${getBeanByKeyMethod}(<#list listPkCol as pkPara>${pkPara.name}<#if (pkPara_has_next)>, </#if></#list>) ;
		
		List<String> key2Del = new ArrayList<String>() ;
		
		//从Redis中删除 KeyValue
		String primaryKey = delFrom${Bean}Set(${bean});
		key2Del.add(primaryKey) ;
		delete(key2Del) ;
		
		//从数据库中删除
		${beanDao}.${deleteBeanDaoMethod}(<#list listPkCol as pkPara>${pkPara.name}<#if (pkPara_has_next)>, </#if></#list>);
	}
	
	public void ${deleteByListMethod}(List<${Bean}> list${Bean}) {
		
		//1.0 从SET 中删除对象Key的关联
		List<String> key2Del = new ArrayList<String>() ;
		for(${Bean} ${bean} : list${Bean}){
			${bean} = ${getBeanByKeyMethod}(<#list listPkCol as pkPara>${bean}.get${pkPara.name?cap_first}()<#if (pkPara_has_next)>, </#if></#list>) ;
			
			String primaryKey = delFrom${Bean}Set(${bean});
			key2Del.add(primaryKey) ;
		}
		//3.0 删除元素
		delete(key2Del) ;
		//4.0 先从数据库中删除
		${beanDao}.${deleteListDaoMethod}(list${Bean});
	}
	
	public void setSomething(final String key, final Serializable value, final long seconds) {
		set(key, value, seconds) ;
	}
    
    public <T extends Serializable> T getSomething(final String key, final Class<T> clazz) {
    	return get(key, clazz) ;
    }

   /**
    * 暴露直接操作Redis的接口，必须和下面的smembersSomething配对使用，否则会报错
    */
    public boolean sadd4Object(final String key, final Serializable value){
    	return sadd(key, value) ;
    }
    
    /**
    * 暴露直接操作Redis的接口
    */
    public <T extends Serializable> Set<T> smembers4Object(final String key, final Class<T> clazz){
    	return smembers(key, clazz) ;
    }
    
    /**
    * 暴露直接操作Redis的接口,必须和下面的 smembersSomething 配对使用，否则会报错
    */
    public boolean sadd4String(final String key, final String value){
    	return sadd(key, value) ;
    }
    
    /**
    * 暴露直接操作Redis的接口
    */
    @SuppressWarnings("unchecked")
    public List<String> smembers4String(final String key){
    	return (List<String>)smembers(key) ;
    }
    
    <#if listPkCol?size == 1>
    /**
    * 暴露个上层直接操作Redis的接口
    */
    public ${listPkCol[0].type} incrSomething(final String key){
    	return ${listPkCol[0].type}.valueOf(incr(key).toString()) ;
    }
	
    </#if>
}