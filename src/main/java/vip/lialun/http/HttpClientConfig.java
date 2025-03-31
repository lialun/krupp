package vip.lialun.http;

/**
 * HttpClient配置信息
 *
 * @author lialun
 */
public interface HttpClientConfig {

    boolean isTrustAllSslCertificate();

    int getMaxConnection();

    int getMaxConnectionPerRoute();

    int getSslSoTimeout();

    interface FluentBuilder<T extends FluentBuilder<T>> {
        T setTrustAllSslCertificate(boolean trustAllSslCertificate);

        T setMaxConnection(int maxConnection);

        T setMaxConnectionPerRoute(int maxConnectionPerRoute);

        T setSslSoTimeout(int sslSoTimeout);
    }
}
