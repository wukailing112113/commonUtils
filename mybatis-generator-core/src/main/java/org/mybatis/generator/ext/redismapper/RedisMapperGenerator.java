/**
 *    Copyright 2006-2015 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.mybatis.generator.ext.redismapper;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.codegen.AbstractJavaClientGenerator;
import org.mybatis.generator.codegen.AbstractXmlGenerator;
import org.mybatis.generator.codegen.mybatis3.javamapper.elements.AbstractJavaMapperMethodGenerator;
import org.mybatis.generator.config.Context;
import org.mybatis.generator.ext.api.dom.java.FakeCompilationUnit;
import org.mybatis.generator.ext.redismapper.elements.*;
import org.mybatis.generator.internal.util.JavaBeansUtil;
import org.mybatis.generator.util.FreeMarkerUtil;
import org.mybatis.generator.util.GenUtil;
import org.mybatis.generator.util.MergeUtil;
import org.mybatis.generator.util.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 */
public class RedisMapperGenerator extends AbstractJavaClientGenerator {

	String basePackage;

	/**
	 *
	 */
	private boolean useAnalyze;

	public RedisMapperGenerator() {
		super(false);
	}

	public RedisMapperGenerator(Context context, IntrospectedTable introspectedTable, String basePackage,
			boolean useAnalyze) {
		super(false);
		this.context = context;
		this.introspectedTable = introspectedTable;
		this.basePackage = basePackage;
		this.useAnalyze = useAnalyze;
	}

	public void setIntrospectedTable(IntrospectedTable introspectedTable) {
		super.setIntrospectedTable(introspectedTable);

		String targetPackage = context.getJavaClientGeneratorConfiguration().getTargetPackage();
		basePackage = targetPackage.substring(0, targetPackage.indexOf(".cache."));
	}

	@Override
	public AbstractXmlGenerator getMatchedXMLGenerator() {
		return null;
	}

	@Override
	public List<CompilationUnit> getCompilationUnits() {
		List<CompilationUnit> answer = new ArrayList<CompilationUnit>();

		FullyQualifiedJavaType intfType = new FullyQualifiedJavaType(
				GenUtil.getRedisInterfaceFullName(introspectedTable));
		FullyQualifiedJavaType implType = new FullyQualifiedJavaType(GenUtil.getRedisClassFullName(introspectedTable));

		Map<String, String> mapResult = new HashMap<String, String>();
		Interface interfaze = new Interface(intfType);
		TopLevelClass topLevelClass = new TopLevelClass(implType);

		if (useAnalyze) {
			addAllMethod(interfaze, topLevelClass, mapResult);
			answer.add(topLevelClass);
			answer.add(interfaze);
		} else {
			addAllMethod(null, null, mapResult);
			answer.add(new FakeCompilationUnit(mapResult.get("intf"), intfType));
			answer.add(new FakeCompilationUnit(mapResult.get("impl"), implType));
		}

		return answer;
	}

	

	protected void initializeAndExecuteGenerator(AbstractJavaMapperMethodGenerator methodGenerator) {
		methodGenerator.setContext(context);
		methodGenerator.setIntrospectedTable(introspectedTable);
		methodGenerator.setProgressCallback(progressCallback);
		methodGenerator.setWarnings(warnings);
	}

	public static final class PKBean {
		public String name;
		public String type;
		public String colum ;

		public String getColum() {
			return colum;
		}

		public void setColum(String colum) {
			this.colum = colum;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}
	}
	
	public static final class IDXBean {
		public String key;
		public PKBean[] LitsPKBean;
		public String getKey() {
			return key;
		}
		public void setKey(String key) {
			this.key = key;
		}
		public PKBean[] getLitsPKBean() {
			return LitsPKBean;
		}
		public void setLitsPKBean(PKBean[] litsPKBean) {
			LitsPKBean = litsPKBean;
		}
		

	}

	protected void addAllMethod(Interface interfaze, TopLevelClass topLevelClass, Map<String, String> mapResult) {
		try {
			Map<String, Object> templateParaMap = new HashMap<String, Object>();
			templateParaMap.put("Bean", GenUtil.getEntityName(introspectedTable));
			templateParaMap.put("bean", GenUtil.getGeneralEntityParamName(introspectedTable));
			templateParaMap.put("upperBean", GenUtil.getGeneralEntityParamName(introspectedTable).toUpperCase());
			templateParaMap.put("beanDao", GenUtil.getGeneralEntityParamName(introspectedTable) + "Dao");
			List<PKBean> listPkBean = new ArrayList<PKBean>();
			for (IntrospectedColumn introspectedColumn : introspectedTable.getPrimaryKeyColumns()) {
				PKBean mapPKprop = new PKBean();
				mapPKprop.setName(introspectedColumn.getJavaProperty());
				mapPKprop.setType(introspectedColumn.getFullyQualifiedJavaType().getShortName());
				listPkBean.add(mapPKprop);
			}
			templateParaMap.put("listPkCol", listPkBean.toArray(new PKBean[0]));
			
			// 唯一索引
			List<IDXBean> listIDXBean = new ArrayList<IDXBean>();
			Map<String,List<IntrospectedColumn>> mapIndex = introspectedTable.getUniqueIndexes() ;
			for(String key : mapIndex.keySet()){
				IDXBean idxBean = new IDXBean() ;
				idxBean.setKey(key) ;
				List<PKBean> listPk = new ArrayList<PKBean>() ;
				for(IntrospectedColumn introspectedColumn : mapIndex.get(key)){
					PKBean pk = new PKBean() ;
					pk.setName(introspectedColumn.getJavaProperty());
					pk.setType(introspectedColumn.getFullyQualifiedJavaType().getShortName());
					listPk.add(pk) ;
				}
				idxBean.setLitsPKBean(listPk.toArray(new PKBean[0])) ;
				listIDXBean.add(idxBean) ;
			}
			templateParaMap.put("listUniqueIdxCol", listIDXBean.toArray(new IDXBean[0]));
			
			// 其他索引
			listIDXBean = new ArrayList<IDXBean>();
			mapIndex = introspectedTable.getOtherIndexes() ;
			for(String key : mapIndex.keySet()){
				IDXBean idxBean = new IDXBean() ;
				idxBean.setKey(key) ;
				List<PKBean> listPk = new ArrayList<PKBean>() ;
				for(IntrospectedColumn introspectedColumn : mapIndex.get(key)){
					PKBean pk = new PKBean() ;
					pk.setName(introspectedColumn.getJavaProperty());
					pk.setType(introspectedColumn.getFullyQualifiedJavaType().getShortName());
					listPk.add(pk) ;
				}
				idxBean.setLitsPKBean(listPk.toArray(new PKBean[0])) ;
				listIDXBean.add(idxBean) ;
			}
			templateParaMap.put("listOtherIdxCol", listIDXBean.toArray(new IDXBean[0]));
			
			
			
			// 这里可能有bug， split之后的schema是xx.xx的时候
			String[] pkgConf = context.getJavaClientGeneratorConfiguration().getTargetPackage().split(".dao.");
			templateParaMap.put("basePackage", pkgConf[0]);
			templateParaMap.put("schema", pkgConf[1]);
			
			String maxIdCond = GenUtil.getConfig4MaxIdRelateColum(introspectedTable);
			if(maxIdCond != null && maxIdCond != ""){
				
//				String paraType = GenUtil.getEntityType(context, introspectedTable).getShortName() ;
//				paraType = paraType + " " + GenUtil.getGeneralEntityParamName(introspectedTable) ;
//				templateParaMap.put("maxIdCond", paraType);
				IntrospectedColumn maxIdBaseColumn = introspectedTable.getColumn(maxIdCond) ;
				PKBean pk = new PKBean() ;
				pk.setName(maxIdBaseColumn.getJavaProperty());
				pk.setType(maxIdBaseColumn.getFullyQualifiedJavaType().getShortName());
				templateParaMap.put("maxIdCond", pk);
				
				templateParaMap.put("maxIdRelateMethod", "get"+JavaBeansUtil.getCamelCaseString(maxIdCond,true));  // 最大ID的后缀字段如：SEQ:PROD:
				templateParaMap.put("maxIdPrefix", GenUtil.getConfig4MaxIdPrefix(introspectedTable));  
				templateParaMap.put("maxIdPostfix", GenUtil.getConfig4MaxIdPostfix(introspectedTable)); 
			}
			
			// 单表字段之间是否有父子关系
			String parentId = GenUtil.getConfig4ParentColum(introspectedTable);
			if(parentId != null && parentId != ""){
				IntrospectedColumn maxIdBaseColumn = introspectedTable.getColumn(parentId) ;
				templateParaMap.put("pColumRelate", true);
				PKBean pk = new PKBean() ;
				pk.setType(maxIdBaseColumn.getFullyQualifiedJavaType().getShortName());
				pk.setName(JavaBeansUtil.getCamelCaseString(parentId,false));
				templateParaMap.put("relateColumn", pk);
				templateParaMap.put("getParentIdMethod", "get"+JavaBeansUtil.getCamelCaseString(parentId,true));  // 最大ID的后缀字段如：SEQ:PROD:
				
			}
			
			templateParaMap.put("getBeanIdMethod",GenUtil.getGetBeanIdMethodName(introspectedTable, GenUtil.ENUM_METHOD_TYPE.REDIS_TYPE));
			templateParaMap.put("initMaxBeanIdMethod",GenUtil.getInitBeanMaxIdMethodName(introspectedTable, GenUtil.ENUM_METHOD_TYPE.REDIS_TYPE));
			templateParaMap.put("getBeanByKeyMethod",GenUtil.getSelectByKeyMethodName(introspectedTable, GenUtil.ENUM_METHOD_TYPE.REDIS_TYPE));

			templateParaMap.put("clearAllBeanMethod",GenUtil.getClearAllBeanMethodName(introspectedTable, GenUtil.ENUM_METHOD_TYPE.REDIS_TYPE));

			templateParaMap.put("loadAllBeanMethod",GenUtil.getLoaAllBeanMethodName(introspectedTable, GenUtil.ENUM_METHOD_TYPE.REDIS_TYPE));
			templateParaMap.put("getBeanTotalMethod",GenUtil.getSelectCountMethodName(introspectedTable, GenUtil.ENUM_METHOD_TYPE.REDIS_TYPE));
			templateParaMap.put("getBeanPKIdKeyMethod", GenUtil.getGetBeanIdKeyMethodName(introspectedTable));

			templateParaMap.put("addBeanMethod",GenUtil.getAddBeanMethodName(introspectedTable, GenUtil.ENUM_METHOD_TYPE.REDIS_TYPE));
			templateParaMap.put("addListBeanMethod",GenUtil.getAddListMethodName(introspectedTable, GenUtil.ENUM_METHOD_TYPE.REDIS_TYPE));

			templateParaMap.put("updateBeanMethod",GenUtil.getUpdateByBeanMethodName(introspectedTable, GenUtil.ENUM_METHOD_TYPE.REDIS_TYPE));
			templateParaMap.put("updateListBeanMethod",GenUtil.getUpdateListMethodName(introspectedTable, GenUtil.ENUM_METHOD_TYPE.REDIS_TYPE));

			templateParaMap.put("deleteBeanByKeyMethod",GenUtil.getDeleteByKeyMethodName(introspectedTable, GenUtil.ENUM_METHOD_TYPE.REDIS_TYPE));
			templateParaMap.put("deleteByListMethod",GenUtil.getDeleteByListMethodName(introspectedTable, GenUtil.ENUM_METHOD_TYPE.REDIS_TYPE));

			templateParaMap.put("getListBeanMethod", "get" + GenUtil.getEntityName(introspectedTable));
			templateParaMap.put("getBeanByPageMethod", "get" + GenUtil.getEntityName(introspectedTable) + "ByPage");

			// Impl文件中需要调用DAO的方法
			templateParaMap.put("findMaxBeanIdDaoMethod",GenUtil.getMaxIdMethodName(introspectedTable, GenUtil.ENUM_METHOD_TYPE.DAO_TYPE));
			templateParaMap.put("findBeansDaoMethod",GenUtil.getSelectByBeanMethodName(introspectedTable, GenUtil.ENUM_METHOD_TYPE.DAO_TYPE));
			templateParaMap.put("findBeansDaoByPageMethod",GenUtil.getSelectByBeanPageMethodName(introspectedTable, GenUtil.ENUM_METHOD_TYPE.DAO_TYPE));
			templateParaMap.put("getBeanTotalCountDaoMethod",GenUtil.getSelectCountMethodName(introspectedTable, GenUtil.ENUM_METHOD_TYPE.DAO_TYPE));
			templateParaMap.put("findBeanByIdDaoMethod",GenUtil.getSelectByKeyMethodName(introspectedTable, GenUtil.ENUM_METHOD_TYPE.DAO_TYPE));
			templateParaMap.put("addBeanDaoMethod",GenUtil.getAddBeanMethodName(introspectedTable, GenUtil.ENUM_METHOD_TYPE.DAO_TYPE));
			templateParaMap.put("addListDaoMethod",GenUtil.getInsertListMethodName(introspectedTable, GenUtil.ENUM_METHOD_TYPE.DAO_TYPE));
			templateParaMap.put("updateBeanDaoMethod",GenUtil.getUpdateByBeanMethodName(introspectedTable, GenUtil.ENUM_METHOD_TYPE.DAO_TYPE));
			templateParaMap.put("deleteBeanDaoMethod",GenUtil.getDeleteByKeyMethodName(introspectedTable, GenUtil.ENUM_METHOD_TYPE.DAO_TYPE));
			templateParaMap.put("deleteListDaoMethod",GenUtil.getDeleteByListMethodName(introspectedTable, GenUtil.ENUM_METHOD_TYPE.DAO_TYPE));

			boolean useRelateResource = true;
			String dir = null;
			if (context.getProperty("redisFtlPath") != null) {
				useRelateResource = false;
				dir = GenUtil.getRedisFtlPath(context, GenUtil.ENUM_METHOD_TYPE.REDIS_TYPE);
			} else {
				dir = "/org/mybatis/generator/ext/redismapper";
			}

			String clzImplBody = FreeMarkerUtil.getStringFromTemplate(dir, "redisclaz_all.ftl", templateParaMap, useRelateResource);
			String intfBody = FreeMarkerUtil.getStringFromTemplate(dir, "redisintf_all.ftl", templateParaMap, useRelateResource);

			mapResult.put("intf", intfBody);
			mapResult.put("impl", clzImplBody);

			if (interfaze != null)
				MergeUtil.genInterfaceAndClaz(intfBody, interfaze);
			if (topLevelClass != null)
				MergeUtil.genInterfaceAndClaz(clzImplBody, topLevelClass);

		} catch (Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException("generate redis class error !");
		}
	}

	@Deprecated
	protected void addClassFields(TopLevelClass topLevelClass) {
		topLevelClass.addImportedType("org.apache.log4j.Logger");

		Field field = new Field("PRE_KEY", FullyQualifiedJavaType.getStringInstance());
		field.setVisibility(JavaVisibility.PRIVATE);
		field.setStatic(true);
		field.setInitializationString("" + GenUtil.getEntityName(introspectedTable) + ".class.getSimpleName()");
		topLevelClass.addField(field);

		field = new Field("SEQ_PRE_KEY", FullyQualifiedJavaType.getStringInstance());
		field.setVisibility(JavaVisibility.PRIVATE);
		field.setStatic(true);
		field.setInitializationString("new StringBuffer(\"SEQ:\").append(PRE_KEY).toString()");
		topLevelClass.addField(field);

		field = new Field("SET_KEY", FullyQualifiedJavaType.getStringInstance());
		field.setVisibility(JavaVisibility.PRIVATE);
		field.setStatic(true);
		field.setInitializationString("new StringBuffer(\"SET:\").append(PRE_KEY).toString()");
		topLevelClass.addField(field);

		field = new Field("START_ATTRD", PrimitiveTypeWrapper.getIntegerInstance());
		field.setVisibility(JavaVisibility.PRIVATE);
		field.setStatic(true);
		topLevelClass.addField(field);

		field = new Field("logger", new FullyQualifiedJavaType("Logger"));
		field.setVisibility(JavaVisibility.PRIVATE);
		field.setStatic(true);
		field.setFinal(true);
		field.setInitializationString("Logger.getLogger(" + topLevelClass.getType().getShortName() + ".class)");
		topLevelClass.addField(field);

		FullyQualifiedJavaType daoType = new FullyQualifiedJavaType(GenUtil.getDaoInterfaceFullName(introspectedTable));
		field = new Field(StringUtil.uncapitalize(daoType.getShortName().replace("I", "")), daoType);
		field.addAnnotation("@Autowired");
		field.setVisibility(JavaVisibility.PRIVATE);
		field.setStatic(true);
		topLevelClass.addField(field);
	}

	/**
	 * 根据主键id获取bean
	 */
	@Deprecated
	protected void addGetBeanId(Interface interfaze, TopLevelClass classImpl) {
		AbstractRedisMethodGenerator methodGenerator = new GetBeanIdMethodGenerator();
		initializeAndExecuteGenerator(methodGenerator);
		methodGenerator.addInterfaceAndClassImpl(interfaze, classImpl);
	}

	/**
	 * PostContruct调用的初始化最大化
	 */
	protected void addInitMaxBeanId(Interface interfaze, TopLevelClass classImpl) {
		AbstractRedisMethodGenerator methodGenerator = new InitMaxBeanIdMethodGenerator();
		initializeAndExecuteGenerator(methodGenerator);
		methodGenerator.addInterfaceAndClassImpl(interfaze, classImpl);
	}

	/**
	 * PostContruct调用的初始化最大化
	 */
	@Deprecated
	protected void addClearAllBeans(Interface interfaze, TopLevelClass classImpl) {
		AbstractRedisMethodGenerator methodGenerator = new ClearAllBeansMethodGenerator();
		initializeAndExecuteGenerator(methodGenerator);
		methodGenerator.addInterfaceAndClassImpl(interfaze, classImpl);
	}

	/**
	 * 加载所有缓存的方法
	 */
	@Deprecated
	protected void addLoadAllBean(Interface interfaze, TopLevelClass classImpl) {
		AbstractRedisMethodGenerator methodGenerator = new LoadAllBeanMethodGenerator();
		initializeAndExecuteGenerator(methodGenerator);
		methodGenerator.addInterfaceAndClassImpl(interfaze, classImpl);
	}

	/**
	 * 加载所有缓存的方法
	 */
	@Deprecated
	protected void addGetTotalBean(Interface interfaze, TopLevelClass classImpl) {
		AbstractRedisMethodGenerator methodGenerator = new GetTotalBeanMethodGenerator();
		initializeAndExecuteGenerator(methodGenerator);
		methodGenerator.addInterfaceAndClassImpl(interfaze, classImpl);
	}

	/**
	 */
	@Deprecated
	protected void addGetBeanByIdKey(Interface interfaze, TopLevelClass classImpl) {
		AbstractRedisMethodGenerator methodGenerator = new GetBeanIdMethodGenerator();
		initializeAndExecuteGenerator(methodGenerator);
		methodGenerator.addInterfaceAndClassImpl(interfaze, classImpl);
	}

	/**
	 */
	@Deprecated
	protected void addGetBeanIdKey(Interface interfaze, TopLevelClass classImpl) {
	}

	/**
	 */
	@Deprecated
	protected void addGetBeanById(Interface interfaze, TopLevelClass classImpl) {
		AbstractRedisMethodGenerator methodGenerator = new GetBeanByKeyMethodGenerator();
		initializeAndExecuteGenerator(methodGenerator);
		methodGenerator.addInterfaceAndClassImpl(interfaze, classImpl);
	}

	/**
	 */
	@Deprecated
	protected void addAddBean(Interface interfaze, TopLevelClass classImpl) {
		AbstractRedisMethodGenerator methodGenerator = new AddBeanMethodGenerator();
		initializeAndExecuteGenerator(methodGenerator);
		methodGenerator.addInterfaceAndClassImpl(interfaze, classImpl);
	}

	/**
	 */
	@Deprecated
	protected void addUpdateBean(Interface interfaze, TopLevelClass classImpl) {
		AbstractRedisMethodGenerator methodGenerator = new UpdateBeanMethodGenerator();
		initializeAndExecuteGenerator(methodGenerator);
		methodGenerator.addInterfaceAndClassImpl(interfaze, classImpl);
	}

	/**
	 */
	@Deprecated
	protected void addUpdateListBean(Interface interfaze, TopLevelClass classImpl) {
		AbstractRedisMethodGenerator methodGenerator = new UpdateListBeanMethodGenerator();
		initializeAndExecuteGenerator(methodGenerator);
		methodGenerator.addInterfaceAndClassImpl(interfaze, classImpl);
	}

	/**
	 */
	@Deprecated
	protected void addDeleteBean(Interface interfaze, TopLevelClass classImpl) {
		AbstractRedisMethodGenerator methodGenerator = new DeleteBeanMethodGenerator();
		initializeAndExecuteGenerator(methodGenerator);
		methodGenerator.addInterfaceAndClassImpl(interfaze, classImpl);
	}

	/**
	 */
	@Deprecated
	protected void addGetBeanList(Interface interfaze, TopLevelClass classImpl) {
		AbstractRedisMethodGenerator methodGenerator = new GetBeanListMethodGenerator();
		initializeAndExecuteGenerator(methodGenerator);
		methodGenerator.addInterfaceAndClassImpl(interfaze, classImpl);
	}

	/**
	 */
	@Deprecated
	protected void addGetBeanByPage(Interface interfaze, TopLevelClass classImpl) {
		AbstractRedisMethodGenerator methodGenerator = new GetBeanByPageMethodGenerator();
		initializeAndExecuteGenerator(methodGenerator);
		methodGenerator.addInterfaceAndClassImpl(interfaze, classImpl);
	}
}
