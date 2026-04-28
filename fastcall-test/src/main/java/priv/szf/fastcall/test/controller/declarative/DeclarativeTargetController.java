package priv.szf.fastcall.test.controller.declarative;

import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import priv.szf.fastcall.test.model.DeclarativeUser;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/declarative/target")
public class DeclarativeTargetController {

    @GetMapping("/echo/{id}")
    public String echo(@PathVariable("id") String id,
                       @RequestParam("q") String q,
                       @RequestHeader("X-Req-Id") String reqId
    ) {
        return "echo:id=" + id + ",q=" + q + ",reqId=" + reqId;
    }

    @PostMapping("/payload")
    public Map<String, Object> payload(@RequestBody Map<String, Object> body,
                                       @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization
    ) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("received", body);
        result.put("authorized", authorization != null);
        return result;
    }

    @GetMapping("/anonymous")
    public String anonymous(@RequestParam("name") String name,
                            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization
    ) {
        return "anonymous:name=" + name + ",authorization=" + authorization;
    }

    @GetMapping("/users")
    public List<DeclarativeUser> users() {
        DeclarativeUser child = new DeclarativeUser(11L, "child11", null);
        List<DeclarativeUser> childList = Collections.singletonList(child);
        return Arrays.asList(
                new DeclarativeUser(1L, "alice", childList),
                new DeclarativeUser(2L, "bob", childList)
        );
    }

    @GetMapping("/query-map")
    public Map<String, Object> queryMap(
            @RequestParam Map<String, String> query,
            @RequestHeader(value = "X-Req-Id", required = false) String reqId,
            @RequestHeader(value = "X-Biz", required = false) String biz,
            @RequestHeader(value = "X-Tag", required = false) List<String> tags
    ) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("query", query);
        result.put("reqId", reqId);
        result.put("biz", biz);
        result.put("tags", tags);
        return result;
    }

    @PostMapping("/form")
    public Map<String, Object> formSubmit(@RequestParam Map<String, String> form) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("form", form);
        result.put("size", form.size());
        return result;
    }

    @PostMapping("/upload")
    public Map<String, Object> upload(
            @RequestPart(value = "desc", required = false) String desc,
            @RequestPart("files") MultipartFile file,
            @RequestPart(value = "meta", required = false) String meta
    ) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("desc", desc);
        result.put("meta", meta);
        result.put("fileName", file.getOriginalFilename());
        result.put("size", file.getSize());
        result.put("contentType", file.getContentType());
        return result;
    }
}
