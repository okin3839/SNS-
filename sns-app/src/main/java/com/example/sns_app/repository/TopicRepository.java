package com.example.sns_app.repository; // ご自身のパッケージ名に合わせてください

import com.example.sns_app.User;
import com.example.sns_app.entity.Topic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TopicRepository extends JpaRepository<Topic, Long> {

    // 全件取得（新しい順・ページネーション対応）
    Page<Topic> findAllByOrderByCreatedAtDesc(Pageable pageable);

    // ラベルで絞り込み（新しい順・ページネーション対応）
    Page<Topic> findByLabelOrderByCreatedAtDesc(String label, Pageable pageable);

    // タイトルにキーワードが含まれるものを検索（新しい順・ページネーション対応）
    Page<Topic> findByTitleContainingOrderByCreatedAtDesc(String keyword, Pageable pageable);

    List<Topic> findByUserOrderByCreatedAtDesc(User user);

    List<Topic> findByLikedByUsersContainingOrderByCreatedAtDesc(User user);
}