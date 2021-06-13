package com.mrliuli.currentlimit;

import com.mrliuli.currentlimit.limit.CurrentLimit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author leonliu06
 * @date 2021/4/20
 * @description
 */
@RestController
@SpringBootApplication
public class Application {

    public static void main(String[] args) {

        SpringApplication.run(Application.class, args);

    }

    @CurrentLimit
    @GetMapping("limit/test")
    public String limitTest() {
        return "success";
    }


}
