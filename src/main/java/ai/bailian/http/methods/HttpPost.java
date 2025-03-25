package ai.bailian.http.methods;

import static ai.bailian.http.HttpConstants.METHOD_POST;

import ai.bailian.http.HttpClient;
import ai.bailian.http.HttpRequestConfig;;

public class HttpPost extends HttpMethod<HttpPost> {
    public HttpPost(HttpClient httpClient, HttpRequestConfig defaultConfig, String url) {
        super(httpClient, METHOD_POST, defaultConfig, url);
    }
}
