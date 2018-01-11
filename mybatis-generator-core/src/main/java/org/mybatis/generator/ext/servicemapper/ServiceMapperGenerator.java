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
package org.mybatis.generator.ext.servicemapper;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.codegen.AbstractJavaClientGenerator;
import org.mybatis.generator.codegen.AbstractXmlGenerator;
import org.mybatis.generator.codegen.mybatis3.javamapper.elements.AbstractJavaMapperMethodGenerator;
import org.mybatis.generator.config.Context;
import org.mybatis.generator.ext.api.dom.java.FakeCompilationUnit;
import org.mybatis.generator.ext.redismapper.RedisMapperGenerator.IDXBean;
import org.mybatis.generator.ext.redismapper.RedisMapperGenerator.PKBean;
import org.mybatis.generator.ext.redismapper.elements.*;
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
public class ServiceMapperGenerator extends AbstractJavaClientGenerator {

	private boolean useAnalyze;
	private String basePackage;
	public ServiceMapperGenerator(Context context, IntrospectedTable introspectedTable, String basePackage,
								  boolean useAnalyze) {
		super(false);
		this.context = context;
		this.basePackage = basePackage;
		this.introspectedTable = introspectedTable;
		this.useAnalyze = useAnalyze;
	}

	public void setIntrospectedTable(IntrospectedTable introspectedTable) {
		super.setIntrospectedTable(introspectedTable);
	}

	@Override
	public AbstractXmlGenerator getMatchedXMLGenerator() {
		return null;
	}

	@Override
	public List<CompilationUnit> getCompilationUnits() {
		List<CompilationUnit> answer = new ArrayList<CompilationUnit>();

		FullyQualifiedJavaType intfType = new FullyQualifiedJavaType(GenUtil.getServiceInterfaceFullName(introspectedTable, basePackage));
		FullyQualifiedJavaType implType = new FullyQualifiedJavaType(GenUtil.getServiceClassFullName(introspectedTable, basePackage));

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

	public static final class PKBean {
		public String name;
		public String type;

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
			templateParaMap.put("beanRedis", GenUtil.getGeneralEntityParamName(introspectedTable) + "Redis");
			List<PKBean> listPkMap = new ArrayList<PKBean>();
			for (IntrospectedColumn introspectedColumn : introspectedTable.getPrimaryKeyColumns()) {
				PKBean mapPKprop = new PKBean();
				mapPKprop.setName(introspectedColumn.getJavaProperty());
				mapPKprop.setType(introspectedColumn.getFullyQualifiedJavaType().getShortName());
				listPkMap.add(mapPKprop);
			}
			templateParaMap.put("listPkCol", listPkMap.toArray(new PKBean[0]));

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
			
			// 非唯一索引
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
			if (StringUtil.isEmptyString(basePackage)) {
				basePackage = pkgConf[0];
			}
			templateParaMap.put("basePackage4Service", basePackage);
			templateParaMap.put("schema", pkgConf[1]);

			templateParaMap.put("loadAllBeanRedisMethod",GenUtil.getLoaAllBeanMethodName(introspectedTable, GenUtil.ENUM_METHOD_TYPE.REDIS_TYPE));
			templateParaMap.put("getBeanByKeyRedisMethod",GenUtil.getSelectByKeyMethodName(introspectedTable, GenUtil.ENUM_METHOD_TYPE.REDIS_TYPE));
			templateParaMap.put("addBeanRedisMethod",GenUtil.getAddBeanMethodName(introspectedTable, GenUtil.ENUM_METHOD_TYPE.REDIS_TYPE));
			templateParaMap.put("updateBeanRedisMethod",GenUtil.getUpdateByBeanMethodName(introspectedTable, GenUtil.ENUM_METHOD_TYPE.REDIS_TYPE));
			templateParaMap.put("getTotalBeanRedisMethod",GenUtil.getSelectCountMethodName(introspectedTable, GenUtil.ENUM_METHOD_TYPE.REDIS_TYPE));
			templateParaMap.put("getListBeanRedisMethod", "get" + GenUtil.getEntityName(introspectedTable) + "ByPage");
			templateParaMap.put("deleteBeanByKeyRedisMethod",GenUtil.getDeleteByKeyMethodName(introspectedTable, GenUtil.ENUM_METHOD_TYPE.REDIS_TYPE));

			boolean useRelateResource = true;
			String dir = null;
			if (context.getProperty("svrFtlPath") != null) {
				useRelateResource = false;
				dir = GenUtil.getServiceFtlPath(context, GenUtil.ENUM_METHOD_TYPE.REDIS_TYPE);
			} else {
				dir = "/org/mybatis/generator/ext/servicemapper";
			}

			String clzImplBody = FreeMarkerUtil.getStringFromTemplate(dir, "servclaz_all.ftl", templateParaMap, useRelateResource);
			String intfBody = FreeMarkerUtil.getStringFromTemplate(dir, "servintf_all.ftl", templateParaMap, useRelateResource);

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

}
