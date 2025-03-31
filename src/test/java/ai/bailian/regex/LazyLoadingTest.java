package ai.bailian.regex;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 专注于测试RegexUtils的懒加载机制
 */
public class LazyLoadingTest {

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
     * 测试不同方法对缓存的影响
     */
    @Test
    void testMethodsAffectingCache() {
        // 初始状态下缓存应该为空
        assertEquals(0, RegexUtils.getCacheSize(), "初始缓存应为空");
        
        // 使用不依赖缓存的常量不应影响缓存
        String email = RegexUtils.EMAIL;
        assertNotNull(email);
        assertEquals(0, RegexUtils.getCacheSize(), "使用常量不应初始化缓存");
        
        // 使用matches方法应该影响缓存
        boolean result = RegexUtils.matches("\\d+", "123");
        assertTrue(result);
        assertEquals(1, RegexUtils.getCacheSize(), "使用matches方法应该初始化缓存");
        
        // 清除缓存
        RegexUtils.clearCache();
        assertEquals(0, RegexUtils.getCacheSize(), "清除后缓存应为空");
        
        // 使用findFirst方法应该影响缓存
        String found = RegexUtils.findFirst("\\d+", "abc123def");
        assertEquals("123", found);
        assertEquals(1, RegexUtils.getCacheSize(), "使用findFirst方法应该初始化缓存");
        
        // 清除缓存
        RegexUtils.clearCache();
        
        // 使用findAll方法应该影响缓存
        List<String> results = RegexUtils.findAll("\\d+", "123abc456");
        assertEquals(2, results.size());
        assertEquals(1, RegexUtils.getCacheSize(), "使用findAll方法应该初始化缓存");
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
        
        // 缓存大小应该是2
        assertEquals(2, RegexUtils.getCacheSize());
    }
    
    /**
     * 测试同时使用多个正则表达式
     */
    @Test
    void testMultiplePatterns() {
        // 使用10个不同的正则表达式
        for (int i = 0; i < 10; i++) {
            Pattern pattern = RegexUtils.getPattern("pattern" + i);
            assertNotNull(pattern);
        }
        
        // 缓存大小应该是10
        assertEquals(10, RegexUtils.getCacheSize(), "应缓存10个不同的模式");
        
        // 再次使用相同的正则表达式不应增加缓存大小
        for (int i = 0; i < 10; i++) {
            Pattern pattern = RegexUtils.getPattern("pattern" + i);
            assertNotNull(pattern);
        }
        
        // 缓存大小应该仍然是10
        assertEquals(10, RegexUtils.getCacheSize(), "重复使用相同模式不应增加缓存大小");
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
} 