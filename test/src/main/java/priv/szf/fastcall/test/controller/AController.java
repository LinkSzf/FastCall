package priv.szf.fastcall.test.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/a")
public class AController {
    @RequestMapping("/hello")
    public String hello(){
        return "hello world!";
    }

}
