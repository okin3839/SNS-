package com.example.sns_app;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * ユーザー情報を管理するエンティティクラス
 */
@Entity
@Table(name = "user")
public class User {

    /** ユーザーID（主キー、自動採番） */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** ニックネーム（ユーザー名） */
    @NotBlank(message = "ニックネームを入力してください")
    private String username;

    /** パスワード（暗号化されて保存される） */
    @NotBlank(message = "パスワードを入力してください")
    @Size(min = 4, max = 100, message = "パスワードは4文字以上で入力してください")
    private String password;

    /** プロフィール画像ファイル名 */
    @Column(name = "profile_image")
    private String profileImage;

    /** 自己紹介文（最大500文字） */
    @Column(length = 500)
    private String bio;

    // ==========================================
    // ゲッター・セッター（Getter / Setter）
    // ==========================================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }
}
