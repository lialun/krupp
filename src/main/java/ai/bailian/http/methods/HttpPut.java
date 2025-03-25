package ai.bailian.http.methods;

import static ai.bailian.http.HttpConstants.METHOD_PUT;

import ai.bailian.http.HttpClient;
import ai.bailian.http.HttpRequestConfig;

public class HttpPut extends HttpMethod<HttpPut> {
    public HttpPut(HttpClient httpClient, HttpRequestConfig defaultConfig, String url) {
        super(httpClient, METHOD_PUT, defaultConfig, url);
    }
}
