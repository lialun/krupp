package ai.bailian.http.methods;

import ai.bailian.http.HttpClient;
import ai.bailian.http.HttpRequestConfig;

import static ai.bailian.http.HttpConstants.METHOD_DELETE;

public class HttpDelete extends HttpMethod<HttpDelete> {
    public HttpDelete(HttpClient httpClient, HttpRequestConfig defaultConfig, String url) {
        super(httpClient, METHOD_DELETE, defaultConfig, url);
    }
}
