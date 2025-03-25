package ai.bailian.http.methods;

import static ai.bailian.http.HttpConstants.METHOD_OPTIONS;

import ai.bailian.http.HttpClient;
import ai.bailian.http.HttpRequestConfig;

public class HttpOptions extends HttpMethod<HttpOptions> {
    public HttpOptions(HttpClient httpClient, HttpRequestConfig defaultConfig, String url) {
        super(httpClient, METHOD_OPTIONS, defaultConfig, url);
    }
}
