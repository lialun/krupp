package vip.lialun.string;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

/**
 * 使用Jackson将任何数据转换为String，便于日志打印
 * 针对长字符串、大集合、长数组等进行了截断处理
 *
 * @author lialun
 */
@SuppressWarnings({"unused", "Duplicates", "WeakerAccess"})
public class JacksonToString {
    // 长字符串截断阈值和显示长度
    private static final int MAX_STRING_LENGTH = 512;
    private static final int STRING_DISPLAY_HEAD = 128;
    private static final int STRING_DISPLAY_TAIL = 128;

    // 集合截断阈值和显示数量
    private static final int MAX_COLLECTION_SIZE = 30;
    private static final int COLLECTION_DISPLAY_HEAD = 10;
    private static final int COLLECTION_DISPLAY_TAIL = 10;

    // Map截断阈值和显示数量
    private static final int MAX_MAP_SIZE = 30;
    private static final int MAP_DISPLAY_SIZE = 20;

    // 字节数组截断阈值和显示长度
    private static final int MAX_BYTE_ARRAY_LENGTH = 512;
    private static final int BYTE_ARRAY_DISPLAY_HEAD = 128;
    private static final int BYTE_ARRAY_DISPLAY_TAIL = 128;

    private static final ObjectMapper mapper = createObjectMapper();

    // 创建并配置ObjectMapper
    private static ObjectMapper createObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();

        // 配置基本选项
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        mapper.enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        // 避免循环引用导致的栈溢出
        mapper.disable(SerializationFeature.FAIL_ON_SELF_REFERENCES);

        // 注册自定义序列化器
        SimpleModule module = new SimpleModule();
        module.addSerializer(String.class, new StringLimiter());
        module.addSerializer(Collection.class, new CollectionLimiter());
        module.addSerializer(byte[].class, new ByteArrayLimiter());
        module.addSerializer(Map.class, new MapLimiter());
        
        mapper.registerModule(module);

        return mapper;
    }

    /**
     * 将对象转换为字符串
     *
     * @param object 任意对象
     * @return 字符串表示
     */
    public static String from(Object object) {
        if (object == null) {
            return "null";
        }
        
        try {
            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            return "Error serializing object: " + e.getMessage();
        }
    }

    /**
     * 字符串截断序列化器
     * 超过MAX_STRING_LENGTH(512)的字符串会显示前128和后128字符，中间显示省略信息
     */
    private static class StringLimiter extends StdSerializer<String> {
        public StringLimiter() {
            super(String.class);
        }

        @Override
        public void serialize(String value, JsonGenerator gen, SerializerProvider provider)
                throws IOException {
            if (value == null) {
                gen.writeNull();
                return;
            }
            
            if (value.length() <= MAX_STRING_LENGTH) {
                gen.writeString(value);
            } else {
                int omitted = value.length() - STRING_DISPLAY_HEAD - STRING_DISPLAY_TAIL;
                String truncated = value.substring(0, STRING_DISPLAY_HEAD) +
                        "...(省略" + omitted + "字符)..." +
                        value.substring(value.length() - STRING_DISPLAY_TAIL);
                gen.writeString(truncated);
            }
        }
    }

    /**
     * 集合限制序列化器
     * 超过MAX_COLLECTION_SIZE(30)的集合会显示前10和后10项，中间显示省略信息
     */
    private static class CollectionLimiter extends StdSerializer<Collection> {
        public CollectionLimiter() {
            super(Collection.class);
        }

        @Override
        public void serialize(Collection value, JsonGenerator gen, SerializerProvider provider)
                throws IOException {
            if (value == null) {
                gen.writeNull();
                return;
            }

            gen.writeStartArray();
            
            if (value.size() <= MAX_COLLECTION_SIZE) {
                // 集合大小在限制范围内，正常序列化
                for (Object item : value) {
                    provider.defaultSerializeValue(item, gen);
                }
            } else {
                // 集合超出限制，只序列化头尾部分
                Iterator<?> iterator = value.iterator();
                
                // 写入前COLLECTION_DISPLAY_HEAD项
                for (int i = 0; i < COLLECTION_DISPLAY_HEAD && iterator.hasNext(); i++) {
                    provider.defaultSerializeValue(iterator.next(), gen);
                }
                
                // 写入省略信息
                int omitted = value.size() - COLLECTION_DISPLAY_HEAD - COLLECTION_DISPLAY_TAIL;
                gen.writeString("...(省略" + omitted + "项)...");
                
                // 跳过中间项
                for (int i = 0; i < omitted && iterator.hasNext(); i++) {
                    iterator.next();
                }
                
                // 写入后COLLECTION_DISPLAY_TAIL项
                for (int i = 0; i < COLLECTION_DISPLAY_TAIL && iterator.hasNext(); i++) {
                    provider.defaultSerializeValue(iterator.next(), gen);
                }
            }
            
            gen.writeEndArray();
        }
    }

    /**
     * 字节数组限制序列化器
     * 超过MAX_BYTE_ARRAY_LENGTH(512)的字节数组会显示前128和后128字节，中间显示省略信息
     */
    private static class ByteArrayLimiter extends StdSerializer<byte[]> {
        public ByteArrayLimiter() {
            super(byte[].class);
        }

        @Override
        public void serialize(byte[] value, JsonGenerator gen, SerializerProvider provider)
                throws IOException {
            if (value == null) {
                gen.writeNull();
                return;
            }

            gen.writeStartArray();
            
            if (value.length <= MAX_BYTE_ARRAY_LENGTH) {
                // 字节数组在限制范围内，正常序列化
                for (byte b : value) {
                    gen.writeNumber(b);
                }
            } else {
                // 字节数组超出限制，只序列化头尾部分
                
                // 写入前BYTE_ARRAY_DISPLAY_HEAD字节
                for (int i = 0; i < BYTE_ARRAY_DISPLAY_HEAD; i++) {
                    gen.writeNumber(value[i]);
                }
                
                // 写入省略信息
                int omitted = value.length - BYTE_ARRAY_DISPLAY_HEAD - BYTE_ARRAY_DISPLAY_TAIL;
                gen.writeString("...(省略" + omitted + "字节)...");
                
                // 写入后BYTE_ARRAY_DISPLAY_TAIL字节
                for (int i = value.length - BYTE_ARRAY_DISPLAY_TAIL; i < value.length; i++) {
                    gen.writeNumber(value[i]);
                }
            }
            
            gen.writeEndArray();
        }
    }

    /**
     * Map限制序列化器
     * 超过MAX_MAP_SIZE(30)的Map会显示MAP_DISPLAY_SIZE(20)项，并显示省略信息
     */
    private static class MapLimiter extends StdSerializer<Map> {
        public MapLimiter() {
            super(Map.class);
        }

        @Override
        public void serialize(Map value, JsonGenerator gen, SerializerProvider provider)
                throws IOException {
            if (value == null) {
                gen.writeNull();
                return;
            }

            gen.writeStartObject();
            
            if (value.size() <= MAX_MAP_SIZE) {
                // Map大小在限制范围内，正常序列化
                for (Object key : value.keySet()) {
                    gen.writeFieldName(String.valueOf(key));
                    provider.defaultSerializeValue(value.get(key), gen);
                }
            } else {
                // Map超出限制，只序列化一部分
                Iterator<?> iterator = value.keySet().iterator();
                
                // 写入前MAP_DISPLAY_SIZE项
                int count = 0;
                while (iterator.hasNext() && count < MAP_DISPLAY_SIZE) {
                    Object key = iterator.next();
                    gen.writeFieldName(String.valueOf(key));
                    provider.defaultSerializeValue(value.get(key), gen);
                    count++;
                }
                
                // 写入省略信息
                int omitted = value.size() - MAP_DISPLAY_SIZE;
                gen.writeFieldName("__omitted__");
                gen.writeString("(省略" + omitted + "项)...");
            }
            
            gen.writeEndObject();
        }
    }
} 