package vip.lialun;

import java.util.TimeZone;

public class Krupp {
    /**
     * Jackson时间格式化时区
     */
    public static volatile TimeZone JACKSON_TIME_ZONE = TimeZone.getTimeZone("GMT+8");

    /**
     * RegexUtils正则默认缓存大小
     */
    public static volatile int REGEX_CACHE_SIZE = 256;
    /**
     * RegexUtils正则默认缓存过期时间（分钟）
     */
    public static volatile int REGEX_EXPIRE_IN_MINUTES = 60;
}
