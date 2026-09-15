package com.example.sns_app;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * ユーザー情報のデータベース操作を担当するリポジトリインターフェース
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * ユーザー名（ニックネーム）でユーザーを検索します。
     * ログイン処理やユーザー名の重複チェックに使用されます。
     *
     * @param username 検索対象のユーザー名
     * @return 該当するユーザーが存在する場合は Optional に包んで返し、存在しない場合は空の Optional を返します
     */
    Optional<User> findByUsername(String username);
}