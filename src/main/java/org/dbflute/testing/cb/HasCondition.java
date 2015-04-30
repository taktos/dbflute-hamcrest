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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.seasar.dbflute.cbean.ConditionBean;
import org.seasar.dbflute.cbean.ConditionQuery;
import org.seasar.dbflute.cbean.cvalue.ConditionValue;
import org.seasar.dbflute.helper.beans.DfBeanDesc;
import org.seasar.dbflute.helper.beans.exception.DfBeanPropertyNotFoundException;
import org.seasar.dbflute.helper.beans.factory.DfBeanDescFactory;

/**
 * This is the matcher that gets one {@link ConditionValue} from {@code T}
 * instance for further assertion.
 * <p>
 * Example:
 * <pre>{@code
 * MemberCB cb = ...;
 * cb.query().setMemberName_Equal("John Doe");
 *
 * assertThat(cb, hasCondition("memberName", equal("John Doe")));
 * }</pre>
 *
 * @param <T> the type of ConditionBean implementation
 * @author taktos
 *
 */
public class HasCondition<T extends ConditionBean> extends BaseMatcher<T> {

	protected final String column;
	protected final Matcher<?> matcher;

	public HasCondition(String column, Matcher<?> matcher) {
		this.column = column;
		this.matcher = matcher;
	}

	@Override
	public boolean matches(Object item) {
		if (item == null) {
			return false;
		}
		ConditionValue cv = getConditionValue(item, column);
		return matcher.matches(cv);
	}

	@Override
	public void describeTo(Description description) {
		description.appendText(column + " ");
		description.appendDescriptionOf(matcher);
	}

	@Override
	public void describeMismatch(Object item, Description description) {
		description.appendText(column + " ");
		ConditionValue cv = getConditionValue(item, column);
		matcher.describeMismatch(cv, description);

		List<String> values = new ArrayList<String>();
		flatten(cv.getFixed(), values);
		flatten(cv.getVarying(), values);

		description.appendValueList(". (Actual condition(s): ", ", ", ")", values);
	}

	private void flatten(Map<String, Map<String, Object>> map, List<String> flatten) {
		if (map == null) {
			return;
		}
		for (Map<String, Object> v : map.values()) {
			for (Map.Entry<String, Object> e : v.entrySet()) {
				flatten.add(e.getKey() + " " + e.getValue());
			}
		}
	}

	private ConditionValue getConditionValue(Object item, String column) {
		try {
			if (item instanceof ConditionBean){
				return getValue(((ConditionBean) item).localCQ(), column);
			} else if (item instanceof ConditionQuery) {
				return getValue((ConditionQuery) item, column);
			} else {
				throw new IllegalArgumentException("Not a valid argument: " + item);
			}
		} catch (DfBeanPropertyNotFoundException e) {
			throw new IllegalArgumentException("Column '" + column + "' does not exist.", e);
		}
	}

	private ConditionValue getValue(ConditionQuery cq, String column) {
		DfBeanDesc beanDesc = DfBeanDescFactory.getBeanDesc(cq.getClass());
		return (ConditionValue) beanDesc.getPropertyDesc(column).getValue(cq);
	}

	public static <T extends ConditionBean> HasCondition<T> hasCondition(String column, Matcher<?> matcher) {
		return new HasCondition<T>(column, matcher);
	}

}
