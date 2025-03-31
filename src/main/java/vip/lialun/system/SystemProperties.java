package vip.lialun.system;

import vip.lialun.string.Check;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.*;

import static vip.lialun.string.Check.*;

/**
 * 系统信息相关
 *
 * @author lialun
 */
@SuppressWarnings({"WeakerAccess", "unused",})
public class SystemProperties {

    /**
     * 获取本机IPv4
     *
     * @return 本机IPv4列表。没有数据则返回空列表
     */
    public static List<String> resolveIPv4() {
        Set<String> ips = new HashSet<>();
        Enumeration<NetworkInterface> ns;
        try {
            ns = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e) {
            return Collections.emptyList();
        }
        while (ns.hasMoreElements()) {
            NetworkInterface n = ns.nextElement();
            Enumeration<InetAddress> is = n.getInetAddresses();
            while (is.hasMoreElements()) {
                InetAddress i = is.nextElement();
                if (isIPv4(i.getHostAddress())) {
                    ips.add(i.getHostAddress());
                }
            }
        }
        return new ArrayList<>(ips);
    }

    /**
     * 获取本机内网IPv4
     *
     * @return 本机内网IPv4列表。没有数据则返回空列表
     */
    public static List<String> resolveInternalIPv4() {
        List<String> ips = resolveIPv4();
        ips.removeIf(s -> !isInternalIPv4(s));
        return ips;
    }

    /**
     * 获取本机公网IPv4
     *
     * @return 本机公网IPv4列表。没有数据则返回空列表
     */
    public static List<String> resolvePublicIPv4() {
        List<String> ips = resolveIPv4();
        ips.removeIf(s -> !isPublicIPv4(s));
        return ips;
    }

    /**
     * 得到hostname
     *
     * @return hostname。获取不到则空字符串
     */
    public static String getHostName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            return "";
        }
    }

    /**
     * 获取当前操作系统
     *
     * @return 操作系统{@link OS}
     */
    public static OS getOS() {
        Properties properties = System.getProperties();
        String os = properties.getProperty("os.name");
        if (Check.isNullOrEmpty(os)) {
            return OS.OTHER;
        }
        if (os.startsWith("Linux") || os.startsWith("LINUX")) {
            return OS.LINUX;
        } else if (os.startsWith("Windows")) {
            return OS.WIN;
        } else if (os.startsWith("Mac")) {
            return OS.MAC;
        } else {
            return OS.OTHER;
        }
    }

    public enum OS {
        MAC, WIN, LINUX, OTHER
    }
}
