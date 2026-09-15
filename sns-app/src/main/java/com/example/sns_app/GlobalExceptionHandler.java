package com.example.sns_app;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

/**
 * アプリケーション全体の例外処理（エラーハンドリング）を集約・管理するクラス
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * ファイルアップロード時のサイズ上限超過エラーをキャッチして処理します。
     *
     * @param exc キャッチしたMaxUploadSizeExceededExceptionオブジェクト
     * @return エラーフラグ(?sizeError)を付与したプロフィール画面へのリダイレクトURL
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String handleMaxSizeException(MaxUploadSizeExceededException exc) {

        // サイズ上限エラー時は直接的なエラー画面を出さず、プロフィール画面へエラーパラメータ付きでリダイレクト
        return "redirect:/profile?sizeError";
    }
}
