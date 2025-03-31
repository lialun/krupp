package ai.bailian.notify;

import lombok.Getter;
import lombok.Setter;

/**
 * EmailSender的配置属性类
 *
 * @author lialun
 */
@Getter
@Setter
public class EmailProperties {
    private String host;
    private String username;
    private String password;
    private String sender;
    private String senderName;
    private boolean sslEnabled = true;
    private boolean tlsEnabled = true;
    private boolean tlsRequired = true;
    private String charset = "UTF-8";

    /**
     * 创建默认空实例
     */
    public EmailProperties() {
    }

    /**
     * 创建包含基本配置的实例
     */
    public EmailProperties(String host, String username, String password, String sender) {
        this.host = host;
        this.username = username;
        this.password = password;
        this.sender = sender;
        this.senderName = sender; // 默认发件人名称与邮箱相同
    }

    /**
     * 链式设置发件人名称
     */
    public EmailProperties senderName(String senderName) {
        this.senderName = senderName;
        return this;
    }

    /**
     * 链式设置SSL
     */
    public EmailProperties sslEnabled(boolean sslEnabled) {
        this.sslEnabled = sslEnabled;
        return this;
    }

    /**
     * 链式设置TLS
     */
    public EmailProperties tlsEnabled(boolean tlsEnabled) {
        this.tlsEnabled = tlsEnabled;
        return this;
    }

    /**
     * 链式设置TLS必需
     */
    public EmailProperties tlsRequired(boolean tlsRequired) {
        this.tlsRequired = tlsRequired;
        return this;
    }

    /**
     * 链式设置字符集
     */
    public EmailProperties charset(String charset) {
        this.charset = charset;
        return this;
    }
}