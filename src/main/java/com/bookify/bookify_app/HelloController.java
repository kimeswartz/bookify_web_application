package com.bookify.bookify_app;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    @GetMapping("/hej")
    public String hello() {
        return "hej från backend!";
    }

    @GetMapping("/")
    public String home() {
        return "Backend är igång 🚀 (gå till /hej för test)";
    }

}