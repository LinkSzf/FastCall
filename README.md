# FastCall

**[Chinese](README-cn.md)**

## Introduction
FastCall is a convenience tool dedicated to calling HTTP APIs of third-party systems. It handles authentication automatically for a variety of auth schemes, so developers only need to focus on system configuration and business API calls.
The recommended approach is to configure systems and APIs in the database up front; the front end then only needs to call a common API and pass in the required api name.


## Modules
* **fastcall-common:** Shared module referenced by the other modules. It mainly contains constants, exception definitions, interface declarations and so on;
* **fastcall-core:** Core module. Most of the FastCall logic lives here;
* **fastcall-data:** Data persistence module, providing database interaction support;
* **fastcall-api:** Web API module. It provides a simple set of data maintenance endpoints and must be used in a Spring Web project;
* **fastcall-test:** Development and debugging module used to exercise features during development;
* **fastcall-spring-boot-starter:** Spring Boot integration module. For convenience, importing this package automatically pulls in the common, core, data and api modules;

## Features
* No need to worry about authentication when calling business APIs; expired authentication is handled and the API call is retried automatically.
* Supports defining APIs and their parameters.
* Supports reverse proxying and modification of request and response headers.
* Supports configuring the request thread pool size.
* Supports configuring the overall concurrent request limit and the per-system concurrent request limit.
* Supports configuring connect timeout, write timeout and read timeout.
* Supports API response caching.
* Supported authentication schemes: Basic, ApiKey, Digest, Cookie, Token, JWT.
* Supports Feign-style declarative client calls.
* Supported configuration caches: Redis and InMemory. The appropriate one is selected automatically based on the referencing project's environment.
* Supported data sources: database and configuration file.
* Supported persistence frameworks: Hibernate and MybatisPlus. The appropriate one is selected automatically based on the referencing project's environment.
* Supports publishing request events.

## Roadmap
* Add request rate control at system or API granularity

## Requirements
JDK 1.8 +

## Usage

### Import the required module via Maven
```xml
<dependency>
    <groupId>priv.szf</groupId>
    <artifactId>fastcall-spring-boot-starter</artifactId>
    <version>xxx</version>
</dependency>
```

### Configure the application

```yaml
fast-call:
  max-requests: 200
  max-requests-per-host: 20
  allow-event: true
  pool:
    max-idle-connections: 50
    keep-alive-minutes: 5
  client:
    connect-timeout: 15
    read-timeout: 45
    write-timeout: 45
  cache:
    enable: false
    max-size: 10485760
  source-cache:
    enable: true
    engine: memory
    expire: 60
    refresh-when-expire: true
  forward-proxy:
    enable: true
    add-forward-header: true
    prefix: /fc_forward_proxy
  easy-source:
    - system:
        code: none-system
        name: system-without-auth
        enable: true
        host: http://127.0.0.1:8080
      auth:
        type: NONE
        path: /
        content: |
          {
            "username": "admin",
            "password": "123456"
          }
```
> Note:
> All the configuration examples above have default values and can be adjusted to suit your project.

### Configure systems

* System information is mandatory and includes the system name, system address, authentication type and so on.
* To use the API feature, configure the API information and API parameters.
* To use proxy forwarding, configure HeaderAssign.
* Once configured, enable the corresponding scopes on the system: AUTH, API, HEADER_ASSIGN

### Use it in code

Inject the Bean
```java
@Autowired
private FastCall fastCall;
```

Fluent call
```java
FastCallResponse<Object> response = fastCall.getClient(sytemCode)
                .newCall()
                .api(apiName)
                .prepared()
                .callIt();
```

### Declarative client calls (Feign style, built on FastCall)

1. Enable scanning on the application class:
```java
import priv.szf.fastcall.core.declarative.annotation.EnableFastCallClients;

@SpringBootApplication
@EnableFastCallClients(basePackages = "com.demo.client")
public class App {
}
```

2. Define the client interface:
```java
import priv.szf.fastcall.core.FastCallResponse;
import priv.szf.fastcall.common.FcRequestMethod;
import priv.szf.fastcall.core.declarative.annotation.FcBody;
import priv.szf.fastcall.core.declarative.annotation.FcClient;
import priv.szf.fastcall.core.declarative.annotation.FcHeader;
import priv.szf.fastcall.core.declarative.annotation.FcMethod;
import priv.szf.fastcall.core.declarative.annotation.FcPart;
import priv.szf.fastcall.core.declarative.annotation.FcPath;
import priv.szf.fastcall.core.declarative.annotation.FcQuery;
import priv.szf.fastcall.common.FcMediaType;

@FcClient(system = "demo-system")
public interface DemoClient {

    @FcMethod(uri = "/users/{id}", method = FcRequestMethod.GET)
    String getUser(@FcPath("id") String id, @FcQuery("verbose") boolean verbose);

    @FcMethod(api = "createUserApi")
    FastCallResponse<String> create(@FcBody Object req);

    @FcMethod(uri = "/users/search", method = FcRequestMethod.GET)
    String search(@FcQuery Map<String, Object> queryMap,
                  @FcHeader Map<String, Object> headerMap,
                  @FcHeader("X-Tag") List<String> tags);

    @FcMethod(uri = "/token", method = FcRequestMethod.POST)
    String token(@FcBody(mediaType = FcMediaType.APPLICATION_FORM_URLENCODED) Map<String, Object> form);

    @FcMethod(uri = "/upload", method = FcRequestMethod.POST)
    String upload(@FcPart("desc") String desc,
                  @FcPart("files") File file);
}
```

3. Inject the interface and call it directly:
```java
@Autowired
private DemoClient demoClient;
```

## Release notes
* v1.0.0 First official release


