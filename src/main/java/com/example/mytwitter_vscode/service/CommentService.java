package com.example.mytwitter_vscode.service;

import com.example.mytwitter_vscode.model.Comment;
import com.example.mytwitter_vscode.repository.CommentRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CommentService {

    private final CommentRepository repo;

    public CommentService(CommentRepository repo) {
        this.repo = repo;
    }

    public Comment create(String author, String body) {
        Comment c = new Comment(author, body, LocalDateTime.now());
        return repo.save(c);
    }

    public List<Comment> latest() {
        return repo.findTop20ByOrderByCreatedAtDesc();
    }
}
