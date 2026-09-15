package com.example.sns_app;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security のセキュリティ設定（アクセス制限・認証・認可）を行う設定クラス
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * パスワードを暗号化（ハッシュ化）するためのエンコーダーを定義します。
     *
     * @return BCryptアルゴリズムを使用する PasswordEncoder インスタンス
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * セキュリティフィルタチェーン（ページごとのアクセス権限・ログイン・ログアウトの動作）を設定します。
     *
     * @param http HttpSecurityオブジェクト
     * @return 構築された SecurityFilterChain インスタンス
     * @throws Exception 設定処理時に発生する例外
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // URLごとのアクセス制限設定
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/register").permitAll() // 会員登録画面（/register）は未認証ユーザーも含め全員許可
                        .anyRequest().authenticated()              // それ以外のすべてのリクエストはログイン（認証）が必要
                )
                // フォーム認証（ログイン画面）の設定
                .formLogin(login -> login
                        .loginPage("/login")                      // カスタムログイン画面のURL
                        .defaultSuccessUrl("/", true)            // ログイン成功時の遷移先（トップページ）
                        .permitAll()                              // ログイン画面へのアクセスは全員許可
                )
                // ログアウト処理の設定
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout")       // ログアウト完了後の遷移先（ログアウト完了パラメータ付き）
                        .permitAll()                              // ログアウト機能へのアクセスは全員許可
                );

        return http.build();
    }
}