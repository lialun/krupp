package vip.lialun.http.methods;

import vip.lialun.http.HttpClient;
import vip.lialun.http.HttpRequestConfig;

import static vip.lialun.http.HttpConstants.METHOD_GET;

public class HttpGet extends HttpMethod<HttpGet> {
    public HttpGet(HttpClient httpClient, HttpRequestConfig defaultConfig, String url) {
        super(httpClient, METHOD_GET, defaultConfig, url);
    }
}
