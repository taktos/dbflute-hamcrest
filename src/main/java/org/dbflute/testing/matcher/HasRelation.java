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

import java.util.Arrays;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.seasar.dbflute.cbean.ConditionBean;
import org.seasar.dbflute.cbean.ConditionQuery;
import org.seasar.dbflute.dbmeta.DBMeta;
import org.seasar.dbflute.exception.DBMetaNotFoundException;
import org.seasar.dbflute.helper.beans.DfBeanDesc;
import org.seasar.dbflute.helper.beans.exception.DfBeanPropertyNotFoundException;
import org.seasar.dbflute.helper.beans.factory.DfBeanDescFactory;

/**
 * The matcher that gets relation table condition from {@code T} for further assertion.
 *
 * <p>
 * Examle:
 * <pre>{@code
 * MemberCB cb = ...;
 * cb.query().queryMemberStatus().setMemberStatusCode_Equal("ACT");
 *
 * assertThat(cb, hasRelation("memberStatus", hasCondition("memberStatusCode", equal("ACT"))));
 * }</pre>
 * @author taktos
 *
 */
public class HasRelation<T extends ConditionBean> extends BaseMatcher<T> {

    private final String table;
    private final HasCondition<T> subsequent;

    public HasRelation(String table, HasCondition<T> subsequent) {
        this.table = table;
        this.subsequent = subsequent;
    }

    @Override
    public boolean matches(Object item) {
        if (item == null || !(item instanceof ConditionBean)) {
            return false;
        }
        String[] tables = table.split("\\.");
        ConditionQuery cq;
        try {
            cq = getCQ(((ConditionBean) item).localCQ(), tables);
        } catch (DBMetaNotFoundException e) {
            throw new IllegalArgumentException("No relation table '" + table + "' found.", e);
        }
        return subsequent.matches(cq);
    }

    @Override
    public void describeTo(Description description) {
        description.appendText(table + ".");
        subsequent.describeTo(description);
    }

    @Override
    public void describeMismatch(Object item, Description description) {
        description.appendText(table + ".");
        subsequent.describeMismatch(getCQ(((ConditionBean) item).localCQ(), table.split("\\.")), description);
    }

    /**
     * Gets ConditionQuery for related table {@code table}.
     * @param cq the instance of ConditionQuery
     * @param tables names of relation tables
     * @return query for {@code table}
     * @throws DfBeanPropertyNotFoundException no getter method for {@code table}
     */
    protected ConditionQuery getCQ(ConditionQuery cq, String[] tables) {
        DBMeta meta = MatcherHelper.getDBMeta(cq);
        String foreignPropertyName = meta.findForeignInfo(tables[0]).getForeignPropertyName();
        DfBeanDesc beanDesc = DfBeanDescFactory.getBeanDesc(cq.getClass());
        ConditionQuery nested = (ConditionQuery) beanDesc.getPropertyDesc("conditionQuery" + foreignPropertyName).getValue(cq);
        if (tables.length == 1) {
            return nested;
        }
        return getCQ(nested, Arrays.copyOfRange(tables, 1, tables.length));
    }

    public static <T extends ConditionBean> HasRelation<T> hasRelation(String table, HasCondition<T> relationCondition) {
        return new HasRelation<T>(table, relationCondition);
    }
}
