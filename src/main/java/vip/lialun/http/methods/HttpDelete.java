package vip.lialun.http.methods;

import vip.lialun.http.HttpClient;
import vip.lialun.http.HttpRequestConfig;

import static vip.lialun.http.HttpConstants.METHOD_DELETE;

public class HttpDelete extends HttpMethod<HttpDelete> {
    public HttpDelete(HttpClient httpClient, HttpRequestConfig defaultConfig, String url) {
        super(httpClient, METHOD_DELETE, defaultConfig, url);
    }
}
