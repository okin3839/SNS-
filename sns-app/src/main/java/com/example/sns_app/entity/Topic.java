package com.example.sns_app.entity;

import com.example.sns_app.Post;
import com.example.sns_app.User;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
public class Topic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // スレッドのタイトル
    @Column(nullable = false)
    private String title;

    // スレッドの本文（長文が入るように少し長めに設定）
    @Column(nullable = false, length = 1000)
    private String content;

    // 4つのラベルのどれか（質問・相談, 議論・考察, 共有・報告, 雑談）
    @Column(nullable = false)
    private String label;

    // 誰が作成したか（Userテーブルとの紐付け）
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy="topic",cascade=CascadeType.ALL,orphanRemoval=true)
    private List<Post> posts;

    // 作成日時
    private LocalDateTime createdAt = LocalDateTime.now();

    // ↓↓↓ ここから下は IntelliJの機能で Getter と Setter を生成してください ↓↓↓

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public List<Post> getPosts() { return posts; }
    public void setPosts(List<Post> posts) { this.posts = posts; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}