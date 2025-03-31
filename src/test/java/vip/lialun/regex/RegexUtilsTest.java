package vip.lialun.regex;

import vip.lialun.BaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * RegexUtils的单元测试类
 * 测试懒加载机制和主要功能
 */
public class RegexUtilsTest extends BaseTest {

    @BeforeEach
    void setUp() {
        // 每个测试前清空缓存
        RegexUtils.clearCache();
    }

    /**
     * 测试懒加载机制
     * 通过反射检查PatternCacheHolder是否已被加载
     */
    @Test
    void testLazyLoading() throws Exception {
        // 测试类加载后PatternCacheHolder是否已初始化
        boolean isHolderLoaded = isClassLoaded("ai.bailian.regex.RegexUtils$PatternCacheHolder");
        assertFalse(isHolderLoaded, "PatternCacheHolder不应该在RegexUtils加载时就初始化");
        
        // 调用一个不需要缓存的方法，比如访问常量
        String chinese = RegexUtils.CHINESE;
        assertNotNull(chinese);
        
        // 再次检查，PatternCacheHolder应该仍未加载
        isHolderLoaded = isClassLoaded("ai.bailian.regex.RegexUtils$PatternCacheHolder");
        assertFalse(isHolderLoaded, "访问常量不应该触发PatternCacheHolder的加载");
        
        // 调用需要缓存的方法
        Pattern pattern = RegexUtils.getPattern("test");
        assertNotNull(pattern);
        
        // 此时PatternCacheHolder应该已加载
        isHolderLoaded = isClassLoaded("ai.bailian.regex.RegexUtils$PatternCacheHolder");
        assertTrue(isHolderLoaded, "调用getPattern后PatternCacheHolder应该已加载");
        
        // 验证缓存大小
        assertEquals(1, RegexUtils.getCacheSize(), "缓存中应有一个模式");
    }
    
    /**
     * 辅助方法：检查类是否已加载
     */
    private boolean isClassLoaded(String className) {
        try {
            Class.forName(className, false, getClass().getClassLoader());
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
    
    /**
     * 测试缓存功能
     */
    @Test
    void testCaching() {
        // 连续两次获取相同的Pattern
        Pattern p1 = RegexUtils.getPattern("abc");
        Pattern p2 = RegexUtils.getPattern("abc");
        
        // 应该是同一个对象
        assertSame(p1, p2, "相同正则表达式应该返回缓存的相同Pattern对象");
        
        // 获取不同的Pattern
        Pattern p3 = RegexUtils.getPattern("def");
        
        // 不应该是同一个对象
        assertNotSame(p1, p3, "不同正则表达式应该返回不同的Pattern对象");
        
        // 缓存大小应该是2
        assertEquals(2, RegexUtils.getCacheSize(), "缓存大小应该是2");
        
        // 清除缓存
        RegexUtils.clearCache();
        
        // 缓存应该为空
        assertEquals(0, RegexUtils.getCacheSize(), "清除后缓存应为空");
    }
    
    /**
     * 测试正则表达式匹配方法
     */
    @ParameterizedTest
    @CsvSource({
        "abc, abc, true",
        "abc, abcd, false",
        "\\d+, 123, true",
        "\\d+, abc, false"
    })
    void testMatches(String regex, String input, boolean expected) {
        assertEquals(expected, RegexUtils.matches(regex, input));
    }
    
    /**
     * 测试contains方法
     */
    @ParameterizedTest
    @CsvSource({
        "abc, xabcy, true",
        "\\d+, a123b, true",
        "\\d+, abc, false"
    })
    void testContains(String regex, String input, boolean expected) {
        assertEquals(expected, RegexUtils.contains(regex, input));
    }
    
    /**
     * 测试findFirst方法
     */
    @Test
    void testFindFirst() {
        assertEquals("123", RegexUtils.findFirst("\\d+", "abc123def"));
        assertNull(RegexUtils.findFirst("\\d+", "abcdef"));
    }
    
    /**
     * 测试findAll方法
     */
    @Test
    void testFindAll() {
        List<String> results = RegexUtils.findAll("\\d+", "123abc456def789");
        assertEquals(3, results.size());
        assertEquals("123", results.get(0));
        assertEquals("456", results.get(1));
        assertEquals("789", results.get(2));
    }
    
    /**
     * 测试无效正则表达式的异常
     */
    @Test
    void testInvalidRegex() {
        assertThrows(PatternSyntaxException.class, () -> {
            RegexUtils.getPattern("[");
        });
    }
    
    /**
     * 测试常量正则表达式
     */
    @ParameterizedTest
    @ValueSource(strings = {
        "123@example.com",
        "user.name@domain.co.uk",
        "firstname-lastname@company.org"
    })
    void testEmailRegex(String email) {
        assertTrue(RegexUtils.matches(RegexUtils.EMAIL, email));
    }
    
    @ParameterizedTest
    @ValueSource(strings = {
        "13800138000",
        "15912345678",
        "19987654321"
    })
    void testChinaMobileRegex(String mobile) {
        assertTrue(RegexUtils.matches(RegexUtils.CHINA_MOBILE, mobile));
    }
    
    @ParameterizedTest
    @ValueSource(strings = {
        "我是中文",
        "这是一段中文文本"
    })
    void testChineseRegex(String text) {
        for (char c : text.toCharArray()) {
            assertTrue(RegexUtils.matches(RegexUtils.CHINESE, String.valueOf(c)));
        }
    }
    
    /**
     * 测试缓存统计信息
     */
    @Test
    void testCacheStats() {
        // 首次访问，产生一次miss
        RegexUtils.getPattern("pattern1");
        
        // 再次访问，应该是hit
        RegexUtils.getPattern("pattern1");
        
        // 新模式，又一次miss
        RegexUtils.getPattern("pattern2");
        
        // 查看统计信息
        assertNotNull(RegexUtils.getCacheStats());
        // 检查是否包含正确的hit和miss计数
        String stats = RegexUtils.getCacheStats().toString();
        assertTrue(stats.contains("hitCount=1"), "缓存命中次数应为1");
        assertTrue(stats.contains("missCount=2"), "缓存未命中次数应为2");
    }

    /**
     * 测试findFirst方法（替代之前的getFirstMatcher）
     */
    @Test
    public void testFindFirstPattern() {
        assertEquals("c123def4", RegexUtils.findFirst("c.*f4", "abc123def456"));
        assertNull(RegexUtils.findFirst("x.*x", "abc123def456"));
    }

    /**
     * 测试findAll方法（替代之前的getMatcher）
     */
    @Test
    public void testFindAllPatterns() {
        assertEquals(Arrays.asList("1a2", "1b2"), RegexUtils.findAll("1.2", "1a21b21c3"));
    }

    /**
     * 测试findAllByGroupName方法（替代之前的getMatcherByName）
     */
    @Test
    public void testFindAllByGroupName() {
        assertEquals(Collections.singletonList("defghi"), RegexUtils.findAllByGroupName("abc(?<name>.*)jkl", "abcdefghijkl", "name"));
        assertEquals(0, RegexUtils.findAllByGroupName("abc(?<name>.*)jkl", "abcdefghijkl", "xxx").size());
    }

    /**
     * 测试extractFirstGroup和extractGroup方法（替代之前的getFirstCaptured）
     */
    @Test
    public void testExtractGroup() {
        assertEquals("34ab", RegexUtils.extractGroup("12(.*)cd", "1234abcd", 1));
        assertNull(RegexUtils.extractGroup("a(.*)b", "1", 1));
        
        // 测试extractFirstGroup，它是extractGroup(regex, input, 1)的便捷方法
        assertEquals("34ab", RegexUtils.extractFirstGroup("12(.*)cd", "1234abcd"));
        
        try {
            assertNull(RegexUtils.extractGroup("12.*cd", "1234abcd", 1));
            fail("Expected 'IndexOutOfBoundsException: No group 1' to be thrown");
        } catch (IndexOutOfBoundsException exception) {
            assertTrue(exception.getMessage().contains("No group"));
        }
    }

    /**
     * 测试extractAllGroups方法（替代之前的getCaptured）
     */
    @Test
    public void testExtractAllGroups() {
        assertEquals(Arrays.asList("1", "2"), RegexUtils.extractAllGroups("a(.)(.)c", "a12ca2cb3c"));
        assertEquals(Collections.emptyList(), RegexUtils.extractAllGroups("a.c", "a1ca2cb3c"));
        assertEquals(Collections.emptyList(), RegexUtils.extractAllGroups("a(.*)b", "1"));
    }

    /**
     * 测试extractAllMatchesGroups方法（替代之前的getAllCaptured）
     */
    @Test
    public void testExtractAllMatchesGroups() {
        List<List<String>> results = RegexUtils.extractAllMatchesGroups("a(.)(.)c", "a12ca13cb14c");
        assertEquals(2, results.size());
        assertEquals(Arrays.asList("1", "2"), results.get(0));
        assertEquals(Arrays.asList("1", "3"), results.get(1));
        
        // 测试无捕获组的情况
        List<List<String>> emptyGroupResults = RegexUtils.extractAllMatchesGroups("a.c", "a1ca2c");
        assertEquals(2, emptyGroupResults.size());
        assertEquals(Collections.emptyList(), emptyGroupResults.get(0));
        assertEquals(Collections.emptyList(), emptyGroupResults.get(1));
        
        // 测试无匹配的情况
        List<List<String>> noMatchResults = RegexUtils.extractAllMatchesGroups("a(.*)b", "1");
        assertEquals(0, noMatchResults.size());
    }
    
    /**
     * 测试extractNamedGroups方法
     */
    @Test
    public void testExtractNamedGroups() {
        String regex = "(?<year>\\d{4})-(?<month>\\d{2})-(?<day>\\d{2})";
        String input = "Date: 2023-05-15, another date: 2024-01-20";
        
        List<Map<String, String>> results = RegexUtils.extractNamedGroups(regex, input);
        
        assertEquals(2, results.size());
        
        Map<String, String> firstMatch = results.get(0);
        assertEquals("2023", firstMatch.get("year"));
        assertEquals("05", firstMatch.get("month"));
        assertEquals("15", firstMatch.get("day"));
        
        Map<String, String> secondMatch = results.get(1);
        assertEquals("2024", secondMatch.get("year"));
        assertEquals("01", secondMatch.get("month"));
        assertEquals("20", secondMatch.get("day"));
    }
    
    /**
     * 测试replaceAll和replaceFirst方法
     */
    @Test
    public void testReplace() {
        // 测试replaceAll
        String original = "apple orange apple banana";
        String replaced = RegexUtils.replaceAll("apple", original, "grape");
        assertEquals("grape orange grape banana", replaced);
        
        // 测试replaceFirst
        String replacedFirst = RegexUtils.replaceFirst("apple", original, "grape");
        assertEquals("grape orange apple banana", replacedFirst);
    }
    
    /**
     * 测试removeAll方法
     */
    @Test
    public void testRemoveAll() {
        String original = "This 123 is 456 a test 789";
        String result = RegexUtils.removeAll("\\d+", original);
        assertEquals("This  is  a test ", result);
    }
    
    /**
     * 测试split方法
     */
    @Test
    public void testSplit() {
        String input = "apple,orange,banana,grape";
        
        // 基本分割
        String[] parts = RegexUtils.split(",", input);
        assertArrayEquals(new String[]{"apple", "orange", "banana", "grape"}, parts);
        
        // 带限制的分割
        String[] limitedParts = RegexUtils.split(",", input, 2);
        assertArrayEquals(new String[]{"apple", "orange,banana,grape"}, limitedParts);
        
        // 使用正则表达式分割
        String numbersAndText = "123abc456def789";
        String[] mixedParts = RegexUtils.split("\\d+", numbersAndText);
        assertArrayEquals(new String[]{"", "abc", "def", ""}, mixedParts);
    }
}