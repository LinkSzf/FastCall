package priv.szf.fastcall.test.controller.declarative;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import priv.szf.fastcall.core.FastCallResponse;
import priv.szf.fastcall.test.client.DeclarativeDemoClient;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/declarative/demo")
@RequiredArgsConstructor
public class DeclarativeDemoController {

    private final DeclarativeDemoClient client;

    @GetMapping("/echo")
    public String echo(@RequestParam(defaultValue = "1001") String id,
                       @RequestParam(defaultValue = "hello") String q
    ) {
        return client.echo(id, q, "req-" + id);
    }

    @PostMapping("/payload")
    public FastCallResponse<Map<String, Object>> payload(@RequestBody(required = false) Map<String, Object> payload) {
        Map<String, Object> body = (payload == null) ? new LinkedHashMap<>() : payload;
        if (!body.containsKey("time")) {
            body.put("time", System.currentTimeMillis());
        }
        return client.postPayload(body);
    }

    @GetMapping("/anonymous")
    public String anonymous(@RequestParam(defaultValue = "fastcall") String name) {
        return client.anonymous(name);
    }
}

