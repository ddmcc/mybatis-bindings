# mybatis-bindings


### 有什么用？

将对象绑定到`sql`上下文中，然后在表达式中使用它

如：

```xml
<select id="test" resultType="">
     SELECT *
     FROM user
     WHERE 1 = 1
     <if test="StringUtils.isNotBlank(userId)">
         AND user_id = #{userId}
     </if>
</select>
```

### 怎么用？

##### 1， **pom 中引入**

```xml
<dependency>
   <groupId>com.sunsharing.economic</groupId>
   <artifactId>mybatis-bindings</artifactId>
   <version>2.0</version>
</dependency>
```

##### 2， **在要绑定对象的mapper接口或方法中新增 `Bindings` 注解**

```java
@Bindings({
        @Binding(type = StringUtils.class, alias = "Utils"),
        @Binding(type = DateUtils.class, alias = "")
})
```


像那样申明注解，就会将 `StringUtils` 和 `DateUtils` 两个对象绑定，并且还可以给对象取别名 `alias` 。要调用 `StringUtils.class` 类中的方法，可以 StringUtils.isBlank 或 Utils.isBlank。
方法不一定要申明为静态方法，非静态的也可以


### feature

- 在接口中申明：该接口下所有方法都会绑定上
- 在方法中申明：只绑定该方法
- 未申明@Binding，即@Bindings({})。这种情况下会将 **com.sunsharing.economic.mybatis.bindings.helper.Utils** 接口所有实现类对象绑定上去


### 其它
