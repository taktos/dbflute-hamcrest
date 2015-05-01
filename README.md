DBFlute Hamcrest
================

This is a testing support library for DBFlute that helps you to free unit
testing from the database. It provides ConditionBean matchers, utilities for
mock Behavior and some JUnit helpers.

Features
--------

### ConditionBean Matchers

- Assert query conditions
    ```java
    import static org.dbflute.testing.DBFluteMatchers.*;
    ...
    MemberCB cb = ...;
    cb.query().setMemberName_Equal("John Doe");
    cb.query().setMemberId_GreaterThan(10);
    
    assertThat(cb, hasCondition("memberName", equal("John Doe")));
    assertThat(cb, hasCondition("memberName", equal(startsWith("John"))));
    assertThat(cb, hasCondition("memberId", greaterThan(10)));
    ```
- Assert query conditions on joined table
    '''java
    MemberCB cb = ...;
    cb.query().queryMemberStatus().setMemberStatusName_Equal("ACT");
    cb.query().queryMemberServiceAsOne().queryServiceRank().setRankName_Equal("VIP");
    
    assertThat(cb, hasRelation("memberStatus", hasCondition("memberName", equal("ACT"))));
    assertThat(cb, hasRelation("memberService.serviceRank", hasCondition("rankName", equal("ACT"))));
    '''
- Assert column should select
    ```java
    MemberCB cb = ...;
    cb.setupSelect_MemberStatus();
    assertThat(cb, shouldSelect("memberName"));
    assertThat(cb, shouldSelect("memberStatus.memberStatusName"));
    ```

- ArgumentMatcher for changing Behavior return value
    ```java
    MemberBhv bhv = mock(MemberBhv.class);
    
    Member m1 = new Member();
    m1.setMemberName("John Doe");
    
    Member m2 = new Member();
    m2.setMemberName("Jane Doe");
    
    when(bhv.selectEntity(argCB(MemberCB.class
            , hasCondition("memberId", equal(10))))).thenReturn(m1);
    when(bhv.selectEntity(argCB(MemberCB.class
            , hasCondition("memberId", equal(20))))).thenReturn(m2);
    
    MemberCB cb = new MemberCB();
    cb.query.setMemberId_Equal(10);
    
    Member id10 = bhv.selectEntity(cb);
    assertThat(id10.getMemberName(), is("John Doe"));
    
    cb.query.setMemberId_Equal(20);
    Member id20 = bhv.selectEntity(cb);
    assertThat(id20.getMemberName(), is("Jane Doe"));
    
    cb.query().setMemberId_Equal(30);
    Member id30 = bhv.selectEntity(cb);
    assertThat(id30, is(nullValue()));
    ```

### JUnit Helpers
- **@DatabaseTests**
    - Test category indicates that requires database connection. You can use
    this category to include/exclude with surefire/failsafe.
- **AccessContextInitializer**
    - TestRule that setup AccessContext on thread.


Compatibility Matrix
--------------------

|dbflute-testing|dbflute-runtime|JDK|
|---------------|---------------|---|
|1.0.x (1.0-jdk6 branch)|1.0.x|1.6+|
|2.0.x (master branch)  |1.1.x|8+  |


Usage
---------

**TODO** Deploy to maven repos.

```xml
<dependency>
    <groupId>org.dbflute.testing</groupId>
    <artifactId>dbflute-hamcrest</artifactId>
    <version>1.0.0</version>
    <scope>test</scope>
</dependency>
```


Requirements
------------

- DBFlute 1.0.x **(Not compatible with 1.1.x)**
- JUnit
- Hamcrest
- Mockito


License
--------

[Apache Lincense, Version 2.0](https://www.apache.org/licenses/LICENSE-2.0)

