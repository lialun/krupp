package vip.lialun.string;

import com.google.common.base.Strings;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 字符串相关工具类
 *
 * @author lialun
 */
public class StringUtils {
    /**
     * 判断string是否等于集合中的元素之一
     */
    public static boolean equalsInCollection(String string, Collection<String> items) {
        return items.stream().parallel().anyMatch(string::equals);
    }

    /**
     * 判断string是否以集合中的至少一个元素结尾
     */
    public static boolean endWithInCollection(String string, Collection<String> items) {
        return items.stream().parallel().anyMatch(string::endsWith);
    }

    /**
     * 判断string是否包含集合中的元素之一或多个，并取出
     */
    public static List<String> containsInCollection(String string, Collection<String> items) {
        return items.stream().parallel().filter(string::contains).collect(Collectors.toList());
    }

    /**
     * 判断string是否匹配（matches）集合中的元素之一或多个，并取出
     */
    public static List<String> matchesInCollection(String string, Collection<String> items) {
        return items.stream().parallel().filter(string::matches).collect(Collectors.toList());
    }

    /**
     * 找到字符串集合中匹配（matches）正则表达式的字符串
     */
    public static List<String> getMatches(Collection<String> strings, String regex) {
        return strings.stream().parallel().filter(s -> s.matches(regex)).collect(Collectors.toList());
    }

    /**
     * 替换最后一个匹配上的字符串
     */
    public static String replaceLast(String string, String toReplace, String replacement) {
        int pos = string.lastIndexOf(toReplace);
        if (pos > -1) {
            return string.substring(0, pos)
                    + replacement
                    + string.substring(pos + toReplace.length());
        } else {
            return string;
        }
    }

    /**
     * 全角字符转为半角字符
     */
    public static String punctuationToDBC(String input) {
        return punctuationToDBC(input, null);
    }

    /**
     * 全角字符转为半角字符
     *
     * @param ignored 忽略转换的符号列表
     */
    public static String punctuationToDBC(String input, String ignored) {
        char[] inputChars = input.toCharArray();
        Set<Character> ignoredCharSets = new HashSet<>();
        if (!Strings.isNullOrEmpty(ignored)) {
            for (char c1 : ignored.toCharArray()) {
                ignoredCharSets.add(c1);
            }
        }
        for (int i = 0; i < inputChars.length; i++) {
            if (ignoredCharSets.contains(inputChars[i])) {
                continue;
            }
            if (inputChars[i] == 12288) {
                //全角空格为12288，半角空格为32
                inputChars[i] = (char) 32;
                continue;
            }
            if (inputChars[i] == 12290) {
                //全角句号为12290，半角空格为46
                inputChars[i] = (char) 46;
                continue;
            }
            if (inputChars[i] > 65280 && inputChars[i] < 65375) {
                //其他字符半角(33-126)与全角(65281-65374)的对应关系是：均相差65248
                inputChars[i] = (char) (inputChars[i] - 65248);
            }
        }
        return new String(inputChars);
    }

    /**
     * 半角字符转为全角字符
     */
    public static String punctuationToSBC(String input) {
        return punctuationToSBC(input, null);
    }

    /**
     * 半角字符转为全角字符
     *
     * @param ignored 忽略转换的符号列表
     */
    public static String punctuationToSBC(String input, String ignored) {
        char[] inputChars = input.toCharArray();
        Set<Character> ignoredCharSets = new HashSet<>();
        if (!Strings.isNullOrEmpty(ignored)) {
            for (char c1 : ignored.toCharArray()) {
                ignoredCharSets.add(c1);
            }
        }
        for (int i = 0; i < inputChars.length; i++) {
            if (ignoredCharSets.contains(inputChars[i])) {
                continue;
            }
            if (inputChars[i] == 32) {
                //全角空格为12288，半角空格为32
                inputChars[i] = (char) 12288;
                continue;
            }
            if (inputChars[i] == 46) {
                //全角句号为12290，半角空格为46
                inputChars[i] = (char) 12290;
                continue;
            }
            if (inputChars[i] > 32 && inputChars[i] < 127) {
                //其他字符半角(33-126)与全角(65281-65374)的对应关系是：均相差65248
                inputChars[i] = (char) (inputChars[i] + 65248);
            }
        }
        return new String(inputChars);
    }

    /**
     * 将字符串拆分为按行分割的字符串列表
     */
    public static List<String> splitLine(String text) {
        return Arrays.asList(text.split("\r?\n"));
    }

    /**
     * 将字符串左侧的空白字符删除
     */
    public static String leftStrip(String text) {
        if (Check.isNullOrEmpty(text)) {
            return text;
        }
        char[] chars = text.toCharArray();
        int startPosition = 0;
        for (; startPosition < chars.length; startPosition++) {
            if (!Character.isWhitespace(chars[startPosition]) && !Character.isSpaceChar(chars[startPosition])) {
                break;
            }
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = startPosition; i < chars.length; i++) {
            stringBuilder.append(chars[i]);
        }
        return stringBuilder.toString();
    }

    /**
     * 将字符串右侧的空白字符删除
     */
    public static String rightStrip(String text) {
        if (Check.isNullOrEmpty(text)) {
            return text;
        }
        char[] chars = text.toCharArray();
        int endPosition = chars.length - 1;
        for (; endPosition >= 0; endPosition--) {
            if (!Character.isWhitespace(chars[endPosition]) && !Character.isSpaceChar(chars[endPosition])) {
                break;
            }
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i <= endPosition; i++) {
            stringBuilder.append(chars[i]);
        }
        return stringBuilder.toString();
    }

    /**
     * 将字符串两边的空白字符删除
     */
    public static String strip(String text) {
        return rightStrip(leftStrip(text));
    }

    /**
     * 找到字符串中所有的指定字符串位置
     */
    public static List<Integer> allIndexOf(String text, String part) {
        List<Integer> indexList = new ArrayList<>();
        for (int index = text.indexOf(part); index >= 0; index = text.indexOf(part, index + 1)) {
            indexList.add(index);
        }
        return indexList.stream().filter(i -> i >= 0).collect(Collectors.toList());
//        IntStream.iterate(word.indexOf(c), index -> index >= 0, index -> word.indexOf(c, index + 1))
//                .boxed()
//                .collect(Collectors.toList());
    }
}
