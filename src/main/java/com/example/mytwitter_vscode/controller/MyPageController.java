package com.example.mytwitter_vscode.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
public class MyPageController {

    // 簡易的にメモリ上に保存（DBは後で導入予定）
    private String displayName = "user";
    private String bio = "ここに自己紹介や一言コメントを表示できます。";
    private List<String> comments = new ArrayList<>();

    // マイページ表示
    @GetMapping("/mypage")
    public String mypage(Model model) {
        model.addAttribute("displayName", displayName);
        model.addAttribute("bio", bio);
        model.addAttribute("comments", comments);
        return "mypage";
    }

    // プロフィール更新
    @PostMapping("/mypage/profile")
    public String updateProfile(@RequestParam String displayName,
                                @RequestParam String bio) {
        this.displayName = displayName;
        this.bio = bio;
        return "redirect:/mypage"; // 更新後にマイページへリダイレクト
    }

    // コメント投稿
    @PostMapping("/mypage/comment")
    public String addComment(@RequestParam String comment) {
        if (comment != null && !comment.trim().isEmpty()) {
            comments.add(comment);
        }
        return "redirect:/mypage"; // 投稿後にマイページへリダイレクト
    }
}
