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
package org.mybatis.generator.ext.xmlmapper;

import static org.mybatis.generator.internal.util.messages.Messages.getString;

import org.mybatis.generator.api.FullyQualifiedTable;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.AbstractXmlGenerator;
import org.mybatis.generator.codegen.XmlConstants;
import org.mybatis.generator.codegen.mybatis3.xmlmapper.elements.AbstractXmlElementGenerator;
import org.mybatis.generator.codegen.mybatis3.xmlmapper.elements.BlobColumnListElementGenerator;
import org.mybatis.generator.ext.xmlmapper.elements.BaseColumnListElementGenerator;
import org.mybatis.generator.ext.xmlmapper.elements.DeleteByKeyElementGenerator;
import org.mybatis.generator.ext.xmlmapper.elements.DeleteByListElementGenerator;
import org.mybatis.generator.ext.xmlmapper.elements.GetMaxIdElementGenerator;
import org.mybatis.generator.ext.xmlmapper.elements.InsertElementGenerator;
import org.mybatis.generator.ext.xmlmapper.elements.InsertListElementGenerator;
import org.mybatis.generator.ext.xmlmapper.elements.InsertSelectiveElementGenerator;
import org.mybatis.generator.ext.xmlmapper.elements.ResultMapWithBLOBsElementGenerator;
import org.mybatis.generator.ext.xmlmapper.elements.ResultMapWithoutBLOBsElementGenerator;
import org.mybatis.generator.ext.xmlmapper.elements.SelectByBeanElementGenerator;
import org.mybatis.generator.ext.xmlmapper.elements.SelectByBeanPageElementGenerator;
import org.mybatis.generator.ext.xmlmapper.elements.SelectByKeyElementForUpdateGenerator;
import org.mybatis.generator.ext.xmlmapper.elements.SelectByKeyElementGenerator;
import org.mybatis.generator.ext.xmlmapper.elements.SelectCountElementGenerator;
import org.mybatis.generator.ext.xmlmapper.elements.SelectSqlWhereElementGenerator;
import org.mybatis.generator.ext.xmlmapper.elements.UpdateByBeanPKElementGenerator;

/**
 */
public class XMLExtMapperGenerator extends AbstractXmlGenerator {

    public XMLExtMapperGenerator() {
        super();
    }

    protected XmlElement getSqlMapElement() {
        FullyQualifiedTable table = introspectedTable.getFullyQualifiedTable();
        progressCallback.startTask(getString(
                "Progress.12", table.toString())); //$NON-NLS-1$
        XmlElement answer = new XmlElement("mapper"); //$NON-NLS-1$
        String namespace = introspectedTable.getMyBatis3SqlMapNamespace();
        answer.addAttribute(new Attribute("namespace", //$NON-NLS-1$
                namespace));

        context.getCommentGenerator().addRootComment(answer);

        addResultMapWithoutBLOBsElement(answer);
        addResultMapWithBLOBsElement(answer);
        addSelectSqlWhereElement(answer);
        addBaseColumnListElement(answer);
        addBlobColumnListElement(answer);
        
        addSelectMaxKeyElement(answer);
        addSelectByPrimaryKeyElement(answer);
        addSelectByPrimaryKeyForUpdateElement(answer);
        addSelectByBeanElement(answer);
        addSelectByBeanPageElement(answer);
        addSelectCountElement(answer);
        addDeleteByPrimaryKeyElement(answer);
        addDeleteByListElement(answer);
        addInsertElement(answer);
        addInsertListElement(answer);
        //addInsertSelectiveElement(answer);
        addUpdateByPrimaryKeySelectiveElement(answer);
//        addUpdateByPrimaryKeyWithBLOBsElement(answer);
//        addUpdateByPrimaryKeyWithoutBLOBsElement(answer);

        return answer;
    }

    protected void addBlobColumnListElement(XmlElement parentElement) {
        if (introspectedTable.getRules().generateBlobColumnList()) {
            AbstractXmlElementGenerator elementGenerator = new BlobColumnListElementGenerator();
            initializeAndExecuteGenerator(elementGenerator, parentElement);
        }
    }
    
    protected void addResultMapWithoutBLOBsElement(XmlElement parentElement) {
        if (!introspectedTable.getRules().generateBaseResultMap())
            return;

        AbstractXmlElementGenerator elementGenerator = new ResultMapWithoutBLOBsElementGenerator(false);
        initializeAndExecuteGenerator(elementGenerator, parentElement);
    }

    protected void addResultMapWithBLOBsElement(XmlElement parentElement) {
        if (!introspectedTable.getRules().generateResultMapWithBLOBs())
            return;

        AbstractXmlElementGenerator elementGenerator = new ResultMapWithBLOBsElementGenerator();
        initializeAndExecuteGenerator(elementGenerator, parentElement);
    }

    protected void addBaseColumnListElement(XmlElement parentElement) {
        if (!introspectedTable.getRules().generateBaseColumnList())
            return;

        AbstractXmlElementGenerator elementGenerator = new BaseColumnListElementGenerator();
        initializeAndExecuteGenerator(elementGenerator, parentElement);
    }

    /**
     * 生成 <sql id="select_where_sql">  <if test="uid != null and uid != ''"> 类似的格式
     */
    protected void addSelectSqlWhereElement(XmlElement parentElement) {
        AbstractXmlElementGenerator elementGenerator = new SelectSqlWhereElementGenerator();
        initializeAndExecuteGenerator(elementGenerator, parentElement);
    }

    /**
     */
    protected void addSelectByBeanElement(XmlElement parentElement) {
        AbstractXmlElementGenerator elementGenerator = new SelectByBeanElementGenerator();
        initializeAndExecuteGenerator(elementGenerator, parentElement);
    }

    /**
     */
    protected void addSelectCountElement(XmlElement parentElement) {
        AbstractXmlElementGenerator elementGenerator = new SelectCountElementGenerator();
        initializeAndExecuteGenerator(elementGenerator, parentElement);
    }

//    protected void addBlobColumnListElement(XmlElement parentElement) {
//        if (!introspectedTable.getRules().generateBlobColumnList())
//            return;
//
//        AbstractXmlElementGenerator elementGenerator = new BlobColumnListElementGenerator();
//        initializeAndExecuteGenerator(elementGenerator, parentElement);
//    }
    
    protected void addSelectMaxKeyElement(XmlElement parentElement) {
        if (!introspectedTable.getRules().generateSelectByPrimaryKey())
            return;

        AbstractXmlElementGenerator elementGenerator = new GetMaxIdElementGenerator();
        initializeAndExecuteGenerator(elementGenerator, parentElement);
    }

    protected void addSelectByPrimaryKeyElement(XmlElement parentElement) {
        if (!introspectedTable.getRules().generateSelectByPrimaryKey())
            return;

        AbstractXmlElementGenerator elementGenerator = new SelectByKeyElementGenerator();
        initializeAndExecuteGenerator(elementGenerator, parentElement);
    }
    
    protected void addSelectByPrimaryKeyForUpdateElement(XmlElement parentElement) {
        if (!introspectedTable.getRules().generateSelectByPrimaryKey())
            return;

        AbstractXmlElementGenerator elementGenerator = new SelectByKeyElementForUpdateGenerator();
        initializeAndExecuteGenerator(elementGenerator, parentElement);
    }

    /**
     *
     */
    protected void addSelectByBeanPageElement(XmlElement parentElement) {
        AbstractXmlElementGenerator elementGenerator = new SelectByBeanPageElementGenerator();
        initializeAndExecuteGenerator(elementGenerator, parentElement);
    }

    protected void addDeleteByPrimaryKeyElement(XmlElement parentElement) {
        if (!introspectedTable.getRules().generateDeleteByPrimaryKey())
            return;

        AbstractXmlElementGenerator elementGenerator = new DeleteByKeyElementGenerator(false);
        initializeAndExecuteGenerator(elementGenerator, parentElement);
    }

    protected void addDeleteByListElement(XmlElement parentElement) {
        if (!introspectedTable.getRules().generateDeleteByPrimaryKey())
            return;

        AbstractXmlElementGenerator elementGenerator = new DeleteByListElementGenerator(false);
        initializeAndExecuteGenerator(elementGenerator, parentElement);
    }

    
    protected void addInsertElement(XmlElement parentElement) {
        if (!introspectedTable.getRules().generateInsert())
            return;

        AbstractXmlElementGenerator elementGenerator = new InsertElementGenerator(false);
        initializeAndExecuteGenerator(elementGenerator, parentElement);
    }

    
    protected void addInsertListElement(XmlElement parentElement) {
        if (!introspectedTable.getRules().generateInsert())
            return;

        AbstractXmlElementGenerator elementGenerator = new InsertListElementGenerator(false);
        initializeAndExecuteGenerator(elementGenerator, parentElement);
    }
    
    
    protected void addInsertSelectiveElement(XmlElement parentElement) {
        if (!introspectedTable.getRules().generateInsertSelective())
            return;

        AbstractXmlElementGenerator elementGenerator = new InsertSelectiveElementGenerator();
        initializeAndExecuteGenerator(elementGenerator, parentElement);
    }

    protected void addUpdateByPrimaryKeySelectiveElement(
            XmlElement parentElement) {
        if (!introspectedTable.getRules().generateUpdateByPrimaryKeySelective())
            return;

        AbstractXmlElementGenerator elementGenerator = new UpdateByBeanPKElementGenerator();
        initializeAndExecuteGenerator(elementGenerator, parentElement);
    }

//    protected void addUpdateByPrimaryKeyWithBLOBsElement(
//            XmlElement parentElement) {
//        if (introspectedTable.getRules().generateUpdateByPrimaryKeyWithBLOBs())
//            return;
//
//        AbstractXmlElementGenerator elementGenerator = new UpdateByPrimaryKeyWithBLOBsElementGenerator();
//        initializeAndExecuteGenerator(elementGenerator, parentElement);
//    }

//    protected void addUpdateByPrimaryKeyWithoutBLOBsElement(
//            XmlElement parentElement) {
//        if (introspectedTable.getRules().generateUpdateByPrimaryKeyWithoutBLOBs()) {
//            AbstractXmlElementGenerator elementGenerator = new UpdateByPrimaryKeyWithoutBLOBsElementGenerator(false);
//            initializeAndExecuteGenerator(elementGenerator, parentElement);
//        }
//    }

    protected void initializeAndExecuteGenerator(
            AbstractXmlElementGenerator elementGenerator,
            XmlElement parentElement) {
        elementGenerator.setContext(context);
        elementGenerator.setIntrospectedTable(introspectedTable);
        elementGenerator.setProgressCallback(progressCallback);
        elementGenerator.setWarnings(warnings);
        elementGenerator.addElements(parentElement);
    }

    @Override
    public Document getDocument() {
        Document document = new Document(
                XmlConstants.MYBATIS3_MAPPER_PUBLIC_ID,
                XmlConstants.MYBATIS3_MAPPER_SYSTEM_ID);
        document.setRootElement(getSqlMapElement());

        if (!context.getPlugins().sqlMapDocumentGenerated(document,
                introspectedTable)) {
            document = null;
        }

        return document;
    }
}

