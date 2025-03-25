package ai.bailian.notify;

import ai.bailian.http.methods.HttpPost;
import ai.bailian.http.simple.HttpRequest;
import ai.bailian.logging.Log;
import ai.bailian.logging.LogFactory;
import ai.bailian.string.Check;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Joiner;
import org.apache.http.HttpStatus;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 消息通知工具类
 *
 * @author lialun
 */
@SuppressWarnings({"unused", "WeakerAccess", "UnusedReturnValue"})
public final class NotifyUtils {
    private static final Log log = LogFactory.getLog(NotifyUtils.class);


    /**
     * 发送短信
     *
     * @return 是否发送成功.发送至多个手机时, 有一个失败即为失败
     */
    public static boolean sendSMS(String message, Collection<String> phones) {
        if (Check.isNullOrEmpty(message) || phones == null || phones.isEmpty()) {
            return false;
        }
        Set<String> phoneWithoutDuplicate = phones.stream().map(String::trim).collect(Collectors.toSet());
        return sendSMS(message, Joiner.on(",").join(new ArrayList<>(phoneWithoutDuplicate)));
    }

    /**
     * 发送短信
     *
     * @return 是否发送成功.发送至多个手机时, 有一个失败即为失败
     */
    public static boolean sendSMS(String message, String... phones) {
        return sendSMS(message, Arrays.asList(phones));
    }

//    public static boolean sendEmailWithAttachmentByAws(String subject, String message, File attachment, List<String> emails) throws IOException {
//        if (Check.isNullOrEmpty(subject) || emails == null || emails.isEmpty()) {
//            return false;
//        }
//        try {
//            HtmlEmail email = new HtmlEmail();
//            email.setCharset("UTF-8");
//            email.setHostName("email-smtp.us-west-2.amazonaws.com");
//            email.setSSLOnConnect(true);
//            email.setStartTLSEnabled(true);
//            email.setStartTLSRequired(true);
//            email.setAuthentication("AKI123123", "BCei12312");
//            for (String m : emails) {
//                email.addTo(m);
//            }
//            email.setFrom("alarm@mail.bailian-service.com", "Bailian Alarm");
//            email.setSubject(subject);
//            email.setHtmlMsg(Objects.toString(message, ""));
//            //attachment
//            if (attachment != null) {
//                EmailAttachment emailAttachment = new EmailAttachment();
//                emailAttachment.setPath(attachment.getPath());
//                emailAttachment.setDisposition(EmailAttachment.ATTACHMENT);
//                emailAttachment.setName(attachment.getName());
//                email.attach(attachment);
//            }
//            //send
//            email.send();
//            return true;
//        } catch (EmailException e) {
//            //附件导致的IOException单独抛出
//            if (e.getCause() instanceof IOException) {
//                throw (IOException) e.getCause();
//            }
//            log.error("email send failure", e);
//            return false;
//        }
//    }
}
