package cn.shuniverse.base.utils;

import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;

import java.time.Duration;
import java.util.Arrays;

/**
 * Created by 蛮小满Sama at 2025-11-24 15:38
 *
 * @author 蛮小满Sama
 * @description Minio工具类
 */
@Slf4j
public class MinioUtil {
    /**
     * 私有构造函数
     */
    private MinioUtil() {
    }

    /**
     * 创建MinioClient
     *
     * @param accessKey accessKey或账号
     * @param secretKey secretKey或密码
     * @param endpoint  endpoint
     * @return MinioClient
     */
    public static MinioClient minioClient(String accessKey, String secretKey, String endpoint) {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                // 连接超时时间
                .connectTimeout(Duration.ofMinutes(5))
                // 读取超时时间
                .readTimeout(Duration.ofMinutes(10))
                // 写入超时时间
                .writeTimeout(Duration.ofMinutes(2))
                // ping间隔时间
                .pingInterval(Duration.ofMinutes(1))
                // 允许HTTP2
                .protocols(Arrays.asList(Protocol.HTTP_1_1, Protocol.HTTP_2))
                .build();
        return MinioClient.builder().endpoint(endpoint).credentials(accessKey, secretKey).httpClient(okHttpClient).build();
    }
}
