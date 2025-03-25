package ai.bailian.notify;

import ai.bailian.BaseTest;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class NotifyUtilsTest extends BaseTest {


    @Test
    public void sendSMS() {
        assertTrue(NotifyUtils.sendSMS("test send SMS", "13682079876"));
    }

    @Test
    public void sendWeChat() {
        assertTrue(NotifyUtils.sendWeChat("Junit微信发送测试", "lialun"));
    }

    @Test
    public void sendEmail() {
        assertTrue(NotifyUtils.sendEmail("Junit邮件发送测试-标题", String.join("", Collections.nCopies(1000, "Junit邮件发送测试-文本")), "lialun@bailian.ai"));
    }
}