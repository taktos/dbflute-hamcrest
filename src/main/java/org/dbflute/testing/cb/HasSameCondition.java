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
import org.seasar.dbflute.cbean.ConditionBean;

/**
 * Matches when the argument has same conditions.
 * It compares whole SQL by {@link ConditionBean#toDisplaySql()}.
 *
 * @param <T> the type of ConditionBean implementation
 * @author taktos
 *
 */
public class HasSameCondition<T extends ConditionBean> extends BaseMatcher<T> {

    private final T cb;

    public HasSameCondition(T cb) {
        this.cb = cb;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean matches(Object item) {
        return cb.toDisplaySql().equals(((T) item).toDisplaySql());
    }

    @Override
    public void describeTo(Description description) {
        description.appendValue(cb.toDisplaySql());
    }

    @SuppressWarnings("unchecked")
    @Override
    public void describeMismatch(Object item, Description description) {
        description.appendValue(((T) item).toDisplaySql());
    }

}
