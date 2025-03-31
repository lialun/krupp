package vip.lialun.string;

import java.util.regex.Pattern;

import static vip.lialun.regex.RegexUtils.CHINESE;

/**
 * 字符串格式验证工具类
 *
 * @author lialun
 */
@SuppressWarnings("WeakerAccess")
public class Check {
    /**
     * 验证字符串是否符合email格式
     *
     * @param email email地址
     * @return 是否符合email格式
     */
    public static boolean isEmail(String email) {
        String regex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return Pattern.matches(regex, email);
    }

    /**
     * 验证整数
     *
     * @param digit 正数或者负数
     * @return 验证是否为整数
     */
    public static boolean isDigit(String digit) {
        String zero = "0";
        String regexOneDigit = "^-?[1-9]$";
        String regexMoreThanTwoDigit = "^-?[1-9]\\d+$";
        return Pattern.matches(regexMoreThanTwoDigit, digit) ||
                Pattern.matches(regexOneDigit, digit) || zero.equals(digit);
    }

    /**
     * 验证整数和浮点数
     *
     * @param decimals 浮点数
     * @return 验证是否为浮点数
     */
    public static boolean isDecimals(String decimals) {
        String regex = "^-?[1-9]\\d+(\\.\\d+)?$";
        return Pattern.matches(regex, decimals);
    }

    /**
     * 验证中文字符(串)
     *
     * @param chinese 中文字符(串)
     * @return 验证是否为中文字符(串)
     */
    public static boolean isChinese(String chinese) {
        String regex = "^" + CHINESE + "+$";
        return Pattern.matches(regex, chinese);
    }

    /**
     * 验证http(s) URL地址
     *
     * @param url http(s) URL地址
     * @return 验证是否符合http(s) URL地址格式
     */
    public static boolean isURL(String url) {
        String regex = "(https?://(w{3}\\.)?)?\\w+\\.\\w+(\\.[a-zA-Z]+)*(:\\d{1,5})?(/\\w*)*(\\??(.+=.*)?(&.+=.*)?)?";
        return Pattern.matches(regex, url);
    }

    public static boolean isIPv4(String ip) {
        if (isNullOrEmpty(ip) || ip.length() < 7 || ip.length() > 15) {
            return false;
        }
        String regex = "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}";
        return Pattern.matches(regex, ip);
    }


    /**
     * 判断IPv4是否为本地回环地址
     *
     * @param ip IPv4,例如127.0.0.1
     * @return 是否为回环地址
     */
    public static boolean isInternalLoopIPv4(String ip) {
        return !isNullOrEmpty(ip) && ip.startsWith("127.");
    }

    /**
     * 判断IPv4是否为内网IP
     *
     * @param ip IPv4,例如192.168.0.1
     * @return 是否为内网IP
     */
    public static boolean isInternalIPv4(String ip) {
        if (isNullOrEmpty(ip) || !Check.isIPv4(ip)) {
            return false;
        }
        if (isInternalLoopIPv4(ip)) {
            return false;
        }
        try {
            byte[] addr = textToNumericFormatV4(ip);
            final byte b0 = addr[0];
            final byte b1 = addr[1];
            //10.x.x.x/8
            final byte SECTION_1 = 0x0A;
            //172.16.x.x/12
            final byte SECTION_2 = (byte) 0xAC;
            final byte SECTION_3 = (byte) 0x10;
            final byte SECTION_4 = (byte) 0x1F;
            //192.168.x.x/16
            final byte SECTION_5 = (byte) 0xC0;
            final byte SECTION_6 = (byte) 0xA8;
            switch (b0) {
                case SECTION_1:
                    return true;
                case SECTION_2:
                    if (b1 >= SECTION_3 && b1 <= SECTION_4) {
                        return true;
                    }
                case SECTION_5:
                    switch (b1) {
                        case SECTION_6:
                            return true;
                    }
                default:
                    return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 判断IPv4是否为公网IP
     *
     * @param ip IPv4,例如8.8.8.8
     * @return 是否为公网IP
     */
    public static boolean isPublicIPv4(String ip) {
        return Check.isIPv4(ip) && !isInternalLoopIPv4(ip) && !isInternalIPv4(ip);
    }

    public static boolean isNullOrEmpty(String string) {
        return string == null || string.isEmpty();
    }

    public static <T> T checkNotNull(T reference) {
        if (reference == null) {
            throw new NullPointerException();
        }
        return reference;
    }

    public static <T> T checkNotNull(T reference, Object errorMessage) {
        if (reference == null) {
            throw new NullPointerException(String.valueOf(errorMessage));
        }
        return reference;
    }

    private final static int INADDR4SZ = 4;

    /**
     * Converts IPv4 address in its textual presentation form
     * into its numeric binary form.
     *
     * @param src a String representing an IPv4 address in standard format
     * @return a byte array representing the IPv4 numeric address
     */
    @SuppressWarnings("fallthrough")
    private static byte[] textToNumericFormatV4(String src) {
        byte[] res = new byte[INADDR4SZ];

        long tmpValue = 0;
        int currByte = 0;

        int len = src.length();
        if (len == 0 || len > 15) {
            return null;
        }
        /*
         * COPY FROM sun.net.util.IPAddressUtil
         *
         * When only one part is given, the value is stored directly in
         * the network address without any byte rearrangement.
         *
         * When a two part address is supplied, the last part is
         * interpreted as a 24-bit quantity and placed in the right
         * most three bytes of the network address. This makes the
         * two part address format convenient for specifying Class A
         * network addresses as net.host.
         *
         * When a three part address is specified, the last part is
         * interpreted as a 16-bit quantity and placed in the right
         * most two bytes of the network address. This makes the
         * three part address format convenient for specifying
         * Class B net- work addresses as 128.net.host.
         *
         * When four parts are specified, each is interpreted as a
         * byte of data and assigned, from left to right, to the
         * four bytes of an IPv4 address.
         *
         * We determine and parse the leading parts, if any, as single
         * byte values in one pass directly into the resulting byte[],
         * then the remainder is treated as a 8-to-32-bit entity and
         * translated into the remaining bytes in the array.
         */
        for (int i = 0; i < len; i++) {
            char c = src.charAt(i);
            if (c == '.') {
                if (tmpValue < 0 || tmpValue > 0xff || currByte == 3) {
                    return null;
                }
                res[currByte++] = (byte) (tmpValue & 0xff);
                tmpValue = 0;
            } else {
                int digit = Character.digit(c, 10);
                if (digit < 0) {
                    return null;
                }
                tmpValue *= 10;
                tmpValue += digit;
            }
        }
        if (tmpValue < 0 || tmpValue >= (1L << ((4 - currByte) * 8))) {
            return null;
        }
        switch (currByte) {
            case 0:
                res[0] = (byte) ((tmpValue >> 24) & 0xff);
            case 1:
                res[1] = (byte) ((tmpValue >> 16) & 0xff);
            case 2:
                res[2] = (byte) ((tmpValue >> 8) & 0xff);
            case 3:
                res[3] = (byte) ((tmpValue >> 0) & 0xff);
        }
        return res;
    }

    public static boolean isPunctuation(Character c) {
        return String.valueOf(c).matches("[\\pP\\p{Punct} 　]");
    }

    /**
     * 判断字符是否为全角字符，包含了全角标点符号和汉字等
     */
    public static boolean isSBC(Character c) {
        String temp = String.valueOf(c);
        // 正则判断是全角字符
        return temp.matches("[^\\x00-\\xff]");
    }

    /**
     * 判断字符是否为全角标点符号
     */
    public static boolean isSBCPunctuation(Character c) {
        //空格和句号
        if (c == 12288 || c == 12290) {
            return true;
        }
        //其他全角字符
        if (c > 65280 && c < 65375) {
            return true;
        }
        return false;
    }
}
