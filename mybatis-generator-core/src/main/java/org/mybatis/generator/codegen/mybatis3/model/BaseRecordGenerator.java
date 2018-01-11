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
package org.mybatis.generator.codegen.mybatis3.model;

import org.mybatis.generator.api.CommentGenerator;
import org.mybatis.generator.api.FullyQualifiedTable;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.Plugin;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.codegen.AbstractJavaGenerator;
import org.mybatis.generator.codegen.RootClassInfo;
import org.mybatis.generator.util.GenUtil;

import java.util.*;

import static org.mybatis.generator.internal.util.JavaBeansUtil.*;
import static org.mybatis.generator.internal.util.messages.Messages.getString;

/**
 * 
 * @author Jeff Butler
 * 
 */
public class BaseRecordGenerator extends AbstractJavaGenerator {

    public BaseRecordGenerator() {
        super();
    }

    @Override
    public List<CompilationUnit> getCompilationUnits() {
        FullyQualifiedTable table = introspectedTable.getFullyQualifiedTable();
        progressCallback.startTask(getString(
                "Progress.8", table.toString())); //$NON-NLS-1$
        Plugin plugins = context.getPlugins();
        CommentGenerator commentGenerator = context.getCommentGenerator();

        FullyQualifiedJavaType type = new FullyQualifiedJavaType(
                introspectedTable.getBaseRecordType());
        TopLevelClass topLevelClass = new TopLevelClass(type);
        topLevelClass.setVisibility(JavaVisibility.PUBLIC);
        commentGenerator.addJavaFileComment(topLevelClass);

        FullyQualifiedJavaType superClass = getSuperClass();
        if (superClass != null) {
            topLevelClass.setSuperClass(superClass);
            topLevelClass.addImportedType(superClass);
        }
        commentGenerator.addModelClassComment(topLevelClass, introspectedTable);

        List<IntrospectedColumn> introspectedColumns = getColumnsInThisClass();

        if (introspectedTable.isConstructorBased()) {
            addParameterizedConstructor(topLevelClass);
            
            if (!introspectedTable.isImmutable()) {
                addDefaultConstructor(topLevelClass);
            }
        }

        String rootClass = getRootClass();
        Map<String, IntrospectedColumn> mapIntrospectedColumn = new HashMap<String, IntrospectedColumn>();
        for (IntrospectedColumn introspectedColumn : introspectedColumns) {
            if (RootClassInfo.getInstance(rootClass, warnings).containsProperty(introspectedColumn)) {
                continue;
            }
            //
            mapIntrospectedColumn.put(introspectedColumn.getActualColumnName().toLowerCase(), introspectedColumn);

            Field field = getJavaBeansField(introspectedColumn, context, introspectedTable);
            if (plugins.modelFieldGenerated(field, topLevelClass,introspectedColumn, introspectedTable,
            		Plugin.ModelClassType.BASE_RECORD)) {
                topLevelClass.addField(field);
                topLevelClass.addImportedType(field.getType());
            }
            Method method = getJavaBeansGetter(introspectedColumn, context, introspectedTable);
            if (plugins.modelGetterMethodGenerated(method, topLevelClass,
                    introspectedColumn, introspectedTable,
                    Plugin.ModelClassType.BASE_RECORD)) {
                topLevelClass.addMethod(method);
            }
            if (!introspectedTable.isImmutable()) {
                method = getJavaBeansSetter(introspectedColumn, context, introspectedTable);
                if (plugins.modelSetterMethodGenerated(method, topLevelClass,
                        introspectedColumn, introspectedTable,
                        Plugin.ModelClassType.BASE_RECORD)) {
                    topLevelClass.addMethod(method);
                }
            }
            
            if(introspectedColumn.isStringColumn()){
            	field = getJavaBeansBooleanField(introspectedColumn, context, introspectedTable,"Equal");
            	topLevelClass.addField(field);
                topLevelClass.addImportedType(field.getType());
            	
                method = getJavaBeansBooleanGetter(introspectedColumn, context, introspectedTable,"Equal");
                topLevelClass.addMethod(method);
                
                method = getJavaBeansBooleanSetter(introspectedColumn, context, introspectedTable,"Equal");
                topLevelClass.addMethod(method);
                
            }
            
            
            // 如果是Date Time 格式，还需要生产 Start End 两个字段，以方便时间的between 查询
            if(introspectedColumn.isDateColumn()){
            	field = getJavaBeansFieldExtend(introspectedColumn, context, introspectedTable,"Start");
            	topLevelClass.addField(field);
                topLevelClass.addImportedType(field.getType());
                
                method = getJavaBeansGetterExtend(introspectedColumn, context, introspectedTable,"Start");
                topLevelClass.addMethod(method);
                
                method = getJavaBeansSetterExtend(introspectedColumn, context, introspectedTable,"Start");
                topLevelClass.addMethod(method);
                
                field = getJavaBeansFieldExtend(introspectedColumn, context, introspectedTable,"End");
            	topLevelClass.addField(field);
                topLevelClass.addImportedType(field.getType());
                
                method = getJavaBeansGetterExtend(introspectedColumn, context, introspectedTable,"End");
                topLevelClass.addMethod(method);
                
                method = getJavaBeansSetterExtend(introspectedColumn, context, introspectedTable,"End");
                topLevelClass.addMethod(method);
            }

            //如果是包含了要产生IdList的字段
            Set<String> colIdList = GenUtil.getConfig4SelectInColumList(introspectedTable, false);
            if (colIdList.contains(introspectedColumn.getActualColumnName().toLowerCase())) {
                field = GenUtil.getJavaBeansField4ColumnList(introspectedColumn, false);
                field.setTransient(true);
                topLevelClass.addField(field);
                topLevelClass.addImportedType(FullyQualifiedJavaType.getNewListInstance());

                method = GenUtil.getJavaBeansGetter4ColumnList(introspectedColumn, false);
                topLevelClass.addMethod(method);

                method = GenUtil.getJavaBeansSetter4ColumnList(introspectedColumn, false);
                topLevelClass.addMethod(method);
            }

            //如果是包含了要产生notIncludeIdList的字段
            Set<String> colIdNotInList = GenUtil.getConfig4SelectInColumList(introspectedTable, true);
            if (colIdNotInList.contains(introspectedColumn.getActualColumnName().toLowerCase())) {
                field = GenUtil.getJavaBeansField4ColumnList(introspectedColumn, true);
                field.setTransient(true);
                topLevelClass.addField(field);
                topLevelClass.addImportedType(FullyQualifiedJavaType.getNewListInstance());

                method = GenUtil.getJavaBeansGetter4ColumnList(introspectedColumn, true);
                topLevelClass.addMethod(method);

                method = GenUtil.getJavaBeansSetter4ColumnList(introspectedColumn, true);
                topLevelClass.addMethod(method);
            }

        }

        // 如果指定了父子字段，需要增加一些额外的字段。
        String parentId = GenUtil.getConfig4ParentColum(introspectedTable) ;
        if(parentId != null){
        	Field field = getJavaBeansField4Child(introspectedTable);
        	topLevelClass.addImportedType(FullyQualifiedJavaType.getNewListInstance()) ;
        	topLevelClass.addImportedType(new FullyQualifiedJavaType("java.util.Collections")) ;
        	topLevelClass.addField(field);
        	
        	Method method = getJavaBeansGetter4Child(introspectedTable);
            topLevelClass.addMethod(method);
            
            method = getJavaBeansSetter4Child(introspectedTable);
            topLevelClass.addMethod(method);
            
            String boolFiledName = "hasSub" + introspectedTable.getFullyQualifiedTable().getDomainObjectName() ;
            field = getJavaBeansBooleanField(boolFiledName);
        	topLevelClass.addField(field);
        	
        	method = getJavaBeansBooleanGetter(boolFiledName);
            topLevelClass.addMethod(method);
            
            method = getJavaBeansBooleanSetter(boolFiledName);
            topLevelClass.addMethod(method);
        }
        
        // 附加字段，额外的，表里面没有的，暂时只处理一个
        String addition = GenUtil.getConfig4AddtinalColum(introspectedTable) ;
        if(addition != null){
        	String [] addFields = addition.split(",") ;
        	for(String addField : addFields){
        		
        		Field field = getJavaBeansField4Addition(introspectedTable, addField);
            	topLevelClass.addField(field);
            	
            	Method method = getJavaBeansGetter4Addition(introspectedTable, addField);
                topLevelClass.addMethod(method);
                
                method = getJavaBeansSetter4Addition(introspectedTable, addField);
                topLevelClass.addMethod(method);
        	}
        }
        
        
        // 用于有 > >= < <=比较字段的需求的时候
        Map operatorMap = GenUtil.getOperatorColumns(introspectedTable);
        if (operatorMap != null) {
            List<String> operatorList = (List<String>)operatorMap.get("list");
            Map<String, Map> operatorItemMap = (Map<String, Map>)operatorMap.get("map");
            for (String origField: operatorList) {
                List<Map> fields = (List<Map>)operatorItemMap.get(origField);
                for (Map fieldMap: fields) {
                    String fieldStr = (String) fieldMap.get("field");

                    IntrospectedColumn introspectedColumn = mapIntrospectedColumn.get(origField.toLowerCase());
                    if (introspectedColumn == null)
                        continue;

                    Field field = getJavaBeansFieldSelfDefined(introspectedColumn, context, introspectedTable, fieldStr);
                    field.setTransient(true);
                    topLevelClass.addField(field);
                    topLevelClass.addImportedType(field.getType());

                    Method method = getJavaBeansGetterSelfDefined(introspectedColumn, context, introspectedTable, fieldStr);
                    topLevelClass.addMethod(method);

                    method = getJavaBeansSetterSelfDefined(introspectedColumn, context, introspectedTable, fieldStr);
                    topLevelClass.addMethod(method);
                }
            }
        }

        List<CompilationUnit> answer = new ArrayList<CompilationUnit>();
        if (context.getPlugins().modelBaseRecordClassGenerated(topLevelClass, introspectedTable)) {
            answer.add(topLevelClass);
        }
        return answer;
    }

    private FullyQualifiedJavaType getSuperClass() {
        FullyQualifiedJavaType superClass;
        if (introspectedTable.getRules().generatePrimaryKeyClass()) {
            superClass = new FullyQualifiedJavaType(introspectedTable
                    .getPrimaryKeyType());
        } else {
            String rootClass = getRootClass();
            if (rootClass != null) {
                superClass = new FullyQualifiedJavaType(rootClass);
            } else {
                superClass = null;
            }
        }

        return superClass;
    }

    private boolean includePrimaryKeyColumns() {
        return !introspectedTable.getRules().generatePrimaryKeyClass()
                && introspectedTable.hasPrimaryKeyColumns();
    }

    private boolean includeBLOBColumns() {
        return !introspectedTable.getRules().generateRecordWithBLOBsClass()
                && introspectedTable.hasBLOBColumns();
    }

    private void addParameterizedConstructor(TopLevelClass topLevelClass) {
        Method method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setConstructor(true);
        method.setName(topLevelClass.getType().getShortName());
        context.getCommentGenerator().addGeneralMethodComment(method, introspectedTable);

        List<IntrospectedColumn> constructorColumns =
            includeBLOBColumns() ? introspectedTable.getAllColumns() :
                introspectedTable.getNonBLOBColumns();
            
        for (IntrospectedColumn introspectedColumn : constructorColumns) {
            method.addParameter(new Parameter(introspectedColumn.getFullyQualifiedJavaType(),
                    introspectedColumn.getJavaProperty()));
            topLevelClass.addImportedType(introspectedColumn.getFullyQualifiedJavaType());
        }
        
        StringBuilder sb = new StringBuilder();
        if (introspectedTable.getRules().generatePrimaryKeyClass()) {
            boolean comma = false;
            sb.append("super("); //$NON-NLS-1$
            for (IntrospectedColumn introspectedColumn : introspectedTable
                    .getPrimaryKeyColumns()) {
                if (comma) {
                    sb.append(", "); //$NON-NLS-1$
                } else {
                    comma = true;
                }
                sb.append(introspectedColumn.getJavaProperty());
            }
            sb.append(");"); //$NON-NLS-1$
            method.addBodyLine(sb.toString());
        }

        List<IntrospectedColumn> introspectedColumns = getColumnsInThisClass();
        
        for (IntrospectedColumn introspectedColumn : introspectedColumns) {
            sb.setLength(0);
            sb.append("this."); //$NON-NLS-1$
            sb.append(introspectedColumn.getJavaProperty());
            sb.append(" = "); //$NON-NLS-1$
            sb.append(introspectedColumn.getJavaProperty());
            sb.append(';');
            method.addBodyLine(sb.toString());
        }

        topLevelClass.addMethod(method);
    }
    
    private List<IntrospectedColumn> getColumnsInThisClass() {
        List<IntrospectedColumn> introspectedColumns;
        if (includePrimaryKeyColumns()) {
            if (includeBLOBColumns()) {
                introspectedColumns = introspectedTable.getAllColumns();
            } else {
                introspectedColumns = introspectedTable.getNonBLOBColumns();
            }
        } else {
            if (includeBLOBColumns()) {
                introspectedColumns = introspectedTable
                        .getNonPrimaryKeyColumns();
            } else {
                introspectedColumns = introspectedTable.getBaseColumns();
            }
        }
        
        return introspectedColumns;
    }
}
