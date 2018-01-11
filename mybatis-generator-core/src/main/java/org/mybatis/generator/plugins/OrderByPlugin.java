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
package org.mybatis.generator.plugins;

import java.util.List;
import java.util.Properties;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;
import org.mybatis.generator.api.dom.java.TopLevelClass;

/**
 * This plugin adds the java.io.Serializable marker interface to all generated
 * model objects.
 * <p>
 * This plugin demonstrates adding capabilities to generated Java artifacts, and
 * shows the proper way to add imports to a compilation unit.
 * <p>
 * Important: This is a simplistic implementation of serializable and does not
 * attempt to do any versioning of classes.
 * 
 * @author Jeff Butler
 * 
 */
public class OrderByPlugin extends PluginAdapter {


	private FullyQualifiedJavaType field;
	
    public OrderByPlugin() {
    	super();
    	field = new FullyQualifiedJavaType("java.lang.reflect.Field"); 
    }

    public boolean validate(List<String> warnings) {
        // this plugin is always valid
        return true;
    }

    @Override
    public void setProperties(Properties properties) {
        super.setProperties(properties);
    }
    
    @Override
    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass,
            IntrospectedTable introspectedTable) {
        addOrderByFiled(topLevelClass, introspectedTable);
        addOrderByMethod(topLevelClass, introspectedTable);
        return true;
    }

    @Override
    public boolean modelPrimaryKeyClassGenerated(TopLevelClass topLevelClass,
            IntrospectedTable introspectedTable) {
        addOrderByFiled(topLevelClass, introspectedTable);
        addOrderByMethod(topLevelClass, introspectedTable);
        return true;
    }

    @Override
    public boolean modelRecordWithBLOBsClassGenerated(
            TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        addOrderByFiled(topLevelClass, introspectedTable);
        addOrderByMethod(topLevelClass, introspectedTable);
        return true;
    }

    protected void addOrderByFiled(TopLevelClass topLevelClass,
            IntrospectedTable introspectedTable) {
    	topLevelClass.addImportedType(field);
    	
    	Field field = new Field();
        field.setFinal(false);
        field.setInitializationString(""); 
        field.setName("orderBy"); 
        field.setStatic(false);
        field.setType(new FullyQualifiedJavaType("String")); 
        field.setVisibility(JavaVisibility.PRIVATE);
        context.getCommentGenerator().addFieldComment(field, introspectedTable);

        topLevelClass.addField(field);
    }
    
    /**
     * @param topLevelClass
     * @param introspectedTable
     */
    protected void addOrderByMethod(TopLevelClass topLevelClass,IntrospectedTable introspectedTable) {
        Method method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setName("addOrderBy"); 
        Parameter orderFiled = new Parameter(FullyQualifiedJavaType.getStringInstance(),"fieldName",false) ;
        method.addParameter(orderFiled);
        Parameter orderType = new Parameter(FullyQualifiedJavaType.getBooleanPrimitiveInstance() ,"bIsDesc",false) ;
        method.addParameter(orderType);

        context.getCommentGenerator().addGeneralMethodComment(method,
                introspectedTable);
        
        method.addBodyLine("if(orderBy == null){"); 
        method.addBodyLine("orderBy = \"\" ;"); 
        method.addBodyLine("}"); 
        method.addBodyLine("fieldName = toCamel(fieldName) ;"); 
        
        method.addBodyLine("StringBuffer sb = new StringBuffer(orderBy) ;"); 
		
        method.addBodyLine("Field[] fields = this.getClass().getDeclaredFields() ;  "); 
        method.addBodyLine("for (Field field : fields) {"); 
        method.addBodyLine("if (field.getName().equals(fieldName)) {"); 
        method.addBodyLine("if (sb.length() == 0)  {");
        method.addBodyLine("sb.append(toUnderLine(fieldName)) ;");
        method.addBodyLine("if (bIsDesc) {");
        method.addBodyLine("sb.append(\" DESC\") ;");
        method.addBodyLine("}");
        method.addBodyLine("} else {");
        method.addBodyLine("sb.append(\",\" ).append(toUnderLine(fieldName)) ;");
        method.addBodyLine("if (bIsDesc) {");
        method.addBodyLine("sb.append(\" DESC\") ;");
        method.addBodyLine("}");
        method.addBodyLine("}"); 
        method.addBodyLine("break;"); 
        method.addBodyLine("}"); 
        method.addBodyLine("}"); 
        method.addBodyLine("orderBy = sb.toString() ;"); 
        
        topLevelClass.addMethod(method);
    }
}
