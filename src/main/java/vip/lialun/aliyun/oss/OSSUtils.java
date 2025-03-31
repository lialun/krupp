package vip.lialun.aliyun.oss;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;

public class OSSUtils {
    private final OSS ossClient;
    private final CommonOSSConfig ossConfig;

    public OSSUtils(CommonOSSConfig ossConfig) {
        this.ossConfig = ossConfig;
        this.ossClient = new OSSClientBuilder().build(ossConfig.getEndpoint(), ossConfig.getAccessKeyId(),
                ossConfig.getAccessKeySecret());
    }

    /**
     * 上传资源，并返回临时URL地址
     */
    public void upload(byte[] file, String key) {
        ossClient.putObject(ossConfig.getBucketName(), key, new ByteArrayInputStream(file));
    }

    /**
     * 上传资源，并返回临时URL地址
     */
    public void upload(InputStream inputStream, String key) {
        ossClient.putObject(ossConfig.getBucketName(), key, inputStream);
    }

    public String generatePreSignedUrl(String key, int expiresInMinutes) {
        Date expiration = new Date(System.currentTimeMillis() + 1000L * 60 * expiresInMinutes);
        URL url = ossClient.generatePresignedUrl(ossConfig.getBucketName(), key, expiration);
        return url.toString();
    }
}