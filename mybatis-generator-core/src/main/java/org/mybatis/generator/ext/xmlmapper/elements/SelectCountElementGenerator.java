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
package org.mybatis.generator.ext.xmlmapper.elements;

import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.xmlmapper.elements.AbstractXmlElementGenerator;
import org.mybatis.generator.util.GenUtil;

/**
 */
public class SelectCountElementGenerator extends
        AbstractXmlElementGenerator {

    public SelectCountElementGenerator() {
        super();
    }

    @Override
    public void addElements(XmlElement parentElement) {
        if (introspectedTable.getPrimaryKeyColumns().size() < 1)
            return;

        XmlElement answer = new XmlElement("select");
        answer.addAttribute(new Attribute(
                "id", GenUtil.getSelectCountMethodName(introspectedTable, GenUtil.ENUM_METHOD_TYPE.XML_TYPE)));
        answer.addAttribute(new Attribute("resultType", "java.lang.Long"));
        answer.addAttribute(new Attribute("parameterType", introspectedTable.getBaseRecordType()));

        answer.addElement(new TextElement("select count(*) as `count`"));
        answer.addElement(new TextElement(" from " +
                introspectedTable.getAliasedFullyQualifiedTableNameAtRuntime() +
                " where 1=1 "));

        XmlElement includeSqlWhere = new XmlElement("include"); //$NON-NLS-1$
        includeSqlWhere.addAttribute(new Attribute("refid", GenUtil.genSqlWhereExpression(introspectedTable)));
        answer.addElement(includeSqlWhere);

        parentElement.addElement(answer);
    }
}
