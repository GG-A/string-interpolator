# 📚string-interpolator
Very elegant and powerful Scala-like String Interpolator for Java. 类似Scala的强大而优雅的Java字符串插值器


## ❓能做什么 
取代使用`+`号拼接字符串的方式，取代不够优雅且繁琐的字符串插值器`MessageFormat.format()`和`String.format()`  
- **使用Java内置**  
```java
int id = 12345;
String name = "zhangsan";
float height = 1.805f; // 单位：m

// 使用 + 号拼接
String res1 = "id: " + id + "  名字：" + name + "  身高(cm): " + height * 100;
System.out.println(res1);

// 使用 MessageFormat.format
String res2 = MessageFormat.format("id: {0}  名字：{1}  身高(cm): {2}", id, name, height * 100);
System.out.println(res2);

// 使用 String.format
String res3 = String.format("id: %d  名字：%s  身高(cm): %.1f", id, name, height * 100);
System.out.println(res3);
```
- **使用string-interpolator（代码简洁，可读性强）**
```java
String s = "id: ${id}  名字：${name}  身高(cm): ${height * 100}";
System.out.println(s);
```


## 🗺️指南（User Guide）  
- [📘使用string-interpolator](#使用string-interpolator)
  - [`${}`中使用变量](#中使用变量)
  - [`${}`中使用表达式](#中使用表达式)
  - [`${}`中调用方法](#中调用方法)
  - [如何输出带`${}`的字符串](#如何输出带的字符串)
  - [禁用StringInterpolator](#禁用stringInterpolator)
  - [IDENTIFIER模式（忽略表达式与方法调用）](#IDENTIFIER模式忽略表达式与方法调用)
- [⚠免责声明（必看！！！）](#免责声明)
- [💿集成方式](#集成方式)
  - [IDEA Maven 集成](#idea-maven-集成)
  - [IDEA普通项目（不带Maven）](#idea普通项目不带maven)
  - [Android Studio](#android-studio)



## 📘使用string-interpolator  
1. 将 string-interpolator集成到项目中，查看：[集成方式](#集成方式)
2. 在需要使用插值器`${xxx}`的地方（类、字段、方法）上加上注解：`@StringInterpolator`（在类上标注，则作用于整个类的所有成员）
3. 在字符串中使用 `${}` 引用**变量**、**表达式**以及**方法调用**等等

### `${}`中使用变量  
```java
@StringInterpolator
public void testVar() {
    int id = 12345;
    String name = "zhangsan";
    System.out.println("id: ${id} --- 名字：${name}");  // 输出：id: 12345 --- 名字：zhangsan
}
```

### `${}`中使用表达式  
```java
@StringInterpolator
public void testExpr() {
    int i = 5;
    System.out.println("${i < 5 ? 0 : i}");  // 输出：5
}
```

### `${}`中调用方法  
```java
public int add(int i, int i1) {
    return i + i1;
}

@StringInterpolator
public void testCallMethod() {
    System.out.println("1 + 2 = ${add(1, 2)}");  // 输出：1 + 2 = 3
}
```

### 如何输出带`${}`的字符串  
```java
@StringInterpolator
public void testMetaChar() {
    String name = "zhangsan";
    // ${} 将被解析成字符 $
    System.out.println("元字符：${}{name}");     // 输出：元字符：${name}
    System.out.println("名字：${name}");        // 输出：名字：zhangsan
}
```

### 禁用StringInterpolator  
使用 `@StringInterpolator(false)`

### IDENTIFIER模式（忽略表达式与方法调用）  
使用 `@StringInterpolator(parseMode = InterpolationMode.IDENTIFIER)`
```java
@StringInterpolator(parseMode = InterpolationMode.IDENTIFIER)
public void testIdentifier() {
    String name = "zhangsan";
    /*
     * ${name.length()} 不是合法的Java标识符，忽略解析，保持原样输出；
     * ${name} 是合法的标识符，会被 name 的真实值 zhangsan 替换。
     */
    System.out.println("忽略方法解析：${name.length()} --- 解析标识符：${name}");
    // 输出：忽略方法解析：${name.length()} --- 解析标识符：zhangsan
}
```



## ⚠免责声明  
⚠⚠⚠ `string-interpolator` 尚处于**测试阶段！测试阶段！测试阶段！** 还未经过大量项目的验证。由于`string-interpolator`是在编译时期发生作用，所以发生错误，可能提示并不明显，可以通过反编译`.class`文件查看生成后的代码是否是你预期的。  
⚠⚠⚠ **请谨慎使用！请谨慎使用！请谨慎使用！自行承担使用 string-interpolator 可能造成的Bug、风险以及损失。不建议在大型项目中使用。**



## 💿集成方式
目前支持的项目类型：  
- **IDEA Maven项目**
- **IDEA普通项目（不带Maven）**
- **Android Studio**  

由于Eclipse 采用了自己的编译器，并没有使用javac编译器，所以string-interpolator暂**不支持在Eclipse中使用**。

### IDEA Maven 集成
```xml
<dependency>
  <groupId>com.github.GG-A</groupId>
  <artifactId>string-interpolator</artifactId>
  <version>0.0.2</version>
</dependency>
```

### IDEA普通项目（不带Maven）
1. 下载 [string-interpolator.jar](https://repo1.maven.org/maven2/com/github/GG-A/string-interpolator/)  包到你的项目中，记得右键（**Add as Library...**）
2. 进入设置`Settings -- build, execution, deployment -- compiler -- Annotation Processors` ，选中自己的项目，勾选 `Enable annotation processing` 即可。

### Android Studio
```
dependencies {
    compileOnly 'com.github.GG-A:string-interpolator:0.0.2'
    annotationProcessor 'com.github.GG-A:string-interpolator:0.0.2'
}
```



## ⭐点个赞哟
如果你喜欢 string-interpolator，感觉 string-interpolator 帮助到了你，可以点右上角 **Star** 支持一下哦，感谢感谢！

## Copyright

   Copyright (C) 2021 **GG-A**, <yiyikela@qq.com, https://github.com/GG-A/string-interpolator>
 
   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.



