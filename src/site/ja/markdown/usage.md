使い方
=========

- [ConditionBean Matchers](#cbmatchers)
	- [検索条件の評価](#querymatcher)
	- [結合されたテーブルに対する検索条件の評価](#foreignquerymatcher)
	- [特定カラムが取得されることの評価](#specifymatcher)
	- [結合されたテーブルのカラムが取得されることの評価](#foreignspecifymatcher)
- [Mockito helpers](#mockitohelpers)
	- [Argument Captor](#argumentcaptor)
	- [Argument Matcher](#argumentmatcher)
- [JUnit helpers](#junithelpers)

<a name="cbmatchers"></a>ConditionBean Matchers
----------------------------------------------------------

ConditionBeanに設定された検索条件（`cb.query().setXXX`）を評価するためのMatcherと、
クエリの結果特定のカラムが取得されること（`cb.specify().columnXXX`）を評価するためのMatcherを提供します。

どちらも `org.dbflute.testing.DBFluteMatchers` をstatic importすることで使用可能です。
提供されているMatcherの完全なリストは [APIドキュメント](v2/apidocs/org/dbflute/testing/DBFluteMatchers.html) を参照してください。


### <a name="querymatcher"></a>検索条件の評価

hamcrest の `assertThat` と共に使用します。

`MEMBER_NAME`というカラムに対し、`=`演算子で`John Doe` という条件が設定されたこと（`cb.query().setMemberName_Equal("John Doe")`）を評価するには、以下のようにします。

```java
assertThat(cb, hasCondition("memberName", equal("John Doe")));
```

`hasCondition`の第1引数で、「どのカラムに対する条件を評価するか」を指定します。ここに指定する値は、キャメルケース（`memberName`）でもスネークケース（`MEMBER_NAME`）でも、いい感じに解釈します。

次に`hasCondition`の第2引数で、そのカラムに対する条件の評価方法（Matcher）を指定します。
SQLの各演算子に対応するMatcherがあります。（`=`:`equal`, `<`:`lessThan`など）

また、ここでは`equal(String value)`を使っていますが、Stringの代わりにMatcherを渡すことができます。これにより、「`MEMBER_NAME`というカラムに対し、`=`演算子で`John`から始まる文字列が指定されたこと」のように、より多彩な評価が可能となります。

```java
assertThat(cb, hasCondition("memberName", equal(startsWith("John"))));
```


### <a name="foreignquerymatcher"></a>結合されたテーブルに対する検索条件の評価

ConditionBeanで結合先のテーブルに対して検索条件が設定されていることを評価するには、`hasRelation`を使用します。
第1引数にテーブル名を、第2引数に`hasCondition`を取ります。

```java
cb.query().queryMemberStatus().setMemberStatusCode_NotEqual("DEL");
assertThat(cb, hasRelation("memberStatus", hasCondition("memberStatusCode", notEqual("DEL"))));
```

さらに先のテーブル以降の場合は、第1引数に渡すテーブル名を"."（ドット）でつないで指定します。

```java
cb.query().queryMemberServiceAsOne().queryServiceRank().setServiceRankName_Equal("VIP");
assertThat(cb, hasRelation("memberServiceAsOne.serviceRank", hasCondition("serviceRankName", equal("VIP"))));
```

第1引数で渡すテーブル名は、カラム名と同じくキャメルケース、スネークケースどちらもいい感じに解釈します。また、「AsOne」は省略可能です。


### <a name="specifymatcher"></a>特定カラムが取得されることの評価

ConditionBeanで特に何も指定しなければ、テーブルの全カラムが取得されます。DBFluteでは、パフォーマンス上の問題を避けるため、取得するカラムを限定することができます。

この機能（`specify`）は便利である一方、期待したカラムの値が取得できないことに気づきにくい（そもそもselectしていないのか、selectしたけどnullなのか、検索結果からは判別できない）というデメリットも有ります。
そこで、このライブラリには、「特定のカラムがselect句に含まれているか否か」を評価するMatcher `shouldSelect` を提供しています。

```java
cb.specify().columnMemberName();
assertThat(cb, shouldSelect("memberName"));
```

ここでも、カラム名は（ｒｙ


### <a name="foreignspecifymatcher"></a>結合されたテーブルのカラムが取得されることの評価

結合先のテーブルのカラムが取得されることを評価するには、`shoudSelect`の第1引数で"テーブル名.カラム名"のように指定します。
`hasRelation`のような特別なMatcherは必要ありません。

```java
cb.specify().specifyMemberStatus().columnMemberStatusName();
cb.specify().specifyMemberServiceAsOne().specifyServiceRank().columnServiceRankName();

assertThat(cb, shouldSelect("memberStatus.memberStatusName"));
assertThat(cb, shouldSelect("memberService.serviceRank.serviceRankName"));
```

テーブル名、カラム名に指定する文字列は、hasRelationと同じくよしなに解釈されます。


<a name="mockitohelpers"></a>Mockito helpers
-----------------------------------------------

### <a name="argumentcaptor"></a>Argument Captor

Behaviorに対して渡されるラムダ式をテストするには、BehaviorArgumentCaptor を使用します。

```java
bhv.selectEntity(cb -> cb.query().setMemberId_Equal(1));

BehaviorArgumentCaptor<MemberCB> captor = captor(MemberCB.class);
verify(bhv).selectEntity(captor.capture());

MemberCB cb = captor.getCB();
assertThat(cb, hasCondition("memberId", equal(1)));
```


### <a name="argumentmatcher"></a>Argument Matcher

ラムダ式を受け取るBehaviorメソッドをスタブするには、BehaviorArgumentMatcherを使用します。

```java
when(bhv.selectEntity(argCB(MemberCB.class, hasCondition("memberId", equal(1))))).thenReturn(fixture);

verify(mockBhv).selectEntity(argCB(MemberCB.class, hasCondition("memberId", equal(1))));
```

<a name="junithelpers"></a>JUnit helpers
----------------------------------------

### [AccessContextInitializer](v2/apidocs/org/dbflute/testing/rule/AccessContextInitializer.html)

DBFluteのAccessContextを初期化するRuleです。


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

[@DatabaseTests](v2/apidocs/org/dbflute/testing/category/DatabaseTests.html)は、
テスト実行にデータベース接続が必要であることを示す、JUnitの@Categoryアノテーションです。
Categoriesについての詳細は以下のページを参照してください。

> https://github.com/junit-team/junit/wiki/Categories

```xml
<plugin>
	<artifactId>maven-surefire-plugin</artifactId>
	<configuration>
		<excludedGroups>org.dbflute.testing.category.DatabaseTests</excludedGroups>
	</configuration>
</plugin>
```
