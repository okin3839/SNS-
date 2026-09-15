package com.example.sns_app;

import java.security.Principal;
import java.util.List;

import com.example.sns_app.entity.Topic;
import com.example.sns_app.repository.TopicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 掲示板のトップページ表示、返信投稿、投稿削除、いいね切替等を制御するWebコントローラークラス
 */
@Controller
public class PostController {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TopicRepository topicRepository;

    /**
     * トップページの表示および一覧の検索・ページネーション処理を行います。
     * JavaScriptからのAJAXリクエスト（無限スクロール等）の場合は部分テンプレート（フラグメント）のみを返却します。
     *
     * @param model 画面へ渡す属性を保持するモデル
     * @param principal ログイン中のユーザー情報
     * @param keyword 検索キーワード（タイトル部分一致用）
     * @param label カテゴリラベル（絞り込み用）
     * @param pageable ページネーション設定（デフォルト: 1ページ当たり10件）
     * @param requestedWith HTTPリクエストヘッダー（AJAX判定用: X-Requested-With）
     * @return 遷移先ビュー名（通常時は "index"、AJAX時は "index :: topicList"）
     */
    @GetMapping("/")
    public String index(Model model, Principal principal,
                        @RequestParam(name = "keyword", required = false) String keyword,
                        @RequestParam(name = "label", required = false) String label,
                        @PageableDefault(size = 10) Pageable pageable,
                        @RequestHeader(value = "X-Requested-With", required = false) String requestedWith) {

        Page<Topic> topicPage;

        // 検索条件（キーワード・ラベル）に応じてデータベースからの取得処理を分岐
        if (keyword != null && !keyword.isEmpty()) {
            topicPage = topicRepository.findByTitleContainingOrderByCreatedAtDesc(keyword, pageable);
        } else if (label != null && !label.isEmpty()) {
            topicPage = topicRepository.findByLabelOrderByCreatedAtDesc(label, pageable);
        } else {
            topicPage = topicRepository.findAllByOrderByCreatedAtDesc(pageable);
        }

        // 画面に渡すデータをモデルにセット
        model.addAttribute("topics", topicPage.getContent());     // 現在ページのデータリスト（最大10件）
        model.addAttribute("hasNext", topicPage.hasNext());       // 次ページの存在判定フラグ
        model.addAttribute("currentPage", topicPage.getNumber()); // 現在のページ番号（0始まり）
        model.addAttribute("keyword", keyword);                   // 検索キーワードの保持
        model.addAttribute("label", label);                       // 選択されたラベルの保持

        // ログイン中の場合、ログインユーザー情報を取得してセット
        if (principal != null) {
            User user = userRepository.findByUsername(principal.getName()).get();
            model.addAttribute("user", user);
        }

        // JavaScriptからの非同期通信（AJAX）だった場合、「index.html」内の「topicList」フラグメントのみを返却
        if ("XMLHttpRequest".equals(requestedWith)) {
            return "index :: topicList";
        }

        // 通常アクセス時は画面全体（index.html）を返却
        return "index";
    }

    /**
     * 返信投稿（Post）の新規登録を行います。
     *
     * @param post フォームから送信された投稿データ
     * @param principal ログイン中のユーザー情報
     * @return トップページへのリダイレクト
     */
    @PostMapping("/post")
    public String addPost(Post post, Principal principal) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("ユーザーが見つかりません"));
        post.setUser(user);
        postRepository.save(post);
        return "redirect:/";
    }

    /**
     * 指定されたIDの投稿を削除します。
     *
     * @param id 削除対象の投稿ID
     * @return トップページへのリダイレクト
     */
    @PostMapping("/delete")
    public String deletePost(Long id) {
        postRepository.deleteById(id);
        return "redirect:/";
    }

    /**
     * 指定されたスレッドに対する「いいね」の登録／解除を切り替えます。
     * 処理完了後は、元の閲覧ページ（Referer）へ動的にリダイレクトします。
     *
     * @param id 対象スレッドのID
     * @param principal ログイン中のユーザー情報
     * @param request HTTPリクエストオブジェクト（Refererヘッダー参照用）
     * @return 元の閲覧ページ（またはトップページ）へのリダイレクト
     */
    @PostMapping("/topic/{id}/like")
    public String toggleLike(@PathVariable Long id, Principal principal, HttpServletRequest request) {
        // 未ログインの場合はログイン画面へリダイレクト
        if (principal == null) {
            return "redirect:/login";
        }

        User user = userRepository.findByUsername(principal.getName()).get();
        Topic topic = topicRepository.findById(id).orElseThrow();

        // すでに「いいね」している場合は解除、していない場合は登録
        if (topic.getLikedByUsers().contains(user)) {
            topic.getLikedByUsers().remove(user);
        } else {
            topic.getLikedByUsers().add(user);
        }

        topicRepository.save(topic);

        // ボタンを押した元のページ（トップ画面か詳細画面か）を取得して戻る
        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/");
    }

}