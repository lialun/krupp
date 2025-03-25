package ai.bailian.http.methods;

import ai.bailian.http.HttpClient;
import ai.bailian.http.HttpRequestConfig;

/**
 * HTTP方法
 */
public class BasicHttpMethod extends HttpMethod<BasicHttpMethod> {
    public BasicHttpMethod(HttpClient httpClient, String method, HttpRequestConfig defaultConfig, String url) {
        super(httpClient, method, defaultConfig, url);
    }
}