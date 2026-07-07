package com.example.sns_app;

import com.example.sns_app.entity.Topic;
import com.example.sns_app.repository.TopicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
public class TopicController {

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository; // 追加

    // スレッド作成画面を表示
    @GetMapping("/topic/new")
    public String showCreateForm() {
        return "topic_form";
    }

    // 新規スレッド作成
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

    // 【追加】スレッド詳細画面を表示する
    @GetMapping("/topic/{id}")
    public String showTopicDetail(@PathVariable Long id, Model model, Principal principal) {
        Topic topic = topicRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("無効なトピックID:" + id));

        model.addAttribute("topic", topic);

        if (principal != null) {
            User user = userRepository.findByUsername(principal.getName()).get();
            model.addAttribute("user", user);
        }
        return "topic_detail"; // topic_detail.html を表示
    }

    // 【追加】スレッド内に返信（コメント）を投稿する
    @PostMapping("/topic/{id}/reply")
    public String replyToTopic(@PathVariable Long id,
                               @RequestParam String content,
                               Principal principal) {
        Topic topic = topicRepository.findById(id).get();
        User user = userRepository.findByUsername(principal.getName()).get();

        Post post = new Post();
        post.setContent(content);
        post.setTopic(topic); // どのスレッドへの返信かをセット
        post.setUser(user);   // 誰が書いたかをセット

        postRepository.save(post);
        return "redirect:/topic/" + id; // 再度そのスレッド詳細画面にリダイレクト
    }
    // 【追加】返信（コメント）を削除する
    @PostMapping("/topic/{topicId}/post/{postId}/delete")
    public String deletePost(@PathVariable Long topicId,
                             @PathVariable Long postId,
                             Principal principal) {

        // データベースから削除したい返信を探す
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("無効な返信ID:" + postId));

        // 念のため、サーバー側でも「本当に本人が消そうとしているか」を確認する（セキュリティ対策）
        if (principal != null && post.getUser().getUsername().equals(principal.getName())) {
            postRepository.delete(post);
        }

        // 削除が終わったら、元のスレッド詳細画面に戻る
        return "redirect:/topic/" + topicId;
    }
    // 【追加】スレッド（トピック）を丸ごと削除する
    @PostMapping("/topic/{id}/delete")
    public String deleteTopic(@PathVariable Long id, Principal principal) {
        // 削除対象のスレッドを探す
        Topic topic = topicRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("無効なトピックID:" + id));

        // 作成者本人か確認（セキュリティ対策）
        if (principal != null && topic.getUser().getUsername().equals(principal.getName())) {
            // cascade = CascadeType.ALL が効いているため、紐づく返信も自動で全削除されます
            topicRepository.delete(topic);
        }

        // 削除した後はトップ画面（一覧）に戻る
        return "redirect:/";
    }
}