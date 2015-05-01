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
    
    ```java
    MemberCB cb = ...;
    cb.query().queryMemberStatus().setMemberStatusName_Equal("ACT");
    cb.query().queryMemberServiceAsOne().queryServiceRank().setRankName_Equal("VIP");
    
    assertThat(cb, hasRelation("memberStatus", hasCondition("memberName", equal("ACT"))));
    assertThat(cb, hasRelation("memberService.serviceRank", hasCondition("rankName", equal("ACT"))));
    ```
    
- Assert column should select
    
    ```java
    MemberCB cb = ...;
    cb.setupSelect_MemberStatus();
    assertThat(cb, shouldSelect("memberName"));
    assertThat(cb, shouldSelect("memberStatus.memberStatusName"));
    ```
    

### Utilities for Mock Behavior (Only for DBFlute-1.1+)
- ArgumentCaptor for capturing ConditionBean from lambda parameter
    ```java
    MemberBhv bhv = mock(MemberBhv.class);
    bhv.selectEntity(cb -> cb.query().setMemberId_Equal(10));
    
    BehaviorArgumentCaptor<MemberCB> captor = captor(MemberCB.class);
    verify(bhv).selectEntity(captor.capture());
    
    MemberCB cb = captor.getCB();
    assertThat(cb, hasCondition("memberId", equal(10)));
    ```
- ArgumentMatcher for changing Behavior return value
    ```java
    MemberBhv bhv = mock(MemberBhv.class);
    
    Member m1 = new Member();
    m1.setMemberName("John Doe");
    
    Member m2 = new Member();
    m2.setMemberName("Jane Doe");
    
    when(bhv.selectEntity(any())).thenReturn(OptionalEntity.empty());
    when(bhv.selectEntity(argCB(MemberCB.class
            , hasCondition("memberId", equal(10))))).thenReturn(OptionalEntity.of(m1));
    when(bhv.selectEntity(argCB(MemberCB.class
            , hasCondition("memberId", equal(20))))).thenReturn(OptionalEntity.of(m2));
    
    OptionalEntity<Member> id10 = bhv.selectEntity(cb -> cb.query().setMemberId_Equal(10));
    assertThat(id10.get().getMemberName(), is("John Doe"));
    
    OptionalEntity<Member> id20 = bhv.selectEntity(cb -> cb.query().setMemberId_Equal(20));
    assertThat(id20.get().getMemberName(), is("Jane Doe"));
    
    OptionalEntity<Member> id30 = bhv.selectEntity(cb -> cb.query().setMemberId_Equal(30));
    assertThat(id30.isPresent(), is(false));
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
    <version>2.0.0</version>
    <scope>test</scope>
</dependency>
```


Requirements
------------

- DBFlute 1.1+
- JUnit
- Hamcrest
- Mockito


License
--------

[Apache Lincense, Version 2.0](https://www.apache.org/licenses/LICENSE-2.0)

