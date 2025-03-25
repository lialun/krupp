package ai.bailian.string;

import ai.bailian.BaseTest;
import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.Test;

import static ai.bailian.string.StringUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class StringUtilsTest extends BaseTest {
    @Test
    public void punctuationToDBCTest() {
        assertEquals(punctuationToDBC("。"), ".");
        assertEquals(punctuationToDBC("，"), ",");
        assertEquals(punctuationToDBC("　"), " ");
        assertEquals(punctuationToDBC("　", "　"), "　");
        assertEquals(punctuationToDBC("，", "　。；"), ",");
        assertEquals(punctuationToDBC("　", ""), " ");
    }

    @Test
    public void punctuationToSBCTest() {
        assertEquals("。", punctuationToSBC("."));
        assertEquals("，", punctuationToSBC(","));
        assertEquals("　", punctuationToSBC(" "));
        assertEquals(" ", punctuationToSBC(" ", " "));
        assertEquals("，", punctuationToSBC(",", " .;"));
        assertEquals("　", punctuationToSBC(" ", ""));
    }

    @Test
    public void splitLineTest() {
        String lines = "line1\r\nline2\nline3";
        assertEquals(splitLine(lines), ImmutableList.of("line1", "line2", "line3"));
    }

    @Test
    public void leftStripTest() {
        assertEquals(leftStrip("  \ta b c\t  "), "a b c\t  ");
        assertEquals(leftStrip(" "), "");
        assertEquals(leftStrip(" "), "");
        assertNull(leftStrip(null));
    }

    @Test
    public void rightStripTest() {
        assertEquals(rightStrip("  \ta b c\t  "), "  \ta b c");
        assertEquals(rightStrip(" "), "");
        assertEquals(rightStrip(" "), "");
        assertNull(leftStrip(null));
    }

    @Test
    public void stripTest() {
        assertEquals(strip("  \ta b c\t  "), "a b c");
        assertEquals(strip(" "), "");
        assertEquals(strip(" "), "");
        assertNull(strip(null));
    }


    @Test
    public void allIndexOfTest() {
        assertEquals(allIndexOf("aabaacaadaa", "aa"), ImmutableList.of(0, 3, 6, 9));
        assertEquals(allIndexOf("aaa", "a"), ImmutableList.of(0, 1, 2));
        assertEquals(allIndexOf("bbb", "a"), ImmutableList.of());
    }
}
