package ai.bailian.http;

import org.apache.http.HttpHost;
import org.apache.http.client.config.CookieSpecs;
import org.springframework.beans.BeanUtils;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static ai.bailian.http.HttpConstants.UA_CHROME;


/**
 * Http请求配置信息
 *
 * @author lialun
 */
public class DefaultHttpRequestConfig implements HttpRequestConfig, HttpRequestConfig.FluentBuilder<DefaultHttpRequestConfig> {

    private static final int DEFAULT_SO_TIMEOUT = 45_000;
    private static final int DEFAULT_CONN_TIMEOUT = 5_000;
    private static final int DEFAULT_CONNECTION_REQUEST_TIMEOUT = 15_000;
    private static final int DEFAULT_HTTP_EXECUTE_TIMEOUT = -1;
    private static final int DEFAULT_RETRY_TIMES = 0;
    private static final int DEFAULT_RETRY_INTERVAL = 0;
    private static final int DEFAULT_MAX_RESPONSE_CONTENT_LENGTH = 100_000_000;
    private static final Charset DEFAULT_QUERY_CHARSET = StandardCharsets.UTF_8;
    private static final Charset DEFAULT_RESPONSE_CHARSET = StandardCharsets.UTF_8;
    private static final String DEFAULT_USER_AGENT = UA_CHROME;
    private static final boolean DEFAULT_USE_HTML_CONTENT_TYPE = false;
    private static final boolean DEFAULT_CIRCULAR_REDIRECTS_ALLOWED = false;
    private static final boolean DEFAULT_REDIRECTS_ENABLED = true;
    private static final int DEFAULT_MAX_REDIRECTS = 20;
    private static final String DEFAULT_COOKIE_SPECS = CookieSpecs.DEFAULT;

    private int soTimeout = DEFAULT_SO_TIMEOUT;
    private int connTimeout = DEFAULT_CONN_TIMEOUT;
    private int connectionRequestTimeout = DEFAULT_CONNECTION_REQUEST_TIMEOUT;
    private int httpExecuteTimeout = DEFAULT_HTTP_EXECUTE_TIMEOUT;
    private int retryTimes = DEFAULT_RETRY_TIMES;
    private int retryInterval = DEFAULT_RETRY_INTERVAL;
    private int maxResponseContentLength = DEFAULT_MAX_RESPONSE_CONTENT_LENGTH;
    private Charset queryCharset = DEFAULT_QUERY_CHARSET;
    private Charset responseCharset = DEFAULT_RESPONSE_CHARSET;
    private String userAgent = DEFAULT_USER_AGENT;
    private boolean useHtmlContentType = DEFAULT_USE_HTML_CONTENT_TYPE;
    private HttpHost proxy = null;
    private boolean circularRedirectsAllowed = DEFAULT_CIRCULAR_REDIRECTS_ALLOWED;
    private boolean redirectsEnabled = DEFAULT_REDIRECTS_ENABLED;
    private int maxRedirects = DEFAULT_MAX_REDIRECTS;
    private String cookieSpecs = DEFAULT_COOKIE_SPECS;

    public DefaultHttpRequestConfig() {
    }

    protected DefaultHttpRequestConfig(HttpRequestConfig initConfig) {
        if (initConfig != null) {
            BeanUtils.copyProperties(initConfig, this);
        }
    }

    @Override
    public int getSoTimeout() {
        return soTimeout;
    }

    @Override
    public DefaultHttpRequestConfig setSoTimeout(int soTimeout) {
        this.soTimeout = soTimeout;
        return this;
    }

    @Override
    public int getConnTimeout() {
        return connTimeout;
    }

    @Override
    public DefaultHttpRequestConfig setConnTimeout(int connTimeout) {
        this.connTimeout = connTimeout;
        return this;
    }

    @Override
    public int getConnectionRequestTimeout() {
        return connectionRequestTimeout;
    }

    @Override
    public DefaultHttpRequestConfig setConnectionRequestTimeout(int connectionRequestTimeout) {
        this.connectionRequestTimeout = connectionRequestTimeout;
        return this;
    }

    @Override
    public int getHttpExecuteTimeout() {
        return httpExecuteTimeout;
    }

    @Override
    public DefaultHttpRequestConfig setHttpExecuteTimeout(int httpExecuteTimeout) {
        this.httpExecuteTimeout = httpExecuteTimeout;
        return this;
    }

    @Override
    public int getRetryTimes() {
        return retryTimes;
    }

    @Override
    public DefaultHttpRequestConfig setRetryTimes(int retryTimes) {
        this.retryTimes = retryTimes;
        return this;
    }

    @Override
    public int getRetryInterval() {
        return retryInterval;
    }

    @Override
    public DefaultHttpRequestConfig setRetryInterval(int retryInterval) {
        this.retryInterval = retryInterval;
        return this;
    }

    @Override
    public int getMaxResponseContentLength() {
        return maxResponseContentLength;
    }

    @Override
    public DefaultHttpRequestConfig setMaxResponseContentLength(int maxResponseContentLength) {
        this.maxResponseContentLength = maxResponseContentLength;
        return this;
    }

    @Override
    public Charset getQueryCharset() {
        return queryCharset;
    }

    @Override
    public DefaultHttpRequestConfig setQueryCharset(Charset queryCharset) {
        this.queryCharset = queryCharset;
        return this;
    }

    @Override
    public Charset getResponseCharset() {
        return responseCharset;
    }

    @Override
    public DefaultHttpRequestConfig setResponseCharset(Charset responseCharset) {
        this.responseCharset = responseCharset;
        return this;
    }

    @Override
    public String getUserAgent() {
        return userAgent;
    }

    @Override
    public DefaultHttpRequestConfig setUserAgent(String userAgent) {
        this.userAgent = userAgent;
        return this;
    }

    @Override
    public boolean isUseHtmlContentType() {
        return useHtmlContentType;
    }

    @Override
    public DefaultHttpRequestConfig setUseHtmlContentType(boolean useHtmlContentType) {
        this.useHtmlContentType = useHtmlContentType;
        return this;
    }

    @Override
    public HttpHost getProxy() {
        return proxy;
    }

    @Override
    public DefaultHttpRequestConfig setProxy(HttpHost proxy) {
        this.proxy = proxy;
        return this;
    }

    @Override
    public DefaultHttpRequestConfig setProxy(String hostname, int port) {
        return setProxy(new HttpHost(hostname, port));
    }

    @Override
    public boolean isCircularRedirectsAllowed() {
        return circularRedirectsAllowed;
    }

    @Override
    public boolean isRedirectsEnabled() {
        return redirectsEnabled;
    }

    @Override
    public DefaultHttpRequestConfig setCircularRedirectsAllowed(boolean circularRedirectsAllowed) {
        this.circularRedirectsAllowed = circularRedirectsAllowed;
        return this;
    }

    @Override
    public DefaultHttpRequestConfig setRedirectsEnabled(boolean redirectsEnabled) {
        this.redirectsEnabled = redirectsEnabled;
        return this;
    }

    @Override
    public int getMaxRedirects() {
        return maxRedirects;
    }

    @Override
    public DefaultHttpRequestConfig setMaxRedirects(int maxRedirects) {
        this.maxRedirects = maxRedirects;
        return this;
    }

    @Override
    public String getCookieSpecs() {
        return cookieSpecs;
    }

    @Override
    public DefaultHttpRequestConfig setCookieSpecs(String cookieSpecs) {
        this.cookieSpecs = cookieSpecs;
        return this;
    }
}
