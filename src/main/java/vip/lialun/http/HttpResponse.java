package vip.lialun.http;

import vip.lialun.json.JacksonHelper;
import vip.lialun.regex.RegexUtils;
import vip.lialun.string.Check;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.io.ByteStreams;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpCoreContext;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.util.Strings;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static vip.lialun.http.HttpConstants.HEADER_CONTENT_TYPE;

/**
 * Http返回值
 *
 * @author lialun
 */
public class HttpResponse implements Serializable {

    private final StatusLine statusLine;
    private final List<HttpHeader> headers;
    private final byte[] content;
    private final Charset contentTypeCharset;
    private final HttpRequestConfig config;
    private String lastRequestUrl;

    public HttpResponse(final CloseableHttpResponse response, final HttpContext httpContext, final HttpRequestConfig config) throws HttpException {
        try {
            this.config = config;
            this.statusLine = response.getStatusLine();
            this.headers = Arrays.stream(response.getAllHeaders()).map(HttpHeader::new).collect(Collectors.toList());
            this.content = getByteArrayFromEntity(response, config.getMaxResponseContentLength());
            //获取contentType中的response charset
            this.contentTypeCharset = getCharsetFromHeader(response.getEntity());
            //最终请求URL地址
            if (httpContext.getAttribute(HttpCoreContext.HTTP_REQUEST) instanceof HttpUriRequest lastReq) {
                HttpHost lastHost = (HttpHost) httpContext.getAttribute(HttpCoreContext.HTTP_TARGET_HOST);
                this.lastRequestUrl = (lastReq.getURI().isAbsolute()) ? lastReq.getURI().toString() : (lastHost.toURI() + lastReq.getURI());
            }
        } catch (IOException e) {
            String message = e.getMessage();
            if (Strings.isNotBlank(message)) {
                throw new HttpException("Generate http response failure, message: " + message, e);
            } else {
                throw new HttpException("Generate http response failure", e);
            }
        } finally {
            EntityUtils.consumeQuietly(response.getEntity());
            try {
                response.close();
            } catch (IOException e) {
                //ignored
            }
        }
    }

    public int getStatusCode() {
        return statusLine.getStatusCode();
    }

    public String getReasonPhrase() {
        return statusLine.getReasonPhrase();
    }


    public HttpHeader[] getHeaders(final String name) {
        return this.headers.stream()
                .filter(header -> header.getName().equalsIgnoreCase(name))
                .toArray(HttpHeader[]::new);
    }

    public HttpHeader getFirstHeader(final String name) {
        for (HttpHeader header : this.headers) {
            if (header.getName().equalsIgnoreCase(name)) {
                return header;
            }
        }
        return null;
    }

    public HttpHeader getLastHeader(final String name) {
        for (int i = this.headers.size() - 1; i >= 0; --i) {
            HttpHeader header = this.headers.get(i);
            if (header.getName().equalsIgnoreCase(name)) {
                return header;
            }
        }
        return null;
    }

    public List<HttpHeader> getAllHeaders() {
        return this.headers;
    }

    public boolean containsHeader(final String name) {
        for (HttpHeader header : this.headers) {
            if (header.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取Entity Body
     */
    public byte[] getBytes() {
        return content;
    }

    /**
     * 获取Entity Body
     */
    public String getString() {
        //如果Header中的ContentType带有charset信息，则直接返回
        if (contentTypeCharset != null) {
            return new String(content, contentTypeCharset);
        }
        // 如果没有使用Html中的Content Type信息，则尝试获取。否则使用默认编码
        String contentString = new String(content, config.getResponseCharset());
        if (config.isUseHtmlContentType()) {
            Charset charset = getCharsetFromHtml(contentString);
            if (charset != null) {
                return new String(content, charset);
            }
        }
        return contentString;
    }

    /**
     * 获取Entity Body
     *
     * @param encode 编码类型
     */
    public String getString(final String encode) {
        if (Check.isNullOrEmpty(encode)) {
            return getString();
        }
        return new String(content, Charset.forName(encode));
    }

    /**
     * 获取ObjectNode格式Entity Body
     */
    public ObjectNode getJsonObject() {
        return getJsonObject(null);
    }

    /**
     * 获取ObjectNode格式Entity Body
     *
     * @param encode 编码类型
     */
    public ObjectNode getJsonObject(final String encode) {
        String response = getString(encode);
        try {
            return JacksonHelper.parseObject(response);
        } catch (JsonProcessingException e) {
            throw new HttpException("Response entity is not a ObjectNode：" + response, e);
        }
    }

    /**
     * 获取ArrayNode格式Entity Body
     */
    public ArrayNode getJsonArray() {
        return getJsonArray(null);
    }

    /**
     * 获取ArrayNode格式Entity Body
     *
     * @param encode 编码
     */
    public ArrayNode getJsonArray(final String encode) {
        String response = getString(encode);
        try {
            return JacksonHelper.parseArray(response);
        } catch (JsonProcessingException e) {
            throw new HttpException("Response entity is not a ArrayNode：" + response, e);
        }
    }


    /**
     * 获取最终请求URL
     */
    @SuppressWarnings("LombokGetterMayBeUsed")
    public String getLastRequestUrl() {
        return lastRequestUrl;
    }

    private Charset getCharsetFromHtml(String html) {
        Element page = Jsoup.parse(html);
        Elements metas = Optional.ofNullable(page.getElementsByTag("head").first())
                .map(o -> o.getElementsByTag("meta")).orElse(null);
        if (metas != null) {
            for (Element meta : metas) {
                //html5
                if (meta.hasAttr("charset")) {
                    return Charset.forName(meta.attr("charset"));
                }
                //html4
                if (HEADER_CONTENT_TYPE.equalsIgnoreCase(meta.attr("http-equiv"))) {
                    String contentInMeta = meta.attr("content");
                    return getCharsetFromContentTypeString(contentInMeta);
                }
            }
        }
        return null;
    }

    /**
     * header中获取response content编码
     *
     * @return response content编码,或null
     */
    private Charset getCharsetFromHeader(HttpEntity httpEntity) {
        if (httpEntity == null) {
            return null;
        }
        Header contentType = httpEntity.getContentType();
        if (contentType == null || Check.isNullOrEmpty(contentType.getValue())) {
            return null;
        }
        return getCharsetFromContentTypeString(contentType.getValue());
    }

    private Charset getCharsetFromContentTypeString(String contentType) {
        if (Check.isNullOrEmpty(contentType)) {
            return null;
        }
        String charset = RegexUtils.extractFirstGroup("charset=([a-zA-Z0-9\\._:\\+-]+)", contentType);
        if (!Check.isNullOrEmpty(charset)) {
            return Charset.forName(charset);
        }
        return null;
    }

    /**
     * 得到response的entity
     */
    private byte[] getByteArrayFromEntity(final CloseableHttpResponse response, int maxContentLength) throws IOException, HttpException {
        HttpEntity entity = response.getEntity();
        if (entity == null) {
            return null;
        }
        //由于目前的做法是将完整的content放入内存,所以一旦content过大就可能导致问题,所以限制content大小。
        //如果有例如下载等content过大的需求,直接调用httpclient的方法,通过操作流的方法完成本需求。
        if (response.getEntity().getContentLength() > maxContentLength) {
            throw new HttpException("content length to large: " + response.getEntity().getContentLength());
        }
        //读取数据
        return ByteStreams.toByteArray(entity.getContent());
    }

    @Override
    public String toString() {
        return "HttpResponse{" +
                "statusLine=" + statusLine +
                ", headers=" + headers +
                ", lastRequestUrl='" + lastRequestUrl +
                ", content=" + getString() +
                '}';
    }
}
