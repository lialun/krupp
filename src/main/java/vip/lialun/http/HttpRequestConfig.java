package vip.lialun.http;

import org.apache.http.HttpHost;

import java.nio.charset.Charset;

public interface HttpRequestConfig {

    int getSoTimeout();

    int getConnTimeout();

    int getConnectionRequestTimeout();

    int getHttpExecuteTimeout();

    int getRetryTimes();

    int getRetryInterval();

    int getMaxResponseContentLength();

    Charset getQueryCharset();

    Charset getResponseCharset();

    String getUserAgent();

    boolean isUseHtmlContentType();

    HttpHost getProxy();

    boolean isCircularRedirectsAllowed();

    boolean isRedirectsEnabled();

    int getMaxRedirects();

    String getCookieSpecs();

    interface FluentBuilder<T extends FluentBuilder<T>> {
        T setSoTimeout(int soTimeout);

        T setConnTimeout(int connTimeout);

        T setConnectionRequestTimeout(int connectionRequestTimeout);

        T setHttpExecuteTimeout(int httpExecuteTimeout);

        T setRetryTimes(int retryTimes);

        T setRetryInterval(int retryInterval);

        T setMaxResponseContentLength(int maxResponseContentLength);

        T setQueryCharset(Charset queryCharset);

        T setResponseCharset(Charset responseCharset);

        T setUserAgent(String userAgent);

        T setUseHtmlContentType(boolean useHtmlContentType);

        T setProxy(HttpHost proxy);

        T setProxy(String hostname, int port);

        T setCircularRedirectsAllowed(boolean circularRedirectsAllowed);

        T setRedirectsEnabled(boolean redirectsEnabled);

        T setMaxRedirects(int maxRedirects);

        T setCookieSpecs(String cookieSpecs);
    }

}
