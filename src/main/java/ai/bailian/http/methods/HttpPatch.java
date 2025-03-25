package ai.bailian.http.methods;

import static ai.bailian.http.HttpConstants.METHOD_PATCH;

import ai.bailian.http.HttpClient;
import ai.bailian.http.HttpRequestConfig;

public class HttpPatch extends HttpMethod<HttpPatch> {
    public HttpPatch(HttpClient httpClient, HttpRequestConfig defaultConfig, String url) {
        super(httpClient, METHOD_PATCH, defaultConfig, url);
    }
}
