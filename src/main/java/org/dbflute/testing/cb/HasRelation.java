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

import java.lang.reflect.Method;

import org.dbflute.cbean.ConditionBean;
import org.dbflute.cbean.ConditionQuery;
import org.dbflute.util.DfReflectionUtil;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

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

	@SuppressWarnings("unchecked")
	@Override
	public boolean matches(Object item) {
		ConditionQuery cq;
		try {
			cq = getCQ((T) item, table);
		} catch (NoSuchMethodException e) {
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
		subsequent.describeMismatch(item, description);
	}

	/**
	 * Gets ConditionQuery for related table {@code table}.
	 * @param cb the instance of ConditionBean
	 * @param table the name of relation table
	 * @return query for {@code table}
	 * @throws NoSuchMethodException no getter method for {@code table}
	 */
	protected ConditionQuery getCQ(T cb, String table) throws NoSuchMethodException {
		String capitalName = Character.toString(table.charAt(0)).toUpperCase() + table.substring(1);
		ConditionQuery cq = cb.localCQ();

		Method method = cq.getClass().getMethod("xdfgetConditionQuery" + capitalName);
		return (ConditionQuery) DfReflectionUtil.invoke(method, cq, null);
	}

	public static <T extends ConditionBean> HasRelation<T> hasRelation(String table, HasCondition<T> relationCondition) {
		return new HasRelation<T>(table, relationCondition);
	}
}
