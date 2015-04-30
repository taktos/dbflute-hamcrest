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
package org.dbflute.testing.cb;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.seasar.dbflute.cbean.ConditionBean;
import org.seasar.dbflute.cbean.sqlclause.SqlClause;

/**
 * Matches when the ConditionBean has any where clause.
 *
 * @author taktos
 *
 */
public class HasWhereClause<T extends ConditionBean> extends BaseMatcher<T> {

	@Override
	public boolean matches(Object item) {
		if (item instanceof ConditionBean) {
			return hasWhereClause((ConditionBean) item);
		}
		return false;
	}

	protected boolean hasWhereClause(ConditionBean cb) {
		SqlClause sqlClause = cb.localCQ().xgetSqlClause();
		return sqlClause.hasBaseTableInlineWhereClause()
				|| sqlClause.hasOuterJoinInlineWhereClause()
				|| sqlClause.hasWhereClauseOnBaseQuery();
	}

	@Override
	public void describeTo(Description description) {
		description.appendText("any where clause");
	}

	public static <T extends ConditionBean> Matcher<T> hasWhereClause() {
		return new HasWhereClause<T>();
	}
}
