package com.example.mytwitter_vscode.config;

import com.example.mytwitter_vscode.service.CommentService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner init(CommentService commentService) {
        return args -> {
            // 開発用: 一度だけ system を確実に残し、他は削除してから作る
            commentService.deleteAll(); // ← CommentService/Repository に実装が必要
        };
    }
}
