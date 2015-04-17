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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.seasar.dbflute.cbean.ConditionBean;
import org.seasar.dbflute.cbean.ConditionQuery;
import org.seasar.dbflute.cbean.cvalue.ConditionValue;
import org.seasar.dbflute.util.DfReflectionUtil;

/**
 * This is the matcher that gets one {@link ConditionValue} from {@code T}
 * instance for further assertion.
 *
 * @param <T> the type of ConditionBean implementation
 * @author taktos
 *
 */
public class HasCondition<T extends ConditionBean> extends BaseMatcher<T> {

	private String column;
	private Matcher<?> matcher;

	public HasCondition(String column, Matcher<?> matcher) {
		this.column = column;
		this.matcher = matcher;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean matches(Object item) {
		if (item == null) {
			return false;
		}
		ConditionValue cv;
		try {
			cv = getValue((T) item, column);
		} catch (NoSuchMethodException e) {
			throw new IllegalArgumentException(e);
		}
		return matcher.matches(cv);
	}

	@Override
	public void describeTo(Description description) {
		description.appendText(column + " ");
		description.appendDescriptionOf(matcher);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void describeMismatch(Object item, Description description) {
		description.appendText(column + " ");
		ConditionValue cv;
		try {
			cv = getValue((T) item, column);
		} catch (NoSuchMethodException e) {
			description.appendText("has no condition");
			return;
		}
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

	protected ConditionValue getValue(T cb, String column) throws NoSuchMethodException {
		String capitalName = Character.toString(column.charAt(0)).toUpperCase() + column.substring(1);
		ConditionQuery cq = cb.localCQ();

		Method method;
		try {
			method = cq.getClass().getMethod("xdfget" + capitalName);
		} catch (NoSuchMethodException e) {
			// retry with old naming rule
			try {
				method = cq.getClass().getMethod("get" + capitalName);
			} catch (NoSuchMethodException oe) {
				// retry with another naming rule
				try {
					method = cq.getClass().getMethod("xget" + capitalName);
				} catch (NoSuchMethodException xoe) {
					throw e;
				}
			}
		}
		return (ConditionValue) DfReflectionUtil.invoke(method, cq, null);
	}

	public static <T extends ConditionBean> HasCondition<T> hasCondition(String column, Matcher<?> matcher) {
		return new HasCondition<T>(column, matcher);
	}
}
