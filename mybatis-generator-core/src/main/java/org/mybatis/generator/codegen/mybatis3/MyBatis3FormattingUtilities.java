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
package org.mybatis.generator.codegen.mybatis3;

import static org.mybatis.generator.internal.util.StringUtility.escapeStringForJava;
import static org.mybatis.generator.internal.util.StringUtility.stringHasValue;

import org.mybatis.generator.api.IntrospectedColumn;

/**
 * The Class MyBatis3FormattingUtilities.
 *
 * @author Jeff Butler
 */
public class MyBatis3FormattingUtilities {
    
    /**
     * Utility class - no instances.
     */
    private MyBatis3FormattingUtilities() {
        super();
    }

    /**
     * Gets the parameter clause.
     *
     * @param introspectedColumn
     *            the introspected column
     * @return the parameter clause
     */
    public static String getParameterClause(
            IntrospectedColumn introspectedColumn) {
        return getParameterClause(introspectedColumn, null);
    }

    /**
     * 
     * @param introspectedColumn
     * @param prefix
     * @return
     */
    public static String getParameterExtendClause(
            IntrospectedColumn introspectedColumn, String prefix, String postfix) {
        StringBuilder sb = new StringBuilder();

        sb.append("#{"); //$NON-NLS-1$
        sb.append(introspectedColumn.getJavaProperty(prefix, postfix));
        sb.append(",jdbcType="); //$NON-NLS-1$
        sb.append(introspectedColumn.getJdbcTypeName());

        if (stringHasValue(introspectedColumn.getTypeHandler())) {
            sb.append(",typeHandler="); //$NON-NLS-1$
            sb.append(introspectedColumn.getTypeHandler());
        }

        sb.append('}');

        return sb.toString();
    }
    
    public static String getParameterClauseJdbcType(
            IntrospectedColumn introspectedColumn, String prefix) {
        StringBuilder sb = new StringBuilder();

        sb.append("#{"); //$NON-NLS-1$
        sb.append(introspectedColumn.getJavaProperty(prefix));

        sb.append('}');

        return sb.toString();
    }
    
    /**
     * Gets the parameter clause.
     *
     * @param introspectedColumn
     *            the introspected column
     * @param prefix
     *            the prefix
     * @return the parameter clause
     */
    public static String getParameterClause(
            IntrospectedColumn introspectedColumn, String prefix) {
        StringBuilder sb = new StringBuilder();

        sb.append("#{"); //$NON-NLS-1$
        sb.append(introspectedColumn.getJavaProperty(prefix));
        sb.append(",jdbcType="); //$NON-NLS-1$
        sb.append(introspectedColumn.getJdbcTypeName());

        if (stringHasValue(introspectedColumn.getTypeHandler())) {
            sb.append(",typeHandler="); //$NON-NLS-1$
            sb.append(introspectedColumn.getTypeHandler());
        }

        sb.append('}');

        return sb.toString();
    }

    /**
     * The phrase to use in a select list. If there is a table alias, the value will be
     * "alias.columnName as alias_columnName"
     *
     * @param introspectedColumn
     *            the introspected column
     * @return the proper phrase
     */
    public static String getSelectListPhrase(
            IntrospectedColumn introspectedColumn) {
        if (stringHasValue(introspectedColumn.getTableAlias())) {
            StringBuilder sb = new StringBuilder();

            sb.append(getAliasedEscapedColumnName(introspectedColumn));
            sb.append(" as "); //$NON-NLS-1$
            if (introspectedColumn.isColumnNameDelimited()) {
                sb.append(introspectedColumn.getContext()
                        .getBeginningDelimiter());
            }
            sb.append(introspectedColumn.getTableAlias());
            sb.append('_');
            sb.append(escapeStringForMyBatis3(introspectedColumn
                    .getActualColumnName()));
            if (introspectedColumn.isColumnNameDelimited()) {
                sb.append(introspectedColumn.getContext().getEndingDelimiter());
            }
            return sb.toString();
        } else {
            return getEscapedColumnName(introspectedColumn);
        }
    }

    /**
     * Gets the escaped column name.
     *
     * @param introspectedColumn
     *            the introspected column
     * @return the escaped column name
     */
    public static String getEscapedColumnName(
            IntrospectedColumn introspectedColumn) {
        StringBuilder sb = new StringBuilder();
        sb.append(escapeStringForMyBatis3(introspectedColumn
                .getActualColumnName()));

        if (introspectedColumn.isColumnNameDelimited()) {
            sb.insert(0, introspectedColumn.getContext()
                    .getBeginningDelimiter());
            sb.append(introspectedColumn.getContext().getEndingDelimiter());
        }

        return sb.toString();
    }

    /**
     * Calculates the string to use in select phrases in SqlMaps.
     *
     * @param introspectedColumn
     *            the introspected column
     * @return the aliased escaped column name
     */
    public static String getAliasedEscapedColumnName(
            IntrospectedColumn introspectedColumn) {
        if (stringHasValue(introspectedColumn.getTableAlias())) {
            StringBuilder sb = new StringBuilder();

            sb.append(introspectedColumn.getTableAlias());
            sb.append('.');
            sb.append(getEscapedColumnName(introspectedColumn));
            return sb.toString();
        } else {
            return getEscapedColumnName(introspectedColumn);
        }
    }

    /**
     * The aliased column name for a select statement generated by the example clauses. This is not appropriate for
     * selects in SqlMaps because the column is not escaped for MyBatis. If there is a table alias, the value will be
     * alias.columnName.
     * 
     * This method is used in the Example classes and the returned value will be in a Java string. So we need to escape
     * double quotes if they are the delimiters.
     *
     * @param introspectedColumn
     *            the introspected column
     * @return the aliased column name
     */
    public static String getAliasedActualColumnName(
            IntrospectedColumn introspectedColumn) {
        StringBuilder sb = new StringBuilder();
        if (stringHasValue(introspectedColumn.getTableAlias())) {
            sb.append(introspectedColumn.getTableAlias());
            sb.append('.');
        }

        if (introspectedColumn.isColumnNameDelimited()) {
            sb.append(escapeStringForJava(introspectedColumn
                    .getContext().getBeginningDelimiter()));
        }

        sb.append(introspectedColumn.getActualColumnName());

        if (introspectedColumn.isColumnNameDelimited()) {
            sb.append(escapeStringForJava(introspectedColumn
                    .getContext().getEndingDelimiter()));
        }

        return sb.toString();
    }

    /**
     * The renamed column name for a select statement. If there is a table alias, the value will be alias_columnName.
     * This is appropriate for use in a result map.
     *
     * @param introspectedColumn
     *            the introspected column
     * @return the renamed column name
     */
    public static String getRenamedColumnNameForResultMap(
            IntrospectedColumn introspectedColumn) {
        if (stringHasValue(introspectedColumn.getTableAlias())) {
            StringBuilder sb = new StringBuilder();

            sb.append(introspectedColumn.getTableAlias());
            sb.append('_');
            sb.append(introspectedColumn.getActualColumnName());
            return sb.toString();
        } else {
            return introspectedColumn.getActualColumnName();
        }
    }

    /**
     * Escape string for my batis3.
     *
     * @param s
     *            the s
     * @return the string
     */
    public static String escapeStringForMyBatis3(String s) {
        // nothing to do for MyBatis3 so far
        return s;
    }
}
