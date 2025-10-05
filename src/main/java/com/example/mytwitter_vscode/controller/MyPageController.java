package com.example.mytwitter_vscode.controller;

import com.example.mytwitter_vscode.service.CommentService;
import com.example.mytwitter_vscode.model.Comment;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/mypage")
public class MyPageController {

    private final CommentService commentService;

    private String displayName = "user";
    private String bio = "ここに自己紹介や一言コメントを表示できます。";

    private static final int MAX_COMMENT_LENGTH = 500;
    private static final int MAX_AUTHOR_LENGTH = 30;

    public MyPageController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping("")
    public String mypage(Model model, HttpSession session) {
        // セッションに保存された表示名があれば優先して表示
        String sessionDisplayName = (String) session.getAttribute("displayName");
        String effectiveDisplayName = (sessionDisplayName == null || sessionDisplayName.isBlank())
                ? this.displayName
                : sessionDisplayName;

        model.addAttribute("displayName", effectiveDisplayName);
        model.addAttribute("bio", bio);

        List<Comment> comments = commentService.latest();
        model.addAttribute("comments", comments);

        // テンプレートで maxlength や切り詰め表示に使うため
        model.addAttribute("maxCommentLength", MAX_COMMENT_LENGTH);

        return "mypage";
    }

    @PostMapping("/profile")
    public String updateProfile(@RequestParam String displayName,
                                @RequestParam String bio,
                                RedirectAttributes redirectAttrs,
                                HttpSession session) {
        this.displayName = displayName == null ? "" : displayName.trim();
        this.bio = bio == null ? "" : bio.trim();

        // セッションに保存（ホームで参照できるようにする）
        session.setAttribute("displayName", this.displayName);

        redirectAttrs.addFlashAttribute("success", "プロフィールを更新しました。");
        return "redirect:/mypage";
    }

    @PostMapping("/comment")
    public String addComment(@RequestParam String comment,
                             @RequestParam(required = false) String author,
                             RedirectAttributes redirectAttrs) {

        String a = (author == null || author.isBlank()) ? "anonymous" : author.trim();

        if (comment == null || comment.isBlank()) {
            redirectAttrs.addFlashAttribute("error", "コメントは必須です。");
            return "redirect:/mypage";
        }
        if (comment.length() > MAX_COMMENT_LENGTH) {
            redirectAttrs.addFlashAttribute("error", "コメントは" + MAX_COMMENT_LENGTH + "文字以内で入力してください。");
            return "redirect:/mypage";
        }
        if (a.length() > MAX_AUTHOR_LENGTH) {
            redirectAttrs.addFlashAttribute("error", "表示名は" + MAX_AUTHOR_LENGTH + "文字以内で入力してください。");
            return "redirect:/mypage";
        }

        try {
            commentService.create(a, comment);
            redirectAttrs.addFlashAttribute("success", "コメントを投稿しました。");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", "投稿に失敗しました。時間を置いて再度お試しください。");
        }

        return "redirect:/mypage";
    }
}
