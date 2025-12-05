package priv.szf.fastcall.core.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "fast-call")
@Data
public class FastCallProperties {
    /** 客户端配置 */
    private Client client = new Client();

    /** 连接池配置 */
    private Pool pool = new Pool();

    /** 最大并发请求数 */
    private int maxRequests = 200;

    /** 每主机最大并发请求数 */
    private int maxRequestsPerHost = 30;

    /** 是否启用内存缓存 */
    private boolean enableMemoryCache = true;

    private Retry retry = new Retry();

    @Data
    public static class Client {
        /** 连接超时 */
        private int connectTimeout = 10;

        /** 读取超时 */
        private int readTimeout = 30;

        /** 写入超时 */
        private int writeTimeout = 30;
    }

    @Data
    public static class Pool {
        /** 最大空闲连接数 */
        private int maxIdleConnections = 50;

        /** 空闲连接回收时间 */
        private int keepAliveMinutes = 5;
    }

    @Data
    public static class Retry {
        private int maxAttempts = 3;
        private long delayMillis = 1000;
    }
}
