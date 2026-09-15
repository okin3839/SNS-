package com.example.sns_app;

import com.example.sns_app.entity.Topic;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import java.time.LocalDateTime;

/**
 * 掲示板のスレッドに対する返信（投稿）を表すエンティティクラス
 */
@Entity
public class Post {

    /** 投稿ID（主キー / 自動採番） */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 投稿を行ったユーザー情報（多対一リレーション） */
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    /** 投稿が属するスレッド情報（多対一リレーション） */
    @ManyToOne
    @JoinColumn(name = "topic_id")
    private Topic topic;

    /** 投稿内容（本文） */
    private String content;

    /** 投稿日時 */
    private LocalDateTime createdAt;

    // --- ゲッター・セッター ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Topic getTopic() {
        return topic;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * データベースへの新規保存前に自動的に呼び出され、現在日時を投稿日時として設定します。
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}