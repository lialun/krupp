package vip.lialun.http.methods;

import vip.lialun.http.HttpClient;
import vip.lialun.http.HttpRequestConfig;

/**
 * HTTP方法
 */
public class BasicHttpMethod extends HttpMethod<BasicHttpMethod> {
    public BasicHttpMethod(HttpClient httpClient, String method, HttpRequestConfig defaultConfig, String url) {
        super(httpClient, method, defaultConfig, url);
    }
}