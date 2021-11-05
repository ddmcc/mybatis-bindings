# mybatis-bindings


### 有什么用？

将枚举、工具类、常量、对象等绑定到`sql`上下文中，然后在xml中使用它。其实就是把 `<bind />` 标签用注解的方式配置


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

#### 1， **pom 中引入**

```xml
<dependency>
    <groupId>com.yiautos</groupId>
    <artifactId>yiautos-mybatis-bindings</artifactId>
    <version>1.1.0</version>
</dependency>
```

#### 2， **在要绑定对象的mapper接口或方法中新增 `Bindings` 注解**


##### 2.1 枚举

**枚举类必须提供 `getCode()` 方法，并返回枚举值** 

mapper 接口：

```java
    @Bindings({
        @Binding(varType = BaseBinding.VarType.ENUM, type = LogisticsFeeConfirmationDetailInvoiceStatusApiEnum.class, alias = ""),
        @Binding(varType = BaseBinding.VarType.ENUM, type = LogisticsFeeConfirmationDetailPayStatusApiEnum.class, alias = "pay")
    })
```

这样就会将两个枚举的 **`所有变量`** 绑定到sql上下文中。如果有别名，那么只可以用别名来引用，没有别名只能用枚举（ps：最好加个别名，有同名的变量会被覆盖），然后在xml中使用：

xml：

```xml
 AND a.pay_status = #{pay.PAID}
 AND b.invoice_status = #{WAIT_INVOICE}
```


##### 2.2 工具类

mapper 接口：

```java
    @Bindings({
        @Binding(type = StringUtils.class, alias = "v", varType = BaseBinding.VarType.UTIL),
        @Binding(type = DateUtils.class, alias = "d", varType = BaseBinding.VarType.UTIL)
    })
```

会将 `StringUtils` 和 `DateUtils` 两个对象绑定，并且还可以给对象取别名 `alias` 。要调用 `StringUtils.class` 类中的方法，可以 `StringUtils.isBlank` 或 `v.isBlank`。
方法不一定要申明为静态方法，非静态的也可以


xml： #{UTIL_CLASS.method()} or #{alias.VAR_NAME}

```xml
<if test="v.isNotBlank(other_var)">
    AND table_column like CONCAT('%', #{other_var}, '%')
</if>

<if test="StringUtils.isBlank(other_var)">
    AND table_column like CONCAT('%', #{other_var}, '%')
</if>
```


##### 2.3 常量

mapper 接口：

```java
    @Bindings({
        @Binding(type = ObjectCache.class, alias = "b", varType = BaseBinding.VarType.STATIC, varName = {"TEST_NAME"})
    })
```

绑定 `ObjectCache` 类中的 `TEST_NAME` **静态变量**


xml： #{VAR_NAME} or #{alias.VAR_NAME}

```xml
 AND table_column like CONCAT('%', #{b.TEST_NAME}, '%')
 AND table_column like CONCAT('%', #{TEST_NAME}, '%')
```


##### 2.4 表达式

```java
    @Bindings({
        @Binding(alias = "e", varType = BaseBinding.VarType.EXPRESSION, expression = "@com.yiautos.mybatis.bindings.helper.ObjectCache@TEST_NAME"),
        @Binding(alias = "c", varType = BaseBinding.VarType.EXPRESSION, expression = "new com.yiautos.mybatis.bindings.model.User('数据', 2)"),
        @Binding(alias = "d", varType = BaseBinding.VarType.EXPRESSION, expression = "@com.yiautos.mybatis.bindings.model.enums.DingStatusEnum@APPROVALING.getCode()")
    })
```

相当于标签： 

```xml
<bind name = 'alias' value = 'expression' />
```

使用：

```
if中使用变量： <if test="c.userName == 'xxx'">

或者参数拼接： AND table_column = #{c.age}

或者调用方法： <if test="c.method1()">  <if test="c.method2(c.userName)">

或者枚举常量：git config --global user.name "bryan sun" AND table_column = #{d}

或者静态变量： AND table_column = #{e}
```



### 其它
