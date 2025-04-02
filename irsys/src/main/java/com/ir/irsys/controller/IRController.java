package com.ir.irsys.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class IRController {
    @GetMapping("/results")
    public String results() {
        return "This is a test.";
    }

}
