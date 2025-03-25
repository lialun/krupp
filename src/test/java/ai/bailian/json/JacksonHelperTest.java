package ai.bailian.json;

import ai.bailian.BaseTest;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class JacksonHelperTest extends BaseTest {

    private static User kitty;
    private static String targetKittyJsonStr;
    private static String users;

    @BeforeAll
    public static void init() {
        kitty = new User("111111", "kitty", 18);
        User cat = new User("22222", "mi ao", 2);
        kitty.addFriendList(cat);

        User dog = new User("33333", "wa ng", 1);
        kitty.addFriendList(dog);

        targetKittyJsonStr = "{\"用户id\":\"111111\",\"name\":\"kitty\",\"age\":18,\"friendList\":[{\"用户id\":\"22222\",\"name\":\"mi ao\",\"age\":2,\"friendList\":[]},{\"用户id\":\"33333\",\"name\":\"wa ng\",\"age\":1,\"friendList\":[]}]}";
        users = "[\n" +
                "    {\n" +
                "        \"id\":\"1\",\n" +
                "        \"name\":\"tom\",\n" +
                "        \"age\":12\n" +
                "    },\n" +
                "    {\n" +
                "        \"id\":\"2\",\n" +
                "        \"name\":\"jack\",\n" +
                "        \"age\":18\n" +
                "    }" +
                "]";

    }

    @Test
    void getDefaultMapper() {
        assertNotNull(JacksonHelper.getDefaultMapper(), "对象不为空");
    }

    @Test
    void parseEmptyJsonStr() {
        assertDoesNotThrow(() -> assertTrue(JacksonHelper.parseObject("{}").isEmpty()));
    }

    @Test
    void editObjectNode() {
        assertDoesNotThrow(
                () -> {
                    ObjectNode node = JacksonHelper.parseObject("{}");
                    node.put("用户id", "12");
                    node.put("name", "test");
                    node.put("age", 18);
                    node.putArray("friendList");

                    ArrayNode items = (ArrayNode) node.get("friendList");

                    ObjectNode node1 = JacksonHelper.parseObject("{}");
                    node1.put("用户id", "121");
                    node1.put("name", "test1");
                    node1.put("age", 181);
                    items.add(node1);

                    assertEquals("{\"用户id\":\"12\",\"name\":\"test\",\"age\":18,\"friendList\":[{\"用户id\":\"121\",\"name\":\"test1\",\"age\":181}]}", node.toString());
                }
        );
    }

    @Test
    void parseNull() {
        assertThrows(
                IllegalArgumentException.class,
                () -> assertNull(JacksonHelper.parseObject(null))
        );
    }

    @Test
    void parseBadJson() {
        assertThrows(
                JsonProcessingException.class,
                () -> JacksonHelper.parseObject("{")
        );
    }

    @Test
    void parseJsonArray() {
        assertDoesNotThrow(() -> {
            ArrayNode nodes = JacksonHelper.parseArray("[{}]");
            assertEquals(1, nodes.size());
            assertTrue(nodes.get(0).isEmpty());
        });
    }

    @Test
    void parseBeanArray() {
        assertDoesNotThrow(() -> {
            List<User> items = JacksonHelper.jsonToBeanCollection(users, new TypeReference<Collection<User>>() {
            });
            assertNotNull(items);
            assertEquals(2, items.size());

            User tom = items.get(0);
            assertEquals("tom", tom.getName());

            User jack = items.get(1);
            assertEquals("jack", jack.getName());
        });
    }

    @Test
    void parseJsonComplex() {
        String json = "{\n" +
                "    \"v1\":1,\n" +
                "    \"v2\":1.1,\n" +
                "    \"v3\":\"test\",\n" +
                "    \"v4\":{\n" +
                "        \"sub1\":[\n" +
                "            {\n" +
                "                \"sub2\":\"hello world\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"sub3\":\"good morning\"\n" +
                "            }\n" +
                "        ]\n" +
                "    }\n" +
                "}";

        assertDoesNotThrow(
                () -> {
                    ObjectNode node = JacksonHelper.parseObject(json);

                    assertEquals(node.get("v1").asInt(), 1);
                    assertEquals(node.get("v3").asText(), "test");

                    JsonNode subNode = node.get("v4");
                    assertEquals(1, subNode.size());

                    assertEquals(2, subNode.get("sub1").size());

                    Iterator<JsonNode> subNodes = subNode.get("sub1").elements();
                    JsonNode sub2 = subNodes.next();
                    assertEquals(sub2.get("sub2").asText(), "hello world");
                    JsonNode sub3 = subNodes.next();
                    assertEquals(sub3.get("sub3").asText(), "good morning");
                }
        );
    }

    @Test
    void jsonNodeToBean() {
        assertDoesNotThrow(
                () -> {
                    ObjectNode kittyJsonNode = JacksonHelper.parseObject(targetKittyJsonStr);
                    User user = JacksonHelper.jsonNodeToBean(kittyJsonNode, User.class);
                    assertEquals("111111", user.getId());
                    assertEquals("kitty", user.getName());
                    assertEquals(2, user.getFriendList().size());
                    User catFriend = user.getFriendList().get(0);
                    User dogFriend = user.getFriendList().get(1);
                    assertEquals("mi ao", catFriend.getName());
                    assertEquals("wa ng", dogFriend.getName());
                }
        );
    }

    @Test
    void jsonToBean() {
        assertDoesNotThrow(
                () -> {
                    User user = JacksonHelper.jsonToBean(targetKittyJsonStr, User.class);
                    assertEquals("111111", user.getId());
                    assertEquals("kitty", user.getName());
                    assertEquals(2, user.getFriendList().size());
                    User catFriend = user.getFriendList().get(0);
                    User dogFriend = user.getFriendList().get(1);
                    assertEquals("mi ao", catFriend.getName());
                    assertEquals("wa ng", dogFriend.getName());
                }
        );
    }

    @Test
    void beanToJson() {
        assertDoesNotThrow(
                () -> {
                    String kittyJson = JacksonHelper.beanToJson(kitty);
                    assertEquals(targetKittyJsonStr, kittyJson);
                }
        );
    }

    @JsonPropertyOrder({"用户id", "name", "age", "friendList"})
    private static class User {
        @JsonProperty("用户id")
        private final String id;
        private final String name;
        private final Integer age;
        private final List<User> friendList;

        public User() {
            this("", "", 0);
        }

        public User(final String id, final String name, final Integer age) {
            this.id = id;
            this.name = name;
            this.age = age;
            friendList = new LinkedList<>();
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public Integer getAge() {
            return age;
        }

        public List<User> getFriendList() {
            return friendList;
        }

        public void addFriendList(User friend) {
            friendList.add(friend);
        }

        @Override
        public String toString() {
            return "User{" +
                    "id='" + id + '\'' +
                    ", name='" + name + '\'' +
                    ", age=" + age +
                    ", friendList=" + friendList +
                    '}';
        }
    }
}