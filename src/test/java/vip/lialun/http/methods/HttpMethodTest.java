package vip.lialun.http.methods;

import vip.lialun.http.DefaultHttpClientConfig;
import vip.lialun.http.DefaultHttpRequestConfig;
import vip.lialun.http.HttpClient;
import vip.lialun.http.HttpResponse;
import vip.lialun.json.JacksonHelper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.http.HttpHost;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.powermock.reflect.Whitebox;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static vip.lialun.http.HttpConstants.CONTENT_TYPE_APPLICATION_OCTET_STREAM;
import static vip.lialun.http.HttpConstants.HEADER_CONTENT_TYPE;
import static org.junit.jupiter.api.Assertions.*;

public class HttpMethodTest {
    private static final String POSTMAN_ECHO = "https://postman-echo.com/";
    private static final String REDIRECT_URL = "http://nxw.so/5iRv4";
    private static HttpClient client;
    private static ObjectMapper objectMapper;

    @BeforeAll
    public static void beforeAll() {
        client = HttpClient.builder().build();
        objectMapper = JacksonHelper.getDefaultMapper();
    }

    @AfterAll
    public static void afterAll() throws IOException {
        client.close();
    }

    @Test
    public void testMethod() {
        String method = "TEST";
        BasicHttpMethod httpMethod = client.method(method, "");
        assertEquals(method, httpMethod.getMethod());
    }

    @Test
    public void testDefaultConfig() {
        HttpClient client = HttpClient.builder().build();
        assertEquals(((Integer) Whitebox.getInternalState(DefaultHttpClientConfig.class, "DEFAULT_MAX_CONNECTION")),
                client.getClientConfig().getMaxConnection());

        BasicHttpMethod httpMethod = client.method("", "");
        assertEquals(((Integer) Whitebox.getInternalState(DefaultHttpRequestConfig.class, "DEFAULT_CONN_TIMEOUT")),
                httpMethod.getConfig().getConnTimeout());
        assertEquals(Whitebox.getInternalState(DefaultHttpRequestConfig.class, "DEFAULT_USER_AGENT"),
                httpMethod.getConfig().getUserAgent());
    }

    @Test
    public void testConfig() {
        String ua = "TEST_UA";
        int connTimeout = 100;
        HttpHost proxy = new HttpHost("hostname", 8888);

        HttpClient client = HttpClient.builder()
                .setUserAgent(ua)
                .setConnTimeout(connTimeout)
                .setProxy(proxy).build();

        BasicHttpMethod httpMethod = client.method("SIMPLE", "");
        assertEquals(ua, httpMethod.getConfig().getUserAgent());
        assertEquals(connTimeout, httpMethod.getConfig().getConnTimeout());
        assertEquals(proxy, httpMethod.getConfig().getProxy());
    }

    @Test
    public void testParam() {
        String expect = "https://postman-echo.com/get?a=1&a=4&b=test_English%3F&c=%E6%B5%8B%E8%AF%95%E4%B8%AD%E6%96%87%EF%BC%9F";
        //url without param
        ObjectNode response = client.get(POSTMAN_ECHO + "get")
                .addParam("a", "1")
                .addParam("b", "test_English?")
                .addParam("c", "测试中文？")
                .addParam("a", "4").send().getJsonObject();
        assertEquals(expect, response.get("url").asText());

        response = client.get(POSTMAN_ECHO + "get")
                .addParams("a", "1", "b", "test_English?", "c", "测试中文？", "a", "4")
                .send().getJsonObject();
        assertEquals(expect, response.get("url").asText());

        //url with param
        response = client.get(POSTMAN_ECHO + "get?a=1")
                .addParam("b", "test_English?")
                .addParam("c", "测试中文？")
                .addParam("a", "4").send().getJsonObject();
        assertEquals(expect, response.get("url").asText());

    }

    @Test
    public void testHeader() {
        ObjectNode response = client.get(POSTMAN_ECHO + "get")
                .addHeader("test_header1", "value1")
                .addHeader("test_header1", "value2")
                .addHeader("test_header2", "value3")
                .addHeader("Set-Cookie", "value4")
                .addHeader("Set-Cookie", "value5")
                .setHeader("test_header2", "value6")
                .addHeaders("test_header3", "value7", "test_header4", "value8")
                .send().getJsonObject();
        assertEquals("value1, value2", response.get("headers").get("test_header1").asText());
        assertEquals("value6", response.get("headers").get("test_header2").asText());
        assertEquals("value7", response.get("headers").get("test_header3").asText());
        assertEquals("value8", response.get("headers").get("test_header4").asText());
        
        ArrayNode expectedCookies = objectMapper.createArrayNode();
        expectedCookies.add("value4");
        expectedCookies.add("value5");
        assertEquals(expectedCookies.toString(), response.get("headers").get("set-cookie").toString());
    }

    @Test
    public void testAuth() {
        ObjectNode response = client.get(POSTMAN_ECHO + "get")
                .basicAuthentication("username", "password")
                .send().getJsonObject();
        assertEquals("Basic dXNlcm5hbWU6cGFzc3dvcmQ=", response.get("headers").get("authorization").asText());
    }

    @Test
    public void testSetContentType() {
        ObjectNode response = client.get(POSTMAN_ECHO + "get")
                .send().getJsonObject();
        assertFalse(response.get("headers").has("HEADER_CONTENT_TYPE"));

        response = client.get(POSTMAN_ECHO + "get")
                .setContentType(ContentType.create(CONTENT_TYPE_APPLICATION_OCTET_STREAM, StandardCharsets.UTF_8))
                .send().getJsonObject();
        assertEquals("application/octet-stream; charset=UTF-8", response.get("headers").get(HEADER_CONTENT_TYPE).asText());

        response = client.get(POSTMAN_ECHO + "get")
                .setContentType(ContentType.create(CONTENT_TYPE_APPLICATION_OCTET_STREAM))
                .send().getJsonObject();
        assertEquals("application/octet-stream", response.get("headers").get(HEADER_CONTENT_TYPE).asText());
    }

    @Test
    public void testEntity() {
        String content = "abc";
        ObjectNode response;
        response = client.post(POSTMAN_ECHO + "post")
                .setEntity(content)
                .send().getJsonObject();
        assertEquals(content, response.get("data").asText());
        assertEquals("text/plain; charset=UTF-8", response.get("headers").get(HEADER_CONTENT_TYPE).asText());

        response = client.post(POSTMAN_ECHO + "post")
                .setQueryCharset(StandardCharsets.ISO_8859_1)
                .setEntity(content)
                .send().getJsonObject();
        assertEquals(content, response.get("data").asText());
        assertEquals("text/plain; charset=ISO-8859-1", response.get("headers").get(HEADER_CONTENT_TYPE).asText());

        response = client.post(POSTMAN_ECHO + "post")
                .setQueryCharset(StandardCharsets.ISO_8859_1)
                .setEntity(content, StandardCharsets.UTF_8)
                .send().getJsonObject();
        assertEquals(content, response.get("data").asText());
        assertEquals("text/plain; charset=UTF-8", response.get("headers").get(HEADER_CONTENT_TYPE).asText());

        response = client.post(POSTMAN_ECHO + "post")
                .setEntity(content.getBytes(StandardCharsets.UTF_8))
                .send().getJsonObject();
        assertEquals("{\"data\":[97,98,99],\"type\":\"Buffer\"}", response.get("data").toString());
        assertEquals(CONTENT_TYPE_APPLICATION_OCTET_STREAM, response.get("headers").get(HEADER_CONTENT_TYPE).asText());
    }

    @Test
    public void testJsonEntity() {
        ObjectNode entity = objectMapper.createObjectNode();
        entity.put("a", 1);
        ArrayNode arrayNode = objectMapper.createArrayNode();
        arrayNode.add(2);
        arrayNode.add("b");
        entity.set("b", arrayNode);
        
        ObjectNode response;
        response = client.post(POSTMAN_ECHO + "post")
                .setJsonEntity(entity)
                .send().getJsonObject();

        assertEquals(entity.toString(), response.get("data").asText());
        assertEquals("application/json; charset=UTF-8", response.get("headers").get(HEADER_CONTENT_TYPE).asText());
    }

    @Test
    public void testMultiPartEntity() {
        ObjectNode response;
        byte[] xx = new byte[]{};
        response = client.post(POSTMAN_ECHO + "post")
                .addMultiPartEntity("a", "1")
                .addMultiPartEntity("b", xx, ContentType.DEFAULT_BINARY, "test").send().getJsonObject();
        assertTrue(response.get("headers").get(HEADER_CONTENT_TYPE).asText().contains("multipart/form-data; boundary="));
    }

    @Test
    public void testUrlEncodedEntity() {
        ObjectNode response;
        response = client.post(POSTMAN_ECHO + "post")
                .addParamEntity("a", "1")
                .addParamEntity("b", "test_English?")
                .addParamEntity("c", "测试中文？")
                .addParamEntity("a", "4").send().getJsonObject();

        assertEquals("{\"a\":[\"1\",\"4\"],\"b\":\"test_English?\",\"c\":\"测试中文？\"}", response.get("form").toString());
        assertEquals("application/x-www-form-urlencoded; charset=UTF-8", response.get("headers").get(HEADER_CONTENT_TYPE).asText());
    }

    @Test
    public void testJacksonNodeEntity() {
        ObjectNode response = client.post(POSTMAN_ECHO + "post")
                .addParamEntity("a", "1")
                .addParamEntity("b", "test_English?")
                .addParamEntity("c", "测试中文？")
                .addParamEntity("a", "4").send().getJsonObject();
        assertEquals("{\"a\":[\"1\",\"4\"],\"b\":\"test_English?\",\"c\":\"测试中文？\"}", response.get("form").toString());
    }

    @Test
    void testBodyByJacksonNode() {
        ObjectNode body = JacksonHelper.getDefaultMapper().createObjectNode();
        body.put("a", 1);
        body.put("b", "test_English?");
        body.put("c", "测试中文？");

        ObjectNode response = client.post(POSTMAN_ECHO + "post").setJsonEntity(body).send().getJsonObject();
        assertEquals("{\"a\":1,\"b\":\"test_English?\",\"c\":\"测试中文？\"}", response.get("data").toString());
    }

    //TODO 临时取消测试
    //@Test
    public void testRedirect() {
        HttpResponse response = HttpClient.builder().setRedirectsEnabled(false).build().get(REDIRECT_URL).send();
        assertEquals(302, response.getStatusCode());
        assertEquals(REDIRECT_URL, response.getLastRequestUrl());

        response = HttpClient.builder().setRedirectsEnabled(true).build().get(REDIRECT_URL).send();
        assertEquals(200, response.getStatusCode());
        assertEquals("https://www.bailian-ai.com/", response.getLastRequestUrl());
    }
}
