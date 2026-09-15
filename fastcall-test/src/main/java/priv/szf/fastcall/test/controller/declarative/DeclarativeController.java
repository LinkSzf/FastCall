package priv.szf.fastcall.test.controller.declarative;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import priv.szf.fastcall.core.FastCallResponse;
import priv.szf.fastcall.test.client.DeclarativeDemoClient;
import priv.szf.fastcall.test.model.DeclarativeUser;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Arrays;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/declarative")
@RequiredArgsConstructor
public class DeclarativeController {

    private final DeclarativeDemoClient client;

    @GetMapping("/echo")
    public String echo(@RequestParam(defaultValue = "1001") String id,
                       @RequestParam(defaultValue = "hello") String q,
                       @RequestParam(required = false) String system
    ) {
        return client.echo(id, q, "req-" + id, system);
    }

    @PostMapping("/payload")
    public FastCallResponse<Map<String, Object>> payload(@RequestBody(required = false) Map<String, Object> payload) {
        return client.postPayload(payload);
    }

    @GetMapping("/anonymous")
    public String anonymous(@RequestParam(defaultValue = "fastcall") String name) {
        return client.anonymous(name);
    }

    @GetMapping("/users/first-name")
    public String firstUserName() {
        List<DeclarativeUser> users = client.users();
        if (users == null || users.isEmpty()) {
            return null;
        }

        return users.get(0).getChildren().get(0).getName();
    }

    @GetMapping("/query-map")
    public Map<String, Object> queryMap(@RequestParam(defaultValue = "100") int page,
                                        @RequestParam(defaultValue = "20") int size
    ) {
        Map<String, Object> query = new LinkedHashMap<>();
        query.put("page", page);
        query.put("size", size);
        query.put("active", true);

        Map<String, Object> headers = new LinkedHashMap<>();
        headers.put("X-Req-Id", "req-" + System.currentTimeMillis());
        headers.put("X-Biz", "declarative");

        return client.queryMap(query, headers, Arrays.asList("t1", "t2"));
    }

    @PostMapping("/form-submit")
    public Map<String, Object> formSubmit(@RequestParam(defaultValue = "alice") String name) {
        Map<String, Object> form = new LinkedHashMap<>();
        form.put("name", name);
        form.put("roles", Arrays.asList("admin", "ops"));
        form.put("dept", "dev");
        form.put("level", 3);
        return client.formSubmit(form);
    }

    @PostMapping("/upload")
    public Map<String, Object> upload(@RequestParam(defaultValue = "demo upload") String desc) {
        byte[] bytes = ("hello-fastcall-" + System.currentTimeMillis()).getBytes(StandardCharsets.UTF_8);
        return client.upload(desc, bytes, "{\"biz\":\"fastcall\"}");
    }

    @PostMapping("/upload-stream")
    public Map<String, Object> uploadStream(@RequestParam(defaultValue = "demo upload stream") String desc) {
        byte[] bytes = ("hello-fastcall-stream-" + System.currentTimeMillis()).getBytes(StandardCharsets.UTF_8);
        return client.uploadStream(desc, new ByteArrayInputStream(bytes), "{\"biz\":\"fastcall-stream\"}");
    }

    /**
     * 下载为 MultipartFile 后再原样上传，验证响应侧文件语义与请求侧文件部分是否闭环。
     */
    @GetMapping("/download")
    public Map<String, Object> download(@RequestParam(defaultValue = "report.txt") String name) throws IOException {
        MultipartFile file = client.download(name);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("fileName", file.getOriginalFilename());
        result.put("size", file.getSize());
        result.put("contentType", file.getContentType());
        result.put("content", new String(file.getBytes(), StandardCharsets.UTF_8));
        result.put("uploaded", client.uploadFile("downloaded file", file, "{\"biz\":\"fastcall-download\"}"));
        return result;
    }
}
