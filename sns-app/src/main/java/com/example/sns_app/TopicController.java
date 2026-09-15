package com.example.sns_app;

import com.example.sns_app.entity.Topic;
import com.example.sns_app.repository.TopicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

/**
 * 掲示板（トピック）の作成・閲覧・返信・削除および他ユーザープロフィールの表示を制御するコントローラー
 */
@Controller
public class TopicController {

    /** トピックデータ操作用リポジトリ */
    @Autowired
    private TopicRepository topicRepository;

    /** ユーザーデータ操作用リポジトリ */
    @Autowired
    private UserRepository userRepository;

    /** 投稿・返信データ操作用リポジトリ */
    @Autowired
    private PostRepository postRepository;

    /**
     * 新規スレッド（トピック）作成画面を表示します。
     *
     * @return トピック作成画面のテンプレート名 ("topic_form")
     */
    @GetMapping("/topic/new")
    public String showCreateForm() {
        return "topic_form";
    }

    /**
     * 新規スレッド（トピック）を保存します。
     *
     * @param title スレッドタイトル
     * @param label カテゴリラベル
     * @param content 本文
     * @param principal ログインユーザー情報
     * @return トップページへのリダイレクト指示
     */
    @PostMapping("/topic/create")
    public String createTopic(@RequestParam String title,
                              @RequestParam String label,
                              @RequestParam String content,
                              Principal principal) {
        User user = userRepository.findByUsername(principal.getName()).get();

        Topic topic = new Topic();
        topic.setTitle(title);
        topic.setLabel(label);
        topic.setContent(content);
        topic.setUser(user);

        topicRepository.save(topic);
        return "redirect:/";
    }

    /**
     * スレッドの詳細画面（返信一覧含む）を表示します。
     *
     * @param id トピックID
     * @param model 画面引き渡し用モデル
     * @param principal ログインユーザー情報（未ログイン時はnull）
     * @return スレッド詳細画面のテンプレート名 ("topic_detail")
     */
    @GetMapping("/topic/{id}")
    public String showTopicDetail(@PathVariable Long id, Model model, Principal principal) {
        Topic topic = topicRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("無効なトピックID:" + id));

        model.addAttribute("topic", topic);

        if (principal != null) {
            User user = userRepository.findByUsername(principal.getName()).get();
            model.addAttribute("user", user);
        }
        return "topic_detail";
    }

    /**
     * スレッドに対する返信（コメント）を投稿します。
     *
     * @param id トピックID
     * @param content 返信本文
     * @param principal ログインユーザー情報
     * @return 該当スレッド詳細画面へのリダイレクト指示
     */
    @PostMapping("/topic/{id}/reply")
    public String replyToTopic(@PathVariable Long id,
                               @RequestParam String content,
                               Principal principal) {
        Topic topic = topicRepository.findById(id).get();
        User user = userRepository.findByUsername(principal.getName()).get();

        Post post = new Post();
        post.setContent(content);
        post.setTopic(topic);
        post.setUser(user);

        postRepository.save(post);
        return "redirect:/topic/" + id;
    }

    /**
     * スレッド内の特定の返信（コメント）を削除します（作成者本人限定）。
     *
     * @param topicId トピックID
     * @param postId 削除対象の返信ID
     * @param principal ログインユーザー情報
     * @return 該当スレッド詳細画面へのリダイレクト指示
     */
    @PostMapping("/topic/{topicId}/post/{postId}/delete")
    public String deletePost(@PathVariable Long topicId,
                             @PathVariable Long postId,
                             Principal principal) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("無効な返信ID:" + postId));

        // 本人確認を行い、一致する場合のみ削除を実行
        if (principal != null && post.getUser().getUsername().equals(principal.getName())) {
            postRepository.delete(post);
        }

        return "redirect:/topic/" + topicId;
    }

    /**
     * スレッド（トピック）を丸ごと削除します（作成者本人限定）。
     *
     * @param id 削除対象のトピックID
     * @param principal ログインユーザー情報
     * @return トップページへのリダイレクト指示
     */
    @PostMapping("/topic/{id}/delete")
    public String deleteTopic(@PathVariable Long id, Principal principal) {
        Topic topic = topicRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("無効なトピックID:" + id));

        // 本人確認を行い、一致する場合のみ削除を実行（Cascade設定により紐づく返信も自動削除）
        if (principal != null && topic.getUser().getUsername().equals(principal.getName())) {
            topicRepository.delete(topic);
        }

        return "redirect:/";
    }

    /**
     * 他ユーザーのプロフィール詳細画面（作成スレッド・返信一覧）を表示します。
     *
     * @param id 閲覧対象のユーザーID
     * @param model 画面引き渡し用モデル
     * @param principal ログインユーザー情報（未ログイン時はnull）
     * @return ユーザープロフィール画面のテンプレート名 ("user_profile")
     */
    @GetMapping("/user/{id}")
    public String showUserProfile(@PathVariable Long id, Model model, Principal principal) {

        // 閲覧対象のユーザー情報を取得
        User targetUser = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("無効なユーザーID:" + id));

        // 対象ユーザーが作成したトピック一覧・返信一覧を取得
        List<Topic> userTopics = topicRepository.findByUserOrderByCreatedAtDesc(targetUser);
        List<Post> userPosts = postRepository.findByUserOrderByCreatedAtDesc(targetUser);

        model.addAttribute("targetUser", targetUser);
        model.addAttribute("userTopics", userTopics);
        model.addAttribute("userPosts", userPosts);

        // 画面を閲覧しているログイン本人の情報を渡す
        if (principal != null) {
            User currentUser = userRepository.findByUsername(principal.getName()).get();
            model.addAttribute("user", currentUser);
        }

        return "user_profile";
    }
}