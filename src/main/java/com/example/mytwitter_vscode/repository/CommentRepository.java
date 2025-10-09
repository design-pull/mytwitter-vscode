package com.example.mytwitter_vscode.repository;

import com.example.mytwitter_vscode.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findTop20ByOrderByCreatedAtDesc();
    boolean existsByAuthorAndBody(String author, String body);
}
