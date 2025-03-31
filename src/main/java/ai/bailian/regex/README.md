# RegexUtils 正则表达式工具类

RegexUtils 是一个功能强大的正则表达式工具类，专为 Java 开发者设计，简化了正则表达式的各种常见操作。该工具类提供了模式缓存机制，预定义的常用正则表达式，以及丰富的匹配、提取、替换和分割方法。

## 特性
- **性能优化**：内置 Pattern 对象缓存，避免重复编译相同的正则表达式
- **常用正则表达式**：内置常用的正则表达式常量，如邮箱、手机号、URL等
- **丰富的功能**：匹配、提取、替换、分割等全方位功能
- **捕获组支持**：支持普通捕获组和命名捕获组
- **健壮的设计**：全面的null值处理和异常处理


## 使用示例

### 1. 基本匹配功能

#### 1.1 完全匹配

判断字符串是否完全匹配正则表达式：

```java
// 检查是否是有效的邮箱
boolean isEmail = RegexUtils.matches(RegexUtils.EMAIL, "user@example.com");  // true

// 检查是否是中国手机号
boolean isPhoneNumber = RegexUtils.matches(RegexUtils.CHINA_MOBILE, "13812345678");  // true
```

#### 1.2 包含匹配

判断字符串是否包含匹配正则表达式的部分：

```java
// 检查文本中是否包含中文
boolean hasChinese = RegexUtils.contains(RegexUtils.CHINESE, "Hello世界");  // true

// 检查文本中是否包含URL
boolean hasUrl = RegexUtils.contains(RegexUtils.URL, "访问 https://example.com 了解更多");  // true
```

### 2. 提取功能

#### 2.1 提取匹配内容

提取文本中匹配正则表达式的内容：

```java
// 提取第一个匹配的URL
String firstUrl = RegexUtils.findFirst("https?://[\\w.-]+\\.[\\w]+[\\w./]*", 
                                      "请访问 https://example.com 或 http://test.org");
// 结果: https://example.com

// 提取所有匹配的手机号
List<String> phones = RegexUtils.findAll("1[3-9]\\d{9}", 
                                        "联系方式: 13812345678, 15987654321");
// 结果: ["13812345678", "15987654321"]
```

#### 2.2 提取捕获组

提取捕获组内容：

```java
// 提取日期各部分 (使用捕获组)
String date = "2023-05-15";
String regex = "(\\d{4})-(\\d{2})-(\\d{2})";

// 提取第一个捕获组 (年份)
String year = RegexUtils.extractGroup(regex, date, 1);  // "2023"

// 提取所有捕获组
List<String> dateParts = RegexUtils.extractAllGroups(regex, date);
// 结果: ["2023", "05", "15"]
```

#### 2.3 使用命名捕获组

使用命名捕获组更清晰地提取数据：

```java
// 使用命名捕获组提取个人信息
String text = "姓名: 张三, 年龄: 28, 邮箱: zhangsan@example.com";
String regex = "姓名: (?<name>[^,]+), 年龄: (?<age>\\d+), 邮箱: (?<email>[\\w.@]+)";

List<Map<String, String>> results = RegexUtils.extractNamedGroups(regex, text);
// 结果: 一个包含键值对的Map
// {
//   "name": "张三",
//   "age": "28",
//   "email": "zhangsan@example.com"
// }

// 也可以只提取特定的命名捕获组
List<String> emails = RegexUtils.findAllByGroupName(regex, text, "email");
// 结果: ["zhangsan@example.com"]
```

#### 2.4 提取多次匹配的所有捕获组

从多次匹配中提取所有捕获组：

```java
// 从多个日期中提取年月日
String text = "日期1: 2023-05-15, 日期2: 2023-06-20";
String regex = "(\\d{4})-(\\d{2})-(\\d{2})";

List<List<String>> allMatches = RegexUtils.extractAllMatchesGroups(regex, text);
// 结果: [["2023", "05", "15"], ["2023", "06", "20"]]
```

### 3. 替换功能

#### 3.1 替换所有匹配

替换所有匹配正则表达式的部分：

```java
// 将所有数字替换为星号
String masked = RegexUtils.replaceAll("\\d", "手机号: 13812345678", "*");
// 结果: "手机号: ************"

// 将URL替换为链接标签
String html = RegexUtils.replaceAll(
    "(https?://[\\w.-]+\\.[\\w]+[\\w./]*)",
    "访问 https://example.com 了解更多",
    "<a href=\"$1\">$1</a>"
);
// 结果: "访问 <a href="https://example.com">https://example.com</a> 了解更多"
```

#### 3.2 替换第一个匹配

只替换第一个匹配的内容：

```java
// 只替换第一个出现的数字
String result = RegexUtils.replaceFirst("\\d", "编号123", "X");
// 结果: "编号X23"
```

#### 3.3 删除所有匹配

删除所有匹配正则表达式的部分：

```java
// 删除所有空格
String noSpaces = RegexUtils.removeAll("\\s", "Hello World  Test");
// 结果: "HelloWorldTest"

// 删除所有HTML标签
String plainText = RegexUtils.removeAll("<[^>]*>", "<p>这是<b>一段</b>文本</p>");
// 结果: "这是一段文本"
```

### 4. 分割功能

使用正则表达式分割字符串：

```java
// 按逗号或分号分割
String[] parts = RegexUtils.split("[,;]", "apple,banana;orange,grape");
// 结果: ["apple", "banana", "orange", "grape"]

// 限制分割结果数量
String[] limitedParts = RegexUtils.split("[,;]", "apple,banana;orange,grape", 3);
// 结果: ["apple", "banana", "orange,grape"]
```

### 5. 使用预定义正则表达式

RegexUtils提供了多种预定义的常用正则表达式：

```java
// 检查是否包含中文
boolean hasChinese = RegexUtils.contains(RegexUtils.CHINESE, "Hello世界");  // true

// 验证邮箱
boolean isValidEmail = RegexUtils.matches(RegexUtils.EMAIL, "user@domain.com");  // true

// 验证手机号
boolean isValidMobile = RegexUtils.matches(RegexUtils.CHINA_MOBILE, "13812345678");  // true

// 验证URL
boolean isValidUrl = RegexUtils.matches(RegexUtils.URL, "https://example.com");  // true

// 验证IPv4地址
boolean isValidIp = RegexUtils.matches(RegexUtils.IPV4, "192.168.1.1");  // true

// 验证身份证号
boolean isValidIdCard = RegexUtils.matches(RegexUtils.CHINA_ID_CARD, "110101199001011234");  // true
```

### 6. 高级用法

#### 6.1 自定义Pattern标志

使用自定义的Pattern标志，如忽略大小写：

```java
// 忽略大小写匹配
String regex = "test";
String input = "This is a TEST";
Pattern pattern = RegexUtils.getPattern(regex, Pattern.CASE_INSENSITIVE);
Matcher matcher = pattern.matcher(input);

if (matcher.find()) {
    System.out.println("匹配成功: " + matcher.group());  // 输出: 匹配成功: TEST
}
```

#### 6.2 缓存管理
```java
// 设置缓存大小与过期时间
// 需要在RegexUtils被调用前设置，调用后则已经完成初始化，设置将不会生效
Krupp.REGEX_CACHE_SIZE(500);
Krupp.REGEX_EXPIRE_IN_MINUTES(60);
// 清除缓存
RegexUtils.clearPatternCache();
```

## 最佳实践

1. **优先使用预定义正则表达式**：使用类中预定义的正则表达式常量，它们已经过测试和优化。

2. **避免过于复杂的正则表达式**：复杂的正则表达式难以维护，且可能导致性能问题。适当拆分为多个简单表达式。

3. **注意空值处理**：RegexUtils已内置空值处理，但调用前最好仍然检查输入。

4. **合理使用缓存**：对于频繁使用的正则表达式，RegexUtils的缓存机制可以提高性能。对于很少使用的复杂正则，考虑在使用后清除缓存。

5. **处理特殊字符**：在构建正则表达式时，记得对特殊字符进行转义。
