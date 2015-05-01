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
package org.dbflute.testing.rule;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

import org.dbflute.hook.AccessContext;
import org.dbflute.util.DfTypeUtil;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * Test rule that initialize AccessContext.
 * 
 * @author taktos
 *
 */
public class AccessContextInitializer implements TestRule {
    private AccessContext accessContext;

    private String user;
    private String process;
    private String module;
    private Timestamp timestamp;
    private Timestamp defaultTimestamp;

    private class AccessTimestampProvider implements AccessContext.AccessTimestampProvider {
        @Override
        public Timestamp provideTimestamp() {
            if (timestamp == null) {
                return new Timestamp(System.currentTimeMillis());
            }
            return timestamp;
        }
    }

    private class AccessDateProvider implements AccessContext.AccessDateProvider {
        @Override
        public Date provideDate() {
            if (timestamp == null) {
                return new Date();
            }
            return new Date(timestamp.getTime());
        }
    }

    private class AccessLocalDateProvider implements AccessContext.AccessLocalDateProvider {
        @Override
        public LocalDate provideLocalDate() {
            if (timestamp == null) {
                return LocalDate.now();
            }
            return timestamp.toLocalDateTime().toLocalDate();
        }
    }

    private class AccessLocalDateTimeProvider implements AccessContext.AccessLocalDateTimeProvider {
        @Override
        public LocalDateTime provideLocalDateTime() {
            if (timestamp == null) {
                return LocalDateTime.now();
            }
            return timestamp.toLocalDateTime();
        }
    }

    /**
     * Creates a new rule with blank user and current timestamp.
     */
    public AccessContextInitializer() {
        this("");
    }

    /**
     * Creates a new rule with specified username and current timestamp.
     * @param user the name of current user
     */
    public AccessContextInitializer(String user) {
        this(user, (Timestamp) null);
    }

    /**
     * Creates a new rule with specified username and timestamp.
     * {@code timestamp} is parsed flexibly by DfTypeUtil.
     * @param user the name of current user
     * @param timestamp the timestamp string 'yyyy-MM-dd HH:mm:ss.SSS'
     */
    public AccessContextInitializer(String user, String timestamp) {
        this(user, timestamp, "");
    }

    /**
     * Creates a new rule with specified username, timestamp and processname.
     * @param user the name of current user
     * @param timestamp the timestamp string 'yyyy-MM-dd HH:mm:ss.SSS'
     * @param process the name of current process
     */
    public AccessContextInitializer(String user, String timestamp, String process) {
        this(user, DfTypeUtil.toTimestamp(timestamp), process);
    }

    /**
     * Creates a new rule with specified username and timestamp.
     * @param user the name of current user
     * @param timestamp the timestamp
     */
    public AccessContextInitializer(String user, Timestamp timestamp) {
        this(user, timestamp, "");
    }

    /**
     * Creates a new rule with specified username, timestamp and processname.
     * @param user the name of current user
     * @param timestamp the timestamp
     * @param process the name of current process
     */
    public AccessContextInitializer(String user, Timestamp timestamp, String process) {
        this(user, timestamp, process, "");
    }

    /**
     * Creates a new rule with specified username, timestmap, processname and modulename.
     * @param user the name of current user
     * @param timestamp the timestamp
     * @param process the name of current process
     * @param module the name of current module
     */
    public AccessContextInitializer(String user, Timestamp timestamp, String process, String module) {
        this.user = user;
        this.defaultTimestamp = timestamp;
        this.process = process;
        this.module = module;
    }

    @Override
    public Statement apply(final Statement base, final Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                accessContext = new AccessContext();
                accessContext.setAccessUser(user);
                accessContext.setAccessProcess(process);
                accessContext.setAccessModule(module);
                timestamp = defaultTimestamp;
                accessContext.setAccessDateProvider(new AccessDateProvider());
                accessContext.setAccessTimestampProvider(new AccessTimestampProvider());
                accessContext.setAccessLocalDateProvider(new AccessLocalDateProvider());
                accessContext.setAccessLocalDateTimeProvider(new AccessLocalDateTimeProvider());
                AccessContext.setAccessContextOnThread(accessContext);

                base.evaluate();
            }
        };
    }

    /**
     * Reset current user on thread.
     * @param user the name of user
     */
    public AccessContextInitializer user(String user) {
        this.accessContext.setAccessUser(user);
        return this;
    }

    /**
     * Reset current process on thread.
     * @param process the name of process
     */
    public AccessContextInitializer process(String process) {
        this.accessContext.setAccessProcess(process);
        return this;
    }

    /**
     * Rest current module on thread.
     * @param module the name of module
     */
    public AccessContextInitializer module(String module) {
        this.accessContext.setAccessModule(module);
        return this;
    }

    /**
     * Reset current timestamp on thread.
     * @param timestamp the name of timestmap
     */
    public AccessContextInitializer timestamp(String timestamp) {
        this.timestamp = DfTypeUtil.toTimestamp(timestamp);
        return this;
    }

}
