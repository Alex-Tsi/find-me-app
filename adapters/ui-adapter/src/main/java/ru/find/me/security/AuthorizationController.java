package ru.find.me.security;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class AuthorizationController {

    @RequestMapping("/")
    public String authorization() {
        return "greeting";
    }
}
