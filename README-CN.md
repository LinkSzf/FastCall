# FastCall

[![Maven Central](https://img.shields.io/maven-central/v/io.github.linkszf/fastcall-spring-boot-starter)](https://central.sonatype.com/artifact/io.github.linkszf/fastcall-spring-boot-starter)
[![License](https://img.shields.io/github/license/LinkSzf/FastCall)](LICENSE)
[![JDK](https://img.shields.io/badge/JDK-1.8%2B-blue)](#三环境依赖)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-2.7.x-6DB33F)](#三环境依赖)
[![Gitee](https://img.shields.io/badge/Gitee-szf__newbee-C71D23?logo=gitee&logoColor=white)](https://gitee.com/szf_newbee/fastcall)

**中文 | [English](README.md)**

> 面向 Spring Boot 的第三方 HTTP 接口调用工具包：认证自动注入、凭证失效自动重建、Feign 风格声明式客户端。

## 30 秒示例

引入 starter：

```xml
<dependency>
    <groupId>io.github.linkszf</groupId>
    <artifactId>fastcall-spring-boot-starter</artifactId>
    <version>1.0.0</version>
</dependency>
```

把第三方系统描述一次——写在 `application.yml` (或数据库里)：

```yaml
fast-call:
  easy-source:
    - system:
        code: basic-system
        name: Basic 认证系统
        host: https://api.example.com
        auth-type: BASIC
      auth:
        content: |
          {
            "username": "admin",
            "password": "123456"
          }
```

然后直接调用，认证、凭证续期、重试与限流都已经由框架处理：

```java
@Autowired
private FastCall fastCall;

// 1）按 api 名调用已配置好的接口
FastCallResponse<Object> response = fastCall.getClient("basic-system")
        .newApiCall("queryUser")
        .callIt();

// 2) 或者自由定义请求
FastCallResponse<User> userResponse = fastCall.getClient("basic-system")
        .newCall(User.class)
        .uri("/api/users")
        .method(FcRequestMethod.POST)
        .header("X-Tag", "demo")
        .body(payload, FcMediaType.APPLICATION_JSON)
        .prepared()
        .callIt();
```

```java
// 3）或者只声明一次接口（Feign 风格），像本地 Bean 一样注入使用
@SpringBootApplication
@EnableFastCallClients(basePackageClasses = Application.class)
public class Application { /* ... */ }

@FcClient(system = "basic-system")
public interface UserClient {
    @FcMethod(uri = "/api/users/{id}", method = FcRequestMethod.GET)
    User query(@FcPath("id") String id);
}
```

完整快速使用见 [第四章](#四快速使用)，六种认证方案的配置见第五章。

> **要把配置放进数据库（而不是 `easy-source`）？** 用
> [`sql/fastcall-schema-mysql.sql`](sql/fastcall-schema-mysql.sql) 建表，再通过 `fastcall-api` 模块自带的
> 简单运维接口维护数据 —— 见 [4.5](#45-数据库建表脚本与运维接口)。

## 目录

- [一、介绍](#一介绍)
- [二、功能特性](#二功能特性)
- [三、环境依赖](#三环境依赖)
- [四、快速使用](#四快速使用)
- [五、认证方式配置详解](#五认证方式配置详解)
- [六、自定义扩展](#六自定义扩展)

## 一、介绍

调用第三方系统的 HTTP 接口时，每个系统都要重复处理同一批事情：认证方式各不相同且凭证会过期、接口地址与公共参数散落各处、网络抖动要重试、并发要限制、调用要留痕。

FastCall 把这些横切逻辑收敛到框架内部，**把一个外部系统抽象成一份配置**，业务侧只关心调用本身：

```java
FastCallResponse<Object> response = fastCall.getClient("order-system")
        .newApiCall("queryOrder")
        .callIt();
```

提供两种使用姿势：

1. **配置驱动**：在数据库或 `application.yml` 中登记系统与 API，业务侧按 api 名称调用；
2. **声明式客户端**：写一个带注解的 Java 接口（Feign 风格），注入后像调用本地方法一样发起 HTTP 请求。

引入 `fastcall-spring-boot-starter` 即自动装配，无需额外注解。


## 二、功能特性

**认证**

- 六种认证方式开箱即用：`NONE`、`BASIC`、`APIKEY`、`BEARER`（Token）、`COOKIE`、`JWT`；
- 凭证自动获取与自动注入，业务代码无需感知认证过程；
- 凭证失效（默认 HTTP 401，可自定义）后自动重建凭证并**重发当前请求**（对 `BEARER`、`COOKIE`、`JWT` 生效）；
- `JWT` 支持本地签发：HS\* 对称算法与 RSA / EC / EdDSA 等非对称算法，密钥支持 PEM / Base64 / Hex，也支持 `alg=none`。

**配置**

- 两种配置来源：数据库（JPA 或 MyBatis-Plus，按依赖环境自适应）与 `application.yml`（`easy-source` 内联）；
- 配置读取链：缓存 → yml → 数据库，逐级回退，缓存引擎支持 `memory` 与 `redis`；
- 预置 API：把路径、请求方法、公共参数、请求头配置成一个 API，调用时只需 api 名称。

**调用**

- 链式 API：`newCall()` / `newApiCall()` / `host` / `uri` / `method` / `header` / `params` / `body` / `prepared()`；
- 声明式客户端：`@FcClient`、`@FcMethod`、`@FcPath`、`@FcQuery`、`@FcHeader`、`@FcBody`、`@FcPart`、`@FcAppointedSystem`；
- 支持 Map / Bean 展开为多个参数、List 重复参数、multipart 上传（`File` / `byte[]` / `InputStream` / `MultipartFile`）、响应下载为 `MultipartFile`、泛型与集合返回值、匿名调用、运行时切换系统。

**治理与扩展**

- 过滤器链：系统级限流（按时间窗）、客户端级重试（次数 / 间隔）、请求事件发布；
- 统一响应对象 `FastCallResponse`：状态码、消息、反序列化后的数据、响应头、是否成功 / 是否命中缓存 / 异常 / 请求与响应耗时；
- HTTP 层：OkHttp 连接池、全局与系统级超时、并发上限、磁盘响应缓存（默认关闭）；
- 线程池与异步支持；
- 关键组件均为可替换 Bean（认证处理器、凭证提供者、配置来源、过滤器、事件监听器、JSON 编解码），通过 `@ConditionalOnMissingBean` 覆盖。

### 横向对比

| 能力 | FastCall | OpenFeign | RestTemplate / WebClient | 裸 OkHttp |
| --- | --- | --- | --- | --- |
| 声明式接口调用 | ✅ | ✅ | ❌ | ❌ |
| 内置认证（6 种方案） | ✅ | ⚠️ 需自己写 `RequestInterceptor` | ❌ | ❌ |
| 凭证失效自动重建 + 请求重放 | ✅ | ❌ 需自行实现 | ❌ | ❌ |
| 接口即配置（数据库/yml 注册，按名调用） | ✅ | ❌ | ❌ | ❌ |
| 本地 JWT 签名（HS\*、RSA、EC、EdDSA） | ✅ | ❌ | ❌ | ❌ |
| 限流 / 重试 / 请求事件 | ✅ | ⚠️ 仅重试（`Retryer`） | ❌ | ❌ |
| 额外依赖体积 | 一个 starter（基于 OkHttp） | 需引入 Spring Cloud 体系 | Spring Web | 仅 OkHttp |

`❌` 表示该库本身不提供此能力（并非无法在其之上自行实现）。
此处只对比与本项目重叠的关注点，各库的完整能力请以其官方文档为准。

## 三、环境依赖

| 依赖 | 版本 | 说明 |
| --- | --- | --- |
| JDK | 1.8+ |  |
| Spring Boot | 2.7.x | 自动装配基于 `META-INF/spring/...AutoConfiguration.imports`，Boot 2.6 及以下不会生效 |
| OkHttp | 4.11.0 | HTTP 引擎，由 starter 传递引入，无需手动声明 |
| Hutool | 5.8.40 | JSON / JWT / 加解密等工具，由 starter 传递引入 |
| Jackson | 随 Spring Boot | 默认 JSON 编解码实现，可通过自定义 `FcJsonCodec` 替换 |
| 持久化框架（可选） | JPA(Hibernate) 或 MyBatis-Plus | `fastcall-data` 中声明为 `provided`：使用数据库配置源时，由业务工程引入 `spring-boot-starter-data-jpa` 或 `mybatis-plus-boot-starter` |
| Redis（可选） | Spring Data Redis | 仅当 `fast-call.source-cache.engine=redis` 时需要；默认 `memory` 无需 Redis |

## 四、快速使用

### 4.1 引入依赖

Maven：

```xml
<dependency>
    <groupId>io.github.linkszf</groupId>
    <artifactId>fastcall-spring-boot-starter</artifactId>
    <version>{latest-version}</version>
</dependency>
```


### 4.2 配置 application.yml

以下为最小可用配置（除 `easy-source` 外均有默认值）：

```yaml
fast-call:
  max-requests: 200            # 全局并发请求上限，同时决定共享线程池大小
  max-requests-per-host: 30    # 单 host 并发请求上限
  client:
    connect-timeout: 10        # 连接超时（秒），系统未单独配置时生效
    read-timeout: 30           # 读取超时（秒）
    write-timeout: 30          # 写入超时（秒）
  pool:
    max-idle-connections: 50   # 连接池最大空闲连接数
    keep-alive-minutes: 5      # 空闲连接保活时长（分钟）
  filter:
    enable-retry: true         # 网络异常/连接失败时按系统重试配置重发
    enable-rate-limit: false   # 系统级限流
    enable-request-event: false # 发布请求事件
  cache:
    enable: false              # OkHttp 磁盘响应缓存（默认关闭）
    max-size: 10485760         # 缓存目录最大字节数
  source-cache:
    enable: true               # 缓存系统配置
    engine: memory             # 缓存引擎：memory | redis
    expire: 60                 # 缓存过期时间（秒），<= 0 表示永不过期
  easy-source:                 # 内联系统配置（无需数据库）
    - system:
        code: demo-system      # 系统编码，调用时使用
        name: 演示系统
        enable: true
        host: https://api.example.com
        auth-type: NONE        # 认证方式，见第五章
      retry:
        attempts: 3            # 最大重试次数，< 1 表示不重试
        duration: 1000         # 重试间隔（毫秒）
      rate-limits:
        - maximum: 100         # 每个时间窗允许的请求数
          span: MINUTES        # 时间窗：SECONDS 至 YEARS
```

### 4.3 链式调用

注入 `FastCall` 后即可按系统编码获取客户端：

```java
@Autowired
private FastCall fastCall;

// 1) 调用配置好的 API，只需 api 名称
FastCallResponse<Object> response = fastCall.getClient("order-system")
        .newApiCall("queryOrder")
        .callIt();

// 2) 手工构造请求，泛型决定响应反序列化类型，并获取完整响应信息
FastCallResponse<Map<String, Object>> detail = fastCall.getClient("order-system")
        .newCall(Map.class)
        .uri("/api/orders/1001")
        .method(FcRequestMethod.GET)
        .header("X-Tag", "demo")
        .params(Collections.singletonMap("verbose", "true"))
        .prepared()
        .callIt();

// 3) 只要业务数据（失败时抛出异常）
Map<String, Object> data = fastCall.getClient("order-system")
        .newCall(Map.class)
        .uri("/api/orders/1001")
        .method(FcRequestMethod.GET)
        .prepared()
        .call();

// 4) 提交表单或 JSON 请求体
fastCall.getClient("order-system")
        .newCall(String.class)
        .uri("/api/orders")
        .method(FcRequestMethod.POST)
        .body(order, FcMediaType.APPLICATION_JSON)
        .prepared()
        .callIt();

// 5) 匿名调用（不携带认证）
String pong = fastCall.getClient("order-system")
        .newCall(String.class)
        .uri("/public/ping")
        .prepared()
        .anonymousCall();
```

| 方法 | 说明 |
| --- | --- |
| `getClient(system)` | 获取（首次调用时创建并缓存）系统客户端 |
| `newApiCall(apiName)` | 按预置 API 名称调用，自动带上其路径、方法、公共参数 |
| `newCall()` / `newCall(Class)` / `newCall(Type)` | 手工构造请求，参数决定响应反序列化类型 |
| `prepared().callIt()` | 返回 `FastCallResponse<T>`，不抛业务异常 |
| `prepared().call()` | 直接返回反序列化后的数据，失败时抛异常 |
| `prepared().anonymousCallIt()` / `anonymousCall()` | 匿名调用，跳过认证 |

### 4.4 声明式客户端

**第一步**：在启动类开启客户端扫描。

```java
@SpringBootApplication
@EnableFastCallClients(basePackages = "com.demo.client")
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

**第二步**：定义客户端接口（`system` 指向已配置的系统编码）。

```java
@FcClient(system = "declarative-system")
public interface DeclarativeDemoClient {

    // 路径占位符 + 查询参数 + 请求头
    @FcMethod(uri = "/declarative/target/echo/{id}", method = FcRequestMethod.GET)
    String echo(@FcPath("id") String id,
                @FcQuery("q") String q,
                @FcHeader("X-Req-Id") String reqId);

    // JSON 请求体
    @FcMethod(uri = "/declarative/target/payload", method = FcRequestMethod.POST)
    FastCallResponse<Map<String, Object>> postPayload(@FcBody Map<String, Object> payload);

    // 表单提交
    @FcMethod(uri = "/declarative/target/form", method = FcRequestMethod.POST)
    Map<String, Object> formSubmit(
            @FcBody(mediaType = FcMediaType.APPLICATION_FORM_URLENCODED) Map<String, Object> form);

    // Map / 集合展开为多个查询参数或请求头
    @FcMethod(uri = "/declarative/target/query-map", method = FcRequestMethod.GET)
    Map<String, Object> queryMap(@FcQuery Map<String, Object> query,
                                 @FcHeader Map<String, Object> headerMap,
                                 @FcHeader("X-Tag") List<String> tags);

    // 文件上传（声明 @FcPart 后整个请求即为 multipart）
    @FcMethod(uri = "/declarative/target/upload", method = FcRequestMethod.POST)
    Map<String, Object> upload(@FcPart("desc") String desc,
                               @FcPart("files") MultipartFile file,
                               @FcPart("meta") String meta);

    // 匿名调用
    @FcMethod(uri = "/declarative/target/anonymous", method = FcRequestMethod.GET, anonymous = true)
    String anonymous(@FcQuery("name") String name);

    // 运行时指定系统
    @FcMethod(uri = "/declarative/target/echo/{id}", method = FcRequestMethod.GET)
    String echoOf(@FcPath("id") String id, @FcAppointedSystem String system);
}
```

**第三步**：注入接口直接调用。

```java
@Autowired
private DeclarativeDemoClient demoClient;

String result = demoClient.echo("1001", "hello", "req-1");
```

> 接口约束（不满足会在启动或调用时直接报错）：
>
> - 接口必须标注 `@FcClient` 且 `system` 非空，并通过 `@EnableFastCallClients` 开启扫描；接口中每个非 default、非 static 方法都必须有 `@FcMethod`；
> - `@FcMethod` 的 `api` 与 `uri` 必须且只能二选一；`uri` 中的每个 `{占位符}` 都要有对应的 `@FcPath`，且一对一；
> - 每个方法参数必须且只能标注一个参数注解；`@FcBody` 至多一个，且不能与 `@FcPart` 同时使用（`mediaType` 为 form-urlencoded 时实参必须是 `Map`）；
> - `@FcPart` 的 value 必填；`@FcQuery` / `@FcHeader` 用于标量参数时必须显式写名称，用于 `Map` 或自定义 Bean 时可省略并自动展开；
> - `@FcPath` 省略 value 时使用参数名，此时需要编译期保留参数名（编译器 `-parameters` 选项）。

### 4.5 数据库建表脚本与运维接口

当配置放在数据库而不是 `easy-source` 里时，先把表建好 ——
[`sql/fastcall-schema-mysql.sql`](sql/fastcall-schema-mysql.sql) 就是 MySQL 的建表语句：

```bash
mysql -h127.0.0.1 -P3306 -uroot -p your_database < sql/fastcall-schema-mysql.sql
```

脚本创建六张表：`fastcall_system`（系统配置）、`fastcall_api`（系统预置接口配置）、
`fastcall_api_param`（接口参数）、`fastcall_auth`（凭证信息与续期方式）、
`fastcall_rate_limit`（限流窗口）、`fastcall_retry`（重试策略）。

这些数据不必手写 SQL 维护：`fastcall-api` 模块自带一套简单的运维接口（`FcController`），基路径为 `/fc/system`。

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| GET | `/fc/system/all` | 查询全部系统 |
| GET | `/fc/system/{systemId}` | 查询单个系统（含凭证） |
| POST | `/fc/system/save` | 新增或修改系统（含凭证） |
| DELETE | `/fc/system/{systemId}` | 删除系统 |
| GET | `/fc/system/{systemId}/api/all` | 查询系统下的接口列表 |
| POST | `/fc/system/{systemId}/api/save` | 新增或修改系统下的接口 |
| GET | `/fc/system/api/{apiId}/param/all` | 查询接口的参数列表 |
| POST | `/fc/system/api/{apiId}/param/save` | 新增或修改接口的参数 |
| GET | `/fc/system/{systemId}/retry` | 查询系统的重试配置 |
| POST | `/fc/system/{systemId}/retry/save` | 新增或修改系统的重试配置 |
| DELETE | `/fc/system/{systemId}/retry` | 删除系统的重试配置 |

这些接口直接读写配置且自身不带认证，请只在内网暴露。限流数据目前没有对应的运维接口，
`fastcall_rate_limit` 需要自行用 SQL 或代码维护。

## 五、认证方式配置详解

### 5.1 认证类型总览

| `auth-type` | 说明 | 凭证来源 | 注入方式 | 失效自动重建 |
| --- | --- | --- | --- | --- |
| `NONE` | 无认证 | — | 不注入任何凭证 | — |
| `BASIC` | HTTP Basic | 配置中的用户名 / 密码 | `Authorization: Basic base64(username:password)` | 否 |
| `APIKEY` | 以 key-value 传参 | 配置中的 key / value | 按其 `positionOn` 追加到请求头或查询参数 | 否 |
| `BEARER` | 令牌认证（OAuth2 等） | 调用认证接口后从响应体解析 | `Authorization: Bearer <token>` | 是 |
| `COOKIE` | Cookie 认证 | 调用认证接口后取响应 `Set-Cookie` | `Cookie: <cookie>` | 是 |
| `JWT` | 本地签发 JWT | 使用配置的算法与密钥本地签发 | 按其 `positionOn` 放入请求头或查询参数 | 是（按过期时间重新签发） |

> `BEARER` 与 `COOKIE` 属于**交互式认证**：第一次业务调用前，框架会先向配置的认证接口发起一次匿名请求，用响应结果构建凭证。

### 5.2 auth 公共配置项

`easy-source[].auth`（数据库方式对应 `fastcall_auth` 表）：

| 配置项 | 说明 |
| --- | --- |
| `path` | 认证接口路径，相对认证 host；交互式认证（`BEARER`、`COOKIE`）必填 |
| `content` | 凭证内容，**JSON 字符串** |
| `particularHost` | 认证请求使用的 host，默认使用系统 `host` |
| `unauthorizedCode` | 判定凭证失效的响应状态码，默认 `401` |

### 5.3 NONE：无认证

```yaml
    - system:
        code: no-auth-system
        name: 无认证系统
        host: https://api.example.com
        auth-type: NONE
```

### 5.4 BASIC：HTTP Basic

```yaml
    - system:
        code: basic-system
        name: Basic 认证系统
        host: https://api.example.com
        auth-type: BASIC
      auth:
        content: |
          {
            "username": "admin",
            "password": "123456"
          }
```

实际发送：`Authorization: Basic YWRtaW46MTIzNDU2`（用户名密码经 Base64 编码）。

### 5.5 APIKEY：请求头 / 查询参数传 key

`content` 字段：

| 字段 | 必填 | 说明 |
| --- | --- | --- |
| `key` | 是 | 参数名 |
| `value` | 是 | 参数值 |
| `positionOn` | 否 | `HEADER`（默认）或 `QUERY` |

```yaml
    - system:
        code: apikey-system
        name: ApiKey 认证系统
        host: https://api.example.com
        auth-type: APIKEY
      auth:
        content: |
          {
            "key": "X-Api-Key",
            "value": "9f8c1c0d7a2b4e6f",
            "positionOn": "HEADER"
          }
```

`positionOn` 为 `QUERY` 时，参数会以 `?X-Api-Key=...` 形式追加到 URL。

### 5.6 BEARER：交互式获取令牌

`content` 字段：

| 字段 | 必填 | 说明 |
| --- | --- | --- |
| `tokenField` | 是 | 令牌在响应 JSON 中的路径，支持 `a.b.c` 形式 |
| `issuanceField` | 否 | 签发时间字段路径，支持时间戳（毫秒）或日期字符串，默认取当前时间 |
| `expiredInField` | 否 | 有效期字段路径（秒），默认 7 天 |
| `prop` | 否 | 认证请求的附加参数：`headers`、`params`、`body` |

```yaml
    - system:
        code: token-system
        name: 令牌认证系统
        host: https://api.example.com
        auth-type: BEARER
      auth:
        path: /oauth/token                          # 认证接口
        particularHost: https://sso.example.com     # 认证接口的 host（可选）
        content: |
          {
            "tokenField": "data.access_token",
            "issuanceField": "data.issueAt",
            "expiredInField": "data.expiresIn",
            "prop": {
              "headers": { "X-App-Id": "demo" },
              "params": { "grant_type": "client_credentials" },
              "body": { "clientId": "demo", "clientSecret": "secret" }
            }
          }
```

实际发送：`Authorization: Bearer <token>`。

### 5.7 COOKIE：交互式获取 Cookie

`content` 通常只需给出认证请求参数 `prop`：

```yaml
    - system:
        code: cookie-system
        name: Cookie 认证系统
        host: https://api.example.com
        auth-type: COOKIE
      auth:
        path: /login
        content: |
          {
            "prop": {
              "body": { "username": "admin", "password": "123456" }
            }
          }
```

框架会把认证响应中的全部 `Set-Cookie` 头拼接后，作为业务请求的 `Cookie` 头发送。

### 5.8 JWT：本地签发

`content` 字段：

| 字段 | 必填 | 说明 |
| --- | --- | --- |
| `positionOn` | 否 | `HEADER`（默认）或 `QUERY` |
| `algorithm` | 否 | 签名算法，如 `HS256`、`RS256`、`Ed25519`；省略或为 `none` 时不签名 |
| `secret` | 否 | 密钥：对称算法为字符串；非对称算法支持 PEM / Base64 / Hex 编码的私钥 |
| `issueAtOffset` | 否 | 签发时间偏移（秒），`iat = 当前时间 + 该值`，可为负数 |
| `expiresIn` | 否 | 有效期（秒），`exp = 当前时间 + 该值`，默认 86400 |
| `notValidBefore` | 否 | 生效时间偏移（秒），用于生成 `nbf` |
| `payload` | 否 | 自定义载荷；显式给出的 `iat` / `exp` / `nbf` 不会被覆盖 |
| `header` | 否 | JWT 头部，如 `{"alg": "EdDSA", "kid": "xxx"}` |

```yaml
    - system:
        code: jwt-system
        name: JWT 认证系统
        host: https://api.example.com
        auth-type: JWT
      auth:
        content: |
          {
            "positionOn": "HEADER",
            "algorithm": "Ed25519",
            "secret": "MC4CAQAwBQYDK2VwBCIEINxyQ0QWXSdqCh20hqMIsrzGEDBBF7t+I8J7/aKGVf+V",
            "issueAtOffset": -30,
            "expiresIn": 10,
            "header": { "alg": "EdDSA", "kid": "T5B8UFRR36" },
            "payload": { "sub": "248C7MFGFY" }
          }
```

`positionOn` 为 `HEADER` 时发送 `Authorization: Bearer <jwt>`；为 `QUERY` 时以查询参数形式追加。

### 5.9 凭证失效与自动重建流程

1. 业务请求发出前，认证拦截器按 `auth-type` 获取凭证并注入请求；
2. 凭证缺失或已被标记失效时，由凭证提供者重建（`BEARER`、`COOKIE` 会调用认证接口，`JWT` 会重新签发；同一系统内的重建通过锁串行执行，避免并发重复认证）；
3. 业务响应状态码等于 `unauthorizedCode`（默认 `401`）时，框架标记凭证失效，并**自动重发当前请求**（重发前会先完成凭证重建），业务代码无需处理；
4. 网络异常、连接失败等场景的重发由重试过滤器负责，需同时开启 `fast-call.filter.enable-retry: true` 并配置该系统的 `retry.attempts > 0`；
5. 凭证保存在系统配置（pak）中，建议保持 `fast-call.source-cache.enable: true`（默认值）。若关闭配置缓存，交互式认证（`BEARER`、`COOKIE`）可能因凭证无法在多次调用间保留而反复登录。

### 5.10 数据库配置方式

除 `easy-source` 外，系统、认证、API、参数、限流、重试均可维护在数据库中，由 `fastcall-data` 提供的持久化实现读取（JPA 与 MyBatis-Plus 二选一，按业务工程实际引入的框架自适应）：

| 表 | 内容 |
| --- | --- |
| `fastcall_system` | 系统：编码、名称、host、认证方式、超时 |
| `fastcall_auth` | 认证：认证路径、凭证内容（JSON，与 `content` 同构）、失效状态码 |
| `fastcall_api` | API：所属系统、名称、路径、请求方法 |
| `fastcall_api_param` | API 参数：请求头、查询参数、请求体默认值 |
| `fastcall_rate_limit` | 限流规则：时间窗与上限 |
| `fastcall_retry` | 重试策略：次数与间隔 |

数据库中的认证内容与 `content` 使用同一套 JSON 结构，配置方式与第五章各示例一致。

## 六、自定义扩展

FastCall 的组件装配遵循「默认实现 + 可覆盖」：框架自带的实现大多带 `@ConditionalOnMissingBean` 或按 Bean 名称判断的注册条件，业务侧只要声明自己的 Bean 就能接管；过滤器与配置来源链则按集合收集，直接新增 Bean 即可生效。以下逐项说明。

### 6.1 扩展点速查

| 扩展点 | 接口 | 框架默认实现 | 注册条件 | 典型场景 |
| --- | --- | --- | --- | --- |
| JSON 编解码 | `FcJsonCodec` | `FcJacksonJsonCodec` | `@ConditionalOnMissingBean(FcJsonCodec.class)` | 换成 FastJSON / Gson，定制序列化规则 |
| 认证处理器 | `IFcAuthHandler` | 六种认证方式各一个 Handler | 各自 `@ConditionalOnMissingBean(实现类)` | 改变某认证方式的注入形态（自定义头名、附加签名） |
| 凭证提供者 | `IFcCredentialProvider` / `IFcDynCredentialProvider` | 五个 Provider（`NONE` 无需凭证） | 各自 `@ConditionalOnMissingBean(实现类)` | 自定义令牌获取与刷新策略 |
| 请求过滤器 | `FcFilter` | 限流、请求事件、重试 | 收集容器内全部 `FcFilter` Bean，按 `@Order` 排序 | 调用日志、链路追踪、签名、灰度 |
| 配置数据提供者 | `IFcPakProvider` | `FcDefaultPakProvider`（读数据库） | `@ConditionalOnMissingBean` | 改为从 Nacos / Apollo / 配置中心读取 |
| 配置来源链节点 | `IFcChainSource` | 缓存源、yml 源、数据库源 | 收集全部 `IFcChainSource` Bean，按 `@Order` 串链 | 新增一级配置来源（本地文件、远程 HTTP） |
| 缓存来源 | `IFcCacheSource` | `FcInMemoryCacheSource` / `FcRedisCacheSource` | `@ConditionalOnMissingBean(IFcCacheSource.class)` | 换成 Caffeine 或公司统一缓存 |
| 数据库来源 | `IFcDatabaseSource` | `FcDatabaseSource` | `@ConditionalOnMissingBean(IFcDatabaseSource.class)` | 自定义读取、转换与加解密 |
| 事件发布器 | `IFcRequestEventPublisher` | `FcRequestEventPublisher`（异步派发） | `@ConditionalOnMissingBean` + 需开启请求事件 | 事件投递到 MQ / 埋点平台 |
| 源事件监听器 | `IFcSourceEventListener` | `FcSourceEventListener` | `@ConditionalOnMissingBean` | 配置变更后执行自定义动作 |
| 请求事件监听器 | `IFcAuthRequestEventListener`、`IFcApiRequestEventListener` | 回写 auth / api 的最近访问时间 | `@ConditionalOnMissingBean` + 需开启请求事件 | 调用审计、统计、告警 |
| 事件线程池 | 名为 `FastCallAsyncExecutor` 的 `Executor` | `ThreadPoolTaskExecutor`（5/10，队列 10000） | `@ConditionalOnMissingBean(name = ...)` | 调整事件线程池参数 |


---

更多可运行示例见 `fastcall-test` 模块（各认证方式的完整配置位于其 `application.yml`）。
