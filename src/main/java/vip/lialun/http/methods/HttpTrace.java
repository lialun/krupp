package vip.lialun.http.methods;

import vip.lialun.http.HttpClient;
import vip.lialun.http.HttpRequestConfig;
import org.apache.http.entity.ContentType;

import java.io.File;
import java.nio.charset.Charset;

import static vip.lialun.http.HttpConstants.METHOD_TRACE;

public class HttpTrace extends HttpMethod<HttpTrace> {

    public HttpTrace(HttpClient httpClient, HttpRequestConfig defaultConfig, String url) {
        super(httpClient, METHOD_TRACE, defaultConfig, url);
    }

    @Override
    public HttpTrace setContentType(ContentType contentType) {
        throw new IllegalStateException(getMethod() + " requests may not include an entity.");
    }

    @Override
    public HttpTrace setEntity(final byte[] entity) {
        throw new IllegalStateException(getMethod() + " requests may not include an entity.");
    }

    @Override
    public HttpTrace setEntity(final String entity) {
        throw new IllegalStateException(getMethod() + " requests may not include an entity.");
    }

    @Override
    public HttpTrace setEntity(final String entity, final Charset charset) {
        throw new IllegalStateException(getMethod() + " requests may not include an entity.");
    }

    @Override
    public HttpTrace addMultiPartEntity(final String name, final byte[] entity) {
        throw new IllegalStateException(getMethod() + " requests may not include an entity.");
    }

    @Override
    public HttpTrace addMultiPartEntity(final String name, final byte[] entity, ContentType contentType, String fileName) {
        throw new IllegalStateException(getMethod() + " requests may not include an entity.");
    }

    @Override
    public HttpTrace addMultiPartEntity(final String name, final String text) {
        throw new IllegalStateException(getMethod() + " requests may not include an entity.");
    }

    @Override
    public HttpTrace addMultiPartEntity(final String name, final String text, ContentType contentType) {
        throw new IllegalStateException(getMethod() + " requests may not include an entity.");
    }


    @Override
    public HttpTrace addMultiPartEntity(final String name, final File file) {
        throw new IllegalStateException(getMethod() + " requests may not include an entity.");
    }

    @Override
    public HttpTrace addParamEntity(final String name, final String value) {
        throw new IllegalStateException(getMethod() + " requests may not include an entity.");
    }
}
