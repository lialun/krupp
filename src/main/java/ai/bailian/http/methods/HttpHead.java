package ai.bailian.http.methods;

import ai.bailian.http.HttpClient;
import ai.bailian.http.HttpRequestConfig;

import static ai.bailian.http.HttpConstants.METHOD_HEAD;

public class HttpHead extends HttpMethod<HttpHead> {
    public HttpHead(HttpClient httpClient, HttpRequestConfig defaultConfig, String url) {
        super(httpClient, METHOD_HEAD, defaultConfig, url);
    }
}
