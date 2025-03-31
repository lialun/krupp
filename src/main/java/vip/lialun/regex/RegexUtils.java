package vip.lialun.regex;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheStats;
import com.google.common.util.concurrent.UncheckedExecutionException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import static vip.lialun.Krupp.REGEX_CACHE_SIZE;
import static vip.lialun.Krupp.REGEX_EXPIRE_IN_MINUTES;

/**
 * 正则表达式工具类
 * <p>
 * 提供了常用的正则表达式常量和便捷的匹配、提取方法。
 * 包含Pattern缓存机制，避免重复编译相同的正则表达式。
 * </p>
 *
 * @author lialun
 */
@SuppressWarnings({"WeakerAccess", "UnnecessaryUnicodeEscape"})
public class RegexUtils {

    /**
     * Pattern缓存，实现懒加载模式
     */
    private static volatile Cache<String, Pattern> INSTANCE;

    /**
     * 获取正则表达式缓存实例
     *
     * @return Pattern缓存实例
     */
    private static Cache<String, Pattern> getPatternCache() {
        if (INSTANCE == null) {
            synchronized (RegexUtils.class) {
                if (INSTANCE == null) {
                    INSTANCE = buildCache(REGEX_CACHE_SIZE);
                }
            }
        }
        return INSTANCE;
    }

    /**
     * 中文字符
     */
    public static final String CHINESE = "[\u4E00-\u9FA5]";

    /**
     * 中文字符(含常用标点)
     */
    public static final String CHINESE_WITH_PUNCTUATION = "[\u4E00-\u9FA5\u3000-\u303F\uFF00-\uFFEF]";

    /**
     * 英文字母
     */
    public static final String ENGLISH = "[a-zA-Z]";

    /**
     * 数字
     */
    public static final String DIGIT = "\\d";

    /**
     * 邮箱
     */
    public static final String EMAIL = "^[a-zA-Z0-9_.-]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*\\.[a-zA-Z0-9]{2,6}$";

    /**
     * 中国手机号
     */
    public static final String CHINA_MOBILE = "^1[3-9]\\d{9}$";

    /**
     * URL
     */
    public static final String URL = "^(https?|ftp|file)://[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|]";

    /**
     * IPv4地址
     */
    public static final String IPV4 = "^((25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.){3}(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)$";

    /**
     * 中国身份证号（18位）
     */
    public static final String CHINA_ID_CARD = "^[1-9]\\d{5}(19|20)\\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\\d|3[01])\\d{3}[0-9Xx]$";

    /**
     * 构建缓存
     *
     * @param maximumSize 最大缓存大小
     * @return 新构建的缓存
     */
    private static Cache<String, Pattern> buildCache(int maximumSize) {
        return CacheBuilder.newBuilder()
                .maximumSize(maximumSize)
                .expireAfterAccess(REGEX_EXPIRE_IN_MINUTES, TimeUnit.MINUTES)
                .build();
    }

    /**
     * 获取当前缓存大小
     *
     * @return 当前缓存中的正则表达式数量
     */
    public static long getCacheSize() {
        return getPatternCache().size();
    }

    /**
     * 清除正则表达式缓存
     */
    public static void clearCache() {
        getPatternCache().invalidateAll();
    }

    /**
     * 获取编译后的Pattern对象
     *
     * @param regex 正则表达式
     * @return 编译后的Pattern对象
     * @throws PatternSyntaxException 如果正则表达式无效
     */
    public static Pattern getPattern(String regex) throws PatternSyntaxException {
        return getPattern(regex, 0);
    }

    /**
     * 获取编译后的Pattern对象
     *
     * @param regex 正则表达式
     * @param flags 匹配标志，如Pattern.CASE_INSENSITIVE
     * @return 编译后的Pattern对象
     * @throws PatternSyntaxException 如果正则表达式无效
     */
    public static Pattern getPattern(String regex, int flags) throws PatternSyntaxException {
        if (regex == null) {
            throw new IllegalArgumentException("正则表达式不能为null");
        }

        // 生成缓存key，考虑flags
        final String key = flags == 0 ? regex : regex + "#" + flags;

        try {
            // 使用Guava Cache的get方法，如果缓存未命中则编译并加入缓存
            return getPatternCache().get(key, () -> {
                //noinspection MagicConstant
                return Pattern.compile(regex, flags);
            });
        } catch (ExecutionException | UncheckedExecutionException e) {
            // 处理可能的异常
            if (e.getCause() instanceof PatternSyntaxException) {
                throw (PatternSyntaxException) e.getCause();
            }
            // 如果发生其他异常，直接编译返回，不缓存
            //noinspection MagicConstant
            return Pattern.compile(regex, flags);
        }
    }

    // ----- 匹配方法 -----

    /**
     * 判断字符串是否匹配正则表达式
     *
     * @param regex 正则表达式
     * @param input 输入字符串
     * @return 是否匹配
     */
    public static boolean matches(String regex, String input) {
        if (input == null) {
            return false;
        }
        return getPattern(regex).matcher(input).matches();
    }

    /**
     * 判断字符串是否包含匹配正则表达式的部分
     *
     * @param regex 正则表达式
     * @param input 输入字符串
     * @return 是否包含匹配
     */
    public static boolean contains(String regex, String input) {
        if (input == null) {
            return false;
        }
        return getPattern(regex).matcher(input).find();
    }

    // ----- 提取匹配内容的方法 -----

    /**
     * 通过正则表达式提取第一个匹配上的字符串
     *
     * @param regex 正则表达式
     * @param input 输入字符串
     * @return 匹配上的第一个字符串。如果没有匹配则返回null
     */
    public static String findFirst(String regex, String input) {
        if (input == null) {
            return null;
        }
        Matcher matcher = getPattern(regex).matcher(input);
        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }

    /**
     * 通过正则表达式提取所有匹配上的字符串
     *
     * @param regex 正则表达式
     * @param input 输入字符串
     * @return 匹配上的字符串列表。如果没有匹配则返回空列表
     */
    public static List<String> findAll(String regex, String input) {
        List<String> matches = new ArrayList<>();
        if (input == null) {
            return matches;
        }
        Matcher matcher = getPattern(regex).matcher(input);
        while (matcher.find()) {
            matches.add(matcher.group());
        }
        return matches;
    }

    /**
     * 根据正则表达式捕获组名称提取所有匹配的字符串
     *
     * @param regex     正则表达式（必须包含命名捕获组）
     * @param input     输入字符串
     * @param groupName 捕获组名称
     * @return 匹配上的字符串列表。如果没有匹配则返回空列表
     */
    public static List<String> findAllByGroupName(String regex, String input, String groupName) {
        List<String> matches = new ArrayList<>();
        if (input == null || groupName == null) {
            return matches;
        }
        Matcher matcher = getPattern(regex).matcher(input);
        while (matcher.find()) {
            try {
                matches.add(matcher.group(groupName));
            } catch (IllegalArgumentException ignored) {
                // 忽略不存在的组名
            }
        }
        return matches;
    }

    /**
     * 通过正则表达式提取第一次匹配的指定捕获组字符串
     *
     * @param regex      正则表达式
     * @param input      输入字符串
     * @param groupIndex 捕获组索引，0表示整个匹配，1表示第一个捕获组，以此类推
     * @return 捕获的字符串。如果没有匹配则返回null
     */
    public static String extractGroup(String regex, String input, int groupIndex) {
        if (input == null || groupIndex < 0) {
            return null;
        }
        Matcher matcher = getPattern(regex).matcher(input);
        if (matcher.find()) {
            if (groupIndex <= matcher.groupCount()) {
                return matcher.group(groupIndex);
            }
        }
        return null;
    }

    /**
     * 通过正则表达式提取第一次匹配的第一个捕获组字符串
     *
     * @param regex 正则表达式
     * @param input 输入字符串
     * @return 捕获的字符串。如果没有匹配则返回null
     */
    public static String extractFirstGroup(String regex, String input) {
        return extractGroup(regex, input, 1);
    }

    /**
     * 通过正则表达式提取第一次匹配中所有捕获组的字符串
     *
     * @param regex 正则表达式
     * @param input 输入字符串
     * @return 捕获的字符串列表。如果没有匹配则返回空列表
     */
    public static List<String> extractAllGroups(String regex, String input) {
        List<String> matches = new ArrayList<>();
        if (input == null) {
            return matches;
        }
        Matcher matcher = getPattern(regex).matcher(input);
        if (matcher.find()) {
            for (int i = 1; i <= matcher.groupCount(); i++) {
                matches.add(matcher.group(i));
            }
        }
        return matches;
    }

    /**
     * 通过正则表达式提取所有匹配中的所有捕获组
     *
     * @param regex 正则表达式
     * @param input 输入字符串
     * @return 每次匹配的捕获组列表。如果没有匹配则返回空列表
     */
    public static List<List<String>> extractAllMatchesGroups(String regex, String input) {
        List<List<String>> matches = new ArrayList<>();
        if (input == null) {
            return matches;
        }
        Matcher matcher = getPattern(regex).matcher(input);
        while (matcher.find()) {
            List<String> groups = new ArrayList<>();
            for (int i = 1; i <= matcher.groupCount(); i++) {
                groups.add(matcher.group(i));
            }
            matches.add(groups);
        }
        return matches;
    }

    /**
     * 提取所有匹配及其命名捕获组到Map
     *
     * @param regex 正则表达式（包含命名捕获组）
     * @param input 输入字符串
     * @return 每次匹配的捕获组Map列表。键为组名，值为匹配的字符串
     */
    public static List<Map<String, String>> extractNamedGroups(String regex, String input) {
        List<Map<String, String>> result = new ArrayList<>();
        if (input == null) {
            return result;
        }

        Pattern pattern = getPattern(regex);
        Matcher matcher = pattern.matcher(input);

        while (matcher.find()) {
            Map<String, String> match = new HashMap<>();
            for (String name : pattern.namedGroups().keySet()) {
                try {
                    match.put(name, matcher.group(name));
                } catch (IllegalArgumentException ignored) {
                    // 忽略不存在的组名
                }
            }
            result.add(match);
        }

        return result;
    }

    /**
     * 替换所有匹配正则表达式的部分为指定字符串
     *
     * @param regex       正则表达式
     * @param input       输入字符串
     * @param replacement 替换字符串
     * @return 替换后的字符串
     */
    public static String replaceAll(String regex, String input, String replacement) {
        if (input == null) {
            return null;
        }
        return getPattern(regex).matcher(input).replaceAll(replacement);
    }

    /**
     * 替换第一个匹配正则表达式的部分为指定字符串
     *
     * @param regex       正则表达式
     * @param input       输入字符串
     * @param replacement 替换字符串
     * @return 替换后的字符串
     */
    public static String replaceFirst(String regex, String input, String replacement) {
        if (input == null) {
            return null;
        }
        return getPattern(regex).matcher(input).replaceFirst(replacement);
    }

    /**
     * 删除所有匹配正则表达式的部分
     *
     * @param regex 正则表达式
     * @param input 输入字符串
     * @return 删除后的字符串
     */
    public static String removeAll(String regex, String input) {
        return replaceAll(regex, input, "");
    }

    /**
     * 按正则表达式分割字符串
     *
     * @param regex 正则表达式
     * @param input 输入字符串
     * @return 分割后的字符串数组
     */
    public static String[] split(String regex, String input) {
        if (input == null) {
            return new String[0];
        }
        return getPattern(regex).split(input);
    }

    /**
     * 按正则表达式分割字符串，限制返回的子串数量
     *
     * @param regex 正则表达式
     * @param input 输入字符串
     * @param limit 返回的子串数量上限
     * @return 分割后的字符串数组
     */
    public static String[] split(String regex, String input, int limit) {
        if (input == null) {
            return new String[0];
        }
        return getPattern(regex).split(input, limit);
    }
}
