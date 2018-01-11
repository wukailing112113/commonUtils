package ${basePackage}.cache.intf.${schema};

import java.util.List;
import java.util.Date;
import java.util.Set;

import ${basePackage}.entity.${schema}.${Bean};
import java.io.Serializable;

public interface I${Bean}Redis {

    /**
    * 加载所有的数据到内存
    */
    public void ${loadAllBeanMethod}();

   /**
    * 加载所有的数据到内存
    */
    public void ${loadAllBeanMethod}(long expire);

    <#if listPkCol?size == 1 && listPkCol[0].type != "String">
    /**
    * @return
    */
    public ${listPkCol[0].type} ${getBeanIdMethod}(<#if maxIdCond?? >${maxIdCond.type} ${maxIdCond.name}</#if>);
    </#if>

    /**
    * @param params
    * @return
    */
    public Long ${getBeanTotalMethod}(${Bean} ${bean});

    /**
     * 获取记录数，useCache为true的时候存缓存。 过期时间为30秒
     */
    public Long ${getBeanTotalMethod}(${Bean} ${bean}, boolean useCache);
    
    /**
    *
    * @param id
    * @return
    */
    public ${Bean} ${getBeanByKeyMethod}(<#list listPkCol as pkPara>${pkPara.type} ${pkPara.name}<#if pkPara_has_next >, </#if></#list>);

    /**
    *
    * @param id
    * @return
    */
    public ${Bean} ${getBeanByKeyMethod}(<#list listPkCol as pkPara>${pkPara.type} ${pkPara.name}<#if pkPara_has_next >, </#if></#list>, long expire);

	/**
    *
    * @param id
    * @return
    */
    public ${Bean} ${getBeanByKeyMethod}ForUpdate(<#list listPkCol as pkPara>${pkPara.type} ${pkPara.name}<#if pkPara_has_next >, </#if></#list>);


    <#if listPkCol?size == 1>
    /**
    * @param list${Bean}Id
    * @return
    */
    public List<${Bean}> ${getListBeanMethod}(List<${listPkCol[0].type}> list${Bean}Id);
    </#if>

	
    <#list listUniqueIdxCol as idx>
    /**
    * @param 
    * @return
    */
    public ${Bean} get${Bean}By${idx.key}(<#list idx.litsPKBean as pkBean>${pkBean.type} ${pkBean.name}<#if pkBean_has_next >, </#if></#list>);
    
   /**
    * @param 
    * @return
    */
    public ${Bean} get${Bean}By${idx.key}(<#list idx.litsPKBean as pkBean>${pkBean.type} ${pkBean.name}<#if pkBean_has_next >, </#if></#list>, long expire) ;

    </#list>
    
    <#list listOtherIdxCol as idx>
    /**
    * @param 
    * @return
    */
    public List<${Bean}> get${Bean}By${idx.key}(<#list idx.litsPKBean as pkBean>${pkBean.type} ${pkBean.name}<#if pkBean_has_next >, </#if></#list>);
    
   /**
    * @param 
    * @return
    */
    public List<${Bean}> get${Bean}By${idx.key}(<#list idx.litsPKBean as pkBean>${pkBean.type} ${pkBean.name}<#if pkBean_has_next >, </#if></#list>, long expire) ;

    </#list>
   
    /**
    * @param prod
    * @return
    */
    public List<${Bean}> ${getBeanByPageMethod}(${Bean} ${bean}, Boolean bFromCache, long expire);

    /**
    * @param prod
    * @return
    */
    public List<${Bean}> ${getBeanByPageMethod}(${Bean} ${bean}, Boolean bFromCache);

	<#if pColumRelate?? >
	/**
    * @param prod
    * @return
    */
	public List<${Bean}> getSub${Bean}sBy${relateColumn.name}(${relateColumn.type} ${relateColumn.name});
	</#if>
	
    /**
    *
    * @param prod
    * @return
    */
    public ${Bean} ${addBeanMethod}(${Bean} ${bean});

    /**
    *
    * @param prod
    * @return
    */
    public ${Bean} ${addBeanMethod}(${Bean} ${bean}, long expire);

	/**
    *
    * @param prod
    * @return
    */
    public List<${Bean}> ${addListBeanMethod}(List<${Bean}> list${Bean});

    /**
    *
    * @param prod
    * @return
    */
    public List<${Bean}> ${addListBeanMethod}(List<${Bean}> list${Bean}, long expire);

    /**
    *
    * @param prod
    * @return
    */
    public int ${updateBeanMethod}(${Bean} ${bean}, boolean syncdb);

    /**
    *
    * @param prod
    * @return
    */
    public int ${updateBeanMethod}(${Bean} ${bean}, boolean syncdb, long expire);

    /**
    *
    * @param prods
    * @return
    */
    public int ${updateListBeanMethod}(List<${Bean}> list${Bean}, boolean syncdb);

    /**
    *
    * @param prods
    * @return
    */
    public int ${updateListBeanMethod}(List<${Bean}> list${Bean}, boolean syncdb, long expire);

	/**
    * @param prodId
    */
    public void ${deleteBeanByKeyMethod}(<#list listPkCol as pkPara>${pkPara.type} ${pkPara.name}<#if pkPara_has_next>, </#if></#list>) ;
    
    /**
    * @param prodId
    */
    public void ${deleteByListMethod}(List<${Bean}> list${Bean}) ;
    
    /**
    * 暴露个上层直接操作Redis的接口
    */
    public void setSomething(final String key, final Serializable value, final long seconds) ;
    
    
    /**
    * 暴露直接操作Redis的接口
    */
    public <T extends Serializable> T getSomething(final String key, final Class<T> clazz) ;
    
    /**
    * 暴露直接操作Redis的接口
    */
    public boolean sadd4Object(final String key, final Serializable value);
    
    /**
    * 暴露直接操作Redis的接口
    */
    public <T extends Serializable> Set<T> smembers4Object(final String key, final Class<T> clazz);
    
    /**
    * 暴露直接操作Redis的接口
    */
    public boolean sadd4String(final String key, final String value);
    
    /**
    * 暴露直接操作Redis的接口
    */
    public List<String> smembers4String(final String key);
    
    <#if listPkCol?size == 1>
    /**
    * 暴露个上层直接操作Redis的接口
    */
    public ${listPkCol[0].type} incrSomething(final String key);

    </#if>
}
