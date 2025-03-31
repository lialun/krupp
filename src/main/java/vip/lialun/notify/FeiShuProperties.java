package vip.lialun.notify;

import lombok.Getter;
import lombok.Setter;

/**
 * FeiShuSender的配置属性类
 *
 * @author lialun
 */
@Getter
@Setter
public class FeiShuProperties {
    private String appId;
    private String appSecret;

    /**
     * 创建默认空实例
     */
    public FeiShuProperties() {
    }

    /**
     * 创建包含基本配置的实例
     */
    public FeiShuProperties(String appId, String appSecret) {
        this.appId = appId;
        this.appSecret = appSecret;
    }
} 