package ai.bailian.http.methods;

import ai.bailian.http.*;
import com.google.common.net.UrlEscapers;
import org.apache.http.Header;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static ai.bailian.http.HttpConstants.*;

/**
 * Http Method基类
 */
public abstract class HttpMethod<T extends HttpMethod<T>> extends AbstractHttpMethod<HttpMethod<T>> {
    private final HttpClient httpClient;

    protected HttpMethod(HttpClient httpClient, String method, HttpRequestConfig initConfig, String url) {
        super(method, initConfig, url);
        this.httpClient = httpClient;
    }

    @Override
    protected HttpResponse _send() throws IOException {
        // 拼装请求参数,生成最终URL
        String finalUrl = generateURLWithParams();
        // 生成request对象
        HttpEntityEnclosingRequestBase httpRequest = new HttpEntityEnclosingRequestBase() {
            @Override
            public String getMethod() {
                return HttpMethod.super.getMethod();
            }
        };
        httpRequest.setURI(URI.create(finalUrl));
        httpRequest.setConfig(generateRequestConfig());
        // 设置header
        httpRequest.setHeaders(generateHeaders());
        // 设置请求体
        httpRequest.setEntity(getEntity().getApacheHttpClientEntity(getConfig().getQueryCharset()));
        // http执行超时处理
        //TODO 修改为线程池的方式，池大小为=连接池大小、默认超时时间独立设置。
        Thread timeoutThread = null;
        boolean[] isTimeout = new boolean[]{false};
        if (getConfig().getHttpExecuteTimeout() > 0) {
            timeoutThread = new Thread(() -> {
                try {
                    Thread.sleep(getConfig().getHttpExecuteTimeout());
                    httpRequest.abort();
                    isTimeout[0] = true;
                } catch (InterruptedException e) {
                    //thread finish
                }
            });
            timeoutThread.start();
        }
        //发送请求
        HttpContext context = new BasicHttpContext();
        try (CloseableHttpResponse resp = httpClient.getCloseableHttpClient().execute(httpRequest, context)) {
            return new HttpResponse(resp, context, getConfig());
        } catch (IOException e) {
            if (isTimeout[0]) {
                throw new HttpException("http execute timeout, url: " + finalUrl, e);
            } else {
                throw e;
            }
        } finally {
            if (timeoutThread != null && timeoutThread.isAlive()) {
                timeoutThread.interrupt();
            }
        }
    }


    /**
     * 生成请求Headers
     */
    protected Header[] generateHeaders() throws HttpException {
        List<Header> headers = new ArrayList<>();
        if (!getHeaders().isEmpty()) {
            for (Object header : getHeaders().entries()) {
                //noinspection unchecked
                headers.add(new BasicHeader(((Map.Entry<String, String>) header).getKey().toLowerCase(), ((Map.Entry<String, String>) header).getValue()));
            }
        }
        if (headers.stream().noneMatch(header -> header.getName().equals(HEADER_USER_AGENT))) {
            headers.add(new BasicHeader(HEADER_USER_AGENT, getConfig().getUserAgent()));
        }
        if (getEntity().getContentType() != null) {
            headers.add(new BasicHeader(HEADER_CONTENT_TYPE, getEntity().getContentType().toString()));
        }
        return headers.toArray(new Header[0]);
    }

    /**
     * 生成Apache Http Client请求配置
     */
    protected RequestConfig generateRequestConfig() {
        RequestConfig.Builder builder = RequestConfig.custom()
                .setConnectTimeout(getConfig().getConnTimeout())
                .setSocketTimeout(getConfig().getSoTimeout())
                .setMaxRedirects(getConfig().getMaxRedirects())
                .setCircularRedirectsAllowed(getConfig().isCircularRedirectsAllowed())
                .setRedirectsEnabled(getConfig().isRedirectsEnabled())
                .setCookieSpec(getConfig().getCookieSpecs())
                .setConnectionRequestTimeout(getConfig().getConnectionRequestTimeout());
        if (getConfig().getProxy() != null) {
            builder.setProxy(getConfig().getProxy());
        }
        return builder.build();
    }

    /**
     * 拼装请求参数,生成最终URL
     */
    protected String generateURLWithParams() {
        StringBuilder finalUrl = new StringBuilder();
        //如果没有协议类型，自动补全为HTTP协议
        if (!getUrl().toLowerCase().startsWith(HttpConstants.PROTOCOL_HTTP) &&
                !getUrl().toLowerCase().startsWith(PROTOCOL_HTTPS)) {
            finalUrl.append(PROTOCOL_HTTP);
        }
        //设置请求参数
        finalUrl.append(getUrl());
        if (!getParameters().isEmpty()) {
            //如果url不包含问号，这说明原始url中不带有请求参数，则补全问号
            if (finalUrl.indexOf(QUESTION_MARK) < 0) {
                finalUrl.append(QUESTION_MARK);
            }
            //如果原始url带有参数，则添加&符号
            else {
                if (finalUrl.lastIndexOf(AND) != finalUrl.length() - 1) {
                    finalUrl.append(AND);
                }
            }
            //拼装请求参数
            for (Map.Entry<String, String> param : getParameters().entries()) {
                finalUrl.append(UrlEscapers.urlFormParameterEscaper().escape(param.getKey()))
                        .append(EQUAL)
                        .append(UrlEscapers.urlFormParameterEscaper().escape(param.getValue()))
                        .append(AND);
            }
            finalUrl.deleteCharAt(finalUrl.length() - 1);
        }
        return finalUrl.toString();
    }
}
