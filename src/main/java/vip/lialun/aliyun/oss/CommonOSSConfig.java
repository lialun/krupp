package vip.lialun.aliyun.oss;

import lombok.Data;

@Data
public class CommonOSSConfig {
    private String endpoint;
    private String accessKeyId;
    private String accessKeySecret;
    private String bucketName;
}