# FastCall

[![Maven Central](https://img.shields.io/maven-central/v/io.github.linkszf/fastcall-spring-boot-starter)](https://central.sonatype.com/artifact/io.github.linkszf/fastcall-spring-boot-starter)
[![License](https://img.shields.io/github/license/LinkSzf/FastCall)](LICENSE)
[![JDK](https://img.shields.io/badge/JDK-1.8%2B-blue)](#3-requirements)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-2.7.x-6DB33F)](#3-requirements)
[![Gitee](https://img.shields.io/badge/Gitee-szf__newbee-C71D23?logo=gitee&logoColor=white)](https://gitee.com/szf_newbee/fastcall)

**[Chinese](README-CN.md) | English**

> A Spring Boot toolkit for calling third-party HTTP APIs: automatic authentication, transparent credential renewal, and Feign-style declarative clients.

## 30-Second Example

Add the starter:

```xml
<dependency>
    <groupId>io.github.linkszf</groupId>
    <artifactId>fastcall-spring-boot-starter</artifactId>
    <version>1.0.0</version>
</dependency>
```

Describe a third-party system once — in `application.yml` (or in your database):

```yaml
fast-call:
  easy-source:
    - system:
        code: basic-system
        name: Basic auth system
        host: https://api.example.com
        auth-type: BASIC
      auth:
        content: |
          {
            "username": "admin",
            "password": "123456"
          }
```

Then call it. Authentication, credential renewal, retry and rate limiting are already handled:

```java
@Autowired
private FastCall fastCall;

// 1) call a preconfigured API by its name
FastCallResponse<Object> response = fastCall.getClient("basic-system")
        .newApiCall("queryUser")
        .callIt();

// 2) or build the request yourself
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
// 3) or declare the interface once (Feign style) and inject it like a local bean
@SpringBootApplication
@EnableFastCallClients(basePackageClasses = Application.class)
public class Application { /* ... */ }

@FcClient(system = "basic-system")
public interface UserClient {
    @FcMethod(uri = "/api/users/{id}", method = FcRequestMethod.GET)
    User query(@FcPath("id") String id);
}
```

Full quick start: [chapter 4](#4-quick-start) · all six authentication schemes: [chapter 5](#5-authentication-configuration)

> **Keeping the configuration in a database instead of `easy-source`?** Create the tables with
> [`sql/fastcall-schema-mysql.sql`](sql/fastcall-schema-mysql.sql), and maintain the records through the
> small management API shipped by the `fastcall-api` module — see [4.5](#45-database-schema-and-management-api).

## Table of Contents

- [1. Introduction](#1-introduction)
- [2. Features](#2-features)
- [3. Requirements](#3-requirements)
- [4. Quick Start](#4-quick-start)
- [5. Authentication Configuration](#5-authentication-configuration)
- [6. Custom Extensions](#6-custom-extensions)

## 1. Introduction

Calling HTTP APIs of third-party systems means re-implementing the same concerns over and over: every system authenticates differently and credentials expire, hosts and shared parameters are scattered around, flaky connections have to be retried, concurrency has to be limited, and calls have to be traceable.

FastCall moves all of that into the framework, **treating an external system as a piece of configuration**, so business code only deals with the call itself:

```java
FastCallResponse<Object> response = fastCall.getClient("order-system")
        .newApiCall("queryOrder")
        .callIt();
```

Two ways to use it:

1. **Configuration driven** — register systems and APIs in a database or in `application.yml`, then call them by api name;
2. **Declarative clients** — declare an annotated Java interface (Feign style) and inject it to send HTTP requests as if they were local method calls.

Adding `fastcall-spring-boot-starter` is enough: everything is auto-configured, no extra annotation required.


## 2. Features

**Authentication**

- Six auth schemes out of the box: `NONE`, `BASIC`, `APIKEY`, `BEARER` (token), `COOKIE`, `JWT`;
- Credentials are acquired and injected automatically, invisible to business code;
- When a credential is rejected (HTTP 401 by default, configurable), FastCall rebuilds it and **replays the current request** (applies to `BEARER`, `COOKIE` and `JWT`);
- `JWT` is signed locally: HS\* symmetric algorithms as well as RSA / EC / EdDSA, with keys in PEM / Base64 / Hex form, including `alg=none`.

**Configuration**

- Two configuration sources: a database (JPA or MyBatis-Plus, chosen by the environment) and `application.yml` (`easy-source` entries);
- Lookup chain: cache → yml → database, falling back step by step; cache engines `memory` and `redis` are supported;
- Predefined APIs: configure path, request method, default parameters and headers once, then call by api name.

**Calling**

- Fluent API: `newCall()` / `newApiCall()` / `host` / `uri` / `method` / `header` / `params` / `body` / `prepared()`;
- Declarative clients: `@FcClient`, `@FcMethod`, `@FcPath`, `@FcQuery`, `@FcHeader`, `@FcBody`, `@FcPart`, `@FcAppointedSystem`;
- Map / bean expansion into several parameters, repeated parameters from collections, multipart uploads (`File` / `byte[]` / `InputStream` / `MultipartFile`), downloads as `MultipartFile`, generic and collection return types, anonymous calls, and per-call system override.

**Governance and extensibility**

- Filter chain: system-level rate limiting (per time window), client-level retry (attempts / interval), request event publishing;
- A single response type, `FastCallResponse`: status code, message, deserialized data, response headers, success / cache-hit / exception flags and request/response timestamps;
- HTTP layer: OkHttp connection pool, global and per-system timeouts, concurrency limits, disk response cache (disabled by default);
- Thread pool and async support;
- Every key component is a replaceable bean (auth handlers, credential providers, source nodes, filters, event listeners, JSON codec) that can be overridden via `@ConditionalOnMissingBean`.

### How it compares

| | FastCall | OpenFeign | RestTemplate / WebClient | Plain OkHttp |
| --- | --- | --- | --- | --- |
| Declarative interface calls | ✅ | ✅ | ❌ | ❌ |
| Authentication built in (6 schemes) | ✅ | ⚠️ write a `RequestInterceptor` | ❌ | ❌ |
| Credential renewal + request replay on rejection | ✅ | ❌ implement it yourself | ❌ | ❌ |
| APIs as configuration (register in a database or yml, call by name) | ✅ | ❌ | ❌ | ❌ |
| Local JWT signing (HS\*, RSA, EC, EdDSA) | ✅ | ❌ | ❌ | ❌ |
| Rate limiting / retry / request events | ✅ | ⚠️ retry only (`Retryer`) | ❌ | ❌ |
| Extra runtime footprint | one starter on top of OkHttp | Spring Cloud stack | Spring Web | OkHttp only |

`❌` means the library does not ship that capability — not that it cannot be built on top of it.
The comparison covers only the concerns this project addresses; see each project's own documentation for its full feature set.

## 3. Requirements

| Dependency | Version | Notes |
| --- | --- | --- |
| JDK | 1.8+ |  |
| Spring Boot | 2.7.x | Auto-configuration uses `META-INF/spring/...AutoConfiguration.imports`, so Boot 2.6 and earlier will not pick it up |
| OkHttp | 4.11.0 | HTTP engine, pulled in transitively by the starter |
| Hutool | 5.8.40 | JSON / JWT / crypto utilities, pulled in transitively |
| Jackson | managed by Spring Boot | Default JSON codec; replaceable through a custom `FcJsonCodec` |
| Persistence framework (optional) | JPA (Hibernate) or MyBatis-Plus | Declared as `provided` in `fastcall-data`: add `spring-boot-starter-data-jpa` or `mybatis-plus-boot-starter` yourself when using the database source |
| Redis (optional) | Spring Data Redis | Only when `fast-call.source-cache.engine=redis`; the default `memory` engine needs no Redis |

## 4. Quick Start

### 4.1 Add the dependency

Maven:

```xml
<dependency>
    <groupId>io.github.linkszf</groupId>
    <artifactId>fastcall-spring-boot-starter</artifactId>
    <version>{latest-version}</version>
</dependency>
```


### 4.2 Configure application.yml

A minimal configuration (everything except `easy-source` has a default):

```yaml
fast-call:
  max-requests: 200            # global concurrent request limit, also sizes the shared thread pool
  max-requests-per-host: 30    # concurrent requests allowed per host
  client:
    connect-timeout: 10        # connect timeout in seconds, used when a system has none of its own
    read-timeout: 30           # read timeout in seconds
    write-timeout: 30          # write timeout in seconds
  pool:
    max-idle-connections: 50   # maximum idle connections in the pool
    keep-alive-minutes: 5      # how long an idle connection is kept
  filter:
    enable-retry: true         # retry a call that failed or was not connected
    enable-rate-limit: false   # system-level rate limiting
    enable-request-event: false # publish request events
  cache:
    enable: false              # OkHttp disk response cache (disabled by default)
    max-size: 10485760         # maximum size of the cache directory in bytes
  source-cache:
    enable: true               # cache resolved system configuration
    engine: memory             # cache engine: memory | redis
    expire: 60                 # cache TTL in seconds, <= 0 means never expire
  easy-source:                 # inline systems, no database required
    - system:
        code: demo-system      # system code used by the caller
        name: Demo system
        enable: true
        host: https://api.example.com
        auth-type: NONE        # see chapter 5
      retry:
        attempts: 3            # maximum retry attempts, < 1 disables retry
        duration: 1000         # interval between retries in milliseconds
      rate-limits:
        - maximum: 100         # requests allowed per time window
          span: MINUTES        # window: SECONDS up to YEARS
```

### 4.3 Fluent API

Inject `FastCall` and obtain a client by system code:

```java
@Autowired
private FastCall fastCall;

// 1) Call a preconfigured API by name
FastCallResponse<Object> response = fastCall.getClient("order-system")
        .newApiCall("queryOrder")
        .callIt();

// 2) Build a request manually; the type argument drives deserialization
FastCallResponse<Map<String, Object>> detail = fastCall.getClient("order-system")
        .newCall(Map.class)
        .uri("/api/orders/1001")
        .method(FcRequestMethod.GET)
        .header("X-Tag", "demo")
        .params(Collections.singletonMap("verbose", "true"))
        .prepared()
        .callIt();

// 3) Get only the payload (throws on failure)
Map<String, Object> data = fastCall.getClient("order-system")
        .newCall(Map.class)
        .uri("/api/orders/1001")
        .method(FcRequestMethod.GET)
        .prepared()
        .call();

// 4) Post a JSON or form body
fastCall.getClient("order-system")
        .newCall(String.class)
        .uri("/api/orders")
        .method(FcRequestMethod.POST)
        .body(order, FcMediaType.APPLICATION_JSON)
        .prepared()
        .callIt();

// 5) Anonymous call (no authentication)
String pong = fastCall.getClient("order-system")
        .newCall(String.class)
        .uri("/public/ping")
        .prepared()
        .anonymousCall();
```

| Method | Description |
| --- | --- |
| `getClient(system)` | Returns the client of a system, creating and caching it on first use |
| `newApiCall(apiName)` | Calls a preconfigured API, applying its path, method and default parameters |
| `newCall()` / `newCall(Class)` / `newCall(Type)` | Builds a request manually; the argument drives response deserialization |
| `prepared().callIt()` | Returns `FastCallResponse<T>` without throwing business exceptions |
| `prepared().call()` | Returns the deserialized payload, throwing on failure |
| `prepared().anonymousCallIt()` / `anonymousCall()` | Anonymous call, authentication is skipped |

### 4.4 Declarative clients

**Step 1** — enable client scanning on the application class.

```java
@SpringBootApplication
@EnableFastCallClients(basePackages = "com.demo.client")
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

**Step 2** — declare the client interface (`system` refers to a configured system code).

```java
@FcClient(system = "declarative-system")
public interface DeclarativeDemoClient {

    // Path placeholder + query parameter + header
    @FcMethod(uri = "/declarative/target/echo/{id}", method = FcRequestMethod.GET)
    String echo(@FcPath("id") String id,
                @FcQuery("q") String q,
                @FcHeader("X-Req-Id") String reqId);

    // JSON request body
    @FcMethod(uri = "/declarative/target/payload", method = FcRequestMethod.POST)
    FastCallResponse<Map<String, Object>> postPayload(@FcBody Map<String, Object> payload);

    // Form submission
    @FcMethod(uri = "/declarative/target/form", method = FcRequestMethod.POST)
    Map<String, Object> formSubmit(
            @FcBody(mediaType = FcMediaType.APPLICATION_FORM_URLENCODED) Map<String, Object> form);

    // Map / collection arguments expand into several query parameters or headers
    @FcMethod(uri = "/declarative/target/query-map", method = FcRequestMethod.GET)
    Map<String, Object> queryMap(@FcQuery Map<String, Object> query,
                                 @FcHeader Map<String, Object> headerMap,
                                 @FcHeader("X-Tag") List<String> tags);

    // File upload (declaring any @FcPart makes the whole request multipart)
    @FcMethod(uri = "/declarative/target/upload", method = FcRequestMethod.POST)
    Map<String, Object> upload(@FcPart("desc") String desc,
                               @FcPart("files") MultipartFile file,
                               @FcPart("meta") String meta);

    // Anonymous call
    @FcMethod(uri = "/declarative/target/anonymous", method = FcRequestMethod.GET, anonymous = true)
    String anonymous(@FcQuery("name") String name);

    // Override the system at runtime
    @FcMethod(uri = "/declarative/target/echo/{id}", method = FcRequestMethod.GET)
    String echoOf(@FcPath("id") String id, @FcAppointedSystem String system);
}
```

**Step 3** — inject the interface and call it.

```java
@Autowired
private DeclarativeDemoClient demoClient;

String result = demoClient.echo("1001", "hello", "req-1");
```

> Interface rules (violating them fails fast at startup or at call time):
>
> - The interface must carry `@FcClient` with a non-blank `system` and be picked up by `@EnableFastCallClients`; every non-default, non-static method must carry `@FcMethod`;
> - Exactly one of `api` and `uri` must be set on `@FcMethod`; every `{placeholder}` in `uri` needs a matching `@FcPath`, one to one;
> - Each method parameter must carry exactly one parameter annotation; at most one `@FcBody` is allowed and it cannot be combined with `@FcPart` (with form-urlencoded the argument must be a `Map`);
> - `@FcPart` requires a non-blank value; `@FcQuery` / `@FcHeader` need an explicit name for scalar arguments, while `Map` or custom bean arguments may omit it and are expanded automatically;
> - When `@FcPath` omits its value the parameter name is used, which requires parameter names at compile time (the compiler `-parameters` option).

### 4.5 Database schema and management API

When the configuration is kept in a database instead of `easy-source`, create the tables first —
[`sql/fastcall-schema-mysql.sql`](sql/fastcall-schema-mysql.sql) holds the statements for MySQL:

```bash
mysql -h127.0.0.1 -P3306 -uroot -p your_database < sql/fastcall-schema-mysql.sql
```

It creates six tables: `fastcall_system` (system configuration), `fastcall_api` (predefined apis of a
system), `fastcall_api_param` (api parameters), `fastcall_auth` (credential and how to renew it),
`fastcall_rate_limit` (rate limit windows) and `fastcall_retry` (retry policy).

You do not have to maintain those records with hand-written SQL: the `fastcall-api` module ships a small
management API (`FcController`) under the base path `/fc/system`.

| Method | Path | Description |
| --- | --- | --- |
| GET | `/fc/system/all` | list all systems |
| GET | `/fc/system/{systemId}` | get one system together with its credential |
| POST | `/fc/system/save` | create or update a system (credential included) |
| DELETE | `/fc/system/{systemId}` | delete a system |
| GET | `/fc/system/{systemId}/api/all` | list the apis of a system |
| POST | `/fc/system/{systemId}/api/save` | create or update the apis of a system |
| GET | `/fc/system/api/{apiId}/param/all` | list the parameters of an api |
| POST | `/fc/system/api/{apiId}/param/save` | create or update the parameters of an api |
| GET | `/fc/system/{systemId}/retry` | get the retry policy of a system |
| POST | `/fc/system/{systemId}/retry/save` | create or update the retry policy of a system |
| DELETE | `/fc/system/{systemId}/retry` | remove the retry policy of a system |

Those endpoints read and write configuration and add no authentication of their own, so expose them only
inside your own network. Rate limit records are not covered by the management API yet: maintain
`fastcall_rate_limit` with SQL or through your own code.

## 5. Authentication Configuration

### 5.1 Auth types at a glance

| `auth-type` | Description | Credential source | How it is attached | Renewed automatically |
| --- | --- | --- | --- | --- |
| `NONE` | No authentication | — | Nothing is attached | — |
| `BASIC` | HTTP Basic | username / password from the configuration | `Authorization: Basic base64(username:password)` | No |
| `APIKEY` | Key-value parameter | key / value from the configuration | Added as a header or query parameter according to `positionOn` | No |
| `BEARER` | Token authentication (OAuth2 and similar) | Parsed from the response of the auth endpoint | `Authorization: Bearer <token>` | Yes |
| `COOKIE` | Cookie authentication | `Set-Cookie` of the auth response | `Cookie: <cookie>` | Yes |
| `JWT` | Locally signed JWT | Signed locally with the configured algorithm and key | Placed in a header or query parameter according to `positionOn` | Yes (re-signed when it expires) |

> `BEARER` and `COOKIE` are **interactive**: before the first business call, the framework sends an anonymous request to the configured auth endpoint and builds the credential from its response.

### 5.2 Common auth settings

`easy-source[].auth` (in the database this is the `fastcall_auth` table):

| Setting | Description |
| --- | --- |
| `path` | Auth endpoint path, relative to the auth host; required for interactive auth (`BEARER`, `COOKIE`) |
| `content` | Credential content as a **JSON string** |
| `particularHost` | Host used for the auth request; defaults to the system `host` |
| `unauthorizedCode` | Response status code that marks the credential as rejected; defaults to `401` |

### 5.3 NONE

```yaml
    - system:
        code: no-auth-system
        name: System without authentication
        host: https://api.example.com
        auth-type: NONE
```

### 5.4 BASIC

```yaml
    - system:
        code: basic-system
        name: Basic auth system
        host: https://api.example.com
        auth-type: BASIC
      auth:
        content: |
          {
            "username": "admin",
            "password": "123456"
          }
```

Sent as `Authorization: Basic YWRtaW46MTIzNDU2` (username and password Base64 encoded).

### 5.5 APIKEY

`content` fields:

| Field | Required | Description |
| --- | --- | --- |
| `key` | Yes | Parameter name |
| `value` | Yes | Parameter value |
| `positionOn` | No | `HEADER` (default) or `QUERY` |

```yaml
    - system:
        code: apikey-system
        name: ApiKey auth system
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

With `positionOn: QUERY` the value is appended to the URL as `?X-Api-Key=...`.

### 5.6 BEARER (interactive token)

`content` fields:

| Field | Required | Description |
| --- | --- | --- |
| `tokenField` | Yes | JSON path of the token in the auth response, supports `a.b.c` |
| `issuanceField` | No | JSON path of the issue time, either an epoch-millisecond number or a date string; defaults to now |
| `expiredInField` | No | JSON path of the validity period in seconds; defaults to 7 days |
| `prop` | No | Extra auth request settings: `headers`, `params`, `body` |

```yaml
    - system:
        code: token-system
        name: Token auth system
        host: https://api.example.com
        auth-type: BEARER
      auth:
        path: /oauth/token                          # auth endpoint
        particularHost: https://sso.example.com     # host of the auth endpoint (optional)
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

Business requests then carry `Authorization: Bearer <token>`. The auth request itself is sent anonymously as `POST` with `Content-Type: application/json`.

### 5.7 COOKIE (interactive cookie)

`content` normally only needs the auth request settings:

```yaml
    - system:
        code: cookie-system
        name: Cookie auth system
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

All `Set-Cookie` headers of the auth response are joined and sent as the `Cookie` header of business requests.

### 5.8 JWT (signed locally)

`content` fields:

| Field | Required | Description |
| --- | --- | --- |
| `positionOn` | No | `HEADER` (default) or `QUERY` |
| `algorithm` | No | Signature algorithm such as `HS256`, `RS256`, `Ed25519`; omitted or `none` means unsigned |
| `secret` | No | Key material: a plain string for symmetric algorithms, or a private key in PEM / Base64 / Hex for asymmetric ones |
| `issueAtOffset` | No | Issue time offset in seconds, `iat = now + offset`; may be negative |
| `expiresIn` | No | Validity period in seconds, `exp = now + expiresIn`; defaults to 86400 |
| `notValidBefore` | No | Offset in seconds used to build `nbf` |
| `payload` | No | Custom claims; explicit `iat` / `exp` / `nbf` are never overwritten |
| `header` | No | JWT header, for example `{"alg": "EdDSA", "kid": "xxx"}` |

```yaml
    - system:
        code: jwt-system
        name: JWT auth system
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

With `positionOn: HEADER` the request carries `Authorization: Bearer <jwt>`; with `QUERY` it is appended as a query parameter.

### 5.9 Credential renewal in practice

1. Before a business request is sent, the auth interceptor resolves the credential for the system `auth-type` and attaches it;
2. When the credential is missing or already marked as rejected, the credential provider rebuilds it (`BEARER` and `COOKIE` call the auth endpoint, `JWT` re-signs the token). Rebuilds for the same system are serialized by a lock, so concurrent calls do not authenticate twice;
3. When a business response carries the `unauthorizedCode` (default `401`), FastCall marks the credential as rejected and **replays the current request** after rebuilding the credential — business code never sees this;
4. Retrying on network errors or failed connections is the job of the retry filter and requires `fast-call.filter.enable-retry: true` plus `retry.attempts > 0` for that system;
5. Keep `fast-call.source-cache.enable: true` (the default). The credential lives inside the cached system configuration, so disabling the configuration cache may make interactive auth (`BEARER`, `COOKIE`) authenticate again on every call.

### 5.10 Database configuration

Besides `easy-source`, systems, authentication, APIs, parameters, rate limits and retry policies can all live in a database and are read through `fastcall-data` (JPA or MyBatis-Plus, matching whichever framework the application provides):

| Table | Content |
| --- | --- |
| `fastcall_system` | System: code, name, host, auth type, timeouts |
| `fastcall_auth` | Auth: endpoint path, credential content (JSON, same shape as `content`), rejected status code |
| `fastcall_api` | API: owning system, name, path, request method |
| `fastcall_api_param` | API arguments: headers, query parameters, default body |
| `fastcall_rate_limit` | Rate limit rules: time window and maximum |
| `fastcall_retry` | Retry policy: attempts and interval |

The credential JSON stored in the database is identical to the `content` JSON shown above.

## 6. Custom Extensions

FastCall assembles its components as "default implementation + overridable": most built-in implementations carry `@ConditionalOnMissingBean` or register only when no bean of the given name exists, so an application takes over simply by declaring its own bean. Filters and configuration source nodes are collected as a collection instead, where adding a bean is enough. Each extension point is described below.

### 6.1 Extension points at a glance

| Extension point | Interface | Default implementation | Registration condition | Typical use |
| --- | --- | --- | --- | --- |
| JSON codec | `FcJsonCodec` | `FcJacksonJsonCodec` | `@ConditionalOnMissingBean(FcJsonCodec.class)` | Switch to FastJSON / Gson, customize serialization |
| Auth handlers | `IFcAuthHandler` | one handler per auth type | `@ConditionalOnMissingBean(implementation class)` each | Change how a credential is attached (custom header, extra signature) |
| Credential providers | `IFcCredentialProvider` / `IFcDynCredentialProvider` | five providers (`NONE` needs none) | `@ConditionalOnMissingBean(implementation class)` each | Custom token acquisition and renewal |
| Request filters | `FcFilter` | rate limit, request event, retry | every `FcFilter` bean, sorted by `@Order` | Call logging, tracing, signing, canary |
| Configuration data provider | `IFcPakProvider` | `FcDefaultPakProvider` (database) | `@ConditionalOnMissingBean` | Read configuration from Nacos / Apollo |
| Source chain nodes | `IFcChainSource` | cache, yml, database | every `IFcChainSource` bean, chained by `@Order` | Add another configuration level (local file, remote HTTP) |
| Cache source | `IFcCacheSource` | `FcInMemoryCacheSource` / `FcRedisCacheSource` | `@ConditionalOnMissingBean(IFcCacheSource.class)` | Switch to Caffeine or a shared cache |
| Database source | `IFcDatabaseSource` | `FcDatabaseSource` | `@ConditionalOnMissingBean(IFcDatabaseSource.class)` | Custom loading, conversion, encryption |
| Event publisher | `IFcRequestEventPublisher` | `FcRequestEventPublisher` (async) | `@ConditionalOnMissingBean` + request events enabled | Deliver events to MQ / telemetry |
| Source event listener | `IFcSourceEventListener` | `FcSourceEventListener` | `@ConditionalOnMissingBean` | Extra work when configuration changes |
| Request event listeners | `IFcAuthRequestEventListener`, `IFcApiRequestEventListener` | write back the last access time | `@ConditionalOnMissingBean` + request events enabled | Audit, metrics, alerting |
| Event thread pool | `Executor` named `FastCallAsyncExecutor` | `ThreadPoolTaskExecutor` (5/10, queue 10000) | `@ConditionalOnMissingBean(name = ...)` | Tune the event pool |


---

More runnable examples, including the complete configuration of every auth type, live in the `fastcall-test` module.
