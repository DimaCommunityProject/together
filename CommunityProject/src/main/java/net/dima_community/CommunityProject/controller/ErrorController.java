package net.dima_community.CommunityProject.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ErrorController {

    @GetMapping("/main/access-denied")
    public String accessDenied() {
        return "/main/access-denied";  // access-denied.html로 이동
    }
}