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
package org.dbflute.testing;

import org.dbflute.bhv.readable.CBCall;
import org.dbflute.cbean.ConditionBean;
import org.dbflute.util.DfReflectionUtil;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.mockito.ArgumentMatcher;

/**
 * This is a helper to evaluate Behavior's lambda argument.
 *
 * <p>
 * This class is similar to {@link ArgumentMatcher}, but this is not an abstract class.
 * This matcher delegates evaluation to supplied {@link Matcher}
 * by calling {@link Matcher#matches(Object)} with captured ConditionBean.
 *
 * <p>
 * Consider to use {@link DBFluteMatchers#argCB(Class, Matcher)} in your convenience.
 * <pre class="code"><code class="java">
 * // stubbing
 * when(bhv.selectEntity(argThat(new BehaviorArgumentMatcher(MemberCB.class
 *         , hasCondition("memberId", equal(1)))))).thenReturn(fixture);
 *
 * // verification
 * verify(mockBhv).selectEntity(argThat(new BehaviorArgumentMatcher(MemberCB.class
 *         , hasCondition("memberId", equal(1)))));
 *
 * // convenient static method
 * when(bhv.selectEntity(argCB(MemberCB.class, hasCondition("memberId", equal(1))))).thenReturn(fixture);
 * verify(mockBhv).selectEntity(argCB(MemberCB.class, hasCondition("memberId", equal(1))));
 * </code></pre>

 * <h3>Details</h3>
 * Because it requires a instance of ConditionBean to evaluates Behavior's
 * lambda parameter, this matcher creates it by reflection.
 * Once created a ConditionBean instance, this matcher pass it to lambda to capture,
 * and then pass it to subsequent matcher.
 *
 * @param <T> the type of ConditionBean implementaion
 * @author taktos
 * @see DBFluteMatchers#argCB(Class, Matcher)
 */
public class BehaviorArgumentMatcher<T extends ConditionBean> extends BaseMatcher<CBCall<T>> {

    private final Class<T> clazz;
    private final Matcher<T> matcher;

    private T cb;

    /**
     * Creates a new instance that evaluates <code>cbclass</code> argument with <code>matcher</code>.
     * @param cbclass Class instance of ConditionBean implementation
     * @param matcher Matcher to evaluate ConditionBean conditions
     */
    public BehaviorArgumentMatcher(Class<T> cbclass, Matcher<T> matcher) {
        this.clazz = cbclass;
        this.matcher = matcher;
    }

    /**
     * Creates a new {@code T} instance by reflection and pass it to subsequent matcher.
     */
    @SuppressWarnings("unchecked")
    @Override
    public boolean matches(Object item) {
        if (item == null) {
            return false;
        }
        T cb = (T) DfReflectionUtil.newInstance(clazz);
        CBCall<T> callback = (CBCall<T>) item;
        callback.callback(cb);
        // store cb for mismatch
        this.cb = cb;

        return matcher.matches(cb);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void describeTo(Description description) {
        description.appendDescriptionOf(matcher);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void describeMismatch(Object item, Description description) {
        matcher.describeMismatch(cb, description);
    }

}
