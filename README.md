DBFlute Testing
===============

This is a unit testing library for DBFlute.
It helps you to free unit testing from the database.
There are some convenient matchers for ConditionBean conditions.

Features
--------

- Matchers for ConditionBean
	```java
	MemberCB cb = ...;
	cb.query().setMemberName_Equal("John Doe");
	cb.query().setMemberId_GreaterThan(10);
	
	assertThat(cb, hasCondition("memberName", equal("John Doe")));
	assertThat(cb, hasCondition("memberName", equal(startsWith("John"))));
	assertThat(cb, hasCondition("memberId", greaterThan(10)));
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
    <groupId>org.dbflute</groupId>
    <artifactId>dbflute-test-support</artifactId>
    <version>1.0.0</version>
    <scope>test</scope>
</dependency>
```

Requirements
------------

- Java6+
- DBFlute 1.0.x **(Not compatible with 1.1.x)**
- Hamcrest
- Mockito

License
--------

[Apache Lincense, Version 2.0](https://www.apache.org/licenses/LICENSE-2.0)

