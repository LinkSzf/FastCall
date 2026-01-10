package priv.szf.fastcall.core;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import priv.szf.fastcall.common.FcHeaderOperation;
import priv.szf.fastcall.common.FcHeaderType;
import priv.szf.fastcall.common.FcMediaType;
import priv.szf.fastcall.common.FcRequestMethod;
import priv.szf.fastcall.common.exception.FastCallException;
import priv.szf.fastcall.common.exception.FcDataNotFoundException;
import priv.szf.fastcall.common.exception.FcUnexpectedException;
import priv.szf.fastcall.core.config.FastCallProperties;
import priv.szf.fastcall.core.model.FcHeaderAssignPak;
import priv.szf.fastcall.core.model.FcSourcePak;
import priv.szf.fastcall.core.model.IEssentialCheck;
import priv.szf.fastcall.core.source.IFcSource;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.text.html.Option;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@ConditionalOnProperty(prefix = "fastcall.forward-proxy", name = "enable", havingValue = "true")
@RequiredArgsConstructor
@ConditionalOnWebApplication
@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class FastCallForwardProxyFilter extends OncePerRequestFilter {

    private final FastCallProperties properties;

    private final IFcSource source;

    private final FastCall fastCall;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String filterPrefix = properties.getForwardProxy().getPrefix();
        return !request.getRequestURI().startsWith(filterPrefix);
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain
    ) {
        FcSourcePak sourcePak = getSourcePak(request);

        checkAccess(sourcePak);

        String url = buildUrl(request, sourcePak);

        FcRequestMethod method = FcRequestMethod.parse(request.getMethod());

        Map<String, List<String>> headers = buildHeaders(request, sourcePak);

        FcMediaType mediaType = FcMediaType.parse(request.getContentType());
        byte[] requestBody = buildRequestBody(request);

        String code = sourcePak.getSystem().getCode();

        FastCallResponse<InputStream> fcResponse = fastCall.getClient(code)
                .newCall(InputStream.class)
                .url(url)
                .method(method)
                .allHeaders(headers)
                .body(requestBody, mediaType)
                .prepared()
                .callIt();

        writeToResponse(sourcePak, fcResponse, response);
    }

    private void writeToResponse(FcSourcePak sourcePak,
                                 FastCallResponse<InputStream> fcResponse,
                                 HttpServletResponse response
    ) {
        response.setStatus(fcResponse.getCode());

        Map<String, List<String>> headers = fcResponse.getHeaders();
        customHeaders(sourcePak.getHeaderAssigns(), headers, FcHeaderType.RESPONSE);

        for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
            String header = entry.getKey();

            if (HeaderSkip.onResponse(header)) {
                continue;
            }

            List<String> values = entry.getValue();
            if (values.size() == 1) {
                response.setHeader(header, values.get(0));
                continue;
            }

            for (String value : values) {
                response.addHeader(header, value);
            }
        }

        InputStream data = fcResponse.getData();
        if (data != null) {
            Optional.ofNullable(fcResponse.getMediaType())
                    .ifPresent(response::setContentType);
            try(ServletOutputStream outputStream = response.getOutputStream()) {
                StreamUtils.copy(data, outputStream);
                response.flushBuffer();
                return;
            } catch (IOException e) {
                throw new FcUnexpectedException(e, "写入响应体时发生异常");
            }
        }

        try {
            response.flushBuffer();
        } catch (IOException e) {
            throw new FcUnexpectedException(e, "刷新到响应缓冲区时发生异常");
        }
    }

    private byte[] buildRequestBody(HttpServletRequest request) {
        try(ServletInputStream inputStream = request.getInputStream()) {
            return StreamUtils.copyToByteArray(inputStream);
        } catch (IOException e) {
            throw new FcUnexpectedException(e, "构建请求体时发生异常");
        }
    }

    private Map<String, List<String>> buildHeaders(HttpServletRequest request, FcSourcePak sourcePak) {
        Enumeration<String> headerNames = request.getHeaderNames();
        Map<String, List<String>> headers = new HashMap<>();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement().toLowerCase();

            if (HeaderSkip.onRequest(headerName)
                    || StrUtil.equals(properties.getForwardProxy().getSystemProperty().toLowerCase(), headerName)) {
                continue;
            }

            List<String> values = new ArrayList<>();
            Enumeration<String> headerValues = request.getHeaders(headerName);
            while (headerValues.hasMoreElements()) {
                String headerValue = headerValues.nextElement();
                values.add(headerValue);
            }
            headers.put(headerName, values);
        }

        changeHostHeader(sourcePak, headers);

        addForwardHeader(request, headers);

        customHeaders(sourcePak.getHeaderAssigns(), headers, FcHeaderType.REQUEST);

        return headers;
    }

    private void customHeaders(List<FcHeaderAssignPak> headerAssigns,
                               Map<String, List<String>> headers,
                               FcHeaderType type
    ) {
        if (CollectionUtil.isEmpty(headerAssigns)) {
            return;
        }

        for (FcHeaderAssignPak headerAssign : headerAssigns) {
            if (type != headerAssign.getType() && headerAssign.getType() != FcHeaderType.REQUEST_RESPONSE) {
                continue;
            }

            headerAssign.check();

            String name = headerAssign.getName().toLowerCase();
            FcHeaderOperation operation = headerAssign.getOperation();
            switch (operation) {
                case SET:
                    headers.put(name, Collections.singletonList(headerAssign.getValue()));
                    break;
                case ADD:
                    headers.computeIfAbsent(name, k -> new ArrayList<>())
                            .add(headerAssign.getValue());
                    break;
                case REMOVE:
                    headers.remove(name);
                    break;
                default:
                    throw new FcUnexpectedException("未知的Header操作[%s]", operation);
            }
        }
    }

    private void changeHostHeader(FcSourcePak sourcePak, Map<String, List<String>> headers) {
        String host = sourcePak.getSystem().getHost();
        Optional.of(host)
                .map(h -> StrUtil.removePrefixIgnoreCase(h, "http://"))
                .map(h -> StrUtil.removePrefixIgnoreCase(h, "https://"))
                .map(h -> StrUtil.subBefore(h, "/", false))
                .ifPresent(h -> {
                    headers.put("host", Collections.singletonList(h));
                });
    }

    private void addForwardHeader(HttpServletRequest request, Map<String, List<String>> headers) {
        if (!properties.getForwardProxy().isAddForwardHeader()) {
            return;
        }

        String clientIp = request.getRemoteAddr();
        // X-Forwarded-For: 追加客户端IP
        String xff = Optional.ofNullable(request.getHeader("x-forwarded-for"))
                .map(s -> s + ", " + clientIp)
                .orElse(clientIp);
        headers.put("x-forwarded-for", Collections.singletonList(xff));

        // X-Forwarded-Proto: 原始协议
        headers.putIfAbsent("x-forwarded-proto", Collections.singletonList(request.getScheme()));

        // X-Forwarded-Host: 原始Host
        Optional.ofNullable(request.getHeader("host"))
                .ifPresent(host -> {
                        headers.putIfAbsent("x-forwarded-host", Collections.singletonList(host));
                });

        // X-Real-IP: 真实客户端IP
        headers.putIfAbsent("x-real-ip", Collections.singletonList(clientIp));
    }

    private String buildUrl(HttpServletRequest request, FcSourcePak sourcePak) {
        String host = sourcePak.getSystem().getHost();
        String requestUri = request.getRequestURI();
        String prefix = properties.getForwardProxy().getPrefix();
        String resourceUri = StrUtil.removePrefix(requestUri, prefix);
        String url = URLUtil.completeUrl(host, resourceUri);
        String queryString = request.getQueryString();
        return (queryString == null) ? url
                : url + "?" + queryString;
    }

    private FcSourcePak getSourcePak(HttpServletRequest request) {
        String systemProperty = properties.getForwardProxy().getSystemProperty();
        String system = request.getHeader(systemProperty);
        if (StrUtil.isBlank(system)) {
            throw new FastCallException("请在请求头中使用[%s]指定被代理系统", systemProperty);
        }

        return Optional.ofNullable(source.getSourcePak(system))
                .map(IEssentialCheck::check)
                .orElseThrow(() -> new FcDataNotFoundException("代理请求时未找到该系统[%s]", system));
    }

    private void checkAccess(FcSourcePak sourcePak) {
        boolean enable = sourcePak.getSystem().isEnable();
        if (!enable) {
            throw new FastCallException("该系统[%s]已被禁用，无法发起访问!", sourcePak.getSystem().getCode());
        }
    }

    private static class HeaderSkip {
        private static final List<String> REQUEST_HEADERS = Arrays.asList(
                "connection",
                "keep-alive",
                "proxy-authenticate",
                "proxy-authorization",
                "te",
                "trailers",
                "transfer-encoding",
                "upgrade"
        );

        private static final List<String> RESPONSE_HEADERS = Arrays.asList(
                "connection",
                "keep-alive",
                "proxy-authenticate",
                "transfer-encoding",
                "upgrade"
        );

        private static boolean onRequest(String header) {
            return REQUEST_HEADERS.contains(header);
        }

        private static boolean onResponse(String header) {
            return RESPONSE_HEADERS.contains(header);
        }
    }


}
