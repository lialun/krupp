package ai.bailian.regex;

import ai.bailian.BaseTest;
import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

public class RegexUtilsTest extends BaseTest {

    @Test
    public void getFirstMatcher() {
        assertEquals("c123def4", RegexUtils.getFirstMatcher("c.*f4", "abc123def456"));
        assertNull(RegexUtils.getFirstMatcher("x.*x", "abc123def456"));
    }

    @Test
    public void getMatcher() {
        assertEquals(Arrays.asList("1a2", "1b2"), RegexUtils.getMatcher("1.2", "1a21b21c3"));
    }

    @Test
    public void getMatcherByName() {
        assertEquals(ImmutableList.of("defghi"), RegexUtils.getMatcherByName("abc(?<name>.*)jkl", "abcdefghijkl", "name"));
        assertEquals(0, RegexUtils.getMatcherByName("abc(?<name>.*)jkl", "abcdefghijkl", "xxx").size());
    }

    @Test
    public void getFirstCaptured() {
        assertEquals("34ab", RegexUtils.getFirstCaptured("12(.*)cd", "1234abcd"));
        assertNull(RegexUtils.getFirstCaptured("a(.*)b", "1"));
        try {
            assertNull(RegexUtils.getFirstCaptured("12.*cd", "1234abcd"));
            fail("Expected 'IndexOutOfBoundsException: No group 1' to be thrown");
        } catch (IndexOutOfBoundsException exception) {
            assertEquals(exception.getMessage(), "No group 1");
        }
    }

    @Test
    public void getCaptured() {
        assertEquals(Arrays.asList("1", "2"), RegexUtils.getCaptured("a(.)(.)c", "a12ca2cb3c"));
        assertEquals(Collections.emptyList(), RegexUtils.getCaptured("a.c", "a1ca2cb3c"));
        assertEquals(Collections.emptyList(), RegexUtils.getCaptured("a(.*)b", "1"));
    }

    @Test
    public void getAllCaptured() {
        assertEquals(Arrays.asList(Arrays.asList("1", "2"), Arrays.asList("1", "3")), RegexUtils.getAllCaptured("a(.)(.)c", "a12ca13cb14c"));
        assertEquals(Arrays.asList(Collections.emptyList(), Collections.emptyList()), RegexUtils.getAllCaptured("a.c", "a1ca2cb3c"));
        assertEquals(Collections.emptyList(), RegexUtils.getCaptured("a(.*)b", "1"));
    }
}