package vip.lialun.notify;

import com.lark.oapi.Client;
import com.lark.oapi.service.im.v1.model.CreateMessageReq;
import com.lark.oapi.service.im.v1.model.CreateMessageReqBody;
import com.lark.oapi.service.im.v1.model.CreateMessageResp;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import vip.lialun.json.JacksonHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 飞书消息发送工具类，支持创建多个实例，用于不同飞书应用配置
 *
 * @author lialun
 */
@Slf4j
public final class FeiShuSender {
    private final Client client;

    /**
     * 创建飞书消息发送实例的构建器
     */
    public static class Builder {
        private final String appId;
        private final String appSecret;

        /**
         * 构建器构造函数，设置必要参数
         *
         * @param appId     飞书应用ID
         * @param appSecret 飞书应用密钥
         */
        public Builder(@NonNull String appId, @NonNull String appSecret) {
            this.appId = appId;
            this.appSecret = appSecret;
        }

        /**
         * 构建飞书消息发送实例
         */
        public FeiShuSender build() {
            return new FeiShuSender(this);
        }
    }

    /**
     * 使用FeiShuProperties创建FeiShuSender实例
     *
     * @param properties FeiShuProperties配置参数
     * @return FeiShuSender实例
     */
    public static FeiShuSender fromProperties(@NonNull FeiShuProperties properties) {
        return new Builder(
                properties.getAppId(),
                properties.getAppSecret())
                .build();
    }

    /**
     * 私有构造函数，通过Builder创建实例
     */
    private FeiShuSender(Builder builder) {
        String appId = builder.appId;
        String appSecret = builder.appSecret;
        this.client = Client.newBuilder(appId, appSecret).build();
    }

    /**
     * 发送消息到群组
     *
     * @param application 应用名称
     * @param groupId     群组ID
     * @param title       消息标题
     * @param content     消息内容
     * @return 发送是否成功
     * @throws Exception 发送异常
     */
    @SuppressWarnings("UnusedReturnValue")
    public boolean sendMessageToGroup(String application, String groupId, String title, List<List<Map<String, String>>> content) throws Exception {
        HashMap<String, Object> zhCnMap = new HashMap<>();
        zhCnMap.put("title", application + " - " + title);
        zhCnMap.put("content", content);

        HashMap<String, Object> contentMap = new HashMap<>();
        contentMap.put("zh_cn", zhCnMap);

        // 创建请求对象
        CreateMessageReq req = CreateMessageReq.newBuilder()
                .receiveIdType("chat_id")
                .createMessageReqBody(CreateMessageReqBody.newBuilder()
                        .receiveId(groupId)
                        .msgType("post")
                        .content(JacksonHelper.beanToJson(contentMap))
                        .build())
                .build();

        // 发起请求
        CreateMessageResp resp = client.im().message().create(req);

        if (!resp.success()) {
            log.error("FeiShu notify failure: {}", JacksonHelper.beanToJson(resp));
        }
        return resp.success();
    }

    /**
     * 创建简单的文本内容
     *
     * @param text 文本内容
     * @return 内容列表
     */
    public static List<Map<String, String>> createTextContent(String text) {
        List<Map<String, String>> contentList = new ArrayList<>();
        contentList.add(Map.of("tag", "text", "text", text));
        return contentList;
    }

    /**
     * 创建包含链接的文本内容
     *
     * @param text 文本内容
     * @param linkText 链接文字
     * @param href 链接地址
     * @return 内容列表
     */
    public static List<Map<String, String>> createTextWithLink(String text, String linkText, String href) {
        List<Map<String, String>> contentList = new ArrayList<>();
        contentList.add(Map.of("tag", "text", "text", text));
        contentList.add(Map.of("tag", "a", "text", linkText, "href", href));
        return contentList;
    }
}
