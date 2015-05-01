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

import org.dbflute.cbean.cvalue.ConditionValue;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

/**
 * Abstract class of matchers that evaluates the column condition value.
 * This matcher is used with {@link HasCondition} to test one column condition
 * of ConditionBean.
 *
 * <p>
 * This matcher instance will receive a {@link ConditionValue} instance
 * from {@link HasCondition}.
 * @author taktos
 *
 */
public class IsColumnExpressed extends BaseMatcher<ConditionValue> {

    private final ComparisonOperator operator;
    private final Matcher<?> valueMatcher;

    public IsColumnExpressed(ComparisonOperator operator, Matcher<?> matcher) {
        this.operator = operator;
        this.valueMatcher = matcher;
    }

    @Override
    public boolean matches(Object item) {
        ConditionValue cv = (ConditionValue) item;
        Object value = operator.getValue(cv);
        return valueMatcher.matches(value);
    }

    @Override
    public void describeTo(Description description) {
        description.appendText(operator.sign()).appendText(" ");
        description.appendDescriptionOf(valueMatcher);
    }

    @Override
    public void describeMismatch(Object item, Description description) {
        Object value;
        try {
            value = operator.getValue((ConditionValue) item);
        } catch (RuntimeException e) {
            description.appendText("has no ").appendText(operator.name()).appendText(" condition");
            return;
        }
        valueMatcher.describeMismatch(value, description);
    }

    public static IsColumnExpressed expressed(ComparisonOperator operator, Matcher<?> valueMatcher) {
        return new IsColumnExpressed(operator, valueMatcher);
    }
}
