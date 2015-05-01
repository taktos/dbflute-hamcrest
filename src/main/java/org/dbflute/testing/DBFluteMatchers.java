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
import org.dbflute.testing.matcher.ComparisonOperator;
import org.dbflute.testing.matcher.HasCondition;
import org.dbflute.testing.matcher.HasRelation;
import org.dbflute.testing.matcher.IsColumnExpressed;
import org.dbflute.testing.matcher.IsColumnIsNotNull;
import org.dbflute.testing.matcher.IsColumnIsNull;
import org.dbflute.testing.matcher.ShouldSelect;
import org.dbflute.testing.mock.BehaviorArgumentCaptor;
import org.dbflute.testing.mock.BehaviorArgumentMatcher;
import org.hamcrest.Matcher;
import org.hamcrest.core.IsCollectionContaining;
import org.hamcrest.core.IsEqual;

/**
 * Static factory of custom matchers.
 *
 * @author taktos
 *
 */
public final class DBFluteMatchers {

    private DBFluteMatchers() {
    }

    /**
     * Creates a method argument captor.
     * @param clazz the class of ConditionBean implementation
     * @return captor
     */
    public static <T extends ConditionBean> BehaviorArgumentCaptor<T> captor(Class<T> clazz) {
        return new BehaviorArgumentCaptor<T>(clazz);
    }

    /**
     * Allows creating custom argument matcher that evaluates ConditionBean.
     * @param cbclass class of ConditionBean implementation
     * @param matcher the matcher to apply to ConditionBean
     * @return <code>null</code>
     * @see org.mockito.Matchers#argThat(Matcher)
     */
    public static <T extends ConditionBean> CBCall<T> argCB(Class<T> cbclass, Matcher<T> matcher) {
        return org.mockito.Matchers.argThat(cb(cbclass, matcher));
    }

    /**
     * Creates an argument matcher that evaluates ConditionBean.
     * @param cbclass class of ConditionBean implementation
     * @param matcher the matcher to apply to ConditionBean
     * @return argument matcher
     */
    public static <T extends ConditionBean> BehaviorArgumentMatcher<T> cb(Class<T> cbclass, Matcher<T> matcher) {
        return new BehaviorArgumentMatcher<T>(cbclass, matcher);
    }

    /**
     * Creates a matcher that gets a {@link org.dbflute.cbean.cvalue.ConditionValue} of specified column
     * and pass it to subsequent matcher.
     * @param column the name of column which evaluates
     * @param matcher the matcher that evaluates {@link org.dbflute.cbean.cvalue.ConditionValue}
     */
    public static <T extends ConditionBean> HasCondition<T> hasCondition(String column, Matcher<?> matcher) {
        return new HasCondition<T>(column, matcher);
    }

    /**
     * Creates a matcher that gets a {@link org.dbflute.cbean.ConditionQuery} of specified table
     * and pass it to subsequent matcher.
     * @param table the name of relating table which evaluates
     * @param hasCondition the matcher that evaluates {@link org.dbflute.cbean.ConditionQuery}
     */
    public static <T extends ConditionBean> HasRelation<T> hasRelation(String table, HasCondition<T> hasCondition) {
        return new HasRelation<T>(table, hasCondition);
    }

    /**
     * A shortcut to {@code hasRelation("table", hasCondition("column", ...))}.
     *
     * @param table the name of relating table
     * @param column the name of column which evaluates
     * @param matcher the matcher that evaluates {@link org.dbflute.cbean.cvalue.ConditionValue}
     */
    public static <T extends ConditionBean> HasRelation<T> hasRelationCondition(String table, String column, Matcher<?> matcher) {
        return new HasRelation<T>(table, new HasCondition<T>(column, matcher));
    }

    /**
     * Creates a matcher that matches when the examined column has
     * EQUAL condition with value matched with the specified {@code matcher}.
     * <p>Example:
     * <pre>{@code
     * cb.query.setMemberName_Equal("John Doe");
     * assertThat(cb, hasCondition("memberName", equal(startsWith("J"))));
     * }</pre>
     *
     * @param matcher a matcher that evaluates the condition value
     */
    public static IsColumnExpressed equal(Matcher<?> matcher) {
        return IsColumnExpressed.expressed(ComparisonOperator.EQUAL, matcher);
    }

    /**
     * A shortcut to {@code equal(equalTo(value))}.
     * <p>Example:
     * <pre>{@code
     * cb.query.setMemberId_Equal(10);
     * assertThat(cb, hasCondition("memberId", equal(10)));
     * }</pre>
     * @param value the value of condition
     */
    public static IsColumnExpressed equal(Object value) {
        return equal(IsEqual.equalTo(value));
    }

    /**
     * Creates a matcher that matches when the examined column has
     * GREATER_THAN condition with value matched with the specified {@code matcher}.
     * <p>Example:
     * <pre>{@code
     * cb.query.setMemberName_GreaterThan("John Doe");
     * assertThat(cb, hasCondition("memberName", greaterThan(startsWith("J"))));
     * }</pre>
     *
     * @param matcher a matcher that evaluates the condition value
     */
    public static IsColumnExpressed greaterThan(Matcher<?> matcher) {
        return IsColumnExpressed.expressed(ComparisonOperator.GREATER_THAN, matcher);
    }

    /**
     * A shortcut to {@code greaterThan(equalTo(value))}.
     * <p>Example:
     * <pre>{@code
     * cb.query.setMemberId_GreaterThan(10);
     * assertThat(cb, hasCondition("memberId", greaterThan(10)));
     * }</pre>
     * @param value the value of condition
     */
    public static IsColumnExpressed greaterThan(Object value) {
        return greaterThan(IsEqual.equalTo(value));
    }

    /**
     * Creates a matcher that matches when the examined column has
     * GREATER_EQUAL condition with value matched with the specified {@code matcher}.
     * <p>Example:
     * <pre>{@code
     * cb.query.setMemberName_GreaterEqual("John Doe");
     * assertThat(cb, hasCondition("memberName", greaterEqual(startsWith("J"))));
     * }</pre>
     *
     * @param matcher a matcher that evaluates the condition value
     */
    public static IsColumnExpressed greaterEqual(Matcher<?> matcher) {
        return IsColumnExpressed.expressed(ComparisonOperator.GREATER_EQUAL, matcher);
    }

    /**
     * A shortcut to {@code greaterEqual(equalTo(value))}.
     * <p>Example:
     * <pre>{@code
     * cb.query.setMemberId_GreaterEqual(10);
     * assertThat(cb, hasCondition("memberId", greaterEqual(10)));
     * }</pre>
     * @param value the value of condition
     */
    public static IsColumnExpressed greaterEqual(Object value) {
        return greaterEqual(IsEqual.equalTo(value));
    }

    /**
     * Creates a matcher that matches when the examined column has
     * GREATER_THAN condition with value matched with the specified {@code matcher}.
     * <p>Example:
     * <pre>{@code
     * cb.query.setMemberName_LessThan("John Doe");
     * assertThat(cb, hasCondition("memberName", lessThan(startsWith("J"))));
     * }</pre>
     *
     * @param matcher a matcher that evaluates the condition value
     */
    public static IsColumnExpressed lessThan(Matcher<?> matcher) {
        return IsColumnExpressed.expressed(ComparisonOperator.LESS_THAN, matcher);
    }

    /**
     * A shortcut to {@code lessThan(equalTo(value))}.
     * <p>Example:
     * <pre>{@code
     * cb.query.setMemberId_LessThan(10);
     * assertThat(cb, hasCondition("memberId", lessThan(10)));
     * }</pre>
     * @param value the value of condition
     */
    public static IsColumnExpressed lessThan(Object value) {
        return lessThan(IsEqual.equalTo(value));
    }

    /**
     * Creates a matcher that matches when the examined column has
     * LESS_EQUAL condition with value matched with the specified {@code matcher}.
     * <p>Example:
     * <pre>{@code
     * cb.query.setMemberName_LessEqual("John Doe");
     * assertThat(cb, hasCondition("memberName", lessEqual(startsWith("J"))));
     * }</pre>
     *
     * @param matcher a matcher that evaluates the condition value
     */
    public static IsColumnExpressed lessEqual(Matcher<?> matcher) {
        return IsColumnExpressed.expressed(ComparisonOperator.LESS_EQUAL, matcher);
    }

    /**
     * A shortcut to {@code lessEqual(equalTo(value))}.
     * <p>Example:
     * <pre>{@code
     * cb.query.setMemberId_LessEqual(10);
     * assertThat(cb, hasCondition("memberId", lessEqual(10)));
     * }</pre>
     * @param value the value of condition
     */
    public static IsColumnExpressed lessEqual(Object value) {
        return lessEqual(IsEqual.equalTo(value));
    }

    /**
     * Creates a matcher that matches when the examined column has
     * IN condition with value matched with the specified {@code matcher}.
     * <p>Example:
     * <pre>{@code
     * cb.query().setMemberName_InScope(Arrays.asList("John Doe", "Jane Doe"));
     * assertThat(cb, hasCondition("memberName", in(contains("John Doe", "Jane Doe"))));
     * }</pre>
     * @param matcher a matcher that evaluates the condition value
     */
    public static IsColumnExpressed in(Matcher<?> matcher) {
        return IsColumnExpressed.expressed(ComparisonOperator.IN, matcher);
    }

    /**
     * A shortcut to {@code in(hasItems(items))}.
     * <p>Example:
     * <pre>{@code
     * cb.query().setMemberName_InScope(Arrays.asList("John Doe", "Jane Doe"));
     * assertThat(cb, hasCondition("memberName", in("John Doe", "Jane Doe")));
     * }</pre>
     * @param items the value of condition
     */
    public static IsColumnExpressed in(Object... items) {
        return in(IsCollectionContaining.hasItems(items));
    }

    /**
     * Creates a matcher that matches when the examined column has
     * NOT_IN condition with value matched with the specified {@code matcher}.
     * <p>Example:
     * <pre>{@code
     * cb.query().setMemberName_NotInScope(Arrays.asList("John Doe", "Jane Doe"));
     * assertThat(cb, hasCondition("memberName", notIn(contains("John Doe", "Jane Doe"))));
     * }</pre>
     * @param matcher a matcher that evaluates the condition value
     */
    public static IsColumnExpressed notIn(Matcher<?> matcher) {
        return IsColumnExpressed.expressed(ComparisonOperator.NOT_IN, matcher);
    }

    /**
     * A shortcut to {@code notIn(contains(items))}.
     * <p>Example:
     * <pre>{@code
     * cb.query().setMemberName_NotInScope(Arrays.asList("John Doe", "Jane Doe"));
     * assertThat(cb, hasCondition("memberName", notIn("John Doe", "Jane Doe")));
     * }</pre>
     * @param items the value of condition
     */
    public static IsColumnExpressed notIn(Object... items) {
        return notIn(IsCollectionContaining.hasItems(items));
    }

    /**
     * Creates a matcher that matches when the examined column has
     * LIKE condition with value matched with the specified {@code matcher}.
     * <p>Example:
     * <pre>{@code
     * cb.query().setMemberName_LikeSearch("John", op -> op.likePrefix());
     * assertThat(cb, hasCondition("memberName", like(equalTo("John%"))));
     * }</pre>
     * @param matcher a matcher that evaluates the condition value
     */
    public static IsColumnExpressed like(Matcher<?> matcher) {
        return IsColumnExpressed.expressed(ComparisonOperator.LIKE, matcher);
    }

    /**
     * A shortcut to {@code like(equalTo(value))}.
     * <p>Example:
     * <pre>{@code
     * cb.query().setMemberName_LikeSearch("John", op -> op.likePrefix());
     * assertThat(cb, hasCondition("memberName", like("John%")));
     * }</pre>
     * @param value the value of condition
     */
    public static IsColumnExpressed like(Object value) {
        return like(IsEqual.equalTo(value));
    }

    /**
     * A shortcut to {@code like(equalTo(value + "%"))}.
     * <p>Example:
     * <pre>{@code
     * cb.query().setMemberName_LikeSearch("John", op -> op.likePrefix());
     * assertThat(cb, hasCondition("memberName", likePrefix("John")));
     * }</pre>
     * @param value the value of condition
     */
    public static IsColumnExpressed likePrefix(String value) {
        return like(IsEqual.equalTo(value + "%"));
    }

    /**
     * A shortcut to {@code like(equalTo("%" + value))}.
     * <p>Example:
     * <pre>{@code
     * cb.query().setMemberName_LikeSearch("Doe", op -> op.likeSuffix());
     * assertThat(cb, hasCondition("memberName", likeSuffix("Doe")));
     * }</pre>
     * @param value the value of condition
     */
    public static IsColumnExpressed likeSuffix(String value) {
        return like(IsEqual.equalTo("%" + value));
    }

    /**
     * A shortcut to {@code like(equalTo("%" + value + "%"))}.
     * <p>Example:
     * <pre>{@code
     * cb.query().setMemberName_LikeSearch("n D", op -> op.likeContain());
     * assertThat(cb, hasCondition("memberName", likeContain("n D")));
     * }</pre>
     * @param value the value of condition
     */
    public static IsColumnExpressed likeContain(String value) {
        return like(IsEqual.equalTo("%" + value + "%"));
    }

    /**
     * Creates a matcher that matches when the examined column has
     * NOT_LIKE condition with value matched with the specified {@code matcher}.
     * <p>Example:
     * <pre>{@code
     * cb.query().setMemberName_NotLikeSearch("John", op -> op.likePrefix());
     * assertThat(cb, hasCondition("memberName", notLike(equalTo("John%"))));
     * }</pre>
     * @param matcher a matcher that evaluates the condition value
     */
    public static IsColumnExpressed notLike(Matcher<?> matcher) {
        return IsColumnExpressed.expressed(ComparisonOperator.NOT_LIKE, matcher);
    }

    /**
     * A shortcut to {@code notLike(equalTo(value))}.
     * <p>Example:
     * <pre>{@code
     * cb.query().setMemberName_NotLikeSearch("John", op -> op.likePrefix());
     * assertThat(cb, hasCondition("memberName", notLike("John%")));
     * }</pre>
     * @param value the value of condition
     */
    public static IsColumnExpressed notLike(Object value) {
        return notLike(IsEqual.equalTo(value));
    }

    /**
     * A shortcut to {@code notLike(equalTo(value + "%"))}.
     * <p>Example:
     * <pre>{@code
     * cb.query().setMemberName_NotLikeSearch("John", op -> op.likePrefix());
     * assertThat(cb, hasCondition("memberName", notLikePrefix("John")));
     * }</pre>
     * @param value the value of condition
     */
    public static IsColumnExpressed notLikePrefix(String value) {
        return notLike(IsEqual.equalTo(value + "%"));
    }

    /**
     * A shortcut to {@code notLike(equalTo("%" + value))}.
     * <p>Example:
     * <pre>{@code
     * cb.query().setMemberName_NotLikeSearch("Doe", op -> op.likeSuffix());
     * assertThat(cb, hasCondition("memberName", notLikeSuffix("Doe")));
     * }</pre>
     * @param value the value of condition
     */
    public static IsColumnExpressed notLikeSuffix(String value) {
        return notLike(IsEqual.equalTo("%" + value));
    }

    /**
     * A shortcut to {@code notLike(equalTo("%" + value + "%"))}.
     * <p>Example:
     * <pre>{@code
     * cb.query().setMemberName_NotLikeSearch("n D", op -> op.likeContain());
     * assertThat(cb, hasCondition("memberName", notLikeContain("n D")));
     * }</pre>
     * @param value the value of condition
     */
    public static IsColumnExpressed notLikeContain(String value) {
        return notLike(IsEqual.equalTo("%" + value + "%"));
    }

    /**
     * Creates a matcher that matches when the examined column has
     * IS_NULL condition.
     * <p>Example:
     * <pre>{@code
     * cb.query.setMemberName_IsNull();
     * assertThat(cb, hasCondition("memberName", isNull()));
     * }</pre>
     */
    public static IsColumnIsNull isNull() {
        return new IsColumnIsNull();
    }

    /**
     * Creates a matcher that matches when the examined column has
     * IS_NOT_NULL condition.
     * <p>Example:
     * <pre>{@code
     * cb.query.setMemberName_IsNotNull();
     * assertThat(cb, hasCondition("memberName", isNotNull()));
     * }</pre>
     */
    public static IsColumnIsNotNull isNotNull() {
        return new IsColumnIsNotNull();
    }

    /**
     * Creates a matcher that matches if the query selects the column.
     * To assert foreign table, join tables with '.' (dot).
     * <p>Example:
     * <pre>{@code
     * cb.specify().columnMemberName();
     * assertThat(cb, shouldSelect("memberName"));
     * 
     * cb.specify().specifyMemberStatus().columnMemberStatusName();
     * assertThat(cb, shouldSelect("memberStatus.memberStatusName"));
     * 
     * cb.specify().specifyMemberServiceAsOne().specifyServiceRank().columnServiceRankName();
     * // 'AsOne' can be omitted from table name
     * assertThat(cb, shouldSelect("memberService.serviceRank.serviceRankName"));
     * }</pre>
     * @param columnName the name of column
     */
    public static <T extends ConditionBean> ShouldSelect<T> shouldSelect(String columnName) {
        return ShouldSelect.shouldSelect(columnName);
    }
}
