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
package org.mybatis.generator.ext.redismapper.elements;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.util.FreeMarkerUtil;
import org.mybatis.generator.util.GenUtil;
import org.mybatis.generator.util.StringUtil;

/**
 */
public class GetBeanIdMethodGenerator extends AbstractRedisMethodGenerator {

    protected Method generateMethod(Set<FullyQualifiedJavaType> importedTypes) {
        Method method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        //only one primary key
        if (introspectedTable.getPrimaryKeyColumns().size() != 1) {
            return null;
        }

        method.setReturnType(introspectedTable.getPrimaryKeyColumns().get(0).getFullyQualifiedJavaType());
        method.setName(GenUtil.getGetBeanIdMethodName(introspectedTable, GenUtil.ENUM_METHOD_TYPE.REDIS_TYPE));
        context.getCommentGenerator().addGeneralMethodComment(method,
                introspectedTable);
        return method;
    }

    @Override
    public void addInterfaceAndClassImpl(Interface interfaze, TopLevelClass topLevelClass) {
        Set<FullyQualifiedJavaType> importedTypes = new TreeSet<FullyQualifiedJavaType>();
        Method method = generateMethod(importedTypes);

        interfaze.addImportedTypes(importedTypes);
        interfaze.addMethod(method);

        method.setVisibility(JavaVisibility.PUBLIC);

        Map<String, String> templateParaMap = new HashMap<String, String>();
        templateParaMap.put("Bean", GenUtil.getEntityName(introspectedTable));
        templateParaMap.put("bean", GenUtil.getGeneralEntityParamName(introspectedTable));
        templateParaMap.put("findMaxIdMethodName", GenUtil.getMaxIdMethodName(introspectedTable, GenUtil.ENUM_METHOD_TYPE.DAO_TYPE));

        String dir = GenUtil.getRedisFtlPath(context, GenUtil.ENUM_METHOD_TYPE.REDIS_TYPE);
        Method methodImpl = generateMethod(importedTypes);
        try {
            String body = FreeMarkerUtil.getStringFromTemplate(dir, "getBeanId.ftl", templateParaMap);
            String[] strLines = StringUtil.split(body, System.getProperty("line.separator"));
            methodImpl.addBodyLines(Arrays.asList(strLines));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        topLevelClass.addImportedTypes(importedTypes);
        topLevelClass.addMethod(methodImpl);
    }

}
