package vip.lialun.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.NonNull;
import vip.lialun.json.mask.PhoneMaskingSerializer;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Objects;
import java.util.TimeZone;

public class JacksonHelper {

    private static final ObjectMapper DEFAULT_MAPPER;

    static {
        DEFAULT_MAPPER = new ObjectMapper();
        DEFAULT_MAPPER.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        DEFAULT_MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        DEFAULT_MAPPER.configure(SerializationFeature.INDENT_OUTPUT, false);
        DEFAULT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        DEFAULT_MAPPER.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        DEFAULT_MAPPER.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        DEFAULT_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        DEFAULT_MAPPER.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        DEFAULT_MAPPER.registerModule(new JavaTimeModule());

        // 配置 LocalDateTime 的序列化格式
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(formatter));
        DEFAULT_MAPPER.registerModule(javaTimeModule);
        DEFAULT_MAPPER.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        SimpleModule module = new SimpleModule();
        module.addSerializer(String.class, new PhoneMaskingSerializer());
        DEFAULT_MAPPER.registerModule(module);
    }

    /**
     * Jackson默认配置实例
     */
    public static ObjectMapper getDefaultMapper() {
        return DEFAULT_MAPPER;
    }

    /**
     * JSON 字符串转为 ObjectNode 对象
     */
    public static ObjectNode parseObject(String json) throws JsonProcessingException {
        return parseObject(json, DEFAULT_MAPPER);
    }

    /**
     * JSONArray 字符串转 ArrayNode 对象
     */
    public static ArrayNode parseArray(String jsonArray) throws JsonProcessingException {
        return parseArray(jsonArray, DEFAULT_MAPPER);
    }

    /**
     * JSON 字符串转为 ObjectNode 对象
     */
    public static ObjectNode parseObject(String json, ObjectMapper objectMapper) throws JsonProcessingException {
        return objectMapper.readValue(json, ObjectNode.class);
    }

    /**
     * JSONArray 字符串转 ArrayNode 对象
     */
    public static ArrayNode parseArray(String jsonArray, ObjectMapper objectMapper) throws JsonProcessingException {
        return objectMapper.readValue(jsonArray, ArrayNode.class);
    }

    /**
     * JSON字符串转换为Jackson对象
     */
    public static JsonNode parse(@NonNull String string) throws JsonProcessingException {
        return parse(string, DEFAULT_MAPPER);
    }

    /**
     * JSON字符串转换为Jackson对象
     */
    public static JsonNode parse(@NonNull String string, @NonNull ObjectMapper objectMapper) throws JsonProcessingException {
        return objectMapper.readValue(string, JsonNode.class);
    }

    /**
     * JSON 字符串转为 Bean
     */
    public static <T> T jsonToBean(String json, Class<T> clazz) throws JsonProcessingException {
        if (Objects.isNull(json) || Objects.isNull(clazz)) {
            return null;
        }
        return DEFAULT_MAPPER.readValue(json, clazz);
    }

    /**
     * ObjectNode 对象转为 Bean
     */
    public static <T> T jsonNodeToBean(ObjectNode node, Class<T> clazz) throws JsonProcessingException {
        if (Objects.isNull(node) || Objects.isNull(clazz)) {
            return null;
        }
        return DEFAULT_MAPPER.treeToValue(node, clazz);
    }

    /**
     * JSONArray 字符串转为 Bean Collection
     */
    @SuppressWarnings("unchecked")
    public static <T, A extends Collection<T>> A jsonToBeanCollection(String json, TypeReference<Collection<T>> clazz) throws JsonProcessingException {
        if (Objects.isNull(json) || Objects.isNull(clazz)) {
            return null;
        }
        return (A) DEFAULT_MAPPER.readValue(json, clazz);
    }

    /**
     * Bean 转为 JSON 字符串
     */
    public static String beanToJson(Object bean) throws JsonProcessingException {
        if (Objects.isNull(bean)) {
            return null;
        }
        return DEFAULT_MAPPER.writeValueAsString(bean);
    }
}
