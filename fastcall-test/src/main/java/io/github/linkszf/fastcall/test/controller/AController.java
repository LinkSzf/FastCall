package io.github.linkszf.fastcall.test.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import io.github.linkszf.fastcall.common.FcRequestMethod;
import io.github.linkszf.fastcall.core.FastCall;
import io.github.linkszf.fastcall.core.FastCallResponse;
import io.github.linkszf.fastcall.test.model.JsonObject;
import io.github.linkszf.fastcall.test.model.JsonObject2;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/a")
public class AController {

    @Autowired
    private FastCall fastCall;

    @RequestMapping("/hello")
    public String hello(){
        return "hello world!";
    }

    @GetMapping("/api/test")
    public FastCallResponse<?> apiTest(@RequestParam String systemCode, @RequestParam String apiName) {
        return fastCall.getClient(systemCode)
                .newCall()
                .uri(apiName)
                .prepared()
                .callIt();
    }

    @GetMapping("/forward/test")
    public String forwardTest(HttpServletRequest request) {
        String header = request.getHeader("X-test-1");
        String header2 = request.getHeader("X-test-2");
        String header3 = request.getHeader("host");
        return header3;
    }

    @GetMapping("/cast/test/trigger")
    public JsonObject2 jsonObjectTestTrigger(@RequestParam(defaultValue = "local") String systemCode) {
        return fastCall.getClient(systemCode)
                .newCall(JsonObject2.class)
                .uri("/a/cast/test")
                .method(FcRequestMethod.GET)
                .prepared()
                .callIt()
                .getData();
    }

   @GetMapping("/cast/test")
    public JsonObject jsonObjectTestSource() {
        return new JsonObject("test", 123, LocalDateTime.now());
    }


}
