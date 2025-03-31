package vip.lialun.http.methods;

import vip.lialun.http.DefaultHttpRequestConfig;
import vip.lialun.http.HttpException;
import vip.lialun.http.HttpRequestConfig;
import vip.lialun.http.HttpResponse;
import org.apache.http.HttpHost;
import org.apache.http.NoHttpResponseException;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.BeanUtils;

import javax.net.ssl.SSLException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.UnknownHostException;
import java.nio.charset.Charset;

public abstract class AbstractHttpRequest<T extends AbstractHttpRequest<T>> implements HttpRequestConfig.FluentBuilder<AbstractHttpRequest<T>> {
    private final DefaultHttpRequestConfig config;

    public AbstractHttpRequest(HttpRequestConfig initConfig) {
        this.config = new DefaultHttpRequestConfig();
        BeanUtils.copyProperties(initConfig, this.config);
    }

    protected HttpRequestConfig getConfig() {
        return config;
    }

    /**
     * 发送请求
     */
    protected abstract HttpResponse _send() throws IOException;

    /**
     * 发送请求
     *
     * @return 请求成功则返回response，请求失败则返回最后一次请求的结果（请求失败的response或者抛出异常）
     */
    public HttpResponse send() {
        return send(config.getRetryTimes());
    }

    /**
     * 发送请求，同时可以设置最大尝试次数
     *
     * @param maxRetryTimes 最大尝试次数
     * @return 请求成功则返回response，请求失败则返回最后一次请求的结果（请求失败的response或者抛出异常）
     */
    public HttpResponse send(int maxRetryTimes) {
        return send(maxRetryTimes, config.getRetryInterval());
    }

    /**
     * 发送请求，同时可以设置最大尝试次数和每次尝试的时间间隔
     *
     * @param maxRetryTimes 最大尝试次数
     * @param retryInterval 每次尝试的时间间隔，单位为ms
     * @return 请求成功则返回response，请求失败则返回最后一次请求的结果（请求失败的response或者抛出异常）
     */
    public HttpResponse send(int maxRetryTimes, int retryInterval) {
        return new RetryHandler(maxRetryTimes, retryInterval).send(this);
    }

    private static class RetryHandler {
        private final int retryTimes;
        private final int retryInterval;

        public RetryHandler(int retryTimes, int retryInterval) {
            this.retryTimes = retryTimes;
            this.retryInterval = retryInterval;
        }

        HttpResponse send(@SuppressWarnings("rawtypes") AbstractHttpRequest httpRequest) throws HttpException {
            for (int retryTime = 0; retryTime <= retryTimes; retryTime++) {
                HttpResponse response = null;
                Exception exception = null;
                try {
                    response = httpRequest._send();
                } catch (Exception e) {
                    exception = e;
                }
                if ((isNeedRetry(exception) || isNeedRetry(response)) && retryTime < retryTimes) {
                    try {
                        Thread.sleep(retryInterval);
                    } catch (InterruptedException ignored) {
                    }
                } else {
                    if (response == null) {
                        String message = exception.getMessage();
                        if (Strings.isNotBlank(message)) {
                            throw new HttpException("Http request send failure. message: " + message, exception);
                        } else {
                            throw new HttpException("Http request send failure.", exception);
                        }
                    } else {
                        return response;
                    }
                }
            }
            return null;
        }

        private boolean isNeedRetry(Exception e) {
            return e instanceof UnknownHostException || e instanceof InterruptedIOException ||
                    e instanceof java.net.ConnectException || e instanceof SSLException ||
                    e instanceof NoHttpResponseException;
        }

        private boolean isNeedRetry(HttpResponse response) {
            return response == null || response.getStatusCode() / 100 == 4 || response.getStatusCode() / 100 == 5;
        }
    }

    @SuppressWarnings("unchecked")
    protected T getThis() {
        return (T) this;
    }

    @Override
    public T setSoTimeout(int soTimeout) {
        config.setSoTimeout(soTimeout);
        return getThis();
    }

    @Override
    public T setConnTimeout(int connTimeout) {
        config.setConnTimeout(connTimeout);
        return getThis();
    }

    @Override
    public T setConnectionRequestTimeout(int connectionRequestTimeout) {
        config.setConnectionRequestTimeout(connectionRequestTimeout);
        return getThis();
    }

    @Override
    public AbstractHttpRequest<T> setHttpExecuteTimeout(int httpExecuteTimeout) {
        config.setHttpExecuteTimeout(httpExecuteTimeout);
        return getThis();
    }

    @Override
    public T setRetryTimes(int retryTimes) {
        config.setRetryTimes(retryTimes);
        return getThis();
    }

    @Override
    public T setRetryInterval(int retryInterval) {
        config.setRetryInterval(retryInterval);
        return getThis();
    }

    @Override
    public T setMaxResponseContentLength(int maxResponseContentLength) {
        config.setMaxResponseContentLength(maxResponseContentLength);
        return getThis();
    }

    @Override
    public T setQueryCharset(Charset queryCharset) {
        config.setQueryCharset(queryCharset);
        return getThis();
    }

    @Override
    public T setResponseCharset(Charset responseCharset) {
        config.setResponseCharset(responseCharset);
        return getThis();
    }

    @Override
    public T setUserAgent(String userAgent) {
        config.setUserAgent(userAgent);
        return getThis();
    }

    @Override
    public T setUseHtmlContentType(boolean useHtmlContentType) {
        config.setUseHtmlContentType(useHtmlContentType);
        return getThis();
    }

    @Override
    public T setProxy(HttpHost proxy) {
        config.setProxy(proxy);
        return getThis();
    }

    @Override
    public T setProxy(String hostname, int port) {
        config.setProxy(hostname, port);
        return getThis();
    }

    @Override
    public T setCircularRedirectsAllowed(boolean circularRedirectsAllowed) {
        config.setCircularRedirectsAllowed(circularRedirectsAllowed);
        return getThis();
    }

    @Override
    public T setRedirectsEnabled(boolean redirectsEnabled) {
        config.setRedirectsEnabled(redirectsEnabled);
        return getThis();
    }

    @Override
    public T setMaxRedirects(int maxRedirects) {
        config.setMaxRedirects(maxRedirects);
        return getThis();
    }

    @Override
    public T setCookieSpecs(String cookieSpecs) {
        config.setCookieSpecs(cookieSpecs);
        return getThis();
    }
}