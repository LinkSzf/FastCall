package priv.szf.fastcall.test.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import priv.szf.fastcall.core.FastCall;
import priv.szf.fastcall.core.FastCallResponse;

import javax.servlet.http.HttpServletRequest;

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

}
