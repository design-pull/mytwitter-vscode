package com.example.mytwitter_vscode.service;

import com.example.mytwitter_vscode.model.Comment;
import com.example.mytwitter_vscode.repository.CommentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CommentService {

    private static final int MAX_AUTHOR_LENGTH = 30;
    private static final int MAX_COMMENT_LENGTH = 800;

    private final CommentRepository repo;

    public CommentService(CommentRepository repo) {
        this.repo = repo;
    }

    @Transactional
    public Comment create(String author, String body) {
        String a = sanitize(author, MAX_AUTHOR_LENGTH, "anonymous");
        String b = sanitize(body, MAX_COMMENT_LENGTH, "");

        if (b.isBlank()) {
            throw new IllegalArgumentException("コメントは必須です。");
        }

        Comment c = new Comment(a, b, LocalDateTime.now());
        return repo.save(c);
    }

    public List<Comment> latest() {
        // リポジトリに合わせて件数は20件（必要ならメソッドを50件に変更可能）
        return repo.findTop20ByOrderByCreatedAtDesc();
    }

    private String sanitize(String s, int maxLen, String defaultVal) {
        if (s == null) return defaultVal;
        String trimmed = s.trim();
        if (trimmed.isEmpty()) return defaultVal;
        return trimmed.length() > maxLen ? trimmed.substring(0, maxLen) : trimmed;
    }
}
