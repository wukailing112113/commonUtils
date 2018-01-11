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
package org.mybatis.generator.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.config.Context;
import org.mybatis.generator.exception.ShellException;
import org.mybatis.generator.internal.util.JavaBeansUtil;

import java.io.File;
import java.util.*;

import static org.mybatis.generator.internal.util.JavaBeansUtil.getGetterMethodName;
import static org.mybatis.generator.internal.util.messages.Messages.getString;

/**
 */
public class GenUtil {

    public static final String MAX_ID_METHODNAME = "maxIdMethodName";
    public static final String SELECT_BY_BEAN_METHODNAME = "selectByBeanMethodName";
    public static final String SELECT_BY_PRIMARYKEY_METHODNAME = "selectByPKMethodName";
    public static final String INSERT_BEAN_METHODNAME = "insertBeanMethodName";
    public static final String INSERT_BEAN_SELECTIVE_METHODNAME = "insertBeanSelMethodName";
    public static final String DELETE_BY_PRIMARYKEY_METHODNAME = "deleteByPKMethodName";
    public static final String SELECT_COUNT_METHODNAME = "selectCountMethodName";
    public static final String SELECT_BY_BEAN_PAGE_METHODNAME = "selectByBeanPageMethodName";
    public static final String UPDATE_BY_BEAN_METHODNAME = "updateByBeanMethodName";
    public static final String UPDATE_BY_BEANLIST_METHODNAME = "updateByBeanListMethodName";

    public static final String XML_MAX_ID_METHODNAME = "XML_maxIdMethodName";
    public static final String XML_SELECT_BY_BEAN_METHODNAME = "XML_selectByBeanMethodName";
    public static final String XML_SELECT_BY_PRIMARYKEY_METHODNAME = "XML_selectByPKMethodName";
    public static final String XML_INSERT_BEAN_METHODNAME = "XML_insertBeanMethodName";
    public static final String XML_INSERT_BEAN_SELECTIVE_METHODNAME = "XML_insertBeanSelectiveMethodName";
    public static final String XML_DELETE_BY_PRIMARYKEY_METHODNAME = "XML_deleteByPKMethodName";
    public static final String XML_SELECT_COUNT_METHODNAME = "XML_selectCountMethodName";
    public static final String XML_SELECT_BY_BEAN_PAGE_METHODNAME = "XML_selectByBeanPageMethodName";
    public static final String XML_UPDATE_BY_BEAN_METHODNAME = "XML_updateByBeanMethodName";
    public static final String XML_UPDATE_BY_BEANLIST_METHODNAME = "XML_updateByBeanListMethodName";

    public static final String GEN_COUNT_BY_PAGE = "genCountByPage";

    public  static enum ENUM_METHOD_TYPE {
        XML_TYPE,
        DAO_TYPE,
        REDIS_TYPE
    };
    /**
     * @param context  上下文
     * @param introspectedTable 表结构
     * @return EntityBean的类名类型
     */
    public static FullyQualifiedJavaType getEntityType(Context context, IntrospectedTable introspectedTable) {
        FullyQualifiedJavaType entityType = new FullyQualifiedJavaType(context.getJavaModelGeneratorConfiguration().getTargetPackage() + "." + introspectedTable.getFullyQualifiedTable().getDomainObjectName());
        return entityType;
    }

    /**
     * @return 取最大值的method name
     */
    public static String getMaxIdMethodName(IntrospectedTable introspectedTable, ENUM_METHOD_TYPE method_type) {
        String propKey = method_type == ENUM_METHOD_TYPE.DAO_TYPE? MAX_ID_METHODNAME:XML_MAX_ID_METHODNAME;
        String config = introspectedTable.getTableConfiguration().getProperty(propKey);
        if (config != null)
            return config;

        return "selectMax" + introspectedTable.getFullyQualifiedTable().getDomainObjectName() + "Id";
    }

    /**
     * @return 根据bean条件取 的method name
     */
    public static String getSelectByBeanMethodName(IntrospectedTable introspectedTable, ENUM_METHOD_TYPE method_type) {
        String propKey = method_type == ENUM_METHOD_TYPE.DAO_TYPE? SELECT_BY_BEAN_METHODNAME:XML_SELECT_BY_BEAN_METHODNAME;
        String config = introspectedTable.getTableConfiguration().getProperty(propKey);
        if (config != null)
            return config;

        return "select" + introspectedTable.getFullyQualifiedTable().getDomainObjectName() + "s"; //"select" + introspectedTable.getFullyQualifiedTable().getDomainObjectName() + "List";
    }
    
    public static String getSelectByPageMethodName(IntrospectedTable introspectedTable, ENUM_METHOD_TYPE method_type) {
        String propKey = method_type == ENUM_METHOD_TYPE.DAO_TYPE? SELECT_BY_BEAN_METHODNAME:XML_SELECT_BY_BEAN_METHODNAME;
        String config = introspectedTable.getTableConfiguration().getProperty(propKey);
        if (config != null)
            return config;

        return "select" + introspectedTable.getFullyQualifiedTable().getDomainObjectName() + "ByPage"; //"select" + introspectedTable.getFullyQualifiedTable().getDomainObjectName() + "List";
    }
    
    

    public static String getSelectForUpdateMethodName(IntrospectedTable introspectedTable, ENUM_METHOD_TYPE method_type) {
        String propKey = method_type == ENUM_METHOD_TYPE.DAO_TYPE? SELECT_BY_BEAN_METHODNAME:XML_SELECT_BY_BEAN_METHODNAME;
        String config = introspectedTable.getTableConfiguration().getProperty(propKey);
        if (config != null)
            return config;

        return "select" + introspectedTable.getFullyQualifiedTable().getDomainObjectName() + "sForUpdate"; //"select" + introspectedTable.getFullyQualifiedTable().getDomainObjectName() + "List";
    }
    
    public static String getSelectByKeyMethodName(IntrospectedTable introspectedTable, ENUM_METHOD_TYPE method_type) {
        String propKey = method_type == ENUM_METHOD_TYPE.DAO_TYPE? SELECT_BY_PRIMARYKEY_METHODNAME:XML_SELECT_BY_PRIMARYKEY_METHODNAME;
        String config = introspectedTable.getTableConfiguration().getProperty(propKey);
        if (config != null)
            return config;

        return "get" + introspectedTable.getFullyQualifiedTable().getDomainObjectName() + "ByKey";// 
    }

    public static String getSelectByIndexMethodName(IntrospectedTable introspectedTable, ENUM_METHOD_TYPE method_type) {
        String propKey = method_type == ENUM_METHOD_TYPE.DAO_TYPE? SELECT_BY_PRIMARYKEY_METHODNAME:XML_SELECT_BY_PRIMARYKEY_METHODNAME;
        String config = introspectedTable.getTableConfiguration().getProperty(propKey);
        if (config != null)
            return config;

        return "get" + introspectedTable.getFullyQualifiedTable().getDomainObjectName() + "By";// 
    }
    
    /**
     * @return 分页方法method Name
     */
    public static String getSelectByBeanPageMethodName(IntrospectedTable introspectedTable, ENUM_METHOD_TYPE method_type) {
        String propKey = method_type == ENUM_METHOD_TYPE.DAO_TYPE? SELECT_BY_BEAN_PAGE_METHODNAME:XML_SELECT_BY_BEAN_PAGE_METHODNAME;
        String config = introspectedTable.getTableConfiguration().getProperty(propKey);
        if (config != null)
            return config;

        return "select" + introspectedTable.getFullyQualifiedTable().getDomainObjectName() + "ByPage";
    }

    /**
     * @return 查询数量
     */
    public static String getSelectCountMethodName(IntrospectedTable introspectedTable, ENUM_METHOD_TYPE method_type) {
        String propKey = method_type == ENUM_METHOD_TYPE.DAO_TYPE? SELECT_COUNT_METHODNAME:XML_SELECT_COUNT_METHODNAME;
        String config = introspectedTable.getTableConfiguration().getProperty(propKey);
        if (config != null)
            return config;

        return "getTotal" + introspectedTable.getFullyQualifiedTable().getDomainObjectName();//return "select" + introspectedTable.getFullyQualifiedTable().getDomainObjectName() + "Count";
    }

    /**
     * @return 插入语句method Name
     */
    public static String getAddBeanMethodName(IntrospectedTable introspectedTable, ENUM_METHOD_TYPE method_type) {
        String propKey = method_type == ENUM_METHOD_TYPE.DAO_TYPE? INSERT_BEAN_METHODNAME:XML_INSERT_BEAN_METHODNAME;
        String config = introspectedTable.getTableConfiguration().getProperty(propKey);
        if (config != null)
            return config;

        return "add" + introspectedTable.getFullyQualifiedTable().getDomainObjectName();
    }

    /**
     * @return 插入语句method Name
     */
    public static String getAddListMethodName(IntrospectedTable introspectedTable, ENUM_METHOD_TYPE method_type) {
        String propKey = method_type == ENUM_METHOD_TYPE.DAO_TYPE? INSERT_BEAN_METHODNAME:XML_INSERT_BEAN_METHODNAME;
        String config = introspectedTable.getTableConfiguration().getProperty(propKey);
        if (config != null)
            return config;

        return "addList" + introspectedTable.getFullyQualifiedTable().getDomainObjectName();
    }

    /**
     * @return 插入语句method Name
     */
    public static String getInsertBeanSelectiveMethodName(IntrospectedTable introspectedTable, ENUM_METHOD_TYPE method_type) {
        String propKey = method_type == ENUM_METHOD_TYPE.DAO_TYPE? INSERT_BEAN_SELECTIVE_METHODNAME:XML_INSERT_BEAN_SELECTIVE_METHODNAME;
        String config = introspectedTable.getTableConfiguration().getProperty(propKey);
        if (config != null)
            return config;

        return "add" + introspectedTable.getFullyQualifiedTable().getDomainObjectName();
    }

    public static String getInsertListMethodName(IntrospectedTable introspectedTable, ENUM_METHOD_TYPE method_type) {
        String propKey = method_type == ENUM_METHOD_TYPE.DAO_TYPE? DELETE_BY_PRIMARYKEY_METHODNAME:XML_DELETE_BY_PRIMARYKEY_METHODNAME;
        String config = introspectedTable.getTableConfiguration().getProperty(propKey);
        if (config != null)
            return config;

        if (method_type == ENUM_METHOD_TYPE.REDIS_TYPE) {
            return "add" + introspectedTable.getFullyQualifiedTable().getDomainObjectName();
        }
        return "add" + introspectedTable.getFullyQualifiedTable().getDomainObjectName() + "List";
    }
    
    /**
     * @return 根据主键删除的method Name
     */
    public static String getDeleteByKeyMethodName(IntrospectedTable introspectedTable, ENUM_METHOD_TYPE method_type) {
        String propKey = method_type == ENUM_METHOD_TYPE.DAO_TYPE? DELETE_BY_PRIMARYKEY_METHODNAME:XML_DELETE_BY_PRIMARYKEY_METHODNAME;
        String config = introspectedTable.getTableConfiguration().getProperty(propKey);
        if (config != null)
            return config;

        if (method_type == ENUM_METHOD_TYPE.REDIS_TYPE) {
            return "delete" + introspectedTable.getFullyQualifiedTable().getDomainObjectName();
        }
        return "delete" + introspectedTable.getFullyQualifiedTable().getDomainObjectName() + "ByKey";
    }
    
    /**
     * @return 根据主键删除的method Name
     */
    public static String getDeleteByListMethodName(IntrospectedTable introspectedTable, ENUM_METHOD_TYPE method_type) {
        String propKey = method_type == ENUM_METHOD_TYPE.DAO_TYPE? DELETE_BY_PRIMARYKEY_METHODNAME:XML_DELETE_BY_PRIMARYKEY_METHODNAME;
        String config = introspectedTable.getTableConfiguration().getProperty(propKey);
        if (config != null)
            return config;

        if (method_type == ENUM_METHOD_TYPE.REDIS_TYPE) {
            return "deleteList" + introspectedTable.getFullyQualifiedTable().getDomainObjectName();
        }
        return "delete" + introspectedTable.getFullyQualifiedTable().getDomainObjectName() + "ByList";
    }

    /**
     * @return 更新语句methodName
     */
    public static String getUpdateByBeanMethodName(IntrospectedTable introspectedTable, ENUM_METHOD_TYPE method_type) {
        String propKey = method_type == ENUM_METHOD_TYPE.DAO_TYPE? UPDATE_BY_BEAN_METHODNAME:XML_UPDATE_BY_BEAN_METHODNAME;
        String config = introspectedTable.getTableConfiguration().getProperty(propKey);
        if (config != null)
            return config;

        return "update" + introspectedTable.getFullyQualifiedTable().getDomainObjectName();
    }

    /**
     * @return 更新批量语句methodName  默认为update
     */
    public static String getUpdateListMethodName(IntrospectedTable introspectedTable, ENUM_METHOD_TYPE method_type) {
        String propKey = method_type == ENUM_METHOD_TYPE.DAO_TYPE? UPDATE_BY_BEANLIST_METHODNAME:XML_UPDATE_BY_BEANLIST_METHODNAME;
        String config = introspectedTable.getTableConfiguration().getProperty(propKey);
        if (config != null)
            return config;

        return "updateList" + introspectedTable.getFullyQualifiedTable().getDomainObjectName();
    }

    /**
     * @return xml文件里面的查询sql的id名字
     */
    public static String genSqlWhereExpression(IntrospectedTable introspectedTable) {
        return "select_where_sql";
    }

    /**
     * 获取实体类的名称
     * @param introspectedTable
     * @return
     */
    public static String getEntityName(IntrospectedTable introspectedTable) {
        return introspectedTable.getFullyQualifiedTable().getDomainObjectName();
    }

    public static String getGeneralEntityParamName(IntrospectedTable introspectedTable) {
        String entityName = introspectedTable.getFullyQualifiedTable().getDomainObjectName();
        return StringUtil.uncapitalize(entityName);
    }

    public static String getCollectionEntityParamName(String prefix, IntrospectedTable introspectedTable) {
        String entityName = introspectedTable.getFullyQualifiedTable().getDomainObjectName();
        //return StringUtil.uncapitalize(entityName);
        return prefix + entityName ;
    }
    
    /**
     * @return 获取entityBean的主键，作为函数的参数的形式的字符串。比如返回 “subsId, prodId”
     */
    public static String getPrimaryKeyJavaParaName(IntrospectedTable introspectedTable) {
        List<String> listPk = getPrimaryKeyJavaParaNameInArray(introspectedTable);
        return StringUtil.join(listPk.toArray(), ",");
    }

    /**
     * @return 获取entityBean的主键，作为函数的参数的形式的数组。 比如返回[subsId, prodId]
     */
    public static List<String> getPrimaryKeyJavaParaNameInArray(IntrospectedTable introspectedTable) {
        if (introspectedTable.getContext().getProperty("pk_" + introspectedTable.getFullyQualifiedTable().getDomainObjectName()) != null) {
            String pkParaName = introspectedTable.getContext().getProperty("pk_" + introspectedTable.getFullyQualifiedTable().getDomainObjectName());

            return new ArrayList(Arrays.asList(pkParaName.split(",")));
        }

        List<String> pkNameList = new ArrayList();
        for (IntrospectedColumn introspectedColumn: introspectedTable.getPrimaryKeyColumns()) {
            pkNameList.add(introspectedColumn.getJavaProperty());
        }

        return pkNameList;
    }

    public static String getGeneralEntityParamName4List(IntrospectedTable introspectedTable) {
        String entityName = introspectedTable.getFullyQualifiedTable().getDomainObjectName();
        return StringUtil.uncapitalize(entityName) + "s";
    }

    public static String getGeneralEntityParamName4ListWithList(IntrospectedTable introspectedTable) {
        String entityName = introspectedTable.getFullyQualifiedTable().getDomainObjectName();
        return "list" + StringUtil.capitalize(entityName);
    }

    /**
     * @param introspectedTable 表bean
     * @return  根据 IntrospectedTable 获取DaoImpl类的全路径字符串
     */
    public static String getDaoImplClassFullName(IntrospectedTable introspectedTable) {
        String daoImpl =  introspectedTable.getDAOImplementationType();
        if (!daoImpl.contains(".impl.")) {
            int firstLastIndex = daoImpl.lastIndexOf(".");
            int secLastIndex = daoImpl.substring(0, firstLastIndex).lastIndexOf(".");
            daoImpl = daoImpl.substring(0, secLastIndex) + ".impl." + daoImpl.substring(secLastIndex + 1).replace("DAO", "Dao");
            introspectedTable.setDAOImplementationType(daoImpl);
        }

        return introspectedTable.getDAOImplementationType();
    }

    /**
     * @param introspectedTable 表bean
     * @return  根据 IntrospectedTable 获取RedisImpl类的全路径字符串
     */
    public static String getDaoInterfaceFullName(IntrospectedTable introspectedTable) {
        String daoIntf = introspectedTable.getDAOInterfaceType();
        if (!daoIntf.contains(".intf.")) {
            int firstLastIndex = daoIntf.lastIndexOf(".");
            String daoName = daoIntf.substring(firstLastIndex + 1);
            int secLastIndex = daoIntf.substring(0, firstLastIndex).lastIndexOf(".");
            daoIntf = daoIntf.substring(0, secLastIndex) + ".intf." + daoIntf.substring(secLastIndex + 1, firstLastIndex) + ".I" + daoName.replace("DAO", "Dao");
            introspectedTable.setDAOInterfaceType(daoIntf);
        }

        return daoIntf;
    }

    //获取redis的类名全路径
    public static String getRedisClassFullName(IntrospectedTable introspectedTable) {
        String daoImpl =  introspectedTable.getDAOImplementationType();
        if (!daoImpl.contains(".impl.")) {
            getDaoImplClassFullName(introspectedTable);
        }

        return daoImpl.replace(".dao.", ".cache.").replace("DaoImpl", "RedisImpl");
    }

    //获取redis的类名全路径
    public static String getRedisInterfaceFullName(IntrospectedTable introspectedTable) {
        String daoIntf = introspectedTable.getDAOInterfaceType();
        if (!daoIntf.contains(".intf.")) {
            getDaoInterfaceFullName(introspectedTable);
        }

        return daoIntf.replace(".dao.", ".cache.").replace("Dao", "Redis");
    }

    //获取service的类名全路径
    public static String getServiceClassFullName(IntrospectedTable introspectedTable, String basePackage) {
        String daoImpl =  introspectedTable.getDAOImplementationType();
        if (!daoImpl.contains(".impl.")) {
            getDaoImplClassFullName(introspectedTable);
        }

        String ret = daoImpl.replace(".dao.", ".service.").replace("DaoImpl", "ServiceImpl");
        if (StringUtil.isEmptyString(basePackage))
            return ret;
        int indexService = ret.indexOf(".service.");
        return new StringBuffer().append(basePackage).append(ret.substring(indexService)).toString();
    }

    //获取service的类名全路径
    public static String getServiceInterfaceFullName(IntrospectedTable introspectedTable, String basePackage) {
        String daoIntf = introspectedTable.getDAOInterfaceType();
        if (!daoIntf.contains(".intf.")) {
            getDaoInterfaceFullName(introspectedTable);
        }

        String ret = daoIntf.replace(".dao.", ".service.").replace("Dao", "Service");
        if (StringUtil.isEmptyString(basePackage))
            return ret;
        int indexService = ret.indexOf(".service.");
        return new StringBuffer().append(basePackage).append(ret.substring(indexService)).toString();
    }

    //目前用于redisMapper
    public static String getGetBeanIdMethodName(IntrospectedTable introspectedTable, ENUM_METHOD_TYPE method_type) {
        String entityName = introspectedTable.getFullyQualifiedTable().getDomainObjectName();
        return "get" + StringUtil.capitalize(entityName) + "Id";
    }

    //目前用于redisMapper
    public static String getClearAllBeanMethodName(IntrospectedTable introspectedTable, ENUM_METHOD_TYPE method_type) {
        String entityName = introspectedTable.getFullyQualifiedTable().getDomainObjectName();
        return "clearAll" + StringUtil.capitalize(entityName);
    }

    //目前用于redisMapper
    public static String getInitBeanMaxIdMethodName(IntrospectedTable introspectedTable, ENUM_METHOD_TYPE redisType) {
        //return "initMax" + StringUtil.capitalize(introspectedTable.getPrimaryKeyColumns().get(0).getJavaProperty());
    	return "initMax" +  introspectedTable.getFullyQualifiedTable().getDomainObjectName() + "Id";
    }

    //目前用于redisMapper
    public static String getLoaAllBeanMethodName(IntrospectedTable introspectedTable, ENUM_METHOD_TYPE redisType) {
        return "loadAll" + introspectedTable.getFullyQualifiedTable().getDomainObjectName() ;
    }

    /**
     * @return redis ftl主文件夹目录的路径, <property>中可以配置
     */
    public static String getRedisFtlPath(Context context, ENUM_METHOD_TYPE method_type) {
        if (context.getProperty("redisFtlPath") != null) {
            return context.getProperty("redisFtlPath");
        }
        return GenUtil.class.getClassLoader().getResource("org/mybatis/generator/ext/redismapper").getPath();
    }

    /**
     * @return redis ftl主文件夹目录的路径, <property>中可以配置
     */
    public static String getServiceFtlPath(Context context, ENUM_METHOD_TYPE method_type) {
        if (context.getProperty("svrFtlPath") != null) {
            return context.getProperty("svrFtlPath");
        }
        return GenUtil.class.getClassLoader().getResource("org/mybatis/generator/ext/servicemapper").getPath();
    }

    public static String getGetBeanIdKeyMethodName(IntrospectedTable introspectedTable) {
        String entityName = introspectedTable.getFullyQualifiedTable().getDomainObjectName();
        return "get" + StringUtil.capitalize(entityName) + "Key";
    }

    /**
     *  获取唯一的一个文件名， 如果有现有文件t.txt则返回  t.txt.1
     * @param directory
     *            the directory
     * @param fileName
     *            the file name
     * @return the unique file name
     */
    public static File getUniqueFileName(File directory, String fileName) {
        File answer = null;

        // try up to 1000 times to generate a unique file name
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < 1000; i++) {
            sb.setLength(0);
            sb.append(fileName);
            sb.append('.');
            sb.append(i);

            File testFile = new File(directory, sb.toString());
            if (!testFile.exists()) {
                answer = testFile;
                break;
            }
        }

        if (answer == null) {
            throw new RuntimeException(getString(
                    "RuntimeError.3", directory.getAbsolutePath())); //$NON-NLS-1$
        }

        return answer;
    }

    public File getDirectory(String targetProject, String targetPackage) throws Exception  {

        File project = new File(targetProject);
        if (!project.isDirectory()) {
            throw new Exception("target Project is Not Directory");
        }

        StringBuilder sb = new StringBuilder();
        StringTokenizer st = new StringTokenizer(targetPackage, "."); //$NON-NLS-1$
        while (st.hasMoreTokens()) {
            sb.append(st.nextToken());
            sb.append(File.separatorChar);
        }

        File directory = new File(project, sb.toString());
        if (!directory.isDirectory()) {
            boolean rc = directory.mkdirs();
            if (!rc) {
                throw new ShellException(getString("Warning.10", //$NON-NLS-1$
                        directory.getAbsolutePath()));
            }
        }

        return directory;
    }

    //返回 包含要产生 in / not in ..的列表
    public static Set<String> getConfig4SelectInColumList(IntrospectedTable introspectedTable, boolean isExclude) {
        Set<String> colIdListSet = new HashSet<String>();
        String column4IdListStr = introspectedTable.getTableConfigurationProperty(isExclude?"selectNotInColumList":"selectInColumList");
        if (StringUtil.isNotEmptyString(column4IdListStr)) {
            JSONArray jsonArray = JSON.parseArray(column4IdListStr);
            colIdListSet.addAll(Arrays.asList(jsonArray.toArray(new String[0])));
        }

        return colIdListSet;
    }

    //返回 {"list":[], "map": {"store":{"field":"storeMin", "oper":">"}}, ... }
    public static Map getOperatorColumns(IntrospectedTable introspectedTable) {
        Set<Map> colIdListSet = new HashSet<Map>();
        String column4IdListStr = introspectedTable.getTableConfigurationProperty("operColumnList");
        if (StringUtil.isNotEmptyString(column4IdListStr)) {
            Map map = (Map)JSON.parseObject(column4IdListStr, Map.class);
            return map;
        }

        return null;
    }

    public static String getConfig4MaxIdRelateColum(IntrospectedTable introspectedTable) {
        String column4IdListStr = introspectedTable.getTableConfigurationProperty("maxIdRelateColum");
        return column4IdListStr;
    }
    
    public static String getConfig4ParentColum(IntrospectedTable introspectedTable) {
        String column4IdListStr = introspectedTable.getTableConfigurationProperty("parentId");
        return column4IdListStr;
    }
    
    public static String getConfig4AddtinalColum(IntrospectedTable introspectedTable) {
        String additionStr = introspectedTable.getTableConfigurationProperty("addition");
        return additionStr;
    }
    
    
    public static String getConfig4MaxIdPrefix(IntrospectedTable introspectedTable) {
        String column4IdListStr = introspectedTable.getTableConfigurationProperty("maxIdPrefix");
        return column4IdListStr;
    }
    
    public static String getConfig4MaxIdPostfix(IntrospectedTable introspectedTable) {
        String column4IdListStr = introspectedTable.getTableConfigurationProperty("maxIdPostfix");
        return column4IdListStr;
    }

    /**
     */
    public static Field getJavaBeansField4ColumnList(IntrospectedColumn introspectedColumn, boolean isExclude) {
        FullyQualifiedJavaType fqjt = introspectedColumn
                .getFullyQualifiedJavaType();
        FullyQualifiedJavaType listType = new FullyQualifiedJavaType("List<" + fqjt.getShortName() + ">");

        String fieldName = introspectedColumn.getJavaProperty() + (isExclude?"ExcludeList":"List");
        Field field = new Field();
        field.setVisibility(JavaVisibility.PRIVATE);
        field.setType(listType);
        field.setTransient(true);
        field.setName(fieldName);

        return field;
    }

    /**
     * 加入 in (xx, xx, xx,, )
     * @param introspectedColumn
     * @return
     */
    public static Method getJavaBeansGetter4ColumnList(IntrospectedColumn introspectedColumn, boolean isExclude) {
        FullyQualifiedJavaType fqjt = introspectedColumn
                .getFullyQualifiedJavaType();
        FullyQualifiedJavaType listType = new FullyQualifiedJavaType("List<" + fqjt.getShortName() + ">");
        String fieldName = introspectedColumn.getJavaProperty() + (isExclude?"ExcludeList":"List");

        Method method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setReturnType(listType);
        method.setName(getGetterMethodName(fieldName, fqjt));

        StringBuilder sb = new StringBuilder();
        sb.append("return ");
        sb.append(fieldName);
        sb.append(';');
        method.addBodyLine(sb.toString());

        return method;
    }

    public static Method getJavaBeansSetter4ColumnList(IntrospectedColumn introspectedColumn, boolean isExclude) {
        FullyQualifiedJavaType fqjt = introspectedColumn
                .getFullyQualifiedJavaType();
        FullyQualifiedJavaType listType = new FullyQualifiedJavaType("List<" + fqjt.getShortName() + ">");
        String fieldName = introspectedColumn.getJavaProperty() + (isExclude?"ExcludeList":"List");

        Method method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setName(JavaBeansUtil.getSetterMethodName(fieldName));
        method.addParameter(new Parameter(listType, fieldName));

        StringBuilder sb = new StringBuilder();
        sb.append("this.");
        sb.append(fieldName);
        sb.append(" = ");
        sb.append(fieldName);
        sb.append(';');
        method.addBodyLine(sb.toString());

        return method;
    }

    public static Method getJavaBeansSetter4ColumnExcludeList(IntrospectedColumn introspectedColumn, boolean isExclude) {
        FullyQualifiedJavaType fqjt = introspectedColumn
                .getFullyQualifiedJavaType();
        FullyQualifiedJavaType listType = new FullyQualifiedJavaType("List<" + fqjt.getShortName() + ">");
        String fieldName = introspectedColumn.getJavaProperty() + (isExclude?"ExcludeList":"List");

        Method method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setName(JavaBeansUtil.getSetterMethodName(fieldName));
        method.addParameter(new Parameter(listType, fieldName));

        StringBuilder sb = new StringBuilder();
        sb.append("this.");
        sb.append(fieldName);
        sb.append(" = ");
        sb.append(fieldName);
        sb.append(';');
        method.addBodyLine(sb.toString());

        return method;
    }

    //返回 包含要产生 > 的列表
    public static Set<String> getConfig4MinColumList(IntrospectedTable introspectedTable, String colName) {
        Set<String> colIdListSet = new HashSet<String>();
        String column4IdListStr = introspectedTable.getTableConfigurationProperty("selectMinColumList");
        if (StringUtil.isNotEmptyString(column4IdListStr)) {
            JSONArray jsonArray = JSON.parseArray(column4IdListStr);
            colIdListSet.addAll(Arrays.asList(jsonArray.toArray(new String[0])));
        }

        return colIdListSet;
    }

    /**
     * 判断表接口是否是 数字型的 唯一主键
     * @param introspectedTable
     * @return
     */
    public static boolean isPrimaryKeyOnlyOneInTable(IntrospectedTable introspectedTable) {
        return introspectedTable.getPrimaryKeyColumns() != null &&
                introspectedTable.getPrimaryKeyColumns().size() == 1 &&
                !"String".equals(
                introspectedTable.getPrimaryKeyColumns().get(0).getFullyQualifiedJavaType().getShortName()
                );
    }
}
