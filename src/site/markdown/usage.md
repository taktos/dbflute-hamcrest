Usage
=====

- [ConditionBean Matchers](#cbmatchers)
	- [Assert query conditions](#querymatcher)
	- [Assert query conditions on joined table](#foreignquerymatcher)
	- [Assert column should select](#specifymatcher)
	- [Assert that should select columns of joind table](#foreignspecifymatcher)
- [Mockito helpers](#mockitohelpers)
	- [Argument Captor](#argumentcaptor)
	- [Argument Matcher](#argumentmatcher)
- [JUnit helpers](#junithelpers)

<a name="cbmatchers"></a>ConditionBean Matchers
----------------------

This library provides Matchers for query conditions (`cb.query().setXXX`)
and for fetching column (`cb.specify().columnXXX`).

You can use these matchers by import statically `org.dbflute.testing.DBFluteMatchers`.
Please refer to [JavaDoc API](v2/apidocs/org/dbflute/testing/DBFluteMatchers.html)
for complete list of APIs.

### <a name="querymatcher"></a>Assert query conditions

These matchers are intended to use with `assertThat` method.

To test condition for column, use `hasCondition` with a operator matcher.

```java
assertThat(cb, hasCondition("memberName", equal("John Doe")));
```

The first argument ("memberName") is a String of column name. This is a case-insensitive and accepting both camel case and snake case.

You can pass any Matcher instance to the operator matcher (`equal(...) in above`).


```java
assertThat(cb, hasCondition("memberName", equal(startsWith("John"))));
```


### <a name="foreignquerymatcher"></a>Assert query conditions on joined table

```java
cb.query().queryMemberStatus().setMemberStatusName_NotEqual("DEL");
cb.query().queryMemberServiceAsOne().queryServiceRank().setRankName_Equal("VIP");

assertThat(cb, hasRelation("memberStatus", hasCondition("memberName", notEqual("DEL"))));
assertThat(cb, hasRelation("memberService.serviceRank", hasCondition("rankName", equal("ACT"))));
```

### <a name="specifymatcher"></a>Assert column should select

```java
cb.specify().columnMemberName();
assertThat(cb, shouldSelect("memberName"));
```

### <a name="foreignspecifymatcher"></a>Assert that should select columns of joind table

```java
cb.specify().specifyMemberStatus().columnMemberStatusName();
cb.specify().specifyMemberServiceAsOne().specifyServiceRank().columnServiceRankName();

assertThat(cb, shouldSelect("memberStatus.memberStatusName"));
assertThat(cb, shouldSelect("memberService.serviceRank.serviceRankName"));
```


<a name="mockitohelpers"></a>Mockito helpers
-----------------------------------------------

### <a name="argumentcaptor"></a>Argument Captor

```java
bhv.selectEntity(cb -> cb.query().setMemberId_Equal(1));

BehaviorArgumentCaptor<MemberCB> captor = captor(MemberCB.class);
verify(bhv).selectEntity(captor.capture());

MemberCB cb = captor.getCB();
assertThat(cb, hasCondition("memberId", equal(1)));
```


### <a name="argumentmatcher"></a>Argument Matcher

```java
when(bhv.selectEntity(argCB(MemberCB.class, hasCondition("memberId", equal(1))))).thenReturn(fixture);

verify(mockBhv).selectEntity(argCB(MemberCB.class, hasCondition("memberId", equal(1))));
```

<a name="junithelpers"></a>JUnit helpers
----------------------------------------

### [AccessContextInitializer](v2/apidocs/org/dbflute/testing/rule/AccessContextInitializer.html)

```java
public class FooTest {
    @Rule
    public AccessContextInitializer ac = new AccessContextInitializer("foo");

    @AutoWired
    MemberBhv bhv;

    @Test
    public void insert() {
        Member m = new Member();
        m.setMemberName("John Doe");
        bhv.insert(m);
        assertThat(m.getRegisterUser(), is("foo"));
    }
}
```


### JUnit Category


[@DatabaseTests](v2/apidocs/org/dbflute/testing/category/DatabaseTests.html) is
a JUnit @Category annotation.
Please refer to JUnit wiki for details of categories.

> https://github.com/junit-team/junit/wiki/Categories

```xml
<plugin>
	<artifactId>maven-surefire-plugin</artifactId>
	<configuration>
		<excludedGroups>org.dbflute.testing.category.DatabaseTests</excludedGroups>
	</configuration>
</plugin>
```
