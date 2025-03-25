package ai.bailian.http;

import org.apache.http.HttpHost;

import java.nio.charset.Charset;

/**
 * HttpClient配置信息
 *
 * @author lialun
 */
public class HttpClientBuilder implements HttpClientConfig.FluentBuilder<HttpClientBuilder>,
        HttpRequestConfig.FluentBuilder<HttpClientBuilder> {
    private final DefaultHttpClientConfig httpClientConfig = new DefaultHttpClientConfig();
    private final DefaultHttpRequestConfig httpRequestConfig = new DefaultHttpRequestConfig();

    protected HttpClientBuilder() {
    }

    public HttpClient build() {
        return new HttpClient(httpClientConfig, httpRequestConfig);
    }

    @Override
    public HttpClientBuilder setTrustAllSslCertificate(boolean trustAllSslCertificate) {
        httpClientConfig.setTrustAllSslCertificate(trustAllSslCertificate);
        return this;
    }

    @Override
    public HttpClientBuilder setMaxConnection(int maxConnection) {
        httpClientConfig.setMaxConnection(maxConnection);
        return this;
    }

    @Override
    public HttpClientBuilder setMaxConnectionPerRoute(int maxConnectionPerRoute) {
        httpClientConfig.setMaxConnectionPerRoute(maxConnectionPerRoute);
        return this;
    }

    @Override
    public HttpClientBuilder setSslSoTimeout(int sslSoTimeout) {
        httpClientConfig.setSslSoTimeout(sslSoTimeout);
        return this;
    }

    @Override
    public HttpClientBuilder setSoTimeout(int soTimeout) {
        httpRequestConfig.setSoTimeout(soTimeout);
        return this;
    }

    @Override
    public HttpClientBuilder setConnTimeout(int connTimeout) {
        httpRequestConfig.setConnTimeout(connTimeout);
        return this;
    }

    @Override
    public HttpClientBuilder setConnectionRequestTimeout(int connectionRequestTimeout) {
        httpRequestConfig.setConnectionRequestTimeout(connectionRequestTimeout);
        return this;
    }

    @Override
    public HttpClientBuilder setHttpExecuteTimeout(int httpExecuteTimeout) {
        httpRequestConfig.setHttpExecuteTimeout(httpExecuteTimeout);
        return this;
    }

    @Override
    public HttpClientBuilder setRetryTimes(int retryTimes) {
        httpRequestConfig.setRetryTimes(retryTimes);
        return this;
    }

    @Override
    public HttpClientBuilder setRetryInterval(int retryInterval) {
        httpRequestConfig.setRetryInterval(retryInterval);
        return this;
    }

    @Override
    public HttpClientBuilder setMaxResponseContentLength(int maxResponseContentLength) {
        httpRequestConfig.setMaxResponseContentLength(maxResponseContentLength);
        return this;
    }

    @Override
    public HttpClientBuilder setQueryCharset(Charset queryCharset) {
        httpRequestConfig.setResponseCharset(queryCharset);
        return this;
    }

    @Override
    public HttpClientBuilder setResponseCharset(Charset responseCharset) {
        httpRequestConfig.setResponseCharset(responseCharset);
        return this;
    }

    @Override
    public HttpClientBuilder setUserAgent(String userAgent) {
        httpRequestConfig.setUserAgent(userAgent);
        return this;
    }

    @Override
    public HttpClientBuilder setUseHtmlContentType(boolean useHtmlContentType) {
        httpRequestConfig.setUseHtmlContentType(useHtmlContentType);
        return this;
    }

    @Override
    public HttpClientBuilder setProxy(HttpHost proxy) {
        httpRequestConfig.setProxy(proxy);
        return this;
    }

    @Override
    public HttpClientBuilder setProxy(String hostname, int port) {
        return setProxy(new HttpHost(hostname, port));
    }

    @Override
    public HttpClientBuilder setCircularRedirectsAllowed(boolean circularRedirectsAllowed) {
        httpRequestConfig.setCircularRedirectsAllowed(circularRedirectsAllowed);
        return this;
    }

    @Override
    public HttpClientBuilder setRedirectsEnabled(boolean redirectsEnabled) {
        httpRequestConfig.setRedirectsEnabled(redirectsEnabled);
        return this;
    }

    @Override
    public HttpClientBuilder setMaxRedirects(int maxRedirects) {
        httpRequestConfig.setMaxRedirects(maxRedirects);
        return this;
    }

    @Override
    public HttpClientBuilder setCookieSpecs(String cookieSpecs) {
        httpRequestConfig.setCookieSpecs(cookieSpecs);
        return this;
    }
}
