package com.example.sns_app;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * 投稿（Post）エンティティのデータベース操作を担当するリポジトリインターフェース
 */
@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    /**
     * 全ての投稿を作成日時の降順（新しい順）で取得します。
     *
     * @return 新しい順にソートされた全投稿リスト
     */
    List<Post> findAllByOrderByCreatedAtDesc();

    /**
     * 特定のユーザーによる投稿を作成日時の降順（新しい順）で取得します。
     *
     * @param user 検索対象のユーザー
     * @return 指定ユーザーの投稿リスト（新しい順）
     */
    List<Post> findByUserOrderByCreatedAtDesc(User user);
}
