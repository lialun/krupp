package vip.lialun.http;

/**
 * HttpClient配置信息
 *
 * @author lialun
 */
public class DefaultHttpClientConfig
        implements HttpClientConfig, HttpClientConfig.FluentBuilder<DefaultHttpClientConfig> {
    private static final boolean DEFAULT_TRUST_ALL_SSL_CERTIFICATE = true;
    private static final int DEFAULT_MAX_CONNECTION = 200;
    private static final int DEFAULT_MAX_CONNECTION_PER_ROUTE = 100;
    private static final int DEFAULT_SSL_SO_TIMEOUT = 5_000;

    private boolean trustAllSslCertificate = DEFAULT_TRUST_ALL_SSL_CERTIFICATE;
    private int maxConnection = DEFAULT_MAX_CONNECTION;
    private int maxConnectionPerRoute = DEFAULT_MAX_CONNECTION_PER_ROUTE;
    private int sslSoTimeout = DEFAULT_SSL_SO_TIMEOUT;

    @Override
    public boolean isTrustAllSslCertificate() {
        return trustAllSslCertificate;
    }

    @Override
    public DefaultHttpClientConfig setTrustAllSslCertificate(boolean trustAllSslCertificate) {
        this.trustAllSslCertificate = trustAllSslCertificate;
        return this;
    }

    @Override
    public int getMaxConnection() {
        return maxConnection;
    }

    @Override
    public DefaultHttpClientConfig setMaxConnection(int maxConnection) {
        this.maxConnection = maxConnection;
        return this;
    }

    @Override
    public int getMaxConnectionPerRoute() {
        return maxConnectionPerRoute;
    }

    @Override
    public DefaultHttpClientConfig setMaxConnectionPerRoute(int maxConnectionPerRoute) {
        this.maxConnectionPerRoute = maxConnectionPerRoute;
        return this;
    }

    @Override
    public int getSslSoTimeout() {
        return sslSoTimeout;
    }

    @Override
    public DefaultHttpClientConfig setSslSoTimeout(int sslSoTimeout) {
        this.sslSoTimeout = sslSoTimeout;
        return this;
    }
}
