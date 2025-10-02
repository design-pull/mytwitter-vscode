package com.example.mytwitter_vscode.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String index() {
        return "index";
    }

    // ★ この部分は削除済み
    // @GetMapping("/mypage")
    // public String mypage() {
    // return "mypage";
    // }
}
