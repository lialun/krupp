package ai.bailian.string;

import ai.bailian.BaseTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CheckTest extends BaseTest {
    @Test
    public void checkEmail() {
        assertTrue(Check.isEmail("a@b.com"));
        assertTrue(Check.isEmail("a@b-c.com"));
        assertTrue(Check.isEmail("a@163.com.cn"));
        assertTrue(Check.isEmail("XYZ@ABCD.COM"));

        assertFalse(Check.isEmail("@b.com"));
        assertFalse(Check.isEmail("a@.com"));
        assertFalse(Check.isEmail("a@b.123"));
    }

    @Test
    public void isDigit() {
        assertTrue(Check.isDigit("0"));
        assertTrue(Check.isDigit("1"));
        assertTrue(Check.isDigit("-1"));
        assertTrue(Check.isDigit("12345"));
        assertTrue(Check.isDigit("12345"));
        assertFalse(Check.isDigit("-0"));
        assertFalse(Check.isDigit("123$"));
        assertFalse(Check.isDigit("123.23"));
        assertFalse(Check.isDigit("abc"));
    }

    @Test
    public void isDecimals() {
        assertTrue(Check.isDecimals("12345.6"));
        assertTrue(Check.isDecimals("123"));
        assertFalse(Check.isDecimals("123%"));
        assertFalse(Check.isDecimals("abc"));
    }

    @Test
    public void isChinese() {
        assertTrue(Check.isChinese("阿"));
        assertTrue(Check.isChinese("测试"));
        assertFalse(Check.isChinese("测 试"));
        assertFalse(Check.isChinese("123"));
        assertFalse(Check.isChinese("abc"));
        assertFalse(Check.isChinese(",。、"));
    }

    @Test
    public void isURL() {
        assertTrue(Check.isURL("http://www.a.com/adf?a=2312312"));
        assertTrue(Check.isURL("http://a.com"));
        assertTrue(Check.isURL("https://a.com"));
        assertTrue(Check.isURL("a.com"));
        assertFalse(Check.isURL("abc"));
        assertFalse(Check.isURL(",。、"));
    }

    @Test
    public void isIpv4() {
        assertTrue(Check.isIPv4("127.0.0.1"));
        assertTrue(Check.isIPv4("8.8.8.8"));
        assertFalse(Check.isIPv4("127.0.0"));
        assertFalse(Check.isIPv4("123"));
    }

    @Test
    public void isInternalLoopIPv4() {
        assertTrue(Check.isInternalLoopIPv4("127.0.0.1"));
        assertTrue(Check.isInternalLoopIPv4("127.0.1.1"));
        assertFalse(Check.isInternalLoopIPv4("1.2.3.4"));
        assertFalse(Check.isInternalLoopIPv4("1"));
    }

    @Test
    public void isInternalIPv4() {
        assertTrue(Check.isInternalIPv4("192.168.1.1"));
        assertTrue(Check.isInternalIPv4("172.16.1.1"));
        assertTrue(Check.isInternalIPv4("10.0.0.1"));
        assertFalse(Check.isInternalIPv4("8.8.8.8"));
        assertFalse(Check.isInternalIPv4("1"));
    }

    @Test
    public void isPublicIPv4() {
        assertTrue(Check.isPublicIPv4("8.8.8.8"));
        assertFalse(Check.isPublicIPv4("127.0.1.1"));
        assertFalse(Check.isPublicIPv4("192.168.1.1"));
        assertFalse(Check.isPublicIPv4("1"));
    }

    @Test
    public void isPunctuation() {
        assertTrue(Check.isPunctuation(','));
        assertTrue(Check.isPunctuation('.'));
        assertTrue(Check.isPunctuation('，'));
        assertTrue(Check.isPunctuation('。'));
        assertTrue(Check.isPunctuation(' '));
        assertTrue(Check.isPunctuation('　'));
        assertFalse(Check.isPunctuation('a'));
        assertFalse(Check.isPunctuation('啊'));
    }

    @Test
    public void isSBC() {
        assertTrue(Check.isSBC('，'));
        assertTrue(Check.isSBC('。'));
        assertTrue(Check.isSBC('啊'));
        assertFalse(Check.isSBC(','));
        assertFalse(Check.isSBC('.'));
        assertFalse(Check.isSBC('a'));
    }

    @Test
    public void isSBCPunctuation() {
        assertTrue(Check.isSBCPunctuation('，'));
        assertTrue(Check.isSBCPunctuation('。'));
        assertFalse(Check.isSBCPunctuation('啊'));
        assertFalse(Check.isSBCPunctuation(','));
        assertFalse(Check.isSBCPunctuation('.'));
        assertFalse(Check.isSBCPunctuation('a'));
    }
}