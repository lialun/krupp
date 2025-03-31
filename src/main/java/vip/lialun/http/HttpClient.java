package vip.lialun.http;

import vip.lialun.http.methods.*;
import vip.lialun.thread.SimpleThreadFactory;
import org.apache.http.client.CookieStore;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.pool.PoolStats;
import org.apache.http.ssl.SSLContextBuilder;

import javax.net.ssl.SSLContext;
import java.io.Closeable;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * http 请求工具类
 * 对HttpClient进行了封装,使用流式API实现了最常用的部分功能。
 *
 * @author lialun
 */
public final class HttpClient implements Closeable {

    private final CloseableHttpClient closeableHttpClient;
    private PoolingHttpClientConnectionManager connMgr = null;
    private final CookieStore cookieStore;
    private final HttpClientConfig clientConfig;
    private final HttpRequestConfig defaultRequestConfig;
    ScheduledExecutorService connectionManagerTimer = new ScheduledThreadPoolExecutor(1,
            new SimpleThreadFactory("HttpClientConnectionManager"));

    HttpClient(final HttpClientConfig httpClientConfig, HttpRequestConfig defaultHttpRequestConfig) {
        this.clientConfig = httpClientConfig;
        this.defaultRequestConfig = defaultHttpRequestConfig;

        // 设置cookie store
        cookieStore = new BasicCookieStore();
        // 创建client
        org.apache.http.impl.client.HttpClientBuilder apacheHttpClientBuilder = org.apache.http.impl.client.HttpClientBuilder.create();
        apacheHttpClientBuilder.setDefaultCookieStore(cookieStore)
                .setRedirectStrategy(new LaxRedirectStrategy())
                //不在client层设置retry，设置在request中
                .setRetryHandler((exception, executionCount, context) -> false);
        if (httpClientConfig.isTrustAllSslCertificate()) {
            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder
                    .<ConnectionSocketFactory>create()
                    .register("http", PlainConnectionSocketFactory.INSTANCE)
                    .register("https", createSslIgnoredConnectionSocketFactory()).build();
            connMgr = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        } else {
            connMgr = new PoolingHttpClientConnectionManager();
        }
        connMgr.setMaxTotal(httpClientConfig.getMaxConnection());
        connMgr.setDefaultMaxPerRoute(httpClientConfig.getMaxConnectionPerRoute());
        connMgr.setDefaultSocketConfig(SocketConfig.custom().setSoTimeout(clientConfig.getSslSoTimeout()).build());
        apacheHttpClientBuilder.setConnectionManager(connMgr);
        this.connectionManagerTimer.scheduleWithFixedDelay(() -> {
            if (connMgr != null) {
                connMgr.closeExpiredConnections();
            }
        }, 90, 30, TimeUnit.SECONDS);
        connMgr.closeExpiredConnections();
        closeableHttpClient = apacheHttpClientBuilder.build();
    }

    private SSLConnectionSocketFactory createSslIgnoredConnectionSocketFactory() {
        try {
            SSLContext sslContext = new SSLContextBuilder()
                    .loadTrustMaterial(null, (x509CertChain, authType) -> true).build();
            return new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);
        } catch (NoSuchAlgorithmException | KeyManagementException | KeyStoreException e) {
            throw new HttpException("HttpClient创建失败", e);
        }
    }

    public static HttpClientBuilder builder() {
        return new HttpClientBuilder();
    }

    public CloseableHttpClient getCloseableHttpClient() {
        return closeableHttpClient;
    }

    @Override
    public void close() throws IOException {
        closeableHttpClient.close();
        connectionManagerTimer.shutdown();
    }

    public HttpGet get(String url) {
        return new HttpGet(this, defaultRequestConfig, url);
    }

    public HttpPost post(String url) {
        return new HttpPost(this, defaultRequestConfig, url);
    }

    public HttpDelete delete(String url) {
        return new HttpDelete(this, defaultRequestConfig, url);
    }

    public HttpPut put(String url) {
        return new HttpPut(this, defaultRequestConfig, url);
    }

    public HttpHead head(String url) {
        return new HttpHead(this, defaultRequestConfig, url);
    }

    public HttpOptions options(String url) {
        return new HttpOptions(this, defaultRequestConfig, url);
    }

    public HttpPatch patch(String url) {
        return new HttpPatch(this, defaultRequestConfig, url);
    }

    public HttpTrace trace(String url) {
        return new HttpTrace(this, defaultRequestConfig, url);
    }

    public BasicHttpMethod method(String method, String url) {
        return new BasicHttpMethod(this, method, defaultRequestConfig, url);
    }

    /**
     * 获取当前连接池信息
     */
    public PoolStats getPoolStats() {
        if (connMgr == null) {
            return null;
        }
        return connMgr.getTotalStats();
    }

    /**
     * 关闭连接池中过期的链接
     */
    public void closeExpiredConnections() {
        if (connMgr == null) {
            return;
        }
        connMgr.closeExpiredConnections();
    }

    /**
     * 关闭连接池中空闲的链接
     */
    public void closeIdleConnections(final long idleTimeout, final TimeUnit timeUnit) {
        if (connMgr == null) {
            return;
        }
        connMgr.closeIdleConnections(idleTimeout, timeUnit);
    }

    public CookieStore getCookieStore() {
        return cookieStore;
    }

    public HttpClientConfig getClientConfig() {
        return clientConfig;
    }

    public HttpRequestConfig getDefaultRequestConfig() {
        return defaultRequestConfig;
    }
}
