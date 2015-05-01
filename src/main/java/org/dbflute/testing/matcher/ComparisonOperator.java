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

import java.lang.reflect.Method;
import java.util.Map;

import org.seasar.dbflute.cbean.cvalue.ConditionValue;
import org.seasar.dbflute.util.DfReflectionUtil;

/**
 * Operators of SQL comparison.
 *
 * @author taktos
 *
 */
public enum ComparisonOperator {

    EQUAL("=") {
        @Override
        public Object getValue(ConditionValue cv) {
            return getFixedValue(cv, "getEqualValueHandler");
        }
    },
    NOT_EQUAL("<>") {
        @Override
        public Object getValue(ConditionValue cv) {
            return getFixedValue(cv, "getNotEqualValueHandler");
        }
    },
    GREATER_THAN(">") {
        @Override
        public Object getValue(ConditionValue cv) {
            return getFixedValue(cv, "getGreaterThanValueHandler");
        }
    },
    GREATER_EQUAL(">=") {
        @Override
        public Object getValue(ConditionValue cv) {
            return getFixedValue(cv, "getGreaterEqualValueHandler");
        }
    },
    LESS_THAN("<") {
        @Override
        public Object getValue(ConditionValue cv) {
            return getFixedValue(cv, "getLessThanValueHandler");
        }
    },
    LESS_EQUAL("<=") {
        @Override
        public Object getValue(ConditionValue cv) {
            return getFixedValue(cv, "getLessEqualValueHandler");
        }
    },

    IN("in") {
        @Override
        public Object getValue(ConditionValue cv) {
            return getVaryingValue(cv, "inScope");
        }
    },
    NOT_IN("not in") {
        @Override
        public Object getValue(ConditionValue cv) {
            return getVaryingValue(cv, "notInScope");
        }
    },
    LIKE("like") {
        @Override
        public Object getValue(ConditionValue cv) {
            return getVaryingValue(cv, "likeSearch");
        }
    },
    NOT_LIKE("not like") {
        @Override
        public Object getValue(ConditionValue cv) {
            return getVaryingValue(cv, "notLikeSearch");
        }
    },
    IS_NULL("is null") {
        @Override
        public Object getValue(ConditionValue cv) {
            return cv.hasIsNull();
        }
    },
    IS_NOT_NULL("is not null") {
        @Override
        public Object getValue(ConditionValue cv) {
            return cv.hasIsNotNull();
        }
    };

    private final String operator;

    private ComparisonOperator(String operator) {
        this.operator = operator;
    }

    public String sign() {
        return operator;
    }

    public abstract Object getValue(ConditionValue cv);

    protected Object getFixedValue(ConditionValue cv, String getterName) {
        Method method = DfReflectionUtil.getAccessibleMethod(cv.getClass(), getterName, null);
        Object valueHandler = DfReflectionUtil.invokeForcedly(method, cv, null);
        Method getter = DfReflectionUtil.getAccessibleMethod(valueHandler.getClass(), "getValue", null);
        return DfReflectionUtil.invoke(getter, valueHandler, null);
    }

    protected Object getVaryingValue(ConditionValue cv, String type) {
        Map<String, Map<String, Object>> varying = cv.getVarying();
        if (varying == null || !varying.containsKey(type)) {
            return null;
        }
        return varying.get(type).values().iterator().next();
    }
}
