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
package org.mybatis.generator.ext.daomapper.elements;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.util.GenUtil;

import com.wins.shop.entity.base.BaseCata;

/**
 */
public class UpdateListMethodGenerator extends AbstractDaoMapperMethodGenerator {

    @Override
    public void setIntrospectedTable(IntrospectedTable introspectedTable) {
        super.setIntrospectedTable(introspectedTable);
    }
    
//    Method method = new Method();
//    method.setVisibility(JavaVisibility.PUBLIC);
//    
//    method.setName(GenUtil.getInsertListMethodName(introspectedTable, GenUtil.ENUM_METHOD_TYPE.DAO_TYPE));
//    String entityParaName = GenUtil.getCollectionEntityParamName("list", introspectedTable);
//    
//    FullyQualifiedJavaType listType = FullyQualifiedJavaType.getListInstance(introspectedTable.getFullyQualifiedTable().getDomainObjectName());
//    method.setReturnType(listType);
//    importedTypes.add(listType);
//    
//    FullyQualifiedJavaType entityType = GenUtil.getEntityType(context, introspectedTable);
//    importedTypes.add(entityType);
//    method.addParameter(new Parameter(listType, entityParaName));
//    
//    context.getCommentGenerator().addGeneralMethodComment(method,introspectedTable);
//    return method;
//    
    protected Method generateMethod(Set<FullyQualifiedJavaType> importedTypes) {
        Method method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);

        method.setName(GenUtil.getUpdateListMethodName(introspectedTable, GenUtil.ENUM_METHOD_TYPE.DAO_TYPE));
        method.setReturnType(FullyQualifiedJavaType.getIntInstance());
        
        FullyQualifiedJavaType listType = FullyQualifiedJavaType.getListInstance(introspectedTable.getFullyQualifiedTable().getDomainObjectName());
        importedTypes.add(listType);
        
        String entityParaName = GenUtil.getCollectionEntityParamName("list", introspectedTable);
        FullyQualifiedJavaType entityType = GenUtil.getEntityType(context, introspectedTable);
        importedTypes.add(entityType);
        
        method.addParameter(new Parameter(listType, entityParaName));

        context.getCommentGenerator().addGeneralMethodComment(method, introspectedTable);
        return method;
    }

    @Override
    public void addInterfaceElements(Interface interfaze) {
        Set<FullyQualifiedJavaType> importedTypes = new TreeSet<FullyQualifiedJavaType>();
        Method method = generateMethod(importedTypes);
        if (method == null)
            return;

        interfaze.addImportedTypes(importedTypes);
        interfaze.addMethod(method);
    }
    
//  public int updateListBaseCata(List<BaseCata> baseCatas) {
//  int result = 0;
//  for (BaseCata baseCatabaseCatas) {
//      result = update("updateBaseCata", baseCata);
//       if (result == -1) {
//              break;
//           }
//      }
//      return result;
//  }
//}	

    @Override
    public void addTopLevelClassElements(TopLevelClass topLevelClass) {
        Set<FullyQualifiedJavaType> importedTypes = new TreeSet<FullyQualifiedJavaType>();
        Method method = generateMethod(importedTypes);
        if (method == null)
            return;

        method.setVisibility(JavaVisibility.PUBLIC);
        StringBuilder sb = new StringBuilder();

        method.addBodyLine("int result = 0;");
        String eachItem = GenUtil.getGeneralEntityParamName(introspectedTable);
        sb.setLength(0);
        sb.append("for (").append(GenUtil.getEntityName(introspectedTable)).append(" ").append(eachItem).append(" : ").append(method.getParameters().get(0).getName()).append("){");
        method.addBodyLine(sb.toString());

        sb.setLength(0);
        sb.append("result = ").append(GenUtil.getUpdateByBeanMethodName(introspectedTable, GenUtil.ENUM_METHOD_TYPE.XML_TYPE)).append("(").append(eachItem).append(");");
        method.addBodyLine(sb.toString());

        method.addBodyLine("if (result == -1) {");
        method.addBodyLine("break;");
        method.addBodyLine("}");
        method.addBodyLine("}");
        method.addBodyLine("return result;");

        topLevelClass.addImportedTypes(importedTypes);
        topLevelClass.addMethod(method);
    }
}