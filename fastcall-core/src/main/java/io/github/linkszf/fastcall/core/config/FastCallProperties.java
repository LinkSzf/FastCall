package io.github.linkszf.fastcall.core.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import io.github.linkszf.fastcall.common.FcAuthType;
import io.github.linkszf.fastcall.common.FcTimeSpan;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Root binding of the {@code fast-call} namespace, holding client, pool, cache and inline system settings.
 */
@ConfigurationProperties(prefix = "fast-call")
@Data
public class FastCallProperties {

    /** Maximum number of concurrent requests allowed in total; also sizes the shared client thread pool. */
    private int maxRequests = 200;

    /** Maximum number of concurrent requests allowed towards a single host. */
    private int maxRequestsPerHost = 30;

    /** Global client settings, applied when a system declares no timeout of its own. */
    private Client client = new Client();

    /** Connection pool settings shared by every generated client. */
    private Pool pool = new Pool();

    /** Switches enabling the optional request filters. */
    private Filter filter = new Filter();

    /** Disk based HTTP response cache of the shared client; disabled by default. */
    private Cache cache = new Cache();

    /** Cache settings for the system paks resolved by the source chain. */
    private SourceCache sourceCache = new SourceCache();

    /** Systems declared inline, so that no database configuration is needed. */
    private List<EasySource> easySource = new ArrayList<>();

    /**
     * Switches enabling the optional request filters, bound from {@code fast-call.filter.*}.
     */
    @Data
    public static class Filter {

        /** Whether the retry filter is registered; disabled by default. */
        private boolean enableRetry = false;

        /** Whether the rate limit filter is registered; disabled by default. */
        private boolean enableRateLimit = false;

        /** Whether request events are published to the default database listeners; disabled by default. */
        private boolean enableRequestEvent = false;

    }

    /**
     * One system declared inline under {@code fast-call.easy-source}, used instead of database configuration.
     */
    @Data
    public static class EasySource {
        /** System settings of this inline definition. */
        private System system = new System();

        /** Authentication settings, applied when the system auth type is not {@code NONE}. */
        private Auth auth = new Auth();

        /** Retry policy of this system; {@code null} leaves the system without retry. */
        private Retry retry;

        /** Rate limit rules of this system, each one counted over its own time window. */
        private List<RateLimit> rateLimits = new ArrayList<>();

        /**
         * System settings of one inline definition, describing how the system is reached and authenticated.
         */
        @Data
        public static class System {
            /** Display name of the system. */
            private String name;

            /** Unique system code used to obtain the client; required and unique across all systems. */
            private String code;

            /** Whether the system is callable, where a disabled system rejects client creation; defaults to true. */
            private boolean enable = true;

            /** Base access address of the system, used as the host of every call without a host of its own. */
            private String host;

            /** Authentication type of the system, where {@code NONE} applies no credential; defaults to {@code NONE}. */
            private FcAuthType authType = FcAuthType.NONE;

            /** Connect timeout in seconds; {@code null} falls back to the global client setting. */
            private Integer connectTimeout;

            /** Read timeout in seconds; {@code null} falls back to the global client setting. */
            private Integer readTimeout;

            /** Write timeout in seconds; {@code null} falls back to the global client setting. */
            private Integer writeTimeout;
        }

        /**
         * Authentication settings of one inline system, whose content is JSON bound to the auth type.
         */
        @Data
        public static class Auth {

            /** Request path of the authentication endpoint, relative to the host used for authentication. */
            private String path;

            /** Credential content as JSON, bound to the content class of the system auth type. */
            private String content;

            /** Host used for authentication requests instead of the system host; {@code null} keeps the system host. */
            private String particularHost;

            /** Response status code marking the credential as invalid; {@code null} means the default 401. */
            private Integer unauthorizedCode;
        }


        /**
         * Retry policy of one inline system, applied after a call that failed or returned disconnected.
         */
        @Data
        public static class Retry {

            /** Maximum retry attempts after the first call, where a value less than 1 disables retry; defaults to 3. */
            private int attempts = 3;

            /** Interval in milliseconds waited before each retry, where a value less than 1 retries at once; defaults to 1000. */
            private long duration = 1000;
        }

        /**
         * Rate limit rule of one inline system, counted per time window on the system level.
         */
        @Data
        public static class RateLimit {

            /** Requests allowed within one {@code span} window; rules with a value less than 1 are ignored. */
            private Long maximum;

            /** Time window the maximum applies to, from {@code SECONDS} up to {@code YEARS}. */
            private FcTimeSpan span;
        }
    }

    /**
     * Global timeouts of the generated HTTP clients, expressed in seconds and overridden per system or api.
     */
    @Data
    public static class Client {
        /** Connect timeout in seconds; defaults to 10. */
        private int connectTimeout = 10;

        /** Read timeout in seconds; defaults to 30. */
        private int readTimeout = 30;

        /** Write timeout in seconds; defaults to 30. */
        private int writeTimeout = 30;
    }

    /**
     * Connection pool settings shared by every generated HTTP client.
     */
    @Data
    public static class Pool {
        /** Maximum number of idle connections kept in the pool; defaults to 50. */
        private int maxIdleConnections = 50;

        /** Minutes an idle connection is kept before it is reclaimed; defaults to 5. */
        private int keepAliveMinutes = 5;
    }

    /**
     * Disk based HTTP response cache of the shared client, backed by an OkHttp cache directory.
     */
    @Data
    public static class Cache {
        /** Whether the shared HTTP response cache is enabled; disabled by default. */
        private boolean enable = false;

        /** Maximum size of the cache directory in bytes; defaults to 10485760. */
        private long maxSize = 10 * 1024 * 1024;

        /** Directory holding the cache files; defaults to a {@code fast-call-cache} folder in the temp directory. */
        private String path = Paths.get(
                                        System.getProperty("java.io.tmpdir"),
                                        "fast-call-cache"
                                ).toString();
    }

    /**
     * Cache settings of the source chain, controlling how long a resolved system pak is cached.
     */
    @Data
    public static class SourceCache {
        /** Whether the source chain caches system paks; enabled by default. */
        private boolean enable = true;

        /** Cache engine used when caching is enabled; defaults to {@code memory}. */
        private Engine engine = Engine.memory;

        /** Cache time to live in seconds, where a value less than 1 means the pak never expires; defaults to 60. */
        private long expire = 60;

        public boolean isIndefinite() {
            return this.expire <= 0;
        }

        /**
         * Cache engine backing the source cache, either Redis or an in-memory timed cache.
         */
        public enum Engine {
            /** Redis backed cache, used when a {@code RedisTemplate} bean is available. */
            redis,
            /** In-memory timed cache holding the paks inside the current JVM. */
            memory
        }
    }
}
