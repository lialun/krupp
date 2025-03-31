package vip.lialun.http.simple;

import vip.lialun.http.HttpClient;
import vip.lialun.http.methods.*;
import vip.lialun.logging.Log;
import vip.lialun.logging.LogFactory;
import vip.lialun.thread.SimpleThreadFactory;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.pool.PoolStats;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * http简易工具类
 * 对HttpClient进行了封装,使用流式API实现了最常用的部分功能。
 * <p>
 * 功能:
 * 　1.https忽略证书验证
 * 　2.默认使用桌面版Chrome UserAgent。
 * 　3.url protocol自动补全(http://)
 * 　4.默认开启连接池
 * 　5.自动识别response content编码(通过Header ContentType)
 * 　6.更细化的参数设置,适用更多场景。
 * 　7.支持Html中meta信息ContentType解析
 * 不适用场景(未完成功能):
 * 　1.文件下载等resp content过大的场景。尚未支持使用流处理content或写入文件。
 *
 * @author lialun
 */
@SuppressWarnings("FieldCanBeLocal")
public final class HttpRequest {
    private static final Log log = LogFactory.getLog(HttpRequest.class);
    /**
     * HttpClient实例
     */
    private static volatile HttpClient httpClient;
    /**
     * HttpClient重建间隔
     */
    private static ScheduledExecutorService httpRequestInstanceRecreateTimer;
    /**
     * HttpClient重建间隔
     */
    private static final Integer HTTP_CLIENT_INSTANCE_RECREATE_INTERVAL_IN_HOURS = 3;
    /**
     * HttpClient重建时，老实例释放延迟时间
     */
    private static final Integer HTTP_CLIENT_CLOSE_AFTER_RECREATE_IN_MILLS = 5 * 60 * 1000;

    public static HttpGet get(String url) {
        return getHttpClient().get(url);
    }

    public static HttpPost post(String url) {
        return getHttpClient().post(url);
    }

    public static HttpHead head(String url) {
        return getHttpClient().head(url);
    }

    public static HttpDelete delete(String url) {
        return getHttpClient().delete(url);
    }

    public static HttpPut put(String url) {
        return getHttpClient().put(url);
    }

    public static HttpOptions options(String url) {
        return getHttpClient().options(url);
    }

    public static HttpPatch patch(String url) {
        return getHttpClient().patch(url);
    }

    public static HttpTrace trace(String url) {
        return getHttpClient().trace(url);
    }

    public static BasicHttpMethod method(String method, String url) {
        return getHttpClient().method(method, url);
    }

    /**
     * 获取当前连接池信息
     *
     * @return 连接池信息 {@link PoolStats}
     */
    public static PoolStats getPoolStats() {
        return httpClient.getPoolStats();
    }

    private static HttpClient getHttpClient() {
        if (httpClient == null) {
            synchronized (HttpClient.class) {
                if (httpClient == null) {
                    log.info("HttpRequest实例初始化");
                    httpClient = createHttpClient();
                    httpRequestInstanceRecreateTimer = new ScheduledThreadPoolExecutor(1,
                            new SimpleThreadFactory("HttpClientConnectionManager"));
                    httpRequestInstanceRecreateTimer.scheduleAtFixedRate(HttpRequest::recreateHttpClient,
                            HTTP_CLIENT_INSTANCE_RECREATE_INTERVAL_IN_HOURS, HTTP_CLIENT_INSTANCE_RECREATE_INTERVAL_IN_HOURS, TimeUnit.HOURS);
                }
            }
        }
        return httpClient;
    }

    /**
     * 重建HttpClient实例
     * 由于client实例关闭时会断开所有连接，所以实例在替换掉后延长一段时间，等待连接处理完毕后，再进行关闭
     */
    private static void recreateHttpClient() {
        try {
            log.info("HttpRequest实例重建, 原始实例: " + httpClient);
            HttpClient oldInstance = httpClient;
            httpClient = createHttpClient();
            log.info("HttpRequest实例重建, 新创建实例: " + httpClient);
            Thread.sleep(HTTP_CLIENT_CLOSE_AFTER_RECREATE_IN_MILLS);
            oldInstance.close();
            log.info("HttpRequest实例重建, 原始实例关闭: " + oldInstance);
        } catch (Exception ignored) {
        }
    }

    /**
     * 创建HttpClient实例
     */
    private static HttpClient createHttpClient() {
        return HttpClient.builder()
                .setCookieSpecs(CookieSpecs.IGNORE_COOKIES)
                .setMaxConnection(8000)
                .setMaxConnectionPerRoute(2000)
                .build();
    }
}
