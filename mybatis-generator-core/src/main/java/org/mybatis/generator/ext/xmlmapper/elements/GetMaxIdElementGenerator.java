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

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities;
import org.mybatis.generator.codegen.mybatis3.xmlmapper.elements.AbstractXmlElementGenerator;
import org.mybatis.generator.internal.util.JavaBeansUtil;
import org.mybatis.generator.util.GenUtil;

/**
 */
public class GetMaxIdElementGenerator extends
        AbstractXmlElementGenerator {

    public GetMaxIdElementGenerator() {
        super();
    }

    @Override
    public void addElements(XmlElement parentElement) {
        if (introspectedTable.getPrimaryKeyColumns().size() != 1)
            return;
        
        String maxIdCond = GenUtil.getConfig4MaxIdRelateColum(introspectedTable);
        Boolean bMaxIdCond = (maxIdCond == null || maxIdCond == "") ;
        
        XmlElement answer = new XmlElement("select");
        answer.addAttribute(new Attribute(
                "id", GenUtil.getMaxIdMethodName(introspectedTable, GenUtil.ENUM_METHOD_TYPE.XML_TYPE)));

        String identityColumnType = (introspectedTable.getRules().generatePrimaryKeyClass())?
                introspectedTable.getPrimaryKeyType():
                introspectedTable.getPrimaryKeyColumns().get(0).getFullyQualifiedJavaType().toString();
        
        if(!bMaxIdCond){
        	//FullyQualifiedJavaType parameterType = introspectedTable.getRules().calculateAllFieldsClass();
            //answer.addAttribute(new Attribute("parameterType", parameterType.getFullyQualifiedName()));
        	String praraterType = introspectedTable.getColumn(maxIdCond).getFullyQualifiedJavaType().toString() ;
        	answer.addAttribute(new Attribute("parameterType", praraterType));
        }
        
        answer.addAttribute(new Attribute("resultType", identityColumnType));

        context.getCommentGenerator().addComment(answer);

        StringBuilder sb = new StringBuilder();
        sb.append("select IFNULL(max(");
        sb.append(introspectedTable.getPrimaryKeyColumns().get(0).getActualColumnName());
        sb.append("), 0) from ");
        sb.append(introspectedTable.getAliasedFullyQualifiedTableNameAtRuntime());
        answer.addElement(new TextElement(sb.toString()));
        
        if(!bMaxIdCond) {
        	sb.setLength(0);
        	IntrospectedColumn introspectedColumn = introspectedTable.getColumn(maxIdCond) ;
        	sb.append(" where ").append(MyBatis3FormattingUtilities.getEscapedColumnName(introspectedColumn)) ;
        	sb.append(" = ").append(MyBatis3FormattingUtilities.getParameterClause(introspectedColumn)) ;
        	answer.addElement(new TextElement(sb.toString()));
        }
        
        if (context.getPlugins()
                .sqlMapDeleteByPrimaryKeyElementGenerated(answer,
                        introspectedTable)) {
            parentElement.addElement(answer);
        }
    }
}