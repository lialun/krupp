package ai.bailian.http.methods;

import ai.bailian.http.HttpClient;
import ai.bailian.http.HttpRequestConfig;

import static ai.bailian.http.HttpConstants.METHOD_GET;

public class HttpGet extends HttpMethod<HttpGet> {
    public HttpGet(HttpClient httpClient, HttpRequestConfig defaultConfig, String url) {
        super(httpClient, METHOD_GET, defaultConfig, url);
    }
}
