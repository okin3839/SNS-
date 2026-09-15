package com.example.sns_app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * SNSアプリケーションのメインクラス（エントリーポイント）
 *
 * @SpringBootApplication 注釈により、コンポーネントスキャンや
 * Spring Boot の自動設定（Auto-configuration）が有効化されます。
 */
@SpringBootApplication
public class SnsAppApplication {

	/**
	 * アプリケーションを起動するためのメインメソッド
	 *
	 * @param args 起動時に渡されるコマンドライン引数
	 */
	public static void main(String[] args) {
		// Spring Boot アプリケーションを初期化し、内蔵サーバーを起動
		SpringApplication.run(SnsAppApplication.class, args);
	}

}