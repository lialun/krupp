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
