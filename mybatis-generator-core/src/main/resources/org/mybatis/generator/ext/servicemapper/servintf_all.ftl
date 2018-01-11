package ${basePackage4Service}.service.intf.${schema};

import java.util.List;
import java.util.Date;

import ${basePackage}.entity.${schema}.${Bean};


public interface I${Bean}Service {

	/**
	 * 加载相关的到内存
	 */
    public void load${Bean}Relate();
	
	<#if listPkCol?size == 1 && listPkCol[0].type != "String">
	/**
	 * 获取最大${Bean}ID
	 */
    public ${listPkCol[0].type} get${Bean}Id();
    </#if>
    
	/**
	 * 
	 * @param <#list listPkCol as pkPara>${pkPara.type} ${pkPara.name}<#if (pkPara_has_next)>, </#if></#list>
	 * @return
	 */
    public ${Bean} load${Bean}ByKey(<#list listPkCol as pkPara>${pkPara.type} ${pkPara.name}<#if (pkPara_has_next)>, </#if></#list>);
	
	<#list listUniqueIdxCol as idx>
    /**
    * @param 
    * @return
    */
    public ${Bean} get${Bean}By${idx.key}(<#list idx.litsPKBean as pkBean>${pkBean.type} ${pkBean.name}<#if pkBean_has_next >, </#if></#list>);
    
    </#list>
    
    <#list listOtherIdxCol as idx>
    /**
    * @param 
    * @return
    */
    public List<${Bean}> get${Bean}By${idx.key}(<#list idx.litsPKBean as pkBean>${pkBean.type} ${pkBean.name}<#if pkBean_has_next >, </#if></#list>);
    
    </#list>
    
	/**
	 * 
	 * @param ${bean}
	 */
    public ${Bean} add${Bean}(${Bean} ${bean});
	
	/**
	 * 
	 * @param ${bean}
	 */
    public int update${Bean}(${Bean} ${bean});
	
	/**
	 * 根据条件查找
	 * @param brand
	 * @return
	 */
    public List<${Bean}> find${Bean}(${Bean} ${bean}, boolean useCache);
	
	
	<#if listPkCol?size == 1>
    /**
    * @param list${Bean}Id
    * @return
    */
    public List<${Bean}> findList${Bean}(List<${listPkCol[0].type}> list${Bean}Id);
    </#if>
    
	/**
	 * 查找在当前条件下的页数
	 * @param ${bean}
	 * @return
	 */
    public Long findTotalPage(${Bean} ${bean}, boolean useCache);

	/**
	 * 删除，一般不提供这样的方法
	 * @param <#list listPkCol as pkPara>${pkPara.type} ${pkPara.name}<#if (pkPara_has_next)>, </#if></#list>
	 */
	public void delete${Bean}(<#list listPkCol as pkPara>${pkPara.type} ${pkPara.name}<#if (pkPara_has_next)>, </#if></#list>);
	
}
