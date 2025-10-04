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
            // 開発用のダミーデータ（起動時に一度だけ追加されます）
            commentService.create("system", "ようこそ MyTwitter へ！");
            commentService.create("alice", "はじめまして。");
            commentService.create("bob", "今日も良い天気ですね。");
        };
    }
}
