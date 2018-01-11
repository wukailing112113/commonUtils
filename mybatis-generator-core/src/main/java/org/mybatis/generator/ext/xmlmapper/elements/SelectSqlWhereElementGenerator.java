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

import org.apache.commons.beanutils.BeanUtils;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities;
import org.mybatis.generator.codegen.mybatis3.xmlmapper.elements.AbstractXmlElementGenerator;
import org.mybatis.generator.util.GenUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.mybatis.generator.internal.util.JavaBeansUtil.getJavaBeansFieldSelfDefined;
import static org.mybatis.generator.internal.util.JavaBeansUtil.getJavaBeansGetterSelfDefined;
import static org.mybatis.generator.internal.util.JavaBeansUtil.getJavaBeansSetterSelfDefined;

/**
 */
public class SelectSqlWhereElementGenerator extends AbstractXmlElementGenerator {
    @Override
    public void addElements(XmlElement parentElement) {

        XmlElement answer = new XmlElement("sql");
        answer.addAttribute(new Attribute("id", GenUtil.genSqlWhereExpression(introspectedTable)));

        // table的 include id list
        Set<String> colIdListSet = GenUtil.getConfig4SelectInColumList(introspectedTable, false);
        // table的 exclude id list
        Set<String> colIdExcludeListSet = GenUtil.getConfig4SelectInColumList(introspectedTable, true);
        //是否包含
        Map operatorMap = GenUtil.getOperatorColumns(introspectedTable);
        Map<String, IntrospectedColumn> mapIntrospectedColumn = new HashMap<String, IntrospectedColumn>();
        for (IntrospectedColumn introspectedColumn : introspectedTable.getAllColumns()) {
            //
            mapIntrospectedColumn.put(introspectedColumn.getActualColumnName().toLowerCase(), introspectedColumn);

            StringBuilder sb = new StringBuilder();
            XmlElement ifNotNullElement = new XmlElement("if");
            sb.setLength(0);
            
            // 时间要用between
            if(introspectedColumn.isDateColumn()){
            	sb.append(introspectedColumn.getJavaProperty() + "Start");
                sb.append(" != null and ");
                sb.append(introspectedColumn.getJavaProperty() + "End");
                sb.append(" != null");
                ifNotNullElement.addAttribute(new Attribute("test", sb.toString()));
                sb.setLength(0);
                
                sb.append(" and ");
            	sb.append(MyBatis3FormattingUtilities.getEscapedColumnName(introspectedColumn));
            	sb.append(" &gt;= ");
                sb.append(MyBatis3FormattingUtilities.getParameterExtendClause(introspectedColumn, null, "Start"));
                sb.append(" and ") ;
                sb.append(MyBatis3FormattingUtilities.getParameterExtendClause(introspectedColumn, null, "End"));
            	sb.append(" &gt;= ");
            	sb.append(MyBatis3FormattingUtilities.getEscapedColumnName(introspectedColumn));
                ifNotNullElement.addElement(new TextElement(sb.toString()));
            }

//        <if test="account != null">
//        	<if test="accountEqual != null and accountEqual">
//           		and account = #{account,jdbcType=VARCHAR}
//           	</if>
//           	<if test="accountEqual != null and not accountEqual">
//           		<![CDATA[and account like CONCAT('%',#{account,jdbcType=VARCHAR} ,'%')]]>
//           	</if>
//        </if>
        
            else if(introspectedColumn.isStringColumn())
            {
            	sb.append(introspectedColumn.getJavaProperty());
                sb.append(" != null");
                ifNotNullElement.addAttribute(new Attribute("test", sb.toString()));
                sb.setLength(0);
                
                String filedName = introspectedColumn.getJavaProperty()+"Equal" ;
                XmlElement subEqualElement = new XmlElement("if");
                
                sb.append(filedName);
                sb.append(" != null and ").append(filedName) ;
                subEqualElement.addAttribute(new Attribute("test", sb.toString()));
                sb.setLength(0);
                sb.append(" and ");
            	sb.append(MyBatis3FormattingUtilities.getEscapedColumnName(introspectedColumn));
            	sb.append(" = ");
                sb.append(MyBatis3FormattingUtilities.getParameterClause(introspectedColumn));
                subEqualElement.addElement(new TextElement(sb.toString()));
                ifNotNullElement.addElement(subEqualElement);
                
                subEqualElement = new XmlElement("if");
                sb.setLength(0);
                sb.append(filedName);
                sb.append(" != null and not ").append(filedName) ;
                subEqualElement.addAttribute(new Attribute("test", sb.toString()));
                sb.setLength(0);
                sb.append(" <![CDATA[and ");
            	sb.append(MyBatis3FormattingUtilities.getEscapedColumnName(introspectedColumn));
            	sb.append(" like CONCAT('%',");
                sb.append(MyBatis3FormattingUtilities.getParameterClause(introspectedColumn));
                sb.append(" ,'%')]]>");
                subEqualElement.addElement(new TextElement(sb.toString()));
                ifNotNullElement.addElement(subEqualElement);
            }
            else{
            	sb.append(introspectedColumn.getJavaProperty());
                sb.append(" != null");
                ifNotNullElement.addAttribute(new Attribute("test", sb.toString()));
                sb.setLength(0);
                
            	sb.append(" and ");
                sb.append(MyBatis3FormattingUtilities.getEscapedColumnName(introspectedColumn));
                sb.append(" = ");
                sb.append(MyBatis3FormattingUtilities.getParameterClause(introspectedColumn));
                ifNotNullElement.addElement(new TextElement(sb.toString()));
            }

            answer.addElement(ifNotNullElement);

            //如果包含有字段要
            if (colIdListSet.contains(introspectedColumn.getActualColumnName().toLowerCase())) {
                String itemListName = introspectedColumn.getJavaProperty() + "List";
                sb.setLength(0);
                ifNotNullElement = new XmlElement("if");
                sb.append(itemListName);
                sb.append(" != null and ");
                sb.append(itemListName);
                sb.append(".size > 0 ");
                ifNotNullElement.addAttribute(new Attribute("test", sb.toString()));
                sb.setLength(0);

                sb.append(" and ").append(MyBatis3FormattingUtilities.getEscapedColumnName(introspectedColumn)).append(" in ");
                ifNotNullElement.addElement(new TextElement(sb.toString()));
                sb.setLength(0);

                XmlElement foreachElem = new XmlElement("foreach");
                foreachElem.addAttribute(new Attribute("item", "item"));
                foreachElem.addAttribute(new Attribute("collection", itemListName));
                foreachElem.addAttribute(new Attribute("open", "("));
                foreachElem.addAttribute(new Attribute("separator", ","));
                foreachElem.addAttribute(new Attribute("close", ")"));
                foreachElem.addElement(new TextElement("#{item}"));
                ifNotNullElement.addElement(foreachElem);
                answer.addElement(ifNotNullElement);
            }

            //如果包含有字段要not in xxx
            if (colIdExcludeListSet.contains(introspectedColumn.getActualColumnName().toLowerCase())) {
                String itemListName = introspectedColumn.getJavaProperty() + "ExcludeList";
                sb.setLength(0);
                ifNotNullElement = new XmlElement("if");
                sb.append(itemListName);
                sb.append(" != null and ");
                sb.append(itemListName);
                sb.append(".size > 0 ");
                ifNotNullElement.addAttribute(new Attribute("test", sb.toString()));
                sb.setLength(0);

                sb.append(" and ").append(MyBatis3FormattingUtilities.getEscapedColumnName(introspectedColumn)).append(" not in ");
                ifNotNullElement.addElement(new TextElement(sb.toString()));
                sb.setLength(0);

                XmlElement foreachElem = new XmlElement("foreach");
                foreachElem.addAttribute(new Attribute("item", "item"));
                foreachElem.addAttribute(new Attribute("collection", itemListName));
                foreachElem.addAttribute(new Attribute("open", "("));
                foreachElem.addAttribute(new Attribute("separator", ","));
                foreachElem.addAttribute(new Attribute("close", ")"));
                foreachElem.addElement(new TextElement("#{item}"));
                ifNotNullElement.addElement(foreachElem);
                answer.addElement(ifNotNullElement);
            }
        }

        if (operatorMap != null) {
            //支持大于 等于 小于的自定义比较
            List<String> operatorList = (List<String>)operatorMap.get("list");
            Map<String, Map> operatorItemMap = (Map<String, Map>)operatorMap.get("map");
            for (String origField: operatorList) {

                List<Map> fields = (List<Map>)operatorItemMap.get(origField);
                for (Map fieldMap: fields) {
                    String fieldStr = (String)fieldMap.get("field");
                    String operStr = (String)fieldMap.get("oper");
                    IntrospectedColumn introspectedColumn = mapIntrospectedColumn.get(origField.toLowerCase());
                    if (introspectedColumn == null)
                        continue;

                    StringBuffer sb = new StringBuffer();
                    XmlElement ifNotNullElement = new XmlElement("if");
                    sb.append(fieldStr);
                    sb.append(" != null ");
                    ifNotNullElement.addAttribute(new Attribute("test", sb.toString()));

                    sb.setLength(0);
                    sb.append(" and ");
                    sb.append(MyBatis3FormattingUtilities.getEscapedColumnName(introspectedColumn));
                    sb.append(" ").append(operStr).append(" ");
                    IntrospectedColumn introspectedColumnNew = new IntrospectedColumn();
                    try {
                        BeanUtils.copyProperties(introspectedColumnNew, introspectedColumn);
                        introspectedColumnNew.setJavaProperty(fieldStr);
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                    sb.append(MyBatis3FormattingUtilities.getParameterClause(introspectedColumnNew));
                    ifNotNullElement.addElement(new TextElement(sb.toString()));
                    answer.addElement(ifNotNullElement);
                }
            }
        }

        parentElement.addElement(answer);
    }
}
//
//<sql id="select_where_sql">
//<if test="bannerId != null and bannerId != ''">
//	and banner_id=#{bannerId}
//</if>
//<if test="type != null and type != ''">
//	and type=#{type}
//</if>
//<if test="status != null and status != ''">
//	and type=#{status}
//</if>
//<if test="brief != null and brief !='' ">
//   <![CDATA[and brief like CONCAT('%',#{brief},'%')]]>
//</if>
//<if test="createTimeStart != null and createTimeEnd != null">
//	and create_time &gt;= #{createTimeStart} and #{createTimeEnd} &gt;= create_time
//</if>
//<if test="statusTimeStart != null and statusTimeEnd != null">
//	and status_time &gt;= #{statusTimeStart} and #{statusTimeEnd} &gt;= status_time
//</if>
//</sql>