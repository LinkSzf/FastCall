package priv.szf.fastcall.core.model;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import lombok.Data;
import priv.szf.fastcall.core.common.FcParamPos;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Data
public class FcApiParamPak {

    private Map<String, String> headers;

    private Map<String, String> params;

    private Object body;

    public static FcApiParamPak empty(){
        FcApiParamPak paramPak = new FcApiParamPak();
        paramPak.headers = Collections.emptyMap();
        paramPak.params = Collections.emptyMap();
        return paramPak;
    }

    public static FcApiParamPak init(){
        FcApiParamPak paramPak = new FcApiParamPak();
        paramPak.headers = new HashMap<>();
        paramPak.params = new HashMap<>();
        return paramPak;
    }

    public static <T extends IFcApiParam> FcApiParamPak from(List<T> apiParams) {
        if (CollectionUtil.isEmpty(apiParams)) {
            return empty();
        }

        FcApiParamPak paramPak = init();

        apiParams.stream()
                .filter(Objects::nonNull)
                .forEach(param -> {
                    FcParamPos position = param.getPosition();
                    String name = param.getName();
                    String defaultValue = param.getDefaultValue();
                    if (position == FcParamPos.HEADER) {
                        paramPak.addHeader(name, defaultValue);
                    } else if (position == FcParamPos.QUERY) {
                        paramPak.addParam(name, defaultValue);
                    } else if (position == FcParamPos.BODY) {
                        Object body = defaultValue;
                        if (Boolean.TRUE.equals(param.getJsonObj())) {
                            body = JSONUtil.parse(defaultValue);
                        }
                        paramPak.setBody(body);
                    }
                });

        return paramPak;
    }

    public FcApiParamPak mergeBy(FcApiParamPak priority) {
        if (Objects.isNull(priority)) {
            return this;
        }

        FcApiParamPak newOne = new FcApiParamPak();
        Map<String, String> newHeaders = new HashMap<>(this.headers);
        newHeaders.putAll(priority.getHeaders());

        Map<String, String> newParams = new HashMap<>(this.params);
        newParams.putAll(priority.getParams());

        Object newBody = Objects.isNull(priority.getBody()) ? this.body : priority.getBody();

        newOne.setHeaders(newHeaders);
        newOne.setParams(newParams);
        newOne.setBody(newBody);

        return newOne;
    }

    public void setHeaders(Map<String, String> headers) {
        if (CollectionUtil.isNotEmpty(headers)) {
            this.headers = headers;
        }
    }

    public void setParams(Map<String, String> params) {
        if (CollectionUtil.isNotEmpty(params)) {
            this.params = params;
        }
    }

    public void addHeader(String key, String value) {
        this.headers.put(key, value);
    }

    public void addParam(String key, String value) {
        this.params.put(key, value);
    }


}
