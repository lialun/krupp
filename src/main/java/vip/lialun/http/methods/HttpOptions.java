package vip.lialun.http.methods;

import static vip.lialun.http.HttpConstants.METHOD_OPTIONS;

import vip.lialun.http.HttpClient;
import vip.lialun.http.HttpRequestConfig;

public class HttpOptions extends HttpMethod<HttpOptions> {
    public HttpOptions(HttpClient httpClient, HttpRequestConfig defaultConfig, String url) {
        super(httpClient, METHOD_OPTIONS, defaultConfig, url);
    }
}
