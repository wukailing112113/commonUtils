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
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities;
import org.mybatis.generator.codegen.mybatis3.xmlmapper.elements.AbstractXmlElementGenerator;
import org.mybatis.generator.util.GenUtil;

/**
 *
 * @author Jeff Butler
 *
 */
public class DeleteByListElementGenerator extends
        AbstractXmlElementGenerator {

    private boolean isSimple;

    public DeleteByListElementGenerator(boolean isSimple) {
        super();
        this.isSimple = isSimple;
    }

    @Override
    public void addElements(XmlElement parentElement) {
    	
    	// 只处理单一主键的情况
    	if(introspectedTable.getPrimaryKeyColumns().size() > 1 || introspectedTable.getPrimaryKeyColumns().size() == 0){
    		return ;
    	}
    	
        XmlElement answer = new XmlElement("delete"); //$NON-NLS-1$
        answer.addAttribute(
        		new Attribute("id", GenUtil.getDeleteByListMethodName(introspectedTable, GenUtil.ENUM_METHOD_TYPE.XML_TYPE))); //$NON-NLS-1$
        String parameterClass;
        if (!isSimple && introspectedTable.getRules().generatePrimaryKeyClass()) {
            parameterClass = introspectedTable.getPrimaryKeyType();
        } else {
            // PK fields are in the base class. If more than on PK
            // field, then they are coming in a map.
            if (introspectedTable.getPrimaryKeyColumns().size() > 1) {
                parameterClass = "map"; //$NON-NLS-1$
            } else {
                parameterClass = introspectedTable.getPrimaryKeyColumns()
                        .get(0).getFullyQualifiedJavaType().toString();
            }
        }
        answer.addAttribute(new Attribute("parameterType", //$NON-NLS-1$
                parameterClass));

        context.getCommentGenerator().addComment(answer);

        StringBuilder sb = new StringBuilder();
        sb.append("delete from "); //$NON-NLS-1$
        sb.append(introspectedTable.getFullyQualifiedTableNameAtRuntime());
        answer.addElement(new TextElement(sb.toString()));
//
//        <foreach collection="list" item = "model" open="(" separator="," close=")">#{ model.id}  
//        
//        </foreach>  
        IntrospectedColumn introspectedColumn = introspectedTable.getPrimaryKeyColumns().get(0) ;
        sb.setLength(0);
        sb.append("where ") ;
        sb.append(MyBatis3FormattingUtilities.getEscapedColumnName(introspectedColumn)) ;
        sb.append(" in \n") ;
        sb.append("\t <foreach collection=\"list\" item = \"item\" open=\"(\" separator=\",\" close=\")\">\n\t  ") ;
        sb.append(MyBatis3FormattingUtilities
                .getParameterClauseJdbcType(introspectedColumn,"item.")) ;
        sb.append("\n\t </foreach>") ;
        answer.addElement(new TextElement(sb.toString()));
        

        if (context.getPlugins()
                .sqlMapDeleteByPrimaryKeyElementGenerated(answer,
                        introspectedTable)) {
            parentElement.addElement(answer);
        }
    }
}
