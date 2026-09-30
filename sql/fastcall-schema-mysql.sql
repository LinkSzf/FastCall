-- =============================================================================
--  FastCall schema for MySQL
--
--  Creates the six tables used when the configuration of systems, apis and
--  credentials is kept in a database.
--
--  Usage
--    mysql -h127.0.0.1 -P3306 -uroot -p your_database < fastcall-schema-mysql.sql
--
--  Notes
--    * `id` is a snowflake id assigned by the application, so the columns have
--      no AUTO_INCREMENT: never let the database generate the primary key.
--    * `enable` and `json_obj` are `bit(1)` because the entities map them to
--      Boolean. Do not change them to `tinyint(1)`, otherwise the Hibernate
--      schema check (`spring.jpa.hibernate.ddl-auto=validate`) fails.
--    * `fastcall_auth.content` is a native JSON column holding the credential
--      described by the auth type of the system.
--    * Table order matters because of the foreign keys: `fastcall_system` must
--      be created before the tables that reference it.
-- =============================================================================

-- Uncomment to rebuild an existing schema (children first):
-- DROP TABLE IF EXISTS `fastcall_api_param`;
-- DROP TABLE IF EXISTS `fastcall_rate_limit`;
-- DROP TABLE IF EXISTS `fastcall_retry`;
-- DROP TABLE IF EXISTS `fastcall_auth`;
-- DROP TABLE IF EXISTS `fastcall_api`;
-- DROP TABLE IF EXISTS `fastcall_system`;

CREATE TABLE `fastcall_system` (
  `id` bigint NOT NULL COMMENT 'Primary key, a snowflake id assigned by the application',
  `name` varchar(255) NOT NULL COMMENT 'Display name of the system',
  `code` varchar(100) NOT NULL COMMENT 'Unique system code used by callers to obtain the client',
  `enable` bit(1) NOT NULL COMMENT 'Whether the system is callable, where a disabled system rejects client creation',
  `host` varchar(500) NOT NULL COMMENT 'Base access address of the system, including the port',
  `auth_type` varchar(50) NOT NULL COMMENT 'Authentication type, one of NONE, BASIC, APIKEY, BEARER, COOKIE or JWT',
  `connect_timeout` int DEFAULT NULL COMMENT 'Connect timeout in seconds, where NULL keeps the global setting',
  `read_timeout` int DEFAULT NULL COMMENT 'Read timeout in seconds, where NULL keeps the global setting',
  `write_timeout` int DEFAULT NULL COMMENT 'Write timeout in seconds, where NULL keeps the global setting',
  `description` varchar(1000) DEFAULT NULL COMMENT 'Free-form description of the system',
  `update_at` datetime NOT NULL COMMENT 'Time the system record was last saved',
  PRIMARY KEY (`id`),
  UNIQUE KEY `code` (`code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='FastCall - third-party system configuration';

CREATE TABLE `fastcall_api` (
  `id` bigint NOT NULL COMMENT 'Primary key, a snowflake id assigned by the application',
  `sys_id` bigint NOT NULL COMMENT 'Id of the system that owns the api',
  `name` varchar(255) NOT NULL COMMENT 'Api name used by callers to select the api, unique across the system',
  `path` varchar(1000) NOT NULL COMMENT 'Request path, combined with the host to build the request url',
  `method` varchar(20) NOT NULL COMMENT 'HTTP method, stored as the name of an FcRequestMethod value such as GET',
  `connect_timeout` int DEFAULT NULL COMMENT 'Connect timeout in seconds, where NULL keeps the system setting',
  `read_timeout` int DEFAULT NULL COMMENT 'Read timeout in seconds, where NULL keeps the system setting',
  `write_timeout` int DEFAULT NULL COMMENT 'Write timeout in seconds, where NULL keeps the system setting',
  `last_access_time` datetime DEFAULT NULL COMMENT 'Time of the last successful call, updated by the request event listener',
  `particular_host` varchar(255) DEFAULT NULL COMMENT 'Host used for this api instead of the system host, where NULL keeps the system host',
  `description` varchar(255) DEFAULT NULL COMMENT 'Free-form description of the api',
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`) USING BTREE,
  KEY `fastcall_sys_api_ibfk_1` (`sys_id`),
  CONSTRAINT `fastcall_api_ibfk_1` FOREIGN KEY (`sys_id`) REFERENCES `fastcall_system` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='FastCall - callable apis of a system';

CREATE TABLE `fastcall_api_param` (
  `id` bigint NOT NULL COMMENT 'Primary key, a snowflake id assigned by the application',
  `api_id` bigint NOT NULL COMMENT 'Id of the api this parameter belongs to',
  `name` varchar(50) NOT NULL COMMENT 'Parameter name, used as the header or query name and unused for the BODY position',
  `position` varchar(20) NOT NULL COMMENT 'Where the parameter is applied, one of HEADER, QUERY or BODY',
  `json_obj` bit(1) DEFAULT NULL COMMENT 'Whether a BODY value is parsed into JSON instead of being sent as text',
  `default_value` varchar(2000) DEFAULT NULL COMMENT 'Default value of the parameter, or the whole body for the BODY position',
  PRIMARY KEY (`id`),
  KEY `api_id` (`api_id`),
  CONSTRAINT `fastcall_api_param_ibfk_1` FOREIGN KEY (`api_id`) REFERENCES `fastcall_api` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='FastCall - parameters of an api';

CREATE TABLE `fastcall_auth` (
  `id` bigint NOT NULL COMMENT 'Primary key, a snowflake id assigned by the application',
  `sys_id` bigint NOT NULL COMMENT 'Id of the system that owns this credential, at most one record per system',
  `type` varchar(50) NOT NULL COMMENT 'Authentication type, which selects the credential content class and the auth handler',
  `path` varchar(1000) DEFAULT NULL COMMENT 'Request path of the authentication endpoint, relative to the host used for authentication',
  `content` json NOT NULL COMMENT 'Credential content as JSON, bound to the content class of the auth type',
  `last_access_time` datetime DEFAULT NULL COMMENT 'Time of the last successful authentication, updated by the auth event listener',
  `particular_host` varchar(255) DEFAULT NULL COMMENT 'Host used for authentication requests instead of the system host, where NULL keeps the system host',
  `unauthorized_code` int DEFAULT NULL COMMENT 'Response code marking the credential as invalid, where NULL means the default 401',
  PRIMARY KEY (`id`),
  UNIQUE KEY `fastcall_sys_auth_ibfk_1` (`sys_id`) USING BTREE,
  CONSTRAINT `fastcall_auth_ibfk_1` FOREIGN KEY (`sys_id`) REFERENCES `fastcall_system` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='FastCall - credential of a system and how to renew it';

CREATE TABLE `fastcall_rate_limit` (
  `id` bigint NOT NULL COMMENT 'Primary key, a snowflake id assigned by the application',
  `sys_id` bigint NOT NULL COMMENT 'Id of the system the limit belongs to, several windows may exist per system',
  `enable` bit(1) NOT NULL COMMENT 'Whether the limit is used, the rate limit filter only loads enabled records',
  `span` varchar(20) NOT NULL COMMENT 'Time window the limit is counted over, from SECONDS up to YEARS',
  `last_time` datetime DEFAULT NULL COMMENT 'Start time of the current counting window, where NULL means no window is open yet',
  `maximum` bigint NOT NULL COMMENT 'Requests allowed within one span window, values below 1 are ignored',
  `current_count` bigint DEFAULT NULL COMMENT 'Requests already counted in the current window, reset when a new window starts',
  PRIMARY KEY (`id`),
  KEY `fk_fastcall_rate_limit_sys_id` (`sys_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='FastCall - request rate limit windows of a system';

CREATE TABLE `fastcall_retry` (
  `id` bigint NOT NULL COMMENT 'Primary key, a snowflake id assigned by the application',
  `sys_id` bigint NOT NULL COMMENT 'Id of the system the retry policy belongs to, at most one policy per system',
  `attempts` int NOT NULL COMMENT 'Maximum retries after the first call, a value below 1 disables retry',
  `duration` bigint NOT NULL COMMENT 'Interval in milliseconds waited before each retry, a value below 1 retries immediately',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_fastcall_retry_sys_id` (`sys_id`),
  CONSTRAINT `fastcall_retry_ibfk_1` FOREIGN KEY (`sys_id`) REFERENCES `fastcall_system` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='FastCall - retry policy of a system';
