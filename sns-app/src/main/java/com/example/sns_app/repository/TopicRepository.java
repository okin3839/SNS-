package com.example.sns_app.repository;

import com.example.sns_app.User;
import com.example.sns_app.entity.Topic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * トピック（スレッド）情報に関するデータベース操作を提供するリポジトリインターフェース
 */
public interface TopicRepository extends JpaRepository<Topic, Long> {

    /**
     * すべてのトピックを作成日時の降順（新しい順）で取得します（ページネーション対応）。
     *
     * @param pageable ページネーション情報
     * @return 該当ページのトピック一覧
     */
    Page<Topic> findAllByOrderByCreatedAtDesc(Pageable pageable);

    /**
     * 指定されたカテゴリラベルに一致するトピックを作成日時の降順（新しい順）で取得します（ページネーション対応）。
     *
     * @param label 検索対象のカテゴリラベル
     * @param pageable ページネーション情報
     * @return 該当ページのトピック一覧
     */
    Page<Topic> findByLabelOrderByCreatedAtDesc(String label, Pageable pageable);

    /**
     * タイトルに指定されたキーワードが含まれるトピックを作成日時の降順（新しい順）で検索します（ページネーション対応）。
     *
     * @param keyword 検索キーワード（部分一致）
     * @param pageable ページネーション情報
     * @return 該当ページのトピック一覧
     */
    Page<Topic> findByTitleContainingOrderByCreatedAtDesc(String keyword, Pageable pageable);

    /**
     * 指定されたユーザーが作成したトピックを作成日時の降順（新しい順）で取得します。
     *
     * @param user 検索対象の作成者ユーザー
     * @return ユーザーが作成したトピック一覧
     */
    List<Topic> findByUserOrderByCreatedAtDesc(User user);

    /**
     * 指定されたユーザーが「いいね」したトピックを作成日時の降順（新しい順）で取得します。
     *
     * @param user 検索対象のユーザー
     * @return ユーザーがいいねしたトピック一覧
     */
    List<Topic> findByLikedByUsersContainingOrderByCreatedAtDesc(User user);
}