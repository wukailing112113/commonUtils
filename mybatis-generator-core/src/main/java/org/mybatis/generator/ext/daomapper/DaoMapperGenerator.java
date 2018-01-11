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
package org.mybatis.generator.ext.daomapper;

import static org.mybatis.generator.internal.util.StringUtility.stringHasValue;
import static org.mybatis.generator.internal.util.messages.Messages.getString;

import java.util.ArrayList;
import java.util.List;

import org.mybatis.generator.api.CommentGenerator;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.CompilationUnit;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.JavaElement;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.codegen.AbstractJavaClientGenerator;
import org.mybatis.generator.codegen.AbstractXmlGenerator;
import org.mybatis.generator.codegen.mybatis3.IntrospectedTableMyBatis3Impl;
import org.mybatis.generator.codegen.mybatis3.javamapper.elements.AbstractJavaMapperMethodGenerator;
import org.mybatis.generator.config.PropertyRegistry;
import org.mybatis.generator.ext.codegen.IntrospectedTableDecorator;
import org.mybatis.generator.ext.daomapper.elements.AbstractDaoMapperMethodGenerator;
import org.mybatis.generator.ext.daomapper.elements.DeleteByKeyMethodGenerator;
import org.mybatis.generator.ext.daomapper.elements.DeleteListMethodGenerator;
import org.mybatis.generator.ext.daomapper.elements.GetMaxIdClientGenerator;
import org.mybatis.generator.ext.daomapper.elements.InsertBeanMethodGenerator;
import org.mybatis.generator.ext.daomapper.elements.InsertListMethodGenerator;
import org.mybatis.generator.ext.daomapper.elements.SelectBeanMethodGenerator;
import org.mybatis.generator.ext.daomapper.elements.SelectByKeyForUpdateMethodGenerator;
import org.mybatis.generator.ext.daomapper.elements.SelectByKeyMethodGenerator;
import org.mybatis.generator.ext.daomapper.elements.SelectByPageMethodGenerator;
import org.mybatis.generator.ext.daomapper.elements.SelectCountMethodGenerator;
import org.mybatis.generator.ext.daomapper.elements.SqlSessionFactoryMethodGenerator;
import org.mybatis.generator.ext.daomapper.elements.UpdateBeanMethodGenerator;
import org.mybatis.generator.ext.daomapper.elements.UpdateListMethodGenerator;
import org.mybatis.generator.ext.redismapper.RedisMapperGenerator;
import org.mybatis.generator.ext.servicemapper.ServiceMapperGenerator;
import org.mybatis.generator.ext.xmlmapper.XMLExtMapperGenerator;
import org.mybatis.generator.util.GenUtil;
import org.mybatis.generator.util.StringUtil;

/**
 */
public class DaoMapperGenerator extends AbstractJavaClientGenerator {

    protected IntrospectedTable introspectedTableDecorator;
    protected String basePackage;

    public DaoMapperGenerator( ) {
        super(true);
    }

    public void setIntrospectedTable(IntrospectedTable introspectedTable) {
        this.introspectedTableDecorator = IntrospectedTableDecorator.newProxyInstance((IntrospectedTableMyBatis3Impl)introspectedTable);
        super.setIntrospectedTable(introspectedTable);


        String targetPackage = context.getJavaClientGeneratorConfiguration().getTargetPackage();
        basePackage = targetPackage.substring(0, targetPackage.indexOf(".dao."));
    }

    public AbstractXmlGenerator getMatchedXMLGenerator() {
        return new XMLExtMapperGenerator();
    }

    protected void initializeAndExecuteGenerator(
            AbstractJavaMapperMethodGenerator methodGenerator) {
        methodGenerator.setContext(context);
        methodGenerator.setIntrospectedTable(introspectedTable);
        methodGenerator.setProgressCallback(progressCallback);
        methodGenerator.setWarnings(warnings);
        
    }

    public List<CompilationUnit> getCompilationUnits() {
        progressCallback.startTask(getString("Progress.17", //$NON-NLS-1$
                introspectedTableDecorator.getFullyQualifiedTable().toString()));
        List<CompilationUnit> answer = new ArrayList<CompilationUnit>();
        Interface interfaze = generateInterfaces();
        TopLevelClass claz = generateImpl(interfaze);

        answer.add(interfaze);
        answer.add(claz);

        if ("true".equals(context.getJavaClientGeneratorConfiguration().getProperty("genRedis"))) {
            boolean useAnalyze = "true".equals(context.getJavaClientGeneratorConfiguration().getProperty("redisAnalyze"))? true: false;
            AbstractJavaClientGenerator redisGenerator = new RedisMapperGenerator(context, introspectedTable, basePackage, useAnalyze);
            List<CompilationUnit> redisCompilationList = redisGenerator.getCompilationUnits();
            answer.addAll(redisCompilationList);
        }

        if ("true".equals(context.getJavaClientGeneratorConfiguration().getProperty("genService"))) {
            boolean useAnalyze = "true".equals(context.getJavaClientGeneratorConfiguration().getProperty("serviceAnalyze"))? true: false;
            String serviceBasePackage = context.getJavaClientGeneratorConfiguration().getProperty("servicePackage");
            if (StringUtil.isEmptyString(serviceBasePackage)) {
                serviceBasePackage = basePackage;
            }
            AbstractJavaClientGenerator serviceMapperGenerator = new ServiceMapperGenerator(context, introspectedTable, serviceBasePackage, useAnalyze);
            List<CompilationUnit> redisCompilationList = serviceMapperGenerator.getCompilationUnits();
            answer.addAll(redisCompilationList);
        }

        return answer;
    }

    protected Interface generateInterfaces() {
        CommentGenerator commentGenerator = context.getCommentGenerator();

        FullyQualifiedJavaType type = new FullyQualifiedJavaType(
                introspectedTableDecorator.getDAOInterfaceType());
        Interface interfaze = new Interface(type);
        interfaze.setVisibility(JavaVisibility.PUBLIC);
        commentGenerator.addJavaFileComment(interfaze);

        String rootInterface = introspectedTable
                .getTableConfigurationProperty(PropertyRegistry.ANY_ROOT_INTERFACE);
        if (!stringHasValue(rootInterface)) {
            rootInterface = context.getJavaClientGeneratorConfiguration()
                    .getProperty(PropertyRegistry.ANY_ROOT_INTERFACE);
        }

        if (stringHasValue(rootInterface)) {
            FullyQualifiedJavaType fqjt = new FullyQualifiedJavaType(
                    rootInterface);
            interfaze.addSuperInterface(fqjt);
            interfaze.addImportedType(fqjt);
        }

//        String maxIdCond = GenUtil.getConfig4MaxIdRelateColum(introspectedTable);
        if (GenUtil.isPrimaryKeyOnlyOneInTable(introspectedTable)) {
            addMaxIdMethod(interfaze);
        }
        addSelectByPrimaryKeyMethod(interfaze);
        addSelectByPrimaryKeyForUpdateMethod(interfaze);
        addSelectByBeanMethod(interfaze);
        addSelectByBeanPageMethod(interfaze);
        addSelectCountMethod(interfaze);
        addDeleteByPrimaryKeyMethod(interfaze);
        addDeleteByListMethod(interfaze);
        addInsertMethod(interfaze);
        addInsertListMethod(interfaze);
        addUpdateSelectiveMethod(interfaze);
        addUpdateListMethod(interfaze);
//        addInsertElement(answer);
//        addInsertListElement(answer);
//        addInsertSelectiveElement(answer);
//        addUpdateByPrimaryKeySelectiveElement(answer);
        
        return interfaze;
    }

    /**
     * 生成impl
     */
    protected TopLevelClass generateImpl(Interface interfaces) {
        FullyQualifiedJavaType type = new FullyQualifiedJavaType(
                introspectedTableDecorator.getDAOImplementationType());
        CommentGenerator commentGenerator = context.getCommentGenerator();
        TopLevelClass topLevelClass = new TopLevelClass(type);
        topLevelClass.setVisibility(JavaVisibility.PUBLIC);
        commentGenerator.addJavaFileComment(topLevelClass);

        topLevelClass.addAnnotation("@Repository(\"" + StringUtil.uncapitalize(topLevelClass.getType().getShortName().replace("Impl", "")) + "\")");
        topLevelClass.addImportedType(GenUtil.getEntityType(context, introspectedTable));
        topLevelClass.addImportedType(basePackage + ".dao.impl.BaseDaoImp");
        topLevelClass.addImportedType(interfaces.getType());
        topLevelClass.addImportedType("org.springframework.stereotype.Repository");
        topLevelClass.addImportedType("org.springframework.beans.factory.annotation.Qualifier");
        topLevelClass.addImportedType("org.springframework.beans.factory.annotation.Autowired");

        topLevelClass.setSuperClass("BaseDaoImp<" + introspectedTable.getFullyQualifiedTable().getDomainObjectName() + ">");
        topLevelClass.addSuperInterface(interfaces.getType());
        commentGenerator.addModelClassComment(topLevelClass, introspectedTable);

        if (GenUtil.isPrimaryKeyOnlyOneInTable(introspectedTable)) {
            addMaxIdMethod(topLevelClass);
        }
        addSelectByPrimaryKeyMethod(topLevelClass);
        addSelectByPrimaryKeyForUpdateMethod(topLevelClass);
        addSelectByBeanMethod(topLevelClass);
        addSelectByBeanPageMethod(topLevelClass);
        addSelectCountMethod(topLevelClass);
        addDeleteByPrimaryKeyMethod(topLevelClass);
        addDeleteByListMethod(topLevelClass);
        addInsertMethod(topLevelClass);
        addInsertListMethod(topLevelClass);
        addUpdateSelectiveMethod(topLevelClass);
        addUpdateListMethod(topLevelClass);
        
        
        addSqlRepositoryMethod(topLevelClass);

        return topLevelClass;
    }

    /**
     * 根据主键删除
     */
    protected void addDeleteByPrimaryKeyMethod(JavaElement element) {
        AbstractDaoMapperMethodGenerator methodGenerator = new DeleteByKeyMethodGenerator();
        initializeAndExecuteGenerator(methodGenerator);
        if (element instanceof Interface) {
            methodGenerator.addInterfaceElements((Interface)element);
        } else if (element instanceof TopLevelClass){
            methodGenerator.addTopLevelClassElements((TopLevelClass)element);
        }
    }
    
    protected void addDeleteByListMethod(JavaElement element) {
        AbstractDaoMapperMethodGenerator methodGenerator = new DeleteListMethodGenerator();
        initializeAndExecuteGenerator(methodGenerator);
        if (element instanceof Interface) {
            methodGenerator.addInterfaceElements((Interface)element);
        } else if (element instanceof TopLevelClass){
            methodGenerator.addTopLevelClassElements((TopLevelClass)element);
        }
    }

    /**
     * 新增
     */
    protected void addInsertMethod(JavaElement element) {
        AbstractDaoMapperMethodGenerator methodGenerator = new InsertBeanMethodGenerator();
        initializeAndExecuteGenerator(methodGenerator);
        if (element instanceof Interface) {
            methodGenerator.addInterfaceElements((Interface)element);
        } else if (element instanceof TopLevelClass){
            methodGenerator.addTopLevelClassElements((TopLevelClass)element);
        }
    }

    protected void addInsertListMethod(JavaElement element) {
        AbstractDaoMapperMethodGenerator methodGenerator = new InsertListMethodGenerator();
        initializeAndExecuteGenerator(methodGenerator);
        if (element instanceof Interface) {
            methodGenerator.addInterfaceElements((Interface)element);
        } else if (element instanceof TopLevelClass){
            methodGenerator.addTopLevelClassElements((TopLevelClass)element);
        }
    }
    
    /**
     * 取最大id值， 当主键只有一个的时候
     */
    protected void addMaxIdMethod(JavaElement element) {
        AbstractDaoMapperMethodGenerator methodGenerator = new GetMaxIdClientGenerator();
        initializeAndExecuteGenerator(methodGenerator);
        if (element instanceof Interface) {
            methodGenerator.addInterfaceElements((Interface)element);
        } else if (element instanceof TopLevelClass){
            methodGenerator.addTopLevelClassElements((TopLevelClass)element);
        }
    }

    /**
     *
     */
    protected void addUpdateSelectiveMethod(JavaElement element) {
        AbstractDaoMapperMethodGenerator methodGenerator = new UpdateBeanMethodGenerator();
        initializeAndExecuteGenerator(methodGenerator);
        if (element instanceof Interface) {
            methodGenerator.addInterfaceElements((Interface)element);
        } else if (element instanceof TopLevelClass){
            methodGenerator.addTopLevelClassElements((TopLevelClass)element);
        }
    }
    
    protected void addUpdateListMethod(JavaElement element) {
        AbstractDaoMapperMethodGenerator methodGenerator = new UpdateListMethodGenerator();
        initializeAndExecuteGenerator(methodGenerator);
        if (element instanceof Interface) {
            methodGenerator.addInterfaceElements((Interface)element);
        } else if (element instanceof TopLevelClass){
            methodGenerator.addTopLevelClassElements((TopLevelClass)element);
        }
    }

    /**
     *
     */
    protected void addSelectByBeanMethod(JavaElement element) {
        AbstractDaoMapperMethodGenerator methodGenerator = new SelectBeanMethodGenerator();
        initializeAndExecuteGenerator(methodGenerator);
        if (element instanceof Interface) {
            methodGenerator.addInterfaceElements((Interface)element);
        } else if (element instanceof TopLevelClass){
            methodGenerator.addTopLevelClassElements((TopLevelClass)element);
        }
    }

    /**
     *
     */
    protected void addSelectByPrimaryKeyMethod(JavaElement element) {
        AbstractDaoMapperMethodGenerator methodGenerator = new SelectByKeyMethodGenerator();
        initializeAndExecuteGenerator(methodGenerator);
        if (element instanceof Interface) {
            methodGenerator.addInterfaceElements((Interface)element);
        } else if (element instanceof TopLevelClass){
            methodGenerator.addTopLevelClassElements((TopLevelClass)element);
        }
    }
    
    protected void addSelectByPrimaryKeyForUpdateMethod(JavaElement element) {
        AbstractDaoMapperMethodGenerator methodGenerator = new SelectByKeyForUpdateMethodGenerator();
        initializeAndExecuteGenerator(methodGenerator);
        if (element instanceof Interface) {
            methodGenerator.addInterfaceElements((Interface)element);
        } else if (element instanceof TopLevelClass){
            methodGenerator.addTopLevelClassElements((TopLevelClass)element);
        }
    }

    protected void addSelectByBeanPageMethod(JavaElement element) {
        AbstractDaoMapperMethodGenerator methodGenerator = new SelectByPageMethodGenerator();
        initializeAndExecuteGenerator(methodGenerator);
        if (element instanceof Interface) {
            methodGenerator.addInterfaceElements((Interface)element);
        } else if (element instanceof TopLevelClass){
            methodGenerator.addTopLevelClassElements((TopLevelClass)element);
        }
    }

    protected void addSelectCountMethod(JavaElement element) {
        AbstractDaoMapperMethodGenerator methodGenerator = new SelectCountMethodGenerator();
        initializeAndExecuteGenerator(methodGenerator);
        if (element instanceof Interface) {
            methodGenerator.addInterfaceElements((Interface)element);
        } else if (element instanceof TopLevelClass){
            methodGenerator.addTopLevelClassElements((TopLevelClass)element);
        }
    }

    /**
     *
     */
    protected void addSqlRepositoryMethod(TopLevelClass element) {
        SqlSessionFactoryMethodGenerator sqlSessionFactoryMethodGenerator = new SqlSessionFactoryMethodGenerator();
        sqlSessionFactoryMethodGenerator.addTopLevelClassElements(element);
    }
}
