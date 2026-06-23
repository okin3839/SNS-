package com.example.sns_app;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

// @ControllerAdvice は「アプリ全体で発生したエラーを監視する見張り番」のマークです
@ControllerAdvice
public class GlobalExceptionHandler {

    // ファイルサイズ上限（今回は10MB）を超えたエラーが発生した時だけ、この処理が動きます
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String handleMaxSizeException(MaxUploadSizeExceededException exc) {

        // エラー画面（白文字や接続リセットの画面）を出す代わりに、
        // プロフィール画面へ「sizeError」という合図をつけて強制的に戻します
        return "redirect:/profile?sizeError";
    }
}
