package com.bookify.bookify_app;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/hej")
    public ResponseEntity<String> hello(@RequestParam(required = false, defaultValue = "false") boolean crash) {
        if (crash) {
            throw new RuntimeException("Simulated crash for testing GlobalExceptionHandler");
        }
        return ResponseEntity.ok("hej från backend!");
    }

    @GetMapping("/")
    public ResponseEntity<String> home() {
        return ResponseEntity.ok("Backend är igång 🚀 (gå till /hej för test)");
    }
}