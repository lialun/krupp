package ai.bailian.http.methods;

import ai.bailian.http.DefaultHttpClientConfig;
import ai.bailian.http.DefaultHttpRequestConfig;
import ai.bailian.http.HttpClient;
import ai.bailian.http.HttpResponse;
import ai.bailian.json.JacksonHelper;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableList;
import org.apache.http.HttpHost;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.powermock.reflect.Whitebox;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static ai.bailian.http.HttpConstants.CONTENT_TYPE_APPLICATION_OCTET_STREAM;
import static ai.bailian.http.HttpConstants.HEADER_CONTENT_TYPE;
import static org.junit.jupiter.api.Assertions.*;

public class HttpMethodTest {
    private static final String POSTMAN_ECHO = "https://postman-echo.com/";
    private static final String REDIRECT_URL = "http://nxw.so/5iRv4";
    private static HttpClient client;

    @BeforeAll
    public static void beforeAll() {
        client = HttpClient.builder().build();
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
        JSONObject response = client.get(POSTMAN_ECHO + "get")
                .addParam("a", "1")
                .addParam("b", "test_English?")
                .addParam("c", "测试中文？")
                .addParam("a", "4").send().getJSONObject();
        assertEquals(expect, response.getString("url"));

        response = client.get(POSTMAN_ECHO + "get")
                .addParams("a", "1", "b", "test_English?", "c", "测试中文？", "a", "4")
                .send().getJSONObject();
        assertEquals(expect, response.getString("url"));

        //url with param
        response = client.get(POSTMAN_ECHO + "get?a=1")
                .addParam("b", "test_English?")
                .addParam("c", "测试中文？")
                .addParam("a", "4").send().getJSONObject();
        assertEquals(expect, response.getString("url"));

    }

    @Test
    public void testHeader() {
        JSONObject response = client.get(POSTMAN_ECHO + "get")
                .addHeader("test_header1", "value1")
                .addHeader("test_header1", "value2")
                .addHeader("test_header2", "value3")
                .addHeader("Set-Cookie", "value4")
                .addHeader("Set-Cookie", "value5")
                .setHeader("test_header2", "value6")
                .addHeaders("test_header3", "value7", "test_header4", "value8")
                .send().getJSONObject();
        assertEquals("value1, value2", response.getJSONObject("headers").getString("test_header1"));
        assertEquals("value6", response.getJSONObject("headers").getString("test_header2"));
        assertEquals("value7", response.getJSONObject("headers").getString("test_header3"));
        assertEquals("value8", response.getJSONObject("headers").getString("test_header4"));
        assertEquals(JSON.parseArray(JSON.toJSONString(ImmutableList.of("value4", "value5"))), response.getJSONObject("headers").getJSONArray("set-cookie"));
    }

    @Test
    public void testAuth() {
        JSONObject response = client.get(POSTMAN_ECHO + "get")
                .basicAuthentication("username", "password")
                .send().getJSONObject();
        assertEquals("Basic dXNlcm5hbWU6cGFzc3dvcmQ=", response.getJSONObject("headers").getString("authorization"));
    }

    @Test
    public void testSetContentType() {
        JSONObject response = client.get(POSTMAN_ECHO + "get")
                .send().getJSONObject();
        assertFalse(response.getJSONObject("headers").containsKey("HEADER_CONTENT_TYPE"));

        response = client.get(POSTMAN_ECHO + "get")
                .setContentType(ContentType.create(CONTENT_TYPE_APPLICATION_OCTET_STREAM, StandardCharsets.UTF_8))
                .send().getJSONObject();
        assertEquals("application/octet-stream; charset=UTF-8", response.getJSONObject("headers").getString(HEADER_CONTENT_TYPE));

        response = client.get(POSTMAN_ECHO + "get")
                .setContentType(ContentType.create(CONTENT_TYPE_APPLICATION_OCTET_STREAM))
                .send().getJSONObject();
        assertEquals("application/octet-stream", response.getJSONObject("headers").getString(HEADER_CONTENT_TYPE));
    }

    @Test
    public void testEntity() {
        String content = "abc";
        JSONObject response;
        response = client.post(POSTMAN_ECHO + "post")
                .setEntity(content)
                .send().getJSONObject();
        assertEquals(content, response.getString("data"));
        assertEquals("text/plain; charset=UTF-8", response.getJSONObject("headers").getString(HEADER_CONTENT_TYPE));

        response = client.post(POSTMAN_ECHO + "post")
                .setQueryCharset(StandardCharsets.ISO_8859_1)
                .setEntity(content)
                .send().getJSONObject();
        assertEquals(content, response.getString("data"));
        assertEquals("text/plain; charset=ISO-8859-1", response.getJSONObject("headers").getString(HEADER_CONTENT_TYPE));

        response = client.post(POSTMAN_ECHO + "post")
                .setQueryCharset(StandardCharsets.ISO_8859_1)
                .setEntity(content, StandardCharsets.UTF_8)
                .send().getJSONObject();
        assertEquals(content, response.getString("data"));
        assertEquals("text/plain; charset=UTF-8", response.getJSONObject("headers").getString(HEADER_CONTENT_TYPE));

        response = client.post(POSTMAN_ECHO + "post")
                .setEntity(content.getBytes(StandardCharsets.UTF_8))
                .send().getJSONObject();
        assertEquals("{\"data\":[97,98,99],\"type\":\"Buffer\"}", response.getString("data"));
        assertEquals(CONTENT_TYPE_APPLICATION_OCTET_STREAM, response.getJSONObject("headers").getString(HEADER_CONTENT_TYPE));
    }

    @Test
    public void testJsonEntity() {
        JSONObject entity = new JSONObject();
        entity.put("a", 1);
        entity.put("b", ImmutableList.of(2, "b"));
        JSONObject response;
        response = client.post(POSTMAN_ECHO + "post")
                .setJsonEntity(entity)
                .send().getJSONObject();

        assertEquals(entity.toString(), response.getString("data"));
        assertEquals("application/json; charset=UTF-8", response.getJSONObject("headers").getString(HEADER_CONTENT_TYPE));
    }

    @Test
    public void testMultiPartEntity() {
        JSONObject response;
        byte[] xx = new byte[]{};
        response = client.post(POSTMAN_ECHO + "post")
                .addMultiPartEntity("a", "1")
                .addMultiPartEntity("b", xx, ContentType.DEFAULT_BINARY, "test").send().getJSONObject();
        assertTrue(response.getJSONObject("headers").getString(HEADER_CONTENT_TYPE).contains("multipart/form-data; boundary="));
    }

    @Test
    public void testUrlEncodedEntity() {
        JSONObject response;
        response = client.post(POSTMAN_ECHO + "post")
                .addParamEntity("a", "1")
                .addParamEntity("b", "test_English?")
                .addParamEntity("c", "测试中文？")
                .addParamEntity("a", "4").send().getJSONObject();

        assertEquals("{\"a\":[\"1\",\"4\"],\"b\":\"test_English?\",\"c\":\"测试中文？\"}", response.getString("form"));
        assertEquals("application/x-www-form-urlencoded; charset=UTF-8", response.getJSONObject("headers").getString(HEADER_CONTENT_TYPE));
    }

    @Test
    public void testJacksonNodeEntity() {
        ObjectNode response = client.post(POSTMAN_ECHO + "post")
                .addParamEntity("a", "1")
                .addParamEntity("b", "test_English?")
                .addParamEntity("c", "测试中文？")
                .addParamEntity("a", "4").send().getJacksonObject();
        assertEquals("{\"a\":[\"1\",\"4\"],\"b\":\"test_English?\",\"c\":\"测试中文？\"}", response.get("form").toString());
    }

    @Test
    void testBodyByJacksonNode() {
        ObjectNode body = JacksonHelper.getDefaultMapper().createObjectNode();
        body.put("a", 1);
        body.put("b", "test_English?");
        body.put("c", "测试中文？");

        ObjectNode response = client.post(POSTMAN_ECHO + "post").setJacksonEntity(body).send().getJacksonObject();
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
