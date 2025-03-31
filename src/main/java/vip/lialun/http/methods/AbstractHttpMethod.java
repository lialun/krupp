package vip.lialun.http.methods;

import vip.lialun.http.HttpConstants;
import vip.lialun.http.HttpRequestConfig;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Preconditions;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.message.BasicNameValuePair;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static vip.lialun.http.HttpConstants.*;

public abstract class AbstractHttpMethod<T extends AbstractHttpMethod<T>> extends AbstractHttpRequest<AbstractHttpMethod<T>> {
    /**
     * 请求类型
     */
    private final String method;
    /**
     * 请求URL
     */
    private final String url;
    /**
     * 请求行请求参数
     */
    private final Multimap<String, String> parameters = ArrayListMultimap.create(4, 1);
    /**
     * 请求头
     */
    private final Multimap<String, String> headers = ArrayListMultimap.create(4, 1);
    /**
     * 请求体
     */
    private final HttpEntity entity = new HttpEntity();

    protected AbstractHttpMethod(String method, HttpRequestConfig initConfig, String url) {
        super(initConfig);
        this.method = Preconditions.checkNotNull(method).toUpperCase();
        this.url = Preconditions.checkNotNull(url);
    }

    /**
     * 设置请求参数
     */
    public T addParam(final String name, final String value) {
        parameters.put(Preconditions.checkNotNull(name).trim(), Preconditions.checkNotNull(value));
        return getThis();
    }

    /**
     * 设置请求参数
     */
    public T addParams(final String... params) {
        if (params == null) {
            return getThis();
        }
        IntStream.range(0, params.length / 2).boxed().forEach(i -> addParam(params[i * 2], params[i * 2 + 1]));
        return getThis();
    }

    /**
     * 设置请求参数
     */
    public T addParams(final Map<String, String> params) {
        if (params == null || params.size() <= 0) {
            return getThis();
        }
        params.forEach(this::addParam);
        return getThis();
    }

    /**
     * 添加header
     * 同一个header name可以有多个value
     */
    public T addHeader(final String name, final String value) {
        String _name = Preconditions.checkNotNull(name).toLowerCase().trim();
        //独立处理ContentType请求头
        if (_name.equals(HEADER_CONTENT_TYPE)) {
            getEntity().setContentType(ContentType.parse(value));
        } else {
            headers.put(Preconditions.checkNotNull(name).trim(), Preconditions.checkNotNull(value));
        }
        return getThis();
    }

    /**
     * 添加多个header
     * 同一个header name可以有多个value
     */
    public T addHeaders(final String... headers) {
        if (headers != null) {
            IntStream.range(0, headers.length / 2).boxed().forEach(i -> addHeader(headers[i * 2], headers[i * 2 + 1]));
        }
        return getThis();
    }

    /**
     * 添加多个header
     * 同一个header name可以有多个value
     */
    public T addHeaders(final Map<String, String> headers) {
        if (headers == null || headers.size() <= 0) {
            return getThis();
        }
        headers.forEach(this::addHeader);
        return getThis();
    }

    /**
     * 设置header
     * 如果有同header name将会覆盖
     */
    public T setHeader(final String name, final String value) {
        getHeaders().removeAll(name);
        addHeader(name, value);
        return getThis();
    }

    /**
     * 设置请求权限验证
     */
    public T basicAuthentication(final String username, final String password) {
        String data = username + ":" + password;
        //rfc2616中定义authentication默认编码为ios8859-1,但是实际上有些浏览器并不是这样实现的
        String base64 = Base64.getEncoder().encodeToString(data.getBytes(StandardCharsets.ISO_8859_1));
        return basicAuthentication(base64);
    }

    /**
     * 设置请求权限验证
     */
    public T basicAuthentication(final String base64) {
        this.setHeader(HEADER_AUTHORIZATION, "Basic " + base64);
        return getThis();
    }

    /**
     * 设置请求头ContentType
     */
    public T setContentType(final ContentType contentType) {
        this.getEntity().setContentType(Preconditions.checkNotNull(contentType));
        return getThis();
    }

    /**
     * 设置请求体
     * 会自动设置请求头Content-type为{@link HttpConstants#CONTENT_TYPE_TEXT_PLAIN}
     * 可以手动设置ContentType进行修改{@link #setContentType(ContentType)}
     */
    public T setEntity(final String entity, final Charset charset) {
        Charset _charset = Optional.ofNullable(charset).orElse(getConfig().getQueryCharset());
        if (getEntity().getContentType() == null) {
            getEntity().setContentType(ContentType.create(CONTENT_TYPE_TEXT_PLAIN, _charset));
        }
        return setEntity(Preconditions.checkNotNull(entity).getBytes(_charset));
    }

    /**
     * 设置请求体
     * 会自动设置请求头Content-type为{@link HttpConstants#CONTENT_TYPE_APPLICATION_JSON}
     */
    public T setJsonEntity(final JsonNode json) {
        if (getEntity().getContentType() == null) {
            getEntity().setContentType(ContentType.create(CONTENT_TYPE_APPLICATION_JSON, getConfig().getQueryCharset()));
        }
        return setEntity(Preconditions.checkNotNull(json).toString());
    }
    /**
     * 设置请求体
     * 会自动设置请求头Content-type为{@link HttpConstants#CONTENT_TYPE_APPLICATION_JSON}
     */
    public T setJsonEntity(final Object object) {
        if (getEntity().getContentType() == null) {
            getEntity().setContentType(ContentType.create(CONTENT_TYPE_APPLICATION_JSON, getConfig().getQueryCharset()));
        }
        return setEntity(Preconditions.checkNotNull(object).toString());
    }
    /**
     * 增加MultiPart请求体
     */
    public T addMultiPartEntity(final String name, final byte[] entity) {
        return addMultiPartEntity(name, entity, null, null);
    }

    /**
     * 增加MultiPart请求体
     */
    public T addMultiPartEntity(final String name, final byte[] entity, ContentType contentType, String fileName) {
        getEntity().addMultiPartEntity(name, new HttpEntity.BinaryMultiPartEntity(entity, contentType, fileName));
        return getThis();
    }

    /**
     * 增加MultiPart请求体
     */
    public T addMultiPartEntity(final String name, final String text) {
        return addMultiPartEntity(name, text, ContentType.create(CONTENT_TYPE_TEXT_PLAIN, getConfig().getQueryCharset()));
    }

    /**
     * 增加MultiPart请求体
     */
    public T addMultiPartEntity(final String name, final String text, ContentType contentType) {
        getEntity().addMultiPartEntity(name, new HttpEntity.StringMultiPartEntity(text, contentType));
        return getThis();
    }

    /**
     * 增加MultiPart请求体
     */
    public T addMultiPartEntity(final String name, final File file) {
        getEntity().addMultiPartEntity(name, file);
        return getThis();
    }

    /**
     * 增加KeyValue类型请求体参数
     * 默认请求体为From表单（KeyValue）格式，自动设置请求头Content-type为{@link HttpConstants#CONTENT_TYPE_APPLICATION_FORM_URLENCODED}
     * 可以手动设置ContentType进行修改{@link #setContentType(ContentType)}，
     */
    public T addParamEntity(final String name, final String value) {
        if (getEntity().getContentType() == null) {
            getEntity().setContentType(ContentType.create(CONTENT_TYPE_APPLICATION_FORM_URLENCODED, getConfig().getQueryCharset()));
        }
        getEntity().addParametersEntity(Preconditions.checkNotNull(name).trim(), Preconditions.checkNotNull(value));
        return getThis();
    }

    /**
     * 增加多个KeyValue类型请求体参数
     * 默认请求体为From表单（KeyValue）格式，自动设置请求头Content-type为{@link HttpConstants#CONTENT_TYPE_APPLICATION_FORM_URLENCODED}
     * 可以手动设置ContentType进行修改{@link #setContentType(ContentType)}，
     */
    public T addParamEntities(final String... params) {
        if (params == null) {
            return getThis();
        }
        return addParamEntities(IntStream.range(0, params.length / 2).boxed()
                .collect(Collectors.toMap(i -> params[i * 2], i -> params[i * 2 + 1])));
    }

    /**
     * 增加KeyValue类型请求体参数
     * 默认请求体为From表单（KeyValue）格式，自动设置请求头Content-type为{@link HttpConstants#CONTENT_TYPE_APPLICATION_FORM_URLENCODED}
     * 可以手动设置ContentType进行修改{@link #setContentType(ContentType)}，
     */
    public T addParamEntities(final Map<String, String> params) {
        if (params == null || params.size() <= 0) {
            return getThis();
        }
        params.forEach(this::addParamEntity);
        return getThis();
    }

    public String getMethod() {
        return method;
    }

    protected String getUrl() {
        return url;
    }

    protected Multimap<String, String> getParameters() {
        return parameters;
    }

    protected Multimap<String, String> getHeaders() {
        return headers;
    }

    protected HttpEntity getEntity() {
        return entity;
    }

    /**
     * 设置请求体
     */
    public T setEntity(final byte[] entity) {
        if (getEntity().getContentType() == null) {
            getEntity().setContentType(ContentType.create(CONTENT_TYPE_APPLICATION_OCTET_STREAM));
        }
        getEntity().setContentEntity(entity);
        return getThis();
    }

    /**
     * 设置请求体
     * 会自动设置请求头Content-type为{@link HttpConstants#CONTENT_TYPE_APPLICATION_OCTET_STREAM}
     * 可以手动设置ContentType进行修改{@link #setContentType(ContentType)}
     */
    public T setEntity(final String entity) {
        if (getEntity().getContentType() == null) {
            getEntity().setContentType(ContentType.create(CONTENT_TYPE_TEXT_PLAIN, getConfig().getQueryCharset()));
        }
        return setEntity(Preconditions.checkNotNull(entity).getBytes(getConfig().getQueryCharset()));
    }

    @Override
    @SuppressWarnings("unchecked")
    protected T getThis() {
        return (T) this;
    }

    protected static class HttpEntity {

        private ContentType contentType;
        private byte[] content;
        private Map<String, Object> multiParts;
        private Multimap<String, String> parameters;

        public ContentType getContentType() {
            return contentType;
        }

        public void setContentType(final ContentType contentType) {
            this.contentType = contentType;
        }

        public void setContentEntity(final byte[] contentEntity) {
            this.content = contentEntity;
        }

        public void addMultiPartEntity(final String name, final Object entity) {
            if (multiParts == null) {
                multiParts = new HashMap<>(4);
            }
            multiParts.put(name, entity);
        }

        public void addParametersEntity(final String name, final String value) {
            if (parameters == null) {
                parameters = ArrayListMultimap.create(4, 1);
            }
            parameters.put(name, value);
        }

        protected org.apache.http.HttpEntity getApacheHttpClientEntity(Charset queryCharset) {
            if (parameters != null) {
                List<NameValuePair> paramList = new ArrayList<>();
                for (Map.Entry<String, String> param : parameters.entries()) {
                    paramList.add(new BasicNameValuePair(param.getKey(), param.getValue()));
                }
                return new UrlEncodedFormEntity(paramList, queryCharset);
            } else if (content != null) {
                return new ByteArrayEntity(content, contentType);
            } else if (multiParts != null) {
                MultipartEntityBuilder builder = MultipartEntityBuilder.create();
                builder.setCharset(queryCharset);
                builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
                for (Map.Entry<String, Object> entity : multiParts.entrySet()) {
                    if (entity.getValue() instanceof AbstractHttpMethod.HttpEntity.StringMultiPartEntity) {
                        AbstractHttpMethod.HttpEntity.StringMultiPartEntity tmp = (AbstractHttpMethod.HttpEntity.StringMultiPartEntity) entity.getValue();
                        builder.addTextBody(entity.getKey(), tmp.text, tmp.contentType);
                    } else if (entity.getValue() instanceof String) {
                        builder.addTextBody(entity.getKey(), (String) entity.getValue());
                    } else if (entity.getValue() instanceof File) {
                        builder.addBinaryBody(entity.getKey(), (File) entity.getValue());
                    } else if (entity.getValue() instanceof AbstractHttpMethod.HttpEntity.BinaryMultiPartEntity) {
                        AbstractHttpMethod.HttpEntity.BinaryMultiPartEntity tmp = (AbstractHttpMethod.HttpEntity.BinaryMultiPartEntity) entity.getValue();
                        builder.addBinaryBody(entity.getKey(), tmp.binary, tmp.contentType, tmp.fileName);
                    } else if (entity.getValue() instanceof ContentBody) {
                        builder.addPart(entity.getKey(), (ContentBody) entity.getValue());
                    }
                }
                return builder.build();
            }
            return null;
        }

        private static class StringMultiPartEntity {
            String text;
            ContentType contentType = ContentType.create("text/plain", StandardCharsets.UTF_8);

            StringMultiPartEntity(final String text, final ContentType contentType) {
                this.text = text;
                if (contentType != null) {
                    this.contentType = contentType;
                }
            }
        }

        private static class BinaryMultiPartEntity {
            byte[] binary;
            ContentType contentType = ContentType.DEFAULT_BINARY;
            String fileName;

            BinaryMultiPartEntity(final byte[] binary, final ContentType contentType, final String fileName) {
                this.binary = binary;
                if (contentType != null) {
                    this.contentType = contentType;
                }
                this.fileName = fileName;
            }
        }
    }
}
