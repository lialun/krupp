package ai.bailian.regex;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 正则表达式工具类
 *
 * @author lialun
 */
@SuppressWarnings("WeakerAccess")
public class RegexUtils {

    public static final String CHINESE = "[\u4E00-\u9FA5]";

    /**
     * 通过正则表达式提取第一个匹配上的字符串
     *
     * @param regex 正则表达式
     * @param str   字符串
     * @return 匹配上的第一个字符串。如果没有匹配则返回null
     */
    public static String getFirstMatcher(String regex, String str) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }

    /**
     * 通过正则表达式提取匹配上的字符串
     *
     * @param regex 正则表达式
     * @param str   字符串
     * @return 匹配上的字符串。如果没有匹配则返回空列表
     */
    public static List<String> getMatcher(String regex, String str) {
        List<String> matches = new ArrayList<>();
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        while (matcher.find()) {
            matches.add(matcher.group());
        }
        return matches;
    }

    /**
     * 根据正则表达式捕获组名称提取匹配上的字符串
     *
     * @param regex 正则表达式
     * @param str   字符串
     * @param name  捕获组名称
     * @return 匹配上的字符串。如果没有匹配则返回空列表
     */
    public static List<String> getMatcherByName(String regex, String str, String name) {
        List<String> matches = new ArrayList<>();
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        while (matcher.find()) {
            try {
                matches.add(matcher.group(name));
            } catch (IllegalArgumentException ignored) {
            }
        }
        return matches;
    }

    /**
     * 通过正则表达式提取第一次匹配捕获(first match)的第一个(group1)字符串
     *
     * @param regex 正则表达式
     * @param str   字符串
     * @return 捕获的字符串。如果没有匹配则返回null
     */
    public static String getFirstCaptured(String regex, String str) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    /**
     * 通过正则表达式提取第一次匹配(first match)捕获的字符串
     *
     * @param regex 正则表达式
     * @param str   字符串
     * @return 捕获的字符串。如果没有匹配则返回空列表
     */
    public static List<String> getCaptured(String regex, String str) {
        List<String> matches = new ArrayList<>();
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        if (matcher.find()) {
            for (int i = 1; i <= matcher.groupCount(); i++) {
                matches.add(matcher.group(i));
            }
        }
        return matches;
    }

    /**
     * 通过正则表达式提取所有次匹配(all match)捕获的字符串
     *
     * @param regex 正则表达式
     * @param str   字符串
     * @return 捕获的字符串。如果没有匹配则返回空列表
     */
    public static List<List<String>> getAllCaptured(String regex, String str) {
        List<List<String>> matches = new ArrayList<>();
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        while (matcher.find()) {
            List<String> match = new ArrayList<>();
            for (int i = 1; i <= matcher.groupCount(); i++) {
                match.add(matcher.group(i));
            }
            matches.add(match);
        }
        return matches;
    }
}
