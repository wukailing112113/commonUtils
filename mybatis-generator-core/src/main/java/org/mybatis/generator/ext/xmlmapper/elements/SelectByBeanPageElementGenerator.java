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
import org.mybatis.generator.config.PropertyRegistry;
import org.mybatis.generator.util.GenUtil;

/**
 */
public class SelectByBeanPageElementGenerator  extends
        AbstractXmlElementGenerator {

    public SelectByBeanPageElementGenerator() {
        super();
    }

    public void addElements(XmlElement parentElement) {
        //没有主键的情况
        if (introspectedTable.getPrimaryKeyColumns() == null || introspectedTable.getPrimaryKeyColumns().size() == 0) {
            return;
        }

        XmlElement answer = new XmlElement("select");
        answer.addAttribute(new Attribute(
                "id", GenUtil.getSelectByBeanPageMethodName(introspectedTable, GenUtil.ENUM_METHOD_TYPE.XML_TYPE)));
        answer.addAttribute(new Attribute("resultMap", introspectedTable.getBaseResultMapId()));

        String identityColumnType = (introspectedTable.getRules().generatePrimaryKeyClass())?
                introspectedTable.getPrimaryKeyType():
                introspectedTable.getPrimaryKeyColumns().get(0).getFullyQualifiedJavaType() .toString();
        //answer.addAttribute(new Attribute("resultType", identityColumnType));
        answer.addAttribute(new Attribute("parameterType", introspectedTable.getBaseRecordType()));

//        context.getCommentGenerator().addComment(answer);
        answer.addElement(new TextElement("select "));
        answer.addElement(getBaseColumnListElement());
        answer.addElement(new TextElement(" from " +
                introspectedTable.getAliasedFullyQualifiedTableNameAtRuntime() +
                " where 1=1 "));

        XmlElement includeSqlWhere = new XmlElement("include");
        includeSqlWhere.addAttribute(new Attribute("refid", GenUtil.genSqlWhereExpression(introspectedTable)));
        answer.addElement(includeSqlWhere);

        String orderByClause = introspectedTable.getTableConfigurationProperty(PropertyRegistry.TABLE_SELECT_ALL_ORDER_BY_CLAUSE);
        if (orderByClause != null) {
            answer.addElement(new TextElement(orderByClause));
        }

        XmlElement oderbySql = new XmlElement("if") ;
        oderbySql.addAttribute(new Attribute("test", "orderBy != null and orderBy != '' ")) ;
        oderbySql.addElement(new TextElement("order by ${orderBy} ")) ;
        answer.addElement(oderbySql) ;

        //分页控制
        XmlElement pageSqlWhere = new XmlElement("if");
        pageSqlWhere.addAttribute(new Attribute("test", "index != null and pageSize != null"));
        pageSqlWhere.addElement(new TextElement("limit #{index,jdbcType=INTEGER}, #{pageSize,jdbcType=INTEGER}"));
        answer.addElement(pageSqlWhere);

        //行级锁
        XmlElement updateSql = new XmlElement("if");
        updateSql.addAttribute(new Attribute("test", "forupdate != null and forupdate"));
        updateSql.addElement(new TextElement("for update"));
        answer.addElement(updateSql);
        

        parentElement.addElement(answer);
    }
}
