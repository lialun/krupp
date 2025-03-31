# EmailSender

一个包含基本功能的Java邮件发送库，支持文本邮件、HTML邮件以及附件发送。

## 基本用法

### 1. 直接使用

```java
// 创建EmailSender实例
EmailSender emailSender = new EmailSender.Builder(
        "smtp.example.com",           // SMTP服务器地址
        "your-username",              // 用户名
        "your-password",              // 密码
        "sender@example.com")         // 发件人邮箱
    .senderName("Your Name")          // （可选）发件人显示名称
    .build();

// 发送简单文本邮件
try {
    List<String> recipients = Arrays.asList("recipient1@example.com", "recipient2@example.com");
    emailSender.sendTextEmail("邮件主题", "邮件内容", recipients);
} catch (EmailException e) {
    // 处理发送异常
    e.printStackTrace();
}

// 发送带附件的邮件
try {
    List<String> recipients = Arrays.asList("recipient@example.com");
    File attachment = new File("/path/to/document.pdf");
    emailSender.sendEmailWithAttachment(
        "带附件的邮件", 
        "<p>请查看附件</p>", 
        attachment, 
        recipients
    );
} catch (EmailException | IOException e) {
    e.printStackTrace();
}
```

### 2. 在Spring中使用

```java
@Configuration
public class EmailConfig {
    
    @Bean
    @ConfigurationProperties(prefix = "email")
    public EmailProperties emailProperties() {
        return new EmailProperties();
    }
    
    @Bean
    public EmailSender emailSender(EmailProperties emailProperties) {
        return EmailSender.fromProperties(emailProperties);
    }
}
```

#### 使用说明

1. 这种方式使用`@ConfigurationProperties(prefix = "email")`注解来自动绑定所有以`email`为前缀的属性到`EmailProperties`对象中。

2. 在`application.properties`或`application.yml`中配置相关属性：

```properties
# application.properties 示例
email.host=smtp.example.com
email.username=your-username
email.password=your-password
email.sender=sender@example.com
email.senderName=发件人姓名
email.sslEnabled=true
email.tlsEnabled=true
email.tlsRequired=true
email.charset=UTF-8
```

或

```yaml
# application.yml 示例
email:
  host: smtp.example.com
  username: your-username
  password: your-password
  sender: sender@example.com
  senderName: 发件人姓名
  sslEnabled: true
  tlsEnabled: true
  tlsRequired: true
  charset: UTF-8
```
# FeiShuSender

一个包含基本功能的Java飞书消息发送库，支持发送富文本消息到飞书群组。

## 基本用法

### 1. 直接使用

```java
// 创建FeiShuSender实例
FeiShuSender feiShuSender = new FeiShuSender.Builder(
        "your-app-id",              // 飞书应用ID
        "your-app-secret")          // 飞书应用密钥
    .build();

// 发送简单文本消息
try {
    // 创建文本内容
    List<Map<String, String>> textContent = FeiShuSender.createTextContent("这是一条测试消息");
    
    // 发送消息到群组
    feiShuSender.sendMessageToGroup(
        "应用名称",                   // 应用名称，会显示在消息标题中
        "oc_xxxxxxxxxx",           // 群组ID
        "消息标题",                   // 消息标题
        List.of(textContent)        // 消息内容列表
    );
} catch (Exception e) {
    // 处理发送异常
    e.printStackTrace();
}

// 发送带链接的消息
try {
    // 创建带链接的文本内容
    List<Map<String, String>> contentWithLink = FeiShuSender.createTextWithLink(
        "这里是文本内容，点击 ",        // 文本内容
        "链接",                      // 链接文字
        "https://example.com"       // 链接地址
    );
    
    // 发送消息到群组
    feiShuSender.sendMessageToGroup(
        "应用名称",                   // 应用名称
        "oc_xxxxxxxxxx",           // 群组ID
        "消息标题",                   // 消息标题
        List.of(contentWithLink)    // 消息内容列表
    );
} catch (Exception e) {
    e.printStackTrace();
}
```

### 2. 在Spring中使用

```java
@Configuration
public class FeiShuConfig {
    
    @Bean
    @ConfigurationProperties(prefix = "feishu")
    public FeiShuProperties feiShuProperties() {
        return new FeiShuProperties();
    }
    
    @Bean
    public FeiShuSender feiShuSender(FeiShuProperties feiShuProperties) {
        return FeiShuSender.fromProperties(feiShuProperties);
    }
}
```

#### 使用说明

1. 这种方式使用`@ConfigurationProperties(prefix = "feishu")`注解来自动绑定所有以`feishu`为前缀的属性到`FeiShuProperties`对象中。

2. 在`application.properties`或`application.yml`中配置相关属性：

```properties
# application.properties 示例
feishu.appId=your-app-id
feishu.appSecret=your-app-secret
```

或

```yaml
# application.yml 示例
feishu:
  appId: your-app-id
  appSecret: your-app-secret
```

## 高级用法

### 自定义富文本消息

飞书支持多种富文本格式，你可以创建复杂的消息内容：

```java
// 创建一条包含多种元素的消息
List<List<Map<String, String>>> content = new ArrayList<>();

// 第一行：纯文本和链接
List<Map<String, String>> line1 = new ArrayList<>();
line1.add(Map.of("tag", "text", "text", "这里是第一行文本，包含"));
line1.add(Map.of("tag", "a", "text", "一个链接", "href", "https://example.com"));
content.add(line1);

// 第二行：加粗文本
List<Map<String, String>> line2 = new ArrayList<>();
line2.add(Map.of("tag", "text", "text", "这里是"));
line2.add(Map.of("tag", "text", "text", "粗体文本", "style", Map.of("bold", true)));
content.add(line2);

// 发送消息
feiShuSender.sendMessageToGroup("应用名称", "oc_xxxxxxxxxx", "富文本消息", content);
```

更多飞书消息格式请参考[飞书开放平台文档](https://open.feishu.cn/document/uAjLw4CM/ukTMukTMukTM/im-v1/message/create)。 