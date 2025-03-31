package vip.lialun.http.methods;

import static vip.lialun.http.HttpConstants.METHOD_POST;

import vip.lialun.http.HttpClient;
import vip.lialun.http.HttpRequestConfig;;

public class HttpPost extends HttpMethod<HttpPost> {
    public HttpPost(HttpClient httpClient, HttpRequestConfig defaultConfig, String url) {
        super(httpClient, METHOD_POST, defaultConfig, url);
    }
}
