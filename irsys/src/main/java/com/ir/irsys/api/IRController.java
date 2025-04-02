package com.ir.irsys.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class IRController {
    @GetMapping("/query")
    public String query() {
        return "This is a test.";
    }

}
