package vip.lialun.string;

import org.junit.jupiter.api.Test;

import java.util.*;

/**
 * JacksonToString的测试类
 */
public class JacksonToStringTest {

    @Test
    public void testStringTruncation() {
        // 测试正常长度的字符串
        String shortString = "这是一个正常长度的字符串";
        System.out.println("短字符串: " + JacksonToString.from(shortString));

        // 测试超长字符串
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            sb.append("这是第").append(i).append("个字符 ");
        }
        String longString = sb.toString();
        System.out.println("长字符串: " + JacksonToString.from(longString));
    }

    @Test
    public void testCollectionTruncation() {
        // 测试正常大小的集合
        List<String> smallList = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            smallList.add("项目-" + i);
        }
        System.out.println("小集合: " + JacksonToString.from(smallList));

        // 测试超大集合
        List<String> largeList = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            largeList.add("项目-" + i);
        }
        System.out.println("大集合: " + JacksonToString.from(largeList));
    }

    @Test
    public void testByteArrayTruncation() {
        // 测试正常大小的字节数组
        byte[] smallArray = new byte[200];
        for (int i = 0; i < smallArray.length; i++) {
            smallArray[i] = (byte) i;
        }
        System.out.println("小字节数组: " + JacksonToString.from(smallArray));

        // 测试超大字节数组
        byte[] largeArray = new byte[1000];
        for (int i = 0; i < largeArray.length; i++) {
            largeArray[i] = (byte) i;
        }
        System.out.println("大字节数组: " + JacksonToString.from(largeArray));
    }

    @Test
    public void testMapTruncation() {
        // 测试正常大小的Map
        Map<String, Integer> smallMap = new HashMap<>();
        for (int i = 0; i < 20; i++) {
            smallMap.put("键-" + i, i);
        }
        System.out.println("小Map: " + JacksonToString.from(smallMap));

        // 测试超大Map
        Map<String, Integer> largeMap = new HashMap<>();
        for (int i = 0; i < 50; i++) {
            largeMap.put("键-" + i, i);
        }
        System.out.println("大Map: " + JacksonToString.from(largeMap));
    }

    @Test
    public void testComplexObject() {
        // 测试复杂对象
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
        
        System.out.println("复杂对象: " + JacksonToString.from(complexObject));
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