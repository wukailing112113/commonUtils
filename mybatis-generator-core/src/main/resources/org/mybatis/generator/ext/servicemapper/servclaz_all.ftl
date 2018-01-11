package ${basePackage4Service}.service.impl.${schema};

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ${basePackage}.cache.intf.${schema}.I${Bean}Redis;
import ${basePackage}.entity.${schema}.${Bean};
import ${basePackage4Service}.service.intf.${schema}.I${Bean}Service;
import ${basePackage}.servutil.ServConst;
import ${basePackage}.status.CommStatus;

@Service("${bean}Service")
public class ${Bean}ServiceImpl implements I${Bean}Service{

	private static final Logger logger = Logger.getLogger(${Bean}ServiceImpl.class);
	
	@Autowired
	I${Bean}Redis ${beanRedis};
	
	public void load${Bean}Relate() {
		${beanRedis}.${loadAllBeanRedisMethod}();
	}
	
	<#if listPkCol?size == 1 && listPkCol[0].type != "String">
    public ${listPkCol[0].type} get${Bean}Id(){
    	return ${beanRedis}.get${Bean}Id() ;
    }
    </#if>
    

	public ${Bean} load${Bean}ByKey(<#list listPkCol as pkPara>${pkPara.type} ${pkPara.name}<#if (pkPara_has_next)>, </#if></#list>) {
		return ${beanRedis}.${getBeanByKeyRedisMethod}(<#list listPkCol as pkPara>${pkPara.name}<#if (pkPara_has_next)>, </#if></#list>);
	}
	
	<#list listUniqueIdxCol as idx>
    public ${Bean} get${Bean}By${idx.key}(<#list idx.litsPKBean as pkBean>${pkBean.type} ${pkBean.name}<#if pkBean_has_next >, </#if></#list>){
    	return ${beanRedis}.get${Bean}By${idx.key}(<#list idx.litsPKBean as pkBean>${pkBean.name}<#if pkBean_has_next >, </#if></#list>) ;
    }
    </#list>
    
    <#list listOtherIdxCol as idx>
    public List<${Bean}> get${Bean}By${idx.key}(<#list idx.litsPKBean as pkBean>${pkBean.type} ${pkBean.name}<#if pkBean_has_next >, </#if></#list>){
    	return ${beanRedis}.get${Bean}By${idx.key}(<#list idx.litsPKBean as pkBean>${pkBean.name}<#if pkBean_has_next >, </#if></#list>) ;
    }
    </#list>
    
	<#if listPkCol?size == 1>
	public List<${Bean}> findList${Bean}(List<${listPkCol[0].type}> list${Bean}Id) {
		return ${beanRedis}.get${Bean}(list${Bean}Id) ;
	}
	</#if>
	
	@Transactional(rollbackFor = Exception.class)
	public ${Bean} add${Bean}(${Bean} ${bean}) {
		Date now = new Date() ;
		${bean}.setCreateTime(now) ;
		${bean}.setStatus(CommStatus.INUSE.getIndex()) ;
		${bean}.setStatusTime(now) ;
		return ${beanRedis}.${addBeanRedisMethod}(${bean}) ;
	}

	@Transactional(rollbackFor = Exception.class)
	public int update${Bean}(${Bean} ${bean}) {
		//判断是不是状态修改了
		${Bean} old${Bean} = ${beanRedis}.${getBeanByKeyRedisMethod}(<#list listPkCol as pkPara>${bean}.get${pkPara.name?cap_first}()<#if (pkPara_has_next)>, </#if></#list>) ;
		if(!old${Bean}.getStatus().equals(${bean}.getStatus())){
			Date now = new Date() ;
			${bean}.setStatusTime(now);
		}
		return ${beanRedis}.${updateBeanRedisMethod}(${bean}, true) ;
	}

	public Long findTotalPage(${Bean} ${bean}, boolean useCache){
		Long total${Bean} = ${beanRedis}.${getTotalBeanRedisMethod}(${bean}, useCache) ;
		Integer pageSize = (${bean}.getPageSize() == null || ${bean}.getPageSize() == 0) ? ServConst.LoadPageSize : ${bean}.getPageSize() ;
		Long totalPage = (long)Math.ceil((double)total${Bean} / pageSize);
		return totalPage ;
	}
	
	
	public List<${Bean}> find${Bean}(${Bean} ${bean}, boolean useCache) {
		List<${Bean}> list${Bean} = ${beanRedis}.${getListBeanRedisMethod}(${bean}, useCache) ;
		return list${Bean} ;
	}

	@Transactional(rollbackFor = Exception.class)
	public void delete${Bean}(<#list listPkCol as pkPara>${pkPara.type} ${pkPara.name}<#if (pkPara_has_next)>, </#if></#list>) {
		${beanRedis}.${deleteBeanByKeyRedisMethod}(<#list listPkCol as pkPara>${pkPara.name}<#if (pkPara_has_next)>, </#if></#list>);
	}

}