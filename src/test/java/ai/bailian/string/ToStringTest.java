package ai.bailian.string;

import ai.bailian.BaseTest;
import ai.bailian.system.SystemProperties;
import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ToStringTest extends BaseTest {
    @Test
    public void from() {
        Outer o = new Outer();
        String format = "Outer{i=1, str=string, list=ArrayList[ArrayList[], Obj{str=str, boo=true}, abc, 123], inner=Inner{i=2, map=RegularImmutableMap{1=SingletonImmutableBiMap{key=value}, 10=Obj{str=str, boo=true}}}, array=String[][s1, s2]}";
        assertEquals(format, ToString.from(o));
    }

    @Test
    public void fromUseInternalString() {
        Outer o = new Outer();
        String format = "Outer{i=1, str=string, list=ArrayList[ArrayList[], str true, abc, 123], inner=Inner{i=2, map=RegularImmutableMap{1=SingletonImmutableBiMap{key=value}, 10=str true}}, array=String[][s1, s2]}";
        assertEquals(format, ToString.from(o, true));
    }

    @Test
    public void formatEnum() {
        String format = "OS(LINUX){}";
        assertEquals(format, ToString.from(SystemProperties.OS.LINUX));
    }
}

class Outer {
    private int i = 1;
    private String str = "string";
    private List list = Arrays.asList(new ArrayList<>(), new Obj(), "abc", 123);
    private Inner inner = new Inner();
    private String[] array = new String[]{"s1", "s2"};
}

class Inner {
    private int i = 2;
    private Map<String, Object> map = ImmutableMap.of("1", ImmutableMap.of("key", "value"), "10", new Obj());
}

class Obj {
    private String str = "str";
    private boolean boo = true;

    @Override
    public String toString() {
        return str + " " + boo;
    }
}
