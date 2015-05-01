/*
 * Copyright 2015 Toshio Takiguchi.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.dbflute.testing.matcher;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;

import org.dbflute.cbean.ConditionBean;
import org.dbflute.cbean.sqlclause.SqlClause;
import org.dbflute.dbmeta.DBMeta;
import org.dbflute.dbmeta.info.ForeignInfo;
import org.dbflute.exception.DBMetaNotFoundException;
import org.dbflute.helper.beans.DfBeanDesc;
import org.dbflute.helper.beans.factory.DfBeanDescFactory;
import org.dbflute.util.DfReflectionUtil;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

/**
 * The matcher that evaluates the ConditionBean should be specified to select a column.
 *
 * @author taktos
 *
 */
public class ShouldSelect<T extends ConditionBean> extends BaseMatcher<T> {

    private final String columnName;

    public ShouldSelect(String columnName) {
        this.columnName = columnName;
    }

    @Override
    public boolean matches(Object item) {
        if (item == null || !(item instanceof ConditionBean)) {
            return false;
        }
        ConditionBean cb = (ConditionBean) item;

        String[] nesting = columnName.split("\\.");
        if (nesting.length == 1) {
            return assertLocalSpecify(cb, nesting[0]);
        }

        String targetTable = resolveForeignTableName(cb.asDBMeta(), nesting);
        String targetColumn = nesting[nesting.length - 1];

        try {
            return assertForeignSpecify(cb, targetTable, targetColumn);
        } catch (Exception e) {
            throw new IllegalStateException("Not supported version of dbflute-runtime.", e);
        }
    }

    private String resolveForeignTableName(DBMeta meta, String[] foreignTree) {
        ForeignInfo foreignInfo = findForeignInfo(meta, foreignTree[0]);
        if (foreignTree.length == 2) {
            return foreignInfo.getForeignPropertyName();
        }
        return resolveForeignTableName(foreignInfo.getForeignDBMeta(), Arrays.copyOfRange(foreignTree, 1, foreignTree.length));
    }

    private ForeignInfo findForeignInfo(DBMeta meta, String foreignTable) {
        try {
            return meta.findForeignInfo(foreignTable);
        } catch (DBMetaNotFoundException e) {
            try {
                return meta.findForeignInfo(foreignTable + "AsOne");
            } catch (DBMetaNotFoundException ne) {
                throw new IllegalArgumentException("Table '" + foreignTable + "' does not exist");
            }
        }
    }

    private boolean assertLocalSpecify(ConditionBean cb, String columnName) {
        DBMeta meta = cb.asDBMeta();
        return !cb.hasSpecifiedColumn() || cb.localSp().isSpecifiedColumn(meta.findDbName(columnName));
    }

    @SuppressWarnings("unchecked")
    private boolean assertForeignSpecify(ConditionBean cb, String table, String column) throws IllegalAccessException {
        SqlClause sqlClause = cb.getSqlClause();
        String relationName = resolveRelationName(sqlClause, table);
        // not joined. should fail
        if (relationName == null) {
            return false;
        }

        String tableAlias = sqlClause.translateSelectedRelationPathToTableAlias(relationName);
        DfBeanDesc beanDesc = DfBeanDescFactory.getBeanDesc(sqlClause.getClass());
        Field field = beanDesc.getField("_specifiedSelectColumnMap");
        field.setAccessible(true);
        Map<String, Map<String, Map<String, ?>>> specifyMap = (Map<String, Map<String, Map<String, ?>>>) field.get(sqlClause);
        // no specification. return true because it will get all columns
        if (specifyMap == null) {
            return true;
        }

        Map<String, Map<String, ?>> map = specifyMap.get(tableAlias);
        // not specified with the table
        if (map == null) {
            return true;
        }
        return map.containsKey(column);
    }

    @SuppressWarnings("unchecked")
    private String resolveRelationName(SqlClause sqlClause, String table) {
        Method method = DfReflectionUtil.getAccessibleMethod(sqlClause.getClass(), "getSelectedRelationBasicMap", null);
        Map<String, String> baseMap = (Map<String, String>) DfReflectionUtil.invokeForcedly(method, sqlClause, null);
        for (Map.Entry<String, String> entry : baseMap.entrySet()) {
            if (entry.getValue().equals(table)) {
                return entry.getKey();
            }
        }
        return null;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText(columnName + " should select");
    }

    /**
     * Creates a matcher that matches if the query selects the column.
     * @param columnName the name of column
     */
    public static <T extends ConditionBean> ShouldSelect<T> shouldSelect(String columnName) {
        return new ShouldSelect<T>(columnName);
    }
}
