package ai.bailian;

import ai.bailian.logging.LogFactory;
import org.junit.jupiter.api.BeforeAll;

import java.lang.reflect.Method;

public abstract class BaseTest {
    @BeforeAll
    public static void beforeAll() {
        try {
            Method method = LogFactory.class.getDeclaredMethod("useStdOutLogging");
            method.setAccessible(true);
            method.invoke(LogFactory.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
