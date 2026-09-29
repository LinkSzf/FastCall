package io.github.linkszf.fastcall.test.client;

import io.github.linkszf.fastcall.common.FcRequestMethod;
import io.github.linkszf.fastcall.common.FcMediaType;
import io.github.linkszf.fastcall.core.FastCallResponse;
import io.github.linkszf.fastcall.core.declarative.annotation.FcAppointedSystem;
import io.github.linkszf.fastcall.core.declarative.annotation.FcBody;
import io.github.linkszf.fastcall.core.declarative.annotation.FcClient;
import io.github.linkszf.fastcall.core.declarative.annotation.FcHeader;
import io.github.linkszf.fastcall.core.declarative.annotation.FcMethod;
import io.github.linkszf.fastcall.core.declarative.annotation.FcPart;
import io.github.linkszf.fastcall.core.declarative.annotation.FcPath;
import io.github.linkszf.fastcall.core.declarative.annotation.FcQuery;
import io.github.linkszf.fastcall.test.model.DeclarativeUser;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

@FcClient(system = "declarative-system")
public interface DeclarativeDemoClient {

    @FcMethod(uri = "/declarative/target/echo/{id}", method = FcRequestMethod.GET)
    String echo(@FcPath("id") String id,
                @FcQuery("q") String q,
                @FcHeader("X-Req-Id") String reqId,
                @FcAppointedSystem String system
    );

    @FcMethod(uri = "/declarative/target/payload", method = FcRequestMethod.POST)
    FastCallResponse<Map<String, Object>> postPayload(@FcBody Map<String, Object> payload);

    @FcMethod(uri = "/declarative/target/anonymous", method = FcRequestMethod.GET, anonymous = true)
    String anonymous(@FcQuery("name") String name);

    @FcMethod(uri = "/declarative/target/users", method = FcRequestMethod.GET)
    List<DeclarativeUser> users();

    @FcMethod(uri = "/declarative/target/query-map", method = FcRequestMethod.GET)
    Map<String, Object> queryMap(
            @FcQuery Map<String, Object> query,
            @FcHeader Map<String, Object> headerMap,
            @FcHeader("X-Tag") List<String> tags
    );

    @FcMethod(uri = "/declarative/target/form", method = FcRequestMethod.POST)
    Map<String, Object> formSubmit(
            @FcBody(mediaType = FcMediaType.APPLICATION_FORM_URLENCODED) Map<String, Object> form
    );

    @FcMethod(uri = "/declarative/target/upload", method = FcRequestMethod.POST)
    Map<String, Object> upload(
            @FcPart("desc") String desc,
            @FcPart(value = "files", fileName = "payload.txt", mediaType = FcMediaType.TEXT_PLAIN) byte[] fileContent,
            @FcPart("meta") String meta
    );

    @FcMethod(uri = "/declarative/target/upload-stream", method = FcRequestMethod.POST)
    Map<String, Object> uploadStream(
            @FcPart("desc") String desc,
            @FcPart(value = "files", fileName = "payload.txt", mediaType = FcMediaType.TEXT_PLAIN) InputStream fileContent,
            @FcPart("meta") String meta
    );

    @FcMethod(uri = "/declarative/target/upload", method = FcRequestMethod.POST)
    Map<String, Object> uploadFile(
            @FcPart("desc") String desc,
            @FcPart("files") MultipartFile fileContent,
            @FcPart("meta") String meta
    );

    @FcMethod(uri = "/declarative/target/download", method = FcRequestMethod.GET)
    MultipartFile download(@FcQuery("name") String name);
}
