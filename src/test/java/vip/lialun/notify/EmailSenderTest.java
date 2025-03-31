package vip.lialun.notify;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.commons.mail.SimpleEmail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmailSenderTest {
    
    private EmailSender emailSender;
    
    @Mock
    private SimpleEmail mockSimpleEmail;
    
    @Mock
    private HtmlEmail mockHtmlEmail;
    
    @Captor
    private ArgumentCaptor<String> stringCaptor;
    
    @BeforeEach
    public void setUp() throws Exception {
        EmailSender.Builder builder = new EmailSender.Builder(
                "smtp.example.com",
                "testuser",
                "testpassword",
                "sender@example.com"
        ).senderName("Test Sender");
        
        emailSender = builder.build();
        
        // 使用反射来注入mock对象
        mockEmailCreation();
    }
    
    private void mockEmailCreation() throws Exception {
        // 注意：这个方法需要根据您的实际实现进行调整
        // 这里只是一个简化的示例，实际上需要找到创建Email对象的地方并进行替换
        
        // 例如，如果您的代码在内部创建了SimpleEmail对象，您可以使用反射注入mock对象：
        try {
            // 创建一个方法来提供模拟的SimpleEmail对象
            setPrivateField(emailSender, "mockSimpleEmailForTest", mockSimpleEmail);
            setPrivateField(emailSender, "mockHtmlEmailForTest", mockHtmlEmail);
            
            // 设置测试模式
            System.setProperty("test.mode", "true");
            
            // 注意：您的EmailSender类可能需要特殊的测试钩子来使用这些模拟对象
            // 这只是一个示例，具体实现需要根据您的代码结构调整
        } catch (Exception e) {
            System.err.println("⚠️ 警告: 无法设置模拟对象，测试可能会尝试发送真实邮件: " + e.getMessage());
        }
    }
    
    @Test
    public void testBuilderCreatesCorrectInstance() {
        // 测试Builder正确创建EmailSender实例
        EmailSender sender = new EmailSender.Builder(
                "smtp.test.com",
                "username",
                "password",
                "test@example.com")
                .senderName("Test Name")
                .sslEnabled(false)
                .tlsEnabled(false)
                .charset("UTF-16")
                .build();
        
        assertEquals("smtp.test.com", getPrivateField(sender, "hostName"));
        assertEquals("username", getPrivateField(sender, "username"));
        assertEquals("password", getPrivateField(sender, "password"));
        assertEquals("test@example.com", getPrivateField(sender, "senderEmail"));
        assertEquals("Test Name", getPrivateField(sender, "senderName"));
        assertEquals(false, getPrivateField(sender, "sslEnabled"));
        assertEquals(false, getPrivateField(sender, "tlsEnabled"));
        assertEquals("UTF-16", getPrivateField(sender, "charset"));
    }
    
    @Test
    public void testSendTextEmail() throws EmailException {
        // ⚠️ 注意：这个测试方法可能需要调整，取决于您如何实现模拟对象的注入
        
        // 准备测试数据
        String subject = "Test Subject";
        String message = "Test Message";
        List<String> recipients = Arrays.asList("recipient1@example.com", "recipient2@example.com");
        
        // 配置mock行为
        doNothing().when(mockSimpleEmail).send();
        
        // 执行测试
        // 如果您不能注入模拟对象，这个测试可能会尝试发送真实邮件
        // 在实际部署前应当确保正确设置了模拟对象
        try {
            emailSender.sendTextEmail(subject, message, recipients);
            
            // 验证结果
            verify(mockSimpleEmail).setSubject(subject);
            verify(mockSimpleEmail).setMsg(message);
            verify(mockSimpleEmail, times(2)).addTo(stringCaptor.capture());
            List<String> capturedRecipients = stringCaptor.getAllValues();
            assertTrue(capturedRecipients.containsAll(recipients));
            verify(mockSimpleEmail).send();
        } catch (Exception e) {
            System.err.println("⚠️ 测试失败，可能是因为无法正确模拟邮件发送: " + e.getMessage());
            fail("测试失败: " + e.getMessage());
        }
    }
    
    @Test
    public void testSendHtmlEmail() throws EmailException {
        // 与上一个测试类似，可能需要调整
        
        // 准备测试数据
        String subject = "HTML Test Subject";
        String htmlMessage = "<h1>Test HTML Message</h1>";
        List<String> recipients = Arrays.asList("recipient1@example.com");
        
        // 配置mock行为
        doNothing().when(mockHtmlEmail).send();
        
        try {
            // 执行测试
            emailSender.sendHtmlEmail(subject, htmlMessage, recipients);
            
            // 验证结果
            verify(mockHtmlEmail).setSubject(subject);
            verify(mockHtmlEmail).setHtmlMsg(htmlMessage);
            verify(mockHtmlEmail).addTo("recipient1@example.com");
            verify(mockHtmlEmail).send();
        } catch (Exception e) {
            System.err.println("⚠️ 测试失败，可能是因为无法正确模拟邮件发送: " + e.getMessage());
            fail("测试失败: " + e.getMessage());
        }
    }
    
    @Test
    public void testSendEmailWithAttachment() throws EmailException, IOException {
        // 准备测试数据
        String subject = "Attachment Test";
        String message = "<p>Test with attachment</p>";
        List<String> recipients = Arrays.asList("recipient@example.com");
        File mockFile = mock(File.class);
        when(mockFile.exists()).thenReturn(true);
        when(mockFile.getName()).thenReturn("test.pdf");
        when(mockFile.getPath()).thenReturn("/tmp/test.pdf");
        
        // 配置mock行为
        doNothing().when(mockHtmlEmail).send();
        
        try {
            // 执行测试
            emailSender.sendEmailWithAttachment(subject, message, mockFile, recipients);
            
            // 验证结果
            verify(mockHtmlEmail).setSubject(subject);
            verify(mockHtmlEmail).setHtmlMsg(message);
            verify(mockHtmlEmail).addTo("recipient@example.com");
            verify(mockHtmlEmail).attach(mockFile);
            verify(mockHtmlEmail).send();
        } catch (Exception e) {
            System.err.println("⚠️ 测试失败，可能是因为无法正确模拟邮件发送: " + e.getMessage());
            fail("测试失败: " + e.getMessage());
        }
    }
    
    @Test
    public void testEmailExceptionHandling() {
        // 准备测试数据
        String subject = "Exception Test";
        String message = "Test Message";
        List<String> recipients = Arrays.asList("recipient@example.com");
        
        // 配置mock行为 - 模拟异常
        try {
            doThrow(new EmailException("Test exception")).when(mockSimpleEmail).send();
            
            // 执行测试并验证异常
            Exception exception = assertThrows(EmailException.class, () -> {
                emailSender.sendTextEmail(subject, message, recipients);
            });
            
            assertEquals("Test exception", exception.getMessage());
        } catch (Exception e) {
            System.err.println("⚠️ 测试失败，可能是因为无法正确模拟邮件发送: " + e.getMessage());
            fail("测试失败: " + e.getMessage());
        }
    }
    
    @Test
    public void testSendEmailWithEmptyRecipientList() throws EmailException {
        // 准备测试数据
        String subject = "Test Subject";
        String message = "Test Message";
        List<String> emptyRecipients = List.of(); // JDK 9+ 的空列表创建方式
        
        // 执行测试
        emailSender.sendTextEmail(subject, message, emptyRecipients);
        
        // 验证结果 - 当收件人列表为空时，不应该尝试发送邮件
        verifyNoInteractions(mockSimpleEmail);
    }
    
    // 辅助方法 - 使用反射获取私有字段值
    @SuppressWarnings("unchecked")
    private <T> T getPrivateField(Object obj, String fieldName) {
        try {
            Field field = obj.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return (T) field.get(obj);
        } catch (Exception e) {
            throw new RuntimeException("获取私有字段失败: " + fieldName, e);
        }
    }
    
    // 辅助方法 - 使用反射设置私有字段值
    private void setPrivateField(Object obj, String fieldName, Object value) {
        try {
            Field field = obj.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(obj, value);
        } catch (Exception e) {
            throw new RuntimeException("设置私有字段失败: " + fieldName, e);
        }
    }
} 