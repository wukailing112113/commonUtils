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

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.util.GenUtil;
import org.mybatis.generator.util.StringUtil;

/**
 */
public class DeleteByKeyMethodGenerator extends AbstractDaoMapperMethodGenerator {

    @Override
    public void setIntrospectedTable(IntrospectedTable introspectedTable) {
        super.setIntrospectedTable(introspectedTable);
    }

    protected Method generateMethod(Set<FullyQualifiedJavaType> importedTypes) {
        Method method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);

        method.setReturnType(FullyQualifiedJavaType.getIntInstance());
        method.setName(GenUtil.getDeleteByKeyMethodName(introspectedTable, GenUtil.ENUM_METHOD_TYPE.DAO_TYPE));

        List<IntrospectedColumn> introspectedColumns = introspectedTable.getPrimaryKeyColumns();
        for (IntrospectedColumn introspectedColumn : introspectedColumns) {
            FullyQualifiedJavaType type = introspectedColumn.getFullyQualifiedJavaType();
            importedTypes.add(type);
            Parameter parameter = new Parameter(type, introspectedColumn.getJavaProperty());
            method.addParameter(parameter);
        }

        context.getCommentGenerator().addGeneralMethodComment(method,introspectedTable);
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

    @Override
    public void addTopLevelClassElements(TopLevelClass topLevelClass) {
        Set<FullyQualifiedJavaType> importedTypes = new TreeSet<FullyQualifiedJavaType>();
        Method method = generateMethod(importedTypes);
        if (method == null)
            return;

        method.setVisibility(JavaVisibility.PUBLIC);
        List<IntrospectedColumn> introspectedColumns = introspectedTable.getPrimaryKeyColumns();
        if (introspectedColumns.size() == 1) {
        	StringBuilder sb = new StringBuilder();
            sb.append("return ");
            sb.append("deleteById(\"");
            sb.append(GenUtil.getDeleteByKeyMethodName(introspectedTable, GenUtil.ENUM_METHOD_TYPE.XML_TYPE));
            sb.append("\", ");
            sb.append(method.getParameters().get(0).getName());
            sb.append("); ");
            method.addBodyLine(sb.toString());
        }
        else{
        	StringBuilder sb = new StringBuilder();
            String entityParaName = GenUtil.getGeneralEntityParamName(introspectedTable);
            String entityTypeName = GenUtil.getEntityType(context, introspectedTable).getShortName();
            sb.append(entityTypeName);
            sb.append(" ").append(entityParaName).append(" = ").append(" new ").append(entityTypeName).append("();");
            method.addBodyLine(sb.toString());
            sb.setLength(0);
            int i = 0;
            for (IntrospectedColumn pkIntrospcectedColumn: introspectedColumns) {
                sb.setLength(0);
                sb.append(entityParaName).append(".set").append(StringUtil.capitalize(pkIntrospcectedColumn.getJavaProperty()))
                        .append("(").append(method.getParameters().get(i).getName()).append(");");
                i++;
                method.addBodyLine(sb.toString());
            }
            sb.setLength(0);

            sb.append("return delete(\"" + GenUtil.getDeleteByKeyMethodName(introspectedTable, GenUtil.ENUM_METHOD_TYPE.XML_TYPE) + "\"");
            sb.append(", ");
            sb.append(entityParaName);
            sb.append(") ; ");
            method.addBodyLine(sb.toString());
        }

        topLevelClass.addImportedTypes(importedTypes);
        topLevelClass.addMethod(method);
    }
}

