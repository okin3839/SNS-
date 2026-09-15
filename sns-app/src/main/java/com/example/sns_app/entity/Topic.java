package com.example.sns_app.entity;

import com.example.sns_app.Post;
import com.example.sns_app.User;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 掲示板のスレッド（トピック）情報を管理するエンティティクラス
 */
@Entity
public class Topic {

    /** トピックID（主キー、自動採番） */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** スレッドのタイトル */
    @Column(nullable = false)
    private String title;

    /** スレッドの本文（最大1000文字） */
    @Column(nullable = false, length = 1000)
    private String content;

    /** カテゴリラベル（質問・相談, 議論・考察, 共有・報告, 雑談） */
    @Column(nullable = false)
    private String label;

    /** トピックを作成したユーザー（Userテーブルへの外部キー: user_id） */
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    /** トピックに紐づく投稿・返信一覧（トピック削除時に紐づく投稿も自動削除） */
    @OneToMany(mappedBy = "topic", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts;

    /** 作成日時（デフォルトは現在日時） */
    private LocalDateTime createdAt = LocalDateTime.now();

    /** トピックに「いいね」したユーザーの集合（中間テーブル: topic_likes で管理） */
    @ManyToMany
    @JoinTable(
            name = "topic_likes", // 自動生成される中間テーブル名
            joinColumns = @JoinColumn(name = "topic_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> likedByUsers = new HashSet<>();

    // ==========================================
    // ゲッター・セッター（Getter / Setter）
    // ==========================================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Post> getPosts() {
        return posts;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Set<User> getLikedByUsers() {
        return likedByUsers;
    }

    public void setLikedByUsers(Set<User> likedByUsers) {
        this.likedByUsers = likedByUsers;
    }
}