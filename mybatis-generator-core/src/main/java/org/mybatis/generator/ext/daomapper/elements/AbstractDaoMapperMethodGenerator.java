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

import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.codegen.mybatis3.javamapper.elements.AbstractJavaMapperMethodGenerator;

/**
 */
public abstract class AbstractDaoMapperMethodGenerator extends AbstractJavaMapperMethodGenerator {

//    FullyQualifiedJavaType entityType;
//
//    public FullyQualifiedJavaType getEntityType() {
//        introspectedTable.get
//    }
//
//    public void setEntityType(FullyQualifiedJavaType entityType) {
//        this.entityType = entityType;
//    }

    public abstract void addTopLevelClassElements(TopLevelClass topLevelClass);


}
