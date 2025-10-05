package com.example.mytwitter_vscode.controller;

import com.example.mytwitter_vscode.service.CommentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class HomeController {

    private final CommentService commentService;
    private static final int MAX_COMMENT_LENGTH = 500;
    private static final int MAX_AUTHOR_LENGTH = 30;

    public HomeController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping({"/", "/home"})
    public String home(Model model) {
        model.addAttribute("recentComments", commentService.latest());
        model.addAttribute("maxCommentLength", MAX_COMMENT_LENGTH);
        return "home";
    }

    @PostMapping("/comments")
    public String postComment(@RequestParam String body,
                              @RequestParam(required = false) String author,
                              RedirectAttributes redirectAttrs) {

        String a = (author == null || author.isBlank()) ? "anonymous" : author.trim();

        if (body == null || body.isBlank()) {
            redirectAttrs.addFlashAttribute("error", "コメントは必須です。");
            return "redirect:/home";
        }

        if (body.length() > MAX_COMMENT_LENGTH) {
            redirectAttrs.addFlashAttribute("error",
                    "コメントは" + MAX_COMMENT_LENGTH + "文字以内で入力してください。");
            return "redirect:/home";
        }

        if (a.length() > MAX_AUTHOR_LENGTH) {
            redirectAttrs.addFlashAttribute("error", "表示名は" + MAX_AUTHOR_LENGTH + "文字以内で入力してください。");
            return "redirect:/home";
        }

        try {
            commentService.create(a, body);
            redirectAttrs.addFlashAttribute("success", "投稿が保存されました。");
        } catch (Exception ex) {
            redirectAttrs.addFlashAttribute("error", "コメントの保存に失敗しました。時間を置いて再度お試しください。");
        }

        return "redirect:/home";
    }

}