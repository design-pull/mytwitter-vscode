package com.example.mytwitter_vscode.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/mypage")
public class MyPageController {

    private String displayName = "user";
    private String bio = "ここに自己紹介や一言コメントを表示できます。";
    private final List<String> comments = new ArrayList<>();

    @GetMapping("")
    public String mypage(Model model) {
        model.addAttribute("displayName", displayName);
        model.addAttribute("bio", bio);
        model.addAttribute("comments", comments);
        return "mypage";
    }

    @PostMapping("/profile")
    public String updateProfile(@RequestParam String displayName,
                                @RequestParam String bio) {
        System.out.println("updateProfile called: " + displayName + " | " + bio);
        this.displayName = displayName;
        this.bio = bio;
        return "redirect:/mypage";
    }

    @PostMapping("/comment")
    public String addComment(@RequestParam String comment) {
        if (comment != null && !comment.trim().isEmpty()) {
            comments.add(comment);
        }
        return "redirect:/mypage";
    }
}
