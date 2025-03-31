package vip.lialun.http.methods;

import static vip.lialun.http.HttpConstants.METHOD_PUT;

import vip.lialun.http.HttpClient;
import vip.lialun.http.HttpRequestConfig;

public class HttpPut extends HttpMethod<HttpPut> {
    public HttpPut(HttpClient httpClient, HttpRequestConfig defaultConfig, String url) {
        super(httpClient, METHOD_PUT, defaultConfig, url);
    }
}
