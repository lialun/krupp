package vip.lialun.string;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import java.util.*;

/**
 * JacksonToString的测试类
 */
public class SafetyToStringTest {

    @Test
    public void testStringTruncation() {
        // 测试正常长度的字符串
        String shortString = "这是一个正常长度的字符串";
        String shortResult = SafetyToString.from(shortString);
        
        // 断言正常长度字符串不被截断
        Assertions.assertEquals("\"" + shortString + "\"", shortResult, "短字符串不应被截断");
        
        // 测试超长字符串
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            sb.append("这是第").append(i).append("个字符 ");
        }
        String longString = sb.toString();
        String longResult = SafetyToString.from(longString);
        
        // 断言超长字符串被截断
        Assertions.assertTrue(longResult.contains("...(省略"), "长字符串应该被截断");
        Assertions.assertTrue(longResult.length() < longString.length(), "截断后的字符串长度应小于原字符串");
        Assertions.assertTrue(longResult.startsWith("\"" + longString.substring(0, 50)), "截断字符串应以原字符串开头");
    }

    @Test
    public void testCollectionTruncation() {
        // 测试正常大小的集合
        List<String> smallList = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            smallList.add("项目-" + i);
        }
        String smallResult = SafetyToString.from(smallList);
        
        // 断言正常大小集合不被截断
        Assertions.assertFalse(smallResult.contains("...(省略"), "小集合不应被截断");
        Assertions.assertTrue(smallResult.contains("\"项目-0\""), "小集合应包含第一项");
        Assertions.assertTrue(smallResult.contains("\"项目-19\""), "小集合应包含最后一项");
        
        // 测试超大集合
        List<String> largeList = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            largeList.add("项目-" + i);
        }
        String largeResult = SafetyToString.from(largeList);
        
        // 断言超大集合被截断
        Assertions.assertTrue(largeResult.contains("...(省略"), "大集合应被截断");
        Assertions.assertTrue(largeResult.contains("\"项目-0\""), "大集合应包含第一项");
        Assertions.assertTrue(largeResult.contains("\"项目-9\""), "大集合应包含前10项的最后一项");
        Assertions.assertTrue(largeResult.contains("\"项目-90\""), "大集合应包含后10项的第一项");
        Assertions.assertTrue(largeResult.contains("\"项目-99\""), "大集合应包含最后一项");
    }

    @Test
    public void testByteArrayTruncation() {
        // 测试正常大小的字节数组
        byte[] smallArray = new byte[200];
        for (int i = 0; i < smallArray.length; i++) {
            smallArray[i] = (byte) i;
        }
        String smallResult = SafetyToString.from(smallArray);
        
        // 断言正常大小字节数组不被截断
        Assertions.assertFalse(smallResult.contains("...(省略"), "小字节数组不应被截断");
        Assertions.assertTrue(smallResult.startsWith("["), "小字节数组应以[开头");
        Assertions.assertTrue(smallResult.endsWith("]"), "小字节数组应以]结尾");
        
        // 测试超大字节数组
        byte[] largeArray = new byte[1000];
        for (int i = 0; i < largeArray.length; i++) {
            largeArray[i] = (byte) i;
        }
        String largeResult = SafetyToString.from(largeArray);
        
        // 断言超大字节数组被截断
        Assertions.assertTrue(largeResult.contains("...(省略"), "大字节数组应被截断");
        Assertions.assertTrue(largeResult.startsWith("["), "大字节数组应以[开头");
        Assertions.assertTrue(largeResult.endsWith("]"), "大字节数组应以]结尾");
    }

    @Test
    public void testMapTruncation() {
        // 测试正常大小的Map
        Map<String, Integer> smallMap = new HashMap<>();
        for (int i = 0; i < 20; i++) {
            smallMap.put("键-" + i, i);
        }
        String smallResult = SafetyToString.from(smallMap);
        
        // 断言正常大小Map不被截断
        Assertions.assertFalse(smallResult.contains("__omitted__"), "小Map不应被截断");
        Assertions.assertTrue(smallResult.startsWith("{"), "小Map应以{开头");
        Assertions.assertTrue(smallResult.endsWith("}"), "小Map应以}结尾");
        
        // 测试超大Map
        Map<String, Integer> largeMap = new HashMap<>();
        for (int i = 0; i < 50; i++) {
            largeMap.put("键-" + i, i);
        }
        String largeResult = SafetyToString.from(largeMap);
        
        // 断言超大Map被截断
        Assertions.assertTrue(largeResult.contains("__omitted__"), "大Map应被截断");
        Assertions.assertTrue(largeResult.contains("省略"), "大Map应显示省略信息");
        Assertions.assertTrue(largeResult.startsWith("{"), "大Map应以{开头");
        Assertions.assertTrue(largeResult.endsWith("}"), "大Map应以}结尾");
    }
    
    @Test
    public void testMaxOutputLength() {
        // 创建一个超大的复杂数据结构，确保序列化后的字符串长度超过默认的MAX_TOTAL_OUTPUT_LENGTH限制
        List<Map<String, Object>> largeDataStructure = createLargeDataStructure();
        
        // 1. 测试使用默认最大长度
        String result1 = SafetyToString.from(largeDataStructure);
        
        // 验证默认最大长度输出
        Assertions.assertTrue(result1.length() > 8000, "默认最大长度输出应该接近设定的8192字符");
        Assertions.assertTrue(result1.length() < 8500, "默认最大长度输出不应该超过8500字符");
        Assertions.assertTrue(result1.contains("输出已截断"), "默认最大长度输出应该包含截断信息");
        
        // 2. 测试使用自定义最大长度 (1000字符)
        String result2 = SafetyToString.from(largeDataStructure, 1000);
        
        // 验证自定义最大长度输出
        Assertions.assertTrue(result2.length() > 1000, "自定义最大长度输出应该略超过1000字符（因为有截断信息）");
        Assertions.assertTrue(result2.length() < 1100, "自定义最大长度输出不应该超过1100字符");
        Assertions.assertTrue(result2.contains("输出已截断"), "自定义最大长度输出应该包含截断信息");
        
        // 3. 测试使用较大的自定义最大长度 (20000字符)
        String result3 = SafetyToString.from(largeDataStructure, 20000);
        
        // 验证超大自定义最大长度输出
        int expectedLength = Math.min(largeDataStructure.toString().length(), 20100);
        Assertions.assertTrue(result3.length() <= expectedLength, 
                "超大自定义最大长度输出不应该超过" + expectedLength + "字符，实际长度：" + result3.length());
        
        // 4. 测试各种数组类型的输出
        testPrimitiveArrays();
    }
    
    /**
     * 创建一个大型测试数据结构
     */
    private List<Map<String, Object>> createLargeDataStructure() {
        List<Map<String, Object>> largeDataStructure = new ArrayList<>();
        
        // 创建50个包含大量数据的Map
        for (int i = 0; i < 50; i++) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", i);
            
            // 添加一个长字符串
            StringBuilder description = new StringBuilder();
            for (int j = 0; j < 200; j++) {
                description.append("这是描述文本-").append(j).append(" ");
            }
            item.put("description", description.toString());
            
            // 添加一个包含50个元素的数组
            String[] tags = new String[50];
            for (int j = 0; j < 50; j++) {
                tags[j] = "标签-" + i + "-" + j;
            }
            item.put("tags", tags);
            
            // 添加一个嵌套的Map
            Map<String, Integer> nestedMap = new HashMap<>();
            for (int j = 0; j < 40; j++) {
                nestedMap.put("属性-" + j, j * 100);
            }
            item.put("properties", nestedMap);
            
            largeDataStructure.add(item);
        }
        
        return largeDataStructure;
    }
    
    /**
     * 测试基本数据类型数组的处理
     */
    private void testPrimitiveArrays() {
        // int数组
        int[] intArray = new int[100];
        for (int i = 0; i < intArray.length; i++) {
            intArray[i] = i * 10;
        }
        String intArrayResult = SafetyToString.from(intArray);
        Assertions.assertTrue(intArrayResult.contains("...(省略80项)..."), "int数组应显示省略信息");
        Assertions.assertTrue(intArrayResult.startsWith("[0,10,20,30,40,50,60,70,80,90,"), "int数组应正确显示前10项");
        Assertions.assertTrue(intArrayResult.endsWith("900,910,920,930,940,950,960,970,980,990]"), "int数组应正确显示后10项");
        
        // long数组
        long[] longArray = new long[100];
        for (int i = 0; i < longArray.length; i++) {
            longArray[i] = i * 1000L;
        }
        String longArrayResult = SafetyToString.from(longArray);
        Assertions.assertTrue(longArrayResult.contains("...(省略80项)..."), "long数组应显示省略信息");
        
        // double数组
        double[] doubleArray = new double[100];
        for (int i = 0; i < doubleArray.length; i++) {
            doubleArray[i] = i * 0.5;
        }
        String doubleArrayResult = SafetyToString.from(doubleArray);
        Assertions.assertTrue(doubleArrayResult.contains("...(省略80项)..."), "double数组应显示省略信息");
        
        // boolean数组
        boolean[] booleanArray = new boolean[100];
        for (int i = 0; i < booleanArray.length; i++) {
            booleanArray[i] = (i % 2 == 0);
        }
        String booleanArrayResult = SafetyToString.from(booleanArray);
        Assertions.assertTrue(booleanArrayResult.contains("...(省略80项)..."), "boolean数组应显示省略信息");
        
        // char数组
        char[] charArray = new char[100];
        for (int i = 0; i < charArray.length; i++) {
            charArray[i] = (char)('A' + (i % 26));
        }
        String charArrayResult = SafetyToString.from(charArray);
        Assertions.assertTrue(charArrayResult.contains("...(省略80项)..."), "char数组应显示省略信息");
        Assertions.assertTrue(charArrayResult.contains("\"A\",\"B\",\"C\""), "char数组应正确格式化字符");
    }

    @Test
    public void testComplexObject() {
        // 测试复杂对象
        ComplexObject complexObject = createComplexObject();
        
        String result = SafetyToString.from(complexObject);
        
        // 断言复杂对象序列化结果包含所有字段
        Assertions.assertTrue(result.contains("\"name\""), "复杂对象结果应包含name字段");
        Assertions.assertTrue(result.contains("\"age\""), "复杂对象结果应包含age字段");
        Assertions.assertTrue(result.contains("\"description\""), "复杂对象结果应包含description字段");
        Assertions.assertTrue(result.contains("\"tags\""), "复杂对象结果应包含tags字段");
        Assertions.assertTrue(result.contains("\"data\""), "复杂对象结果应包含data字段");
        Assertions.assertTrue(result.contains("\"properties\""), "复杂对象结果应包含properties字段");
        
        // 断言长字符串被截断
        Assertions.assertTrue(result.contains("...(省略"), "复杂对象中的长字符串应被截断");
        
        // 断言大集合被截断
        Assertions.assertTrue(result.contains("\"项目-0\""), "复杂对象中的大集合应包含第一项");
        Assertions.assertTrue(result.contains("\"项目-99\""), "复杂对象中的大集合应包含最后一项");
        
        // 断言字节数组被截断
        Assertions.assertTrue(result.contains("...(省略"), "复杂对象中的字节数组应被截断");
    }
    
    /**
     * 创建一个用于测试的复杂对象
     */
    private ComplexObject createComplexObject() {
        ComplexObject complexObject = new ComplexObject();
        complexObject.setName("测试对象");
        complexObject.setAge(30);
        
        // 添加长字符串
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            sb.append("X");
        }
        complexObject.setDescription(sb.toString());
        
        // 添加大集合
        List<String> largeList = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            largeList.add("项目-" + i);
        }
        complexObject.setTags(largeList);
        
        // 添加大字节数组
        byte[] largeArray = new byte[1000];
        for (int i = 0; i < largeArray.length; i++) {
            largeArray[i] = (byte) i;
        }
        complexObject.setData(largeArray);
        
        // 添加大Map
        Map<String, Integer> largeMap = new HashMap<>();
        for (int i = 0; i < 50; i++) {
            largeMap.put("键-" + i, i);
        }
        complexObject.setProperties(largeMap);
        
        return complexObject;
    }

    /**
     * 用于测试的复杂对象
     */
    static class ComplexObject {
        private String name;
        private int age;
        private String description;
        private List<String> tags;
        private byte[] data;
        private Map<String, Integer> properties;

        // Getters and Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public int getAge() { return age; }
        public void setAge(int age) { this.age = age; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public List<String> getTags() { return tags; }
        public void setTags(List<String> tags) { this.tags = tags; }
        
        public byte[] getData() { return data; }
        public void setData(byte[] data) { this.data = data; }
        
        public Map<String, Integer> getProperties() { return properties; }
        public void setProperties(Map<String, Integer> properties) { this.properties = properties; }
    }
} 