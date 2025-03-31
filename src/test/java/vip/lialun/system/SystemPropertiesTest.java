package vip.lialun.system;

import vip.lialun.BaseTest;
import vip.lialun.string.Check;
import com.google.common.base.Strings;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SystemPropertiesTest extends BaseTest {
    @Test
    public void resolveIPv4() {
        List<String> ips = SystemProperties.resolveIPv4();
        for (String ip : ips) {
            assertTrue(Check.isIPv4(ip));
        }
    }

    @Test
    public void resolveInternalIPv4() {
        List<String> ips = SystemProperties.resolveInternalIPv4();
        for (String ip : ips) {
            assertTrue(Check.isInternalIPv4(ip));
        }
    }

    @Test
    public void resolvePublicIPv4() {
        List<String> ips = SystemProperties.resolvePublicIPv4();
        for (String ip : ips) {
            assertTrue(Check.isPublicIPv4(ip));
        }
    }

    @Test
    public void getHostName() {
        assertFalse(Strings.isNullOrEmpty(SystemProperties.getHostName()));
        System.out.println("hostname: " + SystemProperties.getHostName());
    }

    @Test
    public void getOS() {
        assertNotEquals(SystemProperties.getOS(), SystemProperties.OS.OTHER);
        System.out.println("OS: " + SystemProperties.getOS());
    }

}