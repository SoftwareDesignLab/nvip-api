package org.nvip.api.controllers;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value="/login")
public class LoginController {

    @PostMapping
    public String login() {
        return "";
    }
}
