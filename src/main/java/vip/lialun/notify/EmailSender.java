package vip.lialun.notify;

import vip.lialun.string.Check;
import lombok.NonNull;
import org.apache.commons.mail.*;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * 邮件发送代理类，支持创建多个实例，用于不同邮件服务配置
 *
 * @author lialun
 */
@SuppressWarnings({"unused"})
public final class EmailSender {

    private final String hostName;
    private final String username;
    private final String password;
    private final String senderEmail;
    private final String senderName;

    private final boolean sslEnabled;
    private final boolean tlsEnabled;
    private final boolean tlsRequired;
    private final String charset;

    // 这些字段仅用于测试目的
    private SimpleEmail mockSimpleEmailForTest;
    private HtmlEmail mockHtmlEmailForTest;

    /**
     * 创建邮件代理实例的构建器
     */
    public static class Builder {
        private final String hostName;
        private final String username;
        private final String password;
        private final String senderEmail;
        private String senderName;
        private boolean sslEnabled = true;
        private boolean tlsEnabled = true;
        private boolean tlsRequired = true;
        private String charset = "UTF-8";

        /**
         * 构建器构造函数，设置必要参数
         *
         * @param hostName    SMTP服务器地址
         * @param username    SMTP认证用户名
         * @param password    SMTP认证密码
         * @param senderEmail 发件人邮箱
         */
        public Builder(@NonNull String hostName, @NonNull String username,
                       @NonNull String password, @NonNull String senderEmail) {
            this.hostName = hostName;
            this.username = username;
            this.password = password;
            this.senderEmail = senderEmail;
            this.senderName = senderEmail;
        }

        /**
         * 设置发件人显示名称
         */
        public Builder senderName(String senderName) {
            this.senderName = senderName;
            return this;
        }

        /**
         * 配置SSL连接
         */
        public Builder sslEnabled(boolean sslEnabled) {
            this.sslEnabled = sslEnabled;
            return this;
        }

        /**
         * 配置TLS启用
         */
        public Builder tlsEnabled(boolean tlsEnabled) {
            this.tlsEnabled = tlsEnabled;
            return this;
        }

        /**
         * 配置TLS要求
         */
        public Builder tlsRequired(boolean tlsRequired) {
            this.tlsRequired = tlsRequired;
            return this;
        }

        /**
         * 配置字符集
         */
        public Builder charset(String charset) {
            if (!Check.isNullOrEmpty(charset)) {
                this.charset = charset;
            }
            return this;
        }

        /**
         * 构建邮件代理实例
         */
        public EmailSender build() {
            return new EmailSender(this);
        }
    }

    /**
     * 使用EmailProperties创建EmailSender实例
     *
     * @param properties EmailProperties配置参数
     * @return EmailSender实例
     */
    public static EmailSender fromProperties(@NonNull EmailProperties properties) {
        return new Builder(
                properties.getHost(),
                properties.getUsername(),
                properties.getPassword(),
                properties.getSender())
                .senderName(properties.getSenderName())
                .sslEnabled(properties.isSslEnabled())
                .tlsEnabled(properties.isTlsEnabled())
                .tlsRequired(properties.isTlsRequired())
                .charset(properties.getCharset())
                .build();
    }

    /**
     * 私有构造函数，通过Builder创建实例
     */
    private EmailSender(Builder builder) {
        this.hostName = builder.hostName;
        this.username = builder.username;
        this.password = builder.password;
        this.senderEmail = builder.senderEmail;
        this.senderName = builder.senderName;
        this.sslEnabled = builder.sslEnabled;
        this.tlsEnabled = builder.tlsEnabled;
        this.tlsRequired = builder.tlsRequired;
        this.charset = builder.charset;
    }

    /**
     * 发送纯文本邮件
     *
     * @param subject 邮件主题
     * @param message 邮件内容
     * @param emails  收件人列表
     * @throws EmailException 邮件发送异常
     */
    public void sendTextEmail(@NonNull String subject, @NonNull String message,
                              @NonNull List<String> emails) throws EmailException {
        if (emails.isEmpty()) {
            return;
        }
        SimpleEmail email = new SimpleEmail();
        configureEmail(email);
        addRecipients(email, emails);
        email.setSubject(subject);
        email.setMsg(Objects.toString(message, ""));
        email.send();
    }

    /**
     * 发送HTML格式邮件
     *
     * @param subject     邮件主题
     * @param htmlMessage HTML格式邮件内容
     * @param emails      收件人列表
     * @throws EmailException 邮件发送异常
     */
    public void sendHtmlEmail(@NonNull String subject, @NonNull String htmlMessage,
                              @NonNull List<String> emails) throws EmailException {
        if (emails.isEmpty()) {
            return;
        }
        HtmlEmail email = new HtmlEmail();
        configureEmail(email);
        addRecipients(email, emails);
        email.setSubject(subject);
        email.setHtmlMsg(Objects.toString(htmlMessage, ""));
        email.send();
    }

    /**
     * 发送带单个附件的HTML邮件
     *
     * @param subject    邮件主题
     * @param message    HTML邮件内容
     * @param attachment 附件
     * @param emails     收件人列表
     * @throws EmailException 邮件发送异常
     * @throws IOException    附件处理异常
     */
    public void sendEmailWithAttachment(@NonNull String subject, @NonNull String message,
                                        File attachment, @NonNull List<String> emails)
            throws EmailException, IOException {
        if (emails.isEmpty()) {
            return;
        }
        HtmlEmail email = new HtmlEmail();
        configureEmail(email);
        addRecipients(email, emails);
        email.setSubject(subject);
        email.setHtmlMsg(Objects.toString(message, ""));
        //添加附件
        if (attachment != null) {
            try {
                EmailAttachment emailAttachment = new EmailAttachment();
                emailAttachment.setPath(attachment.getPath());
                emailAttachment.setDisposition(EmailAttachment.ATTACHMENT);
                emailAttachment.setName(attachment.getName());
                email.attach(attachment);
            } catch (EmailException e) {
                if (e.getCause() instanceof IOException) {
                    throw (IOException) e.getCause();
                }
                throw e;
            }
        }
        email.send();
    }

    /**
     * 发送带多个附件的HTML邮件
     *
     * @param subject     邮件主题
     * @param message     HTML邮件内容
     * @param attachments 附件列表
     * @param emails      收件人列表
     * @throws EmailException 邮件发送异常
     * @throws IOException    附件处理异常
     */
    public void sendEmailWithAttachments(@NonNull String subject, @NonNull String message,
                                         List<File> attachments, @NonNull List<String> emails)
            throws EmailException, IOException {
        if (emails.isEmpty()) {
            return;
        }
        HtmlEmail email = new HtmlEmail();
        configureEmail(email);
        addRecipients(email, emails);
        email.setSubject(subject);
        email.setHtmlMsg(Objects.toString(message, ""));

        // 添加多个附件
        if (attachments != null && !attachments.isEmpty()) {
            try {
                for (File attachment : attachments) {
                    if (attachment != null && attachment.exists()) {
                        EmailAttachment emailAttachment = new EmailAttachment();
                        emailAttachment.setPath(attachment.getPath());
                        emailAttachment.setDisposition(EmailAttachment.ATTACHMENT);
                        emailAttachment.setName(attachment.getName());
                        email.attach(attachment);
                    }
                }
            } catch (EmailException e) {
                if (e.getCause() instanceof IOException) {
                    throw (IOException) e.getCause();
                }
                throw e;
            }
        }

        email.send();
    }

    /**
     * 配置邮件基本参数
     */
    private void configureEmail(Email email) throws EmailException {
        email.setCharset(charset);
        email.setHostName(hostName);
        email.setSSLOnConnect(sslEnabled);
        email.setStartTLSEnabled(tlsEnabled);
        email.setStartTLSRequired(tlsRequired);
        email.setAuthentication(username, password);
        email.setFrom(senderEmail, senderName);
    }

    /**
     * 添加收件人
     */
    private void addRecipients(Email email, List<String> emails) throws EmailException {
        for (String m : emails) {
            email.addTo(m);
        }
    }
}
