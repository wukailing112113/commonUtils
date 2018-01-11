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

import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;
import org.mybatis.generator.api.dom.java.TopLevelClass;

/**
 * 生成 类似这样的格式
 * @Autowired
 @Qualifier("userSqlSessionFactory")
 public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
 super.sqlSessionFactory = sqlSessionFactory;
 }
 */
public class SqlSessionFactoryMethodGenerator extends AbstractDaoMapperMethodGenerator {

    @Override
    public void addTopLevelClassElements(TopLevelClass topLevelClass) {
        Method method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.addAnnotation("@Autowired");
        String prefix = topLevelClass.getType().getPackageName() ;
        prefix = prefix.substring(prefix.lastIndexOf(".") + 1) ;
        method.addAnnotation("@Qualifier(\""+ prefix +"SqlSessionFactory\")");
        method.setName("setSqlSessionFactory");

        FullyQualifiedJavaType importedTypes = new FullyQualifiedJavaType("org.apache.ibatis.session.SqlSessionFactory");
        method.addParameter(new Parameter(importedTypes, "sqlSessionFactory"));

        method.addBodyLine(" super.sqlSessionFactory = sqlSessionFactory;");

        topLevelClass.addImportedType(importedTypes);
        topLevelClass.addMethod(method);
    }

    @Override
    public void addInterfaceElements(Interface interfaze) {
        return;
    }
}
