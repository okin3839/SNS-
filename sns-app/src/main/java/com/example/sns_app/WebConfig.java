package com.example.sns_app;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Webアプリケーションのカスタム設定（静的リソースのマッピング等）を行う設定クラス
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * アップロードされたファイル等の静的リソースに対するURLパスと実際のディレクトリの静的マッピングを登録します。
     *
     * @param registry リソースハンドラーの設定用レジストリ
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // "/uploads/**" へのリクエストをローカルの "src/main/resources/static/uploads/" ディレクトリへ紐付け
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:src/main/resources/static/uploads/");
    }
}