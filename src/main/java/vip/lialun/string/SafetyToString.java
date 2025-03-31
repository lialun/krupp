package vip.lialun.string;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 使用Jackson将任何数据转换为String，便于日志打印
 * 针对长字符串、大集合、长数组等进行了截断处理
 *
 * @author lialun
 */
@SuppressWarnings({"unused", "Duplicates", "WeakerAccess"})
public class SafetyToString {
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

    // 通用数组截断阈值和显示数量
    private static final int MAX_ARRAY_SIZE = 30;
    private static final int ARRAY_DISPLAY_HEAD = 10;
    private static final int ARRAY_DISPLAY_TAIL = 10;

    // 添加最大嵌套深度限制
    private static final int MAX_NESTING_DEPTH = 10;
    
    // 最大输出长度限制（字符数）
    private static final int MAX_TOTAL_OUTPUT_LENGTH = 8192;

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

        // 处理大数字
        mapper.enable(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN);

        // 注册自定义序列化器
        SimpleModule module = new SimpleModule();
        module.addSerializer(String.class, new StringLimiter());
        module.addSerializer(Collection.class, new CollectionLimiter());
        module.addSerializer(byte[].class, new ByteArrayLimiter());
        module.addSerializer(Map.class, new MapLimiter());
        
        // 注册通用数组序列化器
        module.addSerializer(Object[].class, new ObjectArrayLimiter());
        module.addSerializer(int[].class, new IntArrayLimiter());
        module.addSerializer(long[].class, new LongArrayLimiter());
        module.addSerializer(double[].class, new DoubleArrayLimiter());
        module.addSerializer(float[].class, new FloatArrayLimiter());
        module.addSerializer(boolean[].class, new BooleanArrayLimiter());
        module.addSerializer(char[].class, new CharArrayLimiter());
        module.addSerializer(short[].class, new ShortArrayLimiter());

        mapper.registerModule(module);

        // 简化循环引用和深度处理，使用更兼容的方式
        mapper.getSerializerProvider().setDefaultKeySerializer(new JsonSerializer<Object>() {
            @Override
            public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                gen.writeFieldName(value != null ? value.toString() : "null");
            }
        });

        // 设置最大深度的序列化器
        mapper.getFactory().setCodec(new ObjectMapper() {
            private final ThreadLocal<Integer> depth = ThreadLocal.withInitial(() -> 0);

            @Override
            public <T> T readValue(JsonParser p, Class<T> valueType) throws IOException {
                return super.readValue(p, valueType);
            }

            @Override
            public void writeValue(JsonGenerator gen, Object value) throws IOException {
                Integer currentDepth = depth.get();
                if (currentDepth > MAX_NESTING_DEPTH) {
                    gen.writeString("[深度超过" + MAX_NESTING_DEPTH + "层，不再展开]");
                    return;
                }

                try {
                    depth.set(currentDepth + 1);
                    super.writeValue(gen, value);
                } finally {
                    depth.set(currentDepth);
                }
            }
        });

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
            String result = mapper.writeValueAsString(object);
            // 控制最大输出长度
            if (result.length() > MAX_TOTAL_OUTPUT_LENGTH) {
                result = result.substring(0, MAX_TOTAL_OUTPUT_LENGTH) +
                        "...[输出已截断，超出" + (result.length() - MAX_TOTAL_OUTPUT_LENGTH) + "字符]";
            }
            return result;
        } catch (JsonProcessingException e) {
            // 增强错误处理，加入对象类名
            return "Error serializing object of type [" + object.getClass().getName() + "]: " + e.getMessage();
        } catch (Exception e) {
            // 添加通用异常处理
            return "Unexpected error when serializing object: " + e.getMessage();
        }
    }

    /**
     * 将对象转换为字符串，同时指定最大输出长度
     *
     * @param object 任意对象
     * @param maxLength 最大输出长度
     * @return 字符串表示
     */
    public static String from(Object object, int maxLength) {
        if (object == null) {
            return "null";
        }

        try {
            String result = mapper.writeValueAsString(object);
            // 控制指定的最大输出长度
            if (result.length() > maxLength) {
                result = result.substring(0, maxLength) +
                        "...[输出已截断，超出" + (result.length() - maxLength) + "字符]";
            }
            return result;
        } catch (JsonProcessingException e) {
            // 增强错误处理，加入对象类名
            return "Error serializing object of type [" + object.getClass().getName() + "]: " + e.getMessage();
        } catch (Exception e) {
            // 添加通用异常处理
            return "Unexpected error when serializing object: " + e.getMessage();
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
    @SuppressWarnings("rawtypes")
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
    @SuppressWarnings("rawtypes")
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
    
    /**
     * Object[]数组限制序列化器
     */
    private static class ObjectArrayLimiter extends StdSerializer<Object[]> {
        public ObjectArrayLimiter() {
            super(Object[].class);
        }

        @Override
        public void serialize(Object[] value, JsonGenerator gen, SerializerProvider provider)
                throws IOException {
            if (value == null) {
                gen.writeNull();
                return;
            }

            gen.writeStartArray();

            if (value.length <= MAX_ARRAY_SIZE) {
                // 数组大小在限制范围内，正常序列化
                for (Object item : value) {
                    provider.defaultSerializeValue(item, gen);
                }
            } else {
                // 数组超出限制，只序列化头尾部分

                // 写入前ARRAY_DISPLAY_HEAD项
                for (int i = 0; i < ARRAY_DISPLAY_HEAD; i++) {
                    provider.defaultSerializeValue(value[i], gen);
                }

                // 写入省略信息
                int omitted = value.length - ARRAY_DISPLAY_HEAD - ARRAY_DISPLAY_TAIL;
                gen.writeString("...(省略" + omitted + "项)...");

                // 写入后ARRAY_DISPLAY_TAIL项
                for (int i = value.length - ARRAY_DISPLAY_TAIL; i < value.length; i++) {
                    provider.defaultSerializeValue(value[i], gen);
                }
            }

            gen.writeEndArray();
        }
    }
    
    /**
     * int[]数组限制序列化器
     */
    private static class IntArrayLimiter extends StdSerializer<int[]> {
        public IntArrayLimiter() {
            super(int[].class);
        }

        @Override
        public void serialize(int[] value, JsonGenerator gen, SerializerProvider provider)
                throws IOException {
            if (value == null) {
                gen.writeNull();
                return;
            }

            gen.writeStartArray();

            if (value.length <= MAX_ARRAY_SIZE) {
                // 数组大小在限制范围内，正常序列化
                for (int item : value) {
                    gen.writeNumber(item);
                }
            } else {
                // 数组超出限制，只序列化头尾部分

                // 写入前ARRAY_DISPLAY_HEAD项
                for (int i = 0; i < ARRAY_DISPLAY_HEAD; i++) {
                    gen.writeNumber(value[i]);
                }

                // 写入省略信息
                int omitted = value.length - ARRAY_DISPLAY_HEAD - ARRAY_DISPLAY_TAIL;
                gen.writeString("...(省略" + omitted + "项)...");

                // 写入后ARRAY_DISPLAY_TAIL项
                for (int i = value.length - ARRAY_DISPLAY_TAIL; i < value.length; i++) {
                    gen.writeNumber(value[i]);
                }
            }

            gen.writeEndArray();
        }
    }
    
    /**
     * long[]数组限制序列化器
     */
    private static class LongArrayLimiter extends StdSerializer<long[]> {
        public LongArrayLimiter() {
            super(long[].class);
        }

        @Override
        public void serialize(long[] value, JsonGenerator gen, SerializerProvider provider)
                throws IOException {
            if (value == null) {
                gen.writeNull();
                return;
            }

            gen.writeStartArray();

            if (value.length <= MAX_ARRAY_SIZE) {
                // 数组大小在限制范围内，正常序列化
                for (long item : value) {
                    gen.writeNumber(item);
                }
            } else {
                // 数组超出限制，只序列化头尾部分

                // 写入前ARRAY_DISPLAY_HEAD项
                for (int i = 0; i < ARRAY_DISPLAY_HEAD; i++) {
                    gen.writeNumber(value[i]);
                }

                // 写入省略信息
                int omitted = value.length - ARRAY_DISPLAY_HEAD - ARRAY_DISPLAY_TAIL;
                gen.writeString("...(省略" + omitted + "项)...");

                // 写入后ARRAY_DISPLAY_TAIL项
                for (int i = value.length - ARRAY_DISPLAY_TAIL; i < value.length; i++) {
                    gen.writeNumber(value[i]);
                }
            }

            gen.writeEndArray();
        }
    }
    
    /**
     * double[]数组限制序列化器
     */
    private static class DoubleArrayLimiter extends StdSerializer<double[]> {
        public DoubleArrayLimiter() {
            super(double[].class);
        }

        @Override
        public void serialize(double[] value, JsonGenerator gen, SerializerProvider provider)
                throws IOException {
            if (value == null) {
                gen.writeNull();
                return;
            }

            gen.writeStartArray();

            if (value.length <= MAX_ARRAY_SIZE) {
                // 数组大小在限制范围内，正常序列化
                for (double item : value) {
                    gen.writeNumber(item);
                }
            } else {
                // 数组超出限制，只序列化头尾部分

                // 写入前ARRAY_DISPLAY_HEAD项
                for (int i = 0; i < ARRAY_DISPLAY_HEAD; i++) {
                    gen.writeNumber(value[i]);
                }

                // 写入省略信息
                int omitted = value.length - ARRAY_DISPLAY_HEAD - ARRAY_DISPLAY_TAIL;
                gen.writeString("...(省略" + omitted + "项)...");

                // 写入后ARRAY_DISPLAY_TAIL项
                for (int i = value.length - ARRAY_DISPLAY_TAIL; i < value.length; i++) {
                    gen.writeNumber(value[i]);
                }
            }

            gen.writeEndArray();
        }
    }
    
    /**
     * float[]数组限制序列化器
     */
    private static class FloatArrayLimiter extends StdSerializer<float[]> {
        public FloatArrayLimiter() {
            super(float[].class);
        }

        @Override
        public void serialize(float[] value, JsonGenerator gen, SerializerProvider provider)
                throws IOException {
            if (value == null) {
                gen.writeNull();
                return;
            }

            gen.writeStartArray();

            if (value.length <= MAX_ARRAY_SIZE) {
                // 数组大小在限制范围内，正常序列化
                for (float item : value) {
                    gen.writeNumber(item);
                }
            } else {
                // 数组超出限制，只序列化头尾部分

                // 写入前ARRAY_DISPLAY_HEAD项
                for (int i = 0; i < ARRAY_DISPLAY_HEAD; i++) {
                    gen.writeNumber(value[i]);
                }

                // 写入省略信息
                int omitted = value.length - ARRAY_DISPLAY_HEAD - ARRAY_DISPLAY_TAIL;
                gen.writeString("...(省略" + omitted + "项)...");

                // 写入后ARRAY_DISPLAY_TAIL项
                for (int i = value.length - ARRAY_DISPLAY_TAIL; i < value.length; i++) {
                    gen.writeNumber(value[i]);
                }
            }

            gen.writeEndArray();
        }
    }
    
    /**
     * boolean[]数组限制序列化器
     */
    private static class BooleanArrayLimiter extends StdSerializer<boolean[]> {
        public BooleanArrayLimiter() {
            super(boolean[].class);
        }

        @Override
        public void serialize(boolean[] value, JsonGenerator gen, SerializerProvider provider)
                throws IOException {
            if (value == null) {
                gen.writeNull();
                return;
            }

            gen.writeStartArray();

            if (value.length <= MAX_ARRAY_SIZE) {
                // 数组大小在限制范围内，正常序列化
                for (boolean item : value) {
                    gen.writeBoolean(item);
                }
            } else {
                // 数组超出限制，只序列化头尾部分

                // 写入前ARRAY_DISPLAY_HEAD项
                for (int i = 0; i < ARRAY_DISPLAY_HEAD; i++) {
                    gen.writeBoolean(value[i]);
                }

                // 写入省略信息
                int omitted = value.length - ARRAY_DISPLAY_HEAD - ARRAY_DISPLAY_TAIL;
                gen.writeString("...(省略" + omitted + "项)...");

                // 写入后ARRAY_DISPLAY_TAIL项
                for (int i = value.length - ARRAY_DISPLAY_TAIL; i < value.length; i++) {
                    gen.writeBoolean(value[i]);
                }
            }

            gen.writeEndArray();
        }
    }
    
    /**
     * char[]数组限制序列化器
     */
    private static class CharArrayLimiter extends StdSerializer<char[]> {
        public CharArrayLimiter() {
            super(char[].class);
        }

        @Override
        public void serialize(char[] value, JsonGenerator gen, SerializerProvider provider)
                throws IOException {
            if (value == null) {
                gen.writeNull();
                return;
            }

            gen.writeStartArray();

            if (value.length <= MAX_ARRAY_SIZE) {
                // 数组大小在限制范围内，正常序列化
                for (char item : value) {
                    gen.writeString(String.valueOf(item));
                }
            } else {
                // 数组超出限制，只序列化头尾部分

                // 写入前ARRAY_DISPLAY_HEAD项
                for (int i = 0; i < ARRAY_DISPLAY_HEAD; i++) {
                    gen.writeString(String.valueOf(value[i]));
                }

                // 写入省略信息
                int omitted = value.length - ARRAY_DISPLAY_HEAD - ARRAY_DISPLAY_TAIL;
                gen.writeString("...(省略" + omitted + "项)...");

                // 写入后ARRAY_DISPLAY_TAIL项
                for (int i = value.length - ARRAY_DISPLAY_TAIL; i < value.length; i++) {
                    gen.writeString(String.valueOf(value[i]));
                }
            }

            gen.writeEndArray();
        }
    }
    
    /**
     * short[]数组限制序列化器
     */
    private static class ShortArrayLimiter extends StdSerializer<short[]> {
        public ShortArrayLimiter() {
            super(short[].class);
        }

        @Override
        public void serialize(short[] value, JsonGenerator gen, SerializerProvider provider)
                throws IOException {
            if (value == null) {
                gen.writeNull();
                return;
            }

            gen.writeStartArray();

            if (value.length <= MAX_ARRAY_SIZE) {
                // 数组大小在限制范围内，正常序列化
                for (short item : value) {
                    gen.writeNumber(item);
                }
            } else {
                // 数组超出限制，只序列化头尾部分

                // 写入前ARRAY_DISPLAY_HEAD项
                for (int i = 0; i < ARRAY_DISPLAY_HEAD; i++) {
                    gen.writeNumber(value[i]);
                }

                // 写入省略信息
                int omitted = value.length - ARRAY_DISPLAY_HEAD - ARRAY_DISPLAY_TAIL;
                gen.writeString("...(省略" + omitted + "项)...");

                // 写入后ARRAY_DISPLAY_TAIL项
                for (int i = value.length - ARRAY_DISPLAY_TAIL; i < value.length; i++) {
                    gen.writeNumber(value[i]);
                }
            }

            gen.writeEndArray();
        }
    }
} 