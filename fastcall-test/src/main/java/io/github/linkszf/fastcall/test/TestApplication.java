package io.github.linkszf.fastcall.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.github.linkszf.fastcall.core.declarative.annotation.EnableFastCallClients;

@SpringBootApplication
@EnableFastCallClients(basePackageClasses = TestApplication.class)
public class TestApplication {
    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class, args);
    }
}
