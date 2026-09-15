package com.example.sns_app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Spring Securityの認証処理において、データベースからユーザー情報を取得・変換するサービス実装クラス
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    /**
     * ログイン画面で入力されたユーザー名をもとにデータベースを検索し、
     * Spring Securityが扱う認証用ユーザーオブジェクト（UserDetails）を生成します。
     *
     * @param username ログイン時に入力されたユーザー名
     * @return 認証に必要な情報を含む UserDetails オブジェクト
     * @throws UsernameNotFoundException 指定されたユーザー名が見つからない場合に発生
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // ① 入力されたユーザー名（username）でデータベースを検索
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("ユーザーが見つかりません: " + username));

        // ② 検索結果の独自Userエンティティを、Spring Security内部で認識可能なUserオブジェクトに変換して返却
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword()) // ハッシュ化済みパスワードを設定
                .roles("USER")                 // 基本権限（ROLE_USER）を付与
                .build();
    }
}
