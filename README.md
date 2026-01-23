# FastCall

## 介绍
FastCall是一款专用于进行调用三方系统HTTP接口的便捷访问工具。支持多种认证方式的自动认证，开发只需专注于系统的配置和业务接口调用。
推荐的使用方式为在数据库事先进行系统及接口配置，前端只需调用公共接口传入指定的api名称即可。


## 项目模块说明
* **fastcall-common:** 公共模块，供其他模块引用。主要包含一些常量，异常定义，接口声明等；
* **fastcall-core:** 核心模块，FastCall的主要逻辑都在这里；
* **fastcall-data:** 数据持久化模块，用于支持与数据库交互；
* **fastcall-api:** web接口模块，提供一套简易的数据维护接口，需要在SpringWeb项目中使用；
* **fastcall-test:** 开发调试模块，用于开发过程中调试功能；
* **fastcall-spring-boot-starter:** SpringBoot集成模块，为便于引用，引入此包即自动包含common,core,data,api模块的依赖；

## 功能特性
* 进行业务接口请求时无须额外考虑认证问题，若认证失效会自动处理并重试业务接口。
* 支持定义API，支持定义接口参数。
* 支持反向代理，支持请求头、响应头修改。
* 支持定义请求线程池大小。
* 支持定义并发请求数量及单系统并发请求数量限制。
* 支持定义链接超时，写入超时，读取超时。
* 支持接口缓存。
* 支持的认证方式：Basic，ApiKey，Digest，Cookie，Token。
* 支持的配置缓存：Redis，InMemory。会根据引用项目的环境自动选择。
* 支持的数据源：数据库，配置文件。
* 支持的持久化框架：Hibernate，MybatisPlus。会根据引用项目的环境自动选择。
* 支持发布请求事件。

## 环境依赖
JDK 1.8 +

## 使用说明

### 通过Maven引入需要的模块
```xml
<dependency>
    <groupId>priv.szf</groupId>
    <artifactId>fastcall-spring-boot-starter</artifactId>
    <version>xxx</version>
</dependency>
```

### 在配置文件中进行设置

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
        name: 测试无认证的系统
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
> 注:
> 以上配置示例均有默认值，可根据项目需要自行设置。

### 进行系统配置

* 必须配置系统信息，包含系统名称、系统地址、认证方式等。
* 如需要使用API功能，需要配置API信息和API参数。
* 如需要使用代理转发功能，需要配置HeaderAssign。
* 设置完成后需要在系统的功能中启用对应的scope:AUTH,API,HEADER_ASSIGN

### 在代码中使用

注入Bean
```java
@Autowired
private FastCall fastCall;
```

链式调用
```java
FastCallResponse<Object> response = fastCall.getClient(sytemCode)
                .newCall()
                .api(apiName)
                .prepared()
                .callIt();
```

## 版本说明
* v1.0.0 第一个正式版发布


