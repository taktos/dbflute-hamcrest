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

