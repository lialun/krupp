package vip.lialun.http.methods;

import vip.lialun.http.HttpClient;
import vip.lialun.http.HttpRequestConfig;

import static vip.lialun.http.HttpConstants.METHOD_HEAD;

public class HttpHead extends HttpMethod<HttpHead> {
    public HttpHead(HttpClient httpClient, HttpRequestConfig defaultConfig, String url) {
        super(httpClient, METHOD_HEAD, defaultConfig, url);
    }
}
