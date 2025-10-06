package com.example.mytwitter_vscode.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import java.time.LocalDateTime;

@Entity
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // DB 上の最大長はコントローラや表示と合わせること
    @Column(nullable = false, length = 30)
    private String author;

    @Column(nullable = false, length = 800)
    private String body;

    private LocalDateTime createdAt;

    // アプリケーションで使う最大長の定数（コントローラと合わせてください）
    public static final int MAX_AUTHOR_LENGTH = 30;
    public static final int MAX_BODY_LENGTH = 500;

    public Comment() {}

    public Comment(String author, String body, LocalDateTime createdAt) {
        this.author = author;
        this.body = body;
        this.createdAt = createdAt;
    }

    @PrePersist
    protected void onCreate() {
        // createdAt のセット
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }

        // author のデフォルト化・トリム・切り詰め
        if (this.author == null || this.author.isBlank()) {
            this.author = "anonymous";
        } else {
            this.author = this.author.trim();
            if (this.author.length() > MAX_AUTHOR_LENGTH) {
                this.author = this.author.substring(0, MAX_AUTHOR_LENGTH);
            }
        }

        // body のトリム・切り詰め（必ず存在するようにする）
        if (this.body == null) {
            this.body = "";
        } else {
            this.body = this.body.trim();
            if (this.body.length() > MAX_BODY_LENGTH) {
                this.body = this.body.substring(0, MAX_BODY_LENGTH);
            }
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    // セッタでも安全策を入れておく（PrePersist と重複しても問題なし）
    public void setAuthor(String author) {
        if (author == null || author.isBlank()) {
            this.author = "anonymous";
        } else {
            String a = author.trim();
            this.author = (a.length() > MAX_AUTHOR_LENGTH) ? a.substring(0, MAX_AUTHOR_LENGTH) : a;
        }
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        if (body == null) {
            this.body = "";
        } else {
            String b = body.trim();
            this.body = (b.length() > MAX_BODY_LENGTH) ? b.substring(0, MAX_BODY_LENGTH) : b;
        }
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
