package com.example.mytwitter_vscode.controller;

import com.example.mytwitter_vscode.model.Comment;
import com.example.mytwitter_vscode.service.CommentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/mypage")
public class MyPageController {

    private final CommentService commentService;

    private String displayName = "user";
    private String bio = "ここに自己紹介や一言コメントを表示できます。";

    private static final int MAX_COMMENT_LENGTH = 500;
    private static final int MAX_AUTHOR_LENGTH = 30;

    // プロフィール関連の最大長
    private static final int MAX_DISPLAYNAME_LENGTH = 10;
    private static final int MAX_BIO_LENGTH = 100;

    public MyPageController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping("")
    public String mypage(Model model, HttpSession session, HttpServletRequest request) {
        String sessionDisplayName = (String) session.getAttribute("displayName");
        String effectiveDisplayName = (sessionDisplayName == null || sessionDisplayName.isBlank())
                ? this.displayName
                : sessionDisplayName;

        String sessionBio = (String) session.getAttribute("bio");
        String effectiveBio = (sessionBio == null) ? this.bio : sessionBio;

        model.addAttribute("displayName", effectiveDisplayName);
        model.addAttribute("bio", effectiveBio);

        List<Comment> comments = commentService.latest();
        // createdAt を昇順（古い -> 新しい）に並べ替え
        List<Comment> sorted = comments.stream()
                .sorted(Comparator.comparing(Comment::getCreatedAt, Comparator.nullsFirst(Comparator.naturalOrder())))
                .collect(Collectors.toList());
        model.addAttribute("comments", sorted);

        model.addAttribute("maxCommentLength", MAX_COMMENT_LENGTH);
        model.addAttribute("maxDisplayNameLength", MAX_DISPLAYNAME_LENGTH);
        model.addAttribute("maxBioLength", MAX_BIO_LENGTH);

        Object csrfAttr = request.getAttribute("_csrf");
        if (csrfAttr instanceof CsrfToken) {
            model.addAttribute("_csrf", csrfAttr);
        }

        return "mypage";
    }

    @PostMapping("/profile")
    public String updateProfile(@RequestParam(required = false) String displayName,
                                @RequestParam(required = false) String bio,
                                RedirectAttributes redirectAttrs,
                                HttpSession session) {

        String trimmedName = displayName == null ? "" : displayName.trim();
        String trimmedBio = bio == null ? "" : bio.trim();

        if (trimmedName.length() == 0) {
            redirectAttrs.addFlashAttribute("error", "表示名は必須です。");
            return "redirect:/mypage";
        }
        if (trimmedName.length() > MAX_DISPLAYNAME_LENGTH) {
            redirectAttrs.addFlashAttribute("error", "表示名は" + MAX_DISPLAYNAME_LENGTH + "文字以内で入力してください。");
            return "redirect:/mypage";
        }
        if (trimmedBio.length() > MAX_BIO_LENGTH) {
            redirectAttrs.addFlashAttribute("error", "自己紹介は" + MAX_BIO_LENGTH + "文字以内で入力してください。");
            return "redirect:/mypage";
        }

        this.displayName = trimmedName;
        this.bio = trimmedBio;
        session.setAttribute("displayName", trimmedName);
        session.setAttribute("bio", trimmedBio);

        redirectAttrs.addFlashAttribute("success", "プロフィールを更新しました。");
        return "redirect:/mypage";
    }

    @PostMapping("/comment")
    public String addComment(@RequestParam String comment,
                             @RequestParam(required = false) String author,
                             RedirectAttributes redirectAttrs,
                             HttpSession session) {

        String sessionName = (String) session.getAttribute("displayName");
        String a = (sessionName != null && !sessionName.isBlank()) ? sessionName.trim()
                : (author == null || author.isBlank() ? "anonymous" : author.trim());

        String trimmedComment = comment == null ? "" : comment.trim();

        if (trimmedComment.isEmpty()) {
            redirectAttrs.addFlashAttribute("error", "コメントは必須です。");
            return "redirect:/mypage";
        }
        if (trimmedComment.length() > MAX_COMMENT_LENGTH) {
            redirectAttrs.addFlashAttribute("error", "コメントは" + MAX_COMMENT_LENGTH + "文字以内で入力してください。");
            return "redirect:/mypage";
        }
        if (a.length() > MAX_AUTHOR_LENGTH) {
            redirectAttrs.addFlashAttribute("error", "表示名は" + MAX_AUTHOR_LENGTH + "文字以内で入力してください。");
            return "redirect:/mypage";
        }

        try {
            commentService.create(a, trimmedComment);
            redirectAttrs.addFlashAttribute("success", "コメントを投稿しました。");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", "投稿に失敗しました。時間を置いて再度お試しください。");
        }

        return "redirect:/mypage";
    }

    @PostMapping(value = "/comment/ajax", produces = "application/json")
    @ResponseBody
    public Map<String, Object> addCommentAjax(@RequestParam String comment,
                                              @RequestParam(required = false) String author,
                                              HttpSession session) {
        String trimmedComment = comment == null ? "" : comment.trim();
        if (trimmedComment.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "コメントは必須です。");
        }
        if (trimmedComment.length() > MAX_COMMENT_LENGTH) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "コメントは" + MAX_COMMENT_LENGTH + "文字以内で入力してください。");
        }

        String sessionName = (String) session.getAttribute("displayName");
        String a = (sessionName != null && !sessionName.isBlank()) ? sessionName.trim()
                : (author == null || author.isBlank() ? "anonymous" : author.trim());

        if (a.length() > MAX_AUTHOR_LENGTH) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "表示名は" + MAX_AUTHOR_LENGTH + "文字以内で入力してください。");
        }

        try {
            Comment saved = commentService.create(a, trimmedComment);
            String preview = saved.getBody() == null ? "" : (saved.getBody().length() > 60 ? saved.getBody().substring(0, 60) + "..." : saved.getBody());

            Map<String, Object> resp = new HashMap<>();
            resp.put("author", saved.getAuthor());
            resp.put("preview", preview);
            resp.put("body", saved.getBody());
            resp.put("createdAt", saved.getCreatedAt() == null ? "" : saved.getCreatedAt().toString());
            resp.put("icon", "💬");
            return resp;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "投稿に失敗しました。");
        }
    }
}
