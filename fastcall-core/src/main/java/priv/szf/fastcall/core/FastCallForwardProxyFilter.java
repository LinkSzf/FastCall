package priv.szf.fastcall.core;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import priv.szf.fastcall.common.FastCallConsts;
import priv.szf.fastcall.common.FcHeaderOperation;
import priv.szf.fastcall.common.FcHeaderType;
import priv.szf.fastcall.common.FcMediaType;
import priv.szf.fastcall.common.FcRequestMethod;
import priv.szf.fastcall.common.exception.FastCallException;
import priv.szf.fastcall.common.exception.FcDataNotFoundException;
import priv.szf.fastcall.common.exception.FcUnexpectedException;
import priv.szf.fastcall.core.config.FastCallProperties;
import priv.szf.fastcall.common.model.FcHeaderAssignPak;
import priv.szf.fastcall.common.model.FcSourcePak;
import priv.szf.fastcall.common.model.IEssentialCheck;
import priv.szf.fastcall.common.source.IFcSource;
import priv.szf.fastcall.core.support.FcRequestBuildSupport;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@ConditionalOnProperty(prefix = "fast-call.forward-proxy", name = "enable", havingValue = "true")
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
        log.debug("{}-ForwardProxy filtering...", FastCallConsts.NAME);
        FcSourcePak sourcePak = getSourcePak(request);

        checkAccess(sourcePak);

        String url = buildUrl(request, sourcePak);
        log.debug("{}-ForwardProxy extracts target url[{}]", FastCallConsts.NAME, url);

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
        log.debug("{}-ForwardProxy calls target url[{}] done. successful[{}], code[{}], msg[{}]",
                FastCallConsts.NAME, url, fcResponse.isSuccessful(), fcResponse.getCode(), fcResponse.getMessage());

        writeToResponse(sourcePak, fcResponse, response, request.getRequestURI());
    }

    private void writeToResponse(FcSourcePak sourcePak,
                                 FastCallResponse<InputStream> fcResponse,
                                 HttpServletResponse response,
                                 String requestUri
    ) {
        response.setStatus(fcResponse.getCode());

        Map<String, List<String>> headers = fcResponse.getHeaders();
        List<FcHeaderAssignPak> headerAssigns = sourcePak.getHeaderAssigns(FcHeaderType.RESPONSE);
        customHeaders(headers, headerAssigns, requestUri);

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
                throw new FcUnexpectedException(e, "IO exception occurred when writing to response");
            }
        }

        try {
            response.flushBuffer();
        } catch (IOException e) {
            throw new FcUnexpectedException(e, "IO exception occurred when flushing response");
        }
    }

    private byte[] buildRequestBody(HttpServletRequest request) {
        try(ServletInputStream inputStream = request.getInputStream()) {
            return StreamUtils.copyToByteArray(inputStream);
        } catch (IOException e) {
            throw new FcUnexpectedException(e, "IO exception occurred when reading request body");
        }
    }

    private Map<String, List<String>> buildHeaders(HttpServletRequest request, FcSourcePak sourcePak) {
        Enumeration<String> headerNames = request.getHeaderNames();
        Map<String, List<String>> headers = new HashMap<>();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement().toLowerCase();

            if (HeaderSkip.onRequest(headerName)) {
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

        customHeaders(headers, sourcePak.getHeaderAssigns(FcHeaderType.REQUEST), request.getRequestURI());

        return headers;
    }

    private void customHeaders(Map<String, List<String>> headers,
                               List<FcHeaderAssignPak> headerAssigns,
                               String requestUri
    ) {
        if (CollectionUtil.isEmpty(headerAssigns)) {
            return;
        }

        headerAssigns.stream()
                .filter(h -> Objects.isNull(h.getPath()) || StrUtil.containsIgnoreCase(requestUri, h.getPath()))
                .forEach(h -> {
                    h.check();

                    String name = h.getName().toLowerCase();
                    FcHeaderOperation operation = h.getOperation();
                    switch (operation) {
                        case SET:
                            headers.put(name, Collections.singletonList(h.getValue()));
                            break;
                        case ADD:
                            headers.computeIfAbsent(name, k -> new ArrayList<>())
                                    .add(h.getValue());
                            break;
                        case REMOVE:
                            headers.remove(name);
                            break;
                        default:
                            throw new UnsupportedOperationException();
                    }
                });
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
        return FcRequestBuildSupport.completeUrlWithQuery(host, resourceUri, request.getQueryString());
    }

    private FcSourcePak getSourcePak(HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        String prefix = StrUtil.addSuffixIfNot(properties.getForwardProxy().getPrefix(), "/");
        String system = StrUtil.subBetween(requestUri, prefix,"/");

        return Optional.ofNullable(source.getSourcePak(system))
                .map(IEssentialCheck::check)
                .orElseThrow(() -> new FcDataNotFoundException("Source infos of System[{}] do not exist", system));
    }

    private void checkAccess(FcSourcePak sourcePak) {
        boolean enable = sourcePak.getSystem().isEnable();
        if (!enable) {
            throw new FastCallException("System[{}] has been set to disable and unable to initiate access", sourcePak.getSystem().getCode());
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

