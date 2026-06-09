package priv.szf.fastcall.core.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import priv.szf.fastcall.common.FcAuthType;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = "fast-call")
@Data
public class FastCallProperties {

    /** 最大并发请求数 */
    private int maxRequests = 200;

    /** 每主机最大并发请求数 */
    private int maxRequestsPerHost = 30;

    /** 是否允许事件 */
    private boolean allowEvent = false;

    /** 客户端配置 */
    private Client client = new Client();

    /** 连接池配置 */
    private Pool pool = new Pool();

    /** 请求缓存配置 */
    private Cache cache = new Cache();

    /** 数据源缓存配置 */
    private SourceCache sourceCache = new SourceCache();

    /** 转发代理配置 */
    private ForwardProxy forwardProxy = new ForwardProxy();

    /** 快捷源配置 */
    private List<EasySource> easySource = new ArrayList<>();

    private Retry retry = new Retry();

    @Data
    public static class ForwardProxy {
        /** 是否启用转发代理 */
        private boolean enable = false;

        /** 过滤前缀 */
        private String prefix = "/fc_forward_proxy";

        /** 是否自动添加转发请求头 */
        private boolean addForwardHeader = true;

    }

    @Data
    public static class EasySource {
        /** 系统配置 */
        private System system = new System();

        /** 认证配置 */
        private Auth auth = new Auth();

        @Data
        public static class System {
            /** 系统名称 */
            private String name;

            /** 系统编码 */
            private String code;

            /** 是否启用 */
            private boolean enable = true;

            /** 系统访问地址 */
            private String host;

            /** 认证类型 */
            private FcAuthType authType = FcAuthType.NONE;

            /** 连接超时 */
            private Integer connectTimeout;

            /** 读取超时 */
            private Integer readTimeout;

            /** 写入超时 */
            private Integer writeTimeout;
        }

        @Data
        public static class Auth {

            /** 认证路径 */
            private String path;

            /** 认证内容 */
            private String content;

            /** 特定主机认证 */
            private String particularHost;

            /** 未认证状态码 */
            private Integer unauthorizedCode;
        }
    }

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

    @Data
    public static class Cache {
        /** 启用请求缓存*/
        private boolean enable = false;

        /** 缓存最大大小（Byte）*/
        private long maxSize = 10 * 1024 * 1024;

        /** 缓存存储路径*/
        private String path = Paths.get(
                                        System.getProperty("java.io.tmpdir"),
                                        "fast-call-cache"
                                ).toString();
    }

    @Data
    public static class SourceCache {
        /** 启用数据源缓存*/
        private boolean enable = true;

        /** 缓存引擎*/
        private Engine engine = Engine.memory;

        /** 缓存过期时间（分钟）*/
        private int expire = 60;

        /** 缓存过期时刷新*/
        private boolean refreshWhenExpire = true;

        public enum Engine {
            redis,
            memory
        }
    }
}
