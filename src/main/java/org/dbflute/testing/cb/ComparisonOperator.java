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

/**
 * Operators of SQL comparison.
 *
 * @author taktos
 *
 */
public enum ComparisonOperator {

	EQUAL("=", "getEqualValueHandler"),
	NOT_EQUAL("<>", "getNotEqualValueHandler"),
	GREATER_THAN(">", "getGreaterThanValueHandler"),
	GREATER_EQUAL(">=", "getGreaterEqualValueHandler"),
	LESS_THAN("<", "getLessThanValueHandler"),
	LESS_EQUAL("<=", "getLessEqualValueHandler"),

	IN("in", "getInScopeValueHandler"),
	NOT_IN("not in", "getNotInScopeValueHandler"),
	LIKE("like", "getLikeSearchValueHandler"),
	NOT_LIKE("not like", "getNotLikeSearchValueHandler"),
	IS_NULL("is null", "getIsNullValueHandler"),
	IS_NOT_NULL("is not null", "getIsNotNullValueHandler")
	;

	private final String operator;
	private final String getterName;

	private ComparisonOperator(String operator, String getter) {
		this.operator = operator;
		this.getterName = getter;
	}

	public String sign() {
		return operator;
	}

	public String getGetterName() {
		return getterName;
	}

}
