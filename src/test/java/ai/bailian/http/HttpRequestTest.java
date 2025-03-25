package ai.bailian.http;


import ai.bailian.BaseTest;
import ai.bailian.http.simple.HttpRequest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HttpRequestTest extends BaseTest {
    public static final String BAIDU_URL = "http://www.baidu.com/";
    public static final String BAIDU = "百度";

    @Test
    public void testGet() {
        HttpResponse resp = HttpRequest.get(BAIDU_URL).send();
        assertEquals(200, resp.getStatusCode());
        assertEquals("OK", resp.getReasonPhrase());
        assertTrue(resp.getString().contains(BAIDU));
        assertTrue(resp.getBytes().length > 10000L);
    }
}