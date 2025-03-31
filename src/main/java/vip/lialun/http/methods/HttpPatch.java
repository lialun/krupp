package vip.lialun.http.methods;

import static vip.lialun.http.HttpConstants.METHOD_PATCH;

import vip.lialun.http.HttpClient;
import vip.lialun.http.HttpRequestConfig;

public class HttpPatch extends HttpMethod<HttpPatch> {
    public HttpPatch(HttpClient httpClient, HttpRequestConfig defaultConfig, String url) {
        super(httpClient, METHOD_PATCH, defaultConfig, url);
    }
}
