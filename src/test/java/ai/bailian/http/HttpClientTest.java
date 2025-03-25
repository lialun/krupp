package ai.bailian.http;

import ai.bailian.BaseTest;
import ai.bailian.http.methods.*;
import ai.bailian.thread.MultiThreadExecutor;
import org.junit.jupiter.api.Test;

import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLPeerUnverifiedException;
import java.io.IOException;

import static ai.bailian.http.HttpRequestTest.BAIDU_URL;
import static org.junit.jupiter.api.Assertions.*;

public class HttpClientTest extends BaseTest {

    private static final String URI_STRING_FIXTURE = "http://localhost";

    @Test
    public void testTrustAllSslCertificate() {
        HttpClient client = HttpClient.builder().setTrustAllSslCertificate(true).build();
        client.get("https://expired.badssl.com/").send();
        client.get("https://wrong.host.badssl.com/").send();
        client.get("https://self-signed.badssl.com/").send();
        client.get("https://untrusted-root.badssl.com/").send();
        client.get("https://revoked.badssl.com/").send();
        client.get("https://pinning-test.badssl.com/").send();
    }

    @Test
    public void testNotTrustAllSslCertificate() {
        HttpClient client = HttpClient.builder().setTrustAllSslCertificate(false).build();

        Exception httpException = assertThrows(HttpException.class, () -> client.get("https://expired.badssl.com/").send());
        assertTrue(httpException.getCause() instanceof SSLHandshakeException);

        httpException = assertThrows(HttpException.class, () -> client.get("https://wrong.host.badssl.com/").send());
        assertTrue(httpException.getCause() instanceof SSLPeerUnverifiedException);

        httpException = assertThrows(HttpException.class, () -> client.get("https://self-signed.badssl.com/").send());
        assertTrue(httpException.getCause() instanceof SSLHandshakeException);

        httpException = assertThrows(HttpException.class, () -> client.get("https://untrusted-root.badssl.com/").send());
        assertTrue(httpException.getCause() instanceof SSLHandshakeException);

//        client.get("https://revoked.badssl.com/").send();
//        client.get("https://pinning-test.badssl.com/").send();
    }

    @Test
    public void testGet() {
        HttpGet request = HttpClient.builder().build().get(URI_STRING_FIXTURE);
        assertEquals(HttpGet.class, request.getClass());
        assertEquals("GET", request.getMethod());
    }


    @Test
    public void testPost() {
        HttpPost request = HttpClient.builder().build().post(URI_STRING_FIXTURE);
        assertEquals(HttpPost.class, request.getClass());
        assertEquals("POST", request.getMethod());
    }

    @Test
    public void testDelete() {
        HttpDelete request = HttpClient.builder().build().delete(URI_STRING_FIXTURE);
        assertEquals(HttpDelete.class, request.getClass());
        assertEquals("DELETE", request.getMethod());
    }

    @Test
    public void testPut() {
        HttpPut request = HttpClient.builder().build().put(URI_STRING_FIXTURE);
        assertEquals(HttpPut.class, request.getClass());
        assertEquals("PUT", request.getMethod());
    }

    @Test
    public void testHead() {
        HttpHead request = HttpClient.builder().build().head(URI_STRING_FIXTURE);
        assertEquals(HttpHead.class, request.getClass());
        assertEquals("HEAD", request.getMethod());
    }

    @Test
    public void testTrace() {
        HttpTrace request = HttpClient.builder().build().trace(URI_STRING_FIXTURE);
        assertEquals(HttpTrace.class, request.getClass());
        assertEquals("TRACE", request.getMethod());
    }

    @Test
    public void testTraceWithEntity() {
        assertThrows(IllegalStateException.class, () ->
                HttpClient.builder().build().trace(URI_STRING_FIXTURE).setEntity(""));
    }

    @Test
    public void testOptions() {
        HttpOptions request = HttpClient.builder().build().options(URI_STRING_FIXTURE);
        assertEquals(HttpOptions.class, request.getClass());
        assertEquals("OPTIONS", request.getMethod());
    }

    @Test
    public void testPatch() {
        HttpPatch request = HttpClient.builder().build().patch(URI_STRING_FIXTURE);
        assertEquals(HttpPatch.class, request.getClass());
        assertEquals("PATCH", request.getMethod());
    }

    @Test
    public void testMethod() {
        String method = "TEST_METHOD";
        BasicHttpMethod request = HttpClient.builder().build().method(method, URI_STRING_FIXTURE);
        assertEquals(BasicHttpMethod.class, request.getClass());
        assertEquals(method, request.getMethod());
    }

    @Test
    public void testPool() throws IOException {
        try (HttpClient client = HttpClient.builder().setMaxConnection(50).setMaxConnectionPerRoute(100).build()) {
            assertEquals(client.getPoolStats().getAvailable(), 0);
            for (int i = 0; i <= 10; i++) {
                client.get(BAIDU_URL).send();
            }
            assertTrue(client.getPoolStats().getAvailable() > 0 && client.getPoolStats().getAvailable() < 3);
            MultiThreadExecutor multiThreadExecutor = new MultiThreadExecutor(5);
            for (int i = 0; i <= 30; i++) {
                multiThreadExecutor.addTask(() -> {
                    client.get(BAIDU_URL).send();
                    return null;
                });
            }
            multiThreadExecutor.waitFinish();
            assertTrue(client.getPoolStats().getAvailable() >= 5 && client.getPoolStats().getAvailable() < 20);
        }
    }
}
