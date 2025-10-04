package com.example.mytwitter_vscode.controller;

import com.example.mytwitter_vscode.service.CommentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class HomeController {

    private final CommentService commentService;

    public HomeController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping({"/", "/home"})
    public String home(Model model) {
        model.addAttribute("recentComments", commentService.latest());
        return "home";
    }

    @PostMapping("/comments")
    public String postComment(@RequestParam String body,
                              @RequestParam(required = false) String author) {
        String a = (author == null || author.isBlank()) ? "anonymous" : author;
        commentService.create(a, body);
        return "redirect:/home";
    }
}
