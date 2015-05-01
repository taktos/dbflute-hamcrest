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

import java.util.ArrayList;
import java.util.List;

import org.dbflute.bhv.readable.CBCall;
import org.dbflute.cbean.ConditionBean;
import org.dbflute.util.DfReflectionUtil;
import org.mockito.ArgumentCaptor;

/**
 * Behavior argument captor to capture ConditionBean for further assertion.
 *
 * <p>
 * This captor captures Behavior's lambda argument by using an internal {@link ArgumentCaptor}.
 * When {@link #getCB()} was called, it creates a new {@code CB} instance and applies lambda and then returns it.
 *
 * <p>
 * Example of capturing ConditionBean:
 *
 * <pre class="code"><code class="java">
 * BehaviorArgumentCaptor&lt;MemberCB&gt; captor = BehaviorArgumentCaptor.of(MemberCB.class);
 * verify(mockBhv).selectEntity(captor.capture());
 * MemberCB cb = captor.getCB();
 * assertThat(cb, hasCondition("memberId", equal(1)));
 * </code></pre>
 *
 * @param <CB> Type of ConditionBean implementation
 * @author taktos
 *
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class BehaviorArgumentCaptor<CB extends ConditionBean> {

    private final ArgumentCaptor<CBCall> captor;
    private final Class<CB> clazz;

    /**
     * Creates a new {@link BehaviorArgumentCaptor} of {@code clazz}.
     *
     * @param clazz Implementation class of ConditionBean
     */
    public BehaviorArgumentCaptor(Class<CB> clazz) {
        this.captor = ArgumentCaptor.forClass(CBCall.class);
        this.clazz = clazz;
    }

    /**
     * Returns captor to capture argument. This method <b>must be used inside of verification</b>.
     *
     * @return lambda argument captor
     * @see ArgumentCaptor#capture()
     */
    public CBCall<CB> capture() {
        return captor.capture();
    }

    /**
     * Returns a new {@code CB} instance that was applied lambda callback.
     * <p>
     * If verified method was called multiple times, this method returns the latest one.
     * If you want to get all ConditionBean, use {@link #getAllCB()} method instead.
     *
     * @return a new {@code CB}
     * @see #getAllCB()
     * @see ArgumentCaptor#getValue()
     */
    public CB getCB() {
        CB cb = (CB) DfReflectionUtil.newInstance(clazz);
        CBCall call = captor.getValue();
        call.callback(cb);
        return cb;
    }

    /**
     * Returns new {@code CB} instances that were applied lambda callback.
     *
     * @return new {@code CB} instances.
     * @see ArgumentCaptor#getAllValues()
     */
    public List<CB> getAllCB() {
        List<CBCall> values = captor.getAllValues();
        List<CB> cbs = new ArrayList<CB>(values.size());
        for (CBCall callback : values) {
            CB cb = (CB) DfReflectionUtil.newInstance(clazz);
            callback.callback(cb);
            cbs.add(cb);
        }
        return cbs;
    }

    /**
     * Create a new {@link BehaviorArgumentCaptor} of {@code clazz}.
     * @param clazz Class of ConditionBean implementation.
     * @return new captor
     */
    public static <CB extends ConditionBean> BehaviorArgumentCaptor<CB> of(Class<CB> clazz) {
        return new BehaviorArgumentCaptor<CB>(clazz);
    }

}
