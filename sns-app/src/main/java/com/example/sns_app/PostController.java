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

// 🌟追加したimport
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class PostController {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TopicRepository topicRepository;

    @GetMapping("/")
    public String index(Model model, Principal principal,
                        @RequestParam(name = "keyword", required = false) String keyword,
                        @RequestParam(name = "label", required = false) String label,
                        @PageableDefault(size = 10) Pageable pageable,
                        @RequestHeader(value = "X-Requested-With", required = false) String requestedWith) {

        Page<Topic> topicPage;

        // 検索条件によってデータベースからのデータの取り出し方を変える
        if (keyword != null && !keyword.isEmpty()) {
            topicPage = topicRepository.findByTitleContainingOrderByCreatedAtDesc(keyword, pageable);
        } else if (label != null && !label.isEmpty()) {
            topicPage = topicRepository.findByLabelOrderByCreatedAtDesc(label, pageable);
        } else {
            topicPage = topicRepository.findAllByOrderByCreatedAtDesc(pageable);
        }

        // 画面に渡すデータをセット
        model.addAttribute("topics", topicPage.getContent()); // 現在のページのデータ（最大10件）
        model.addAttribute("hasNext", topicPage.hasNext());   // 次のページがあるかどうか
        model.addAttribute("currentPage", topicPage.getNumber()); // 現在のページ番号
        model.addAttribute("keyword", keyword);
        model.addAttribute("label", label);

        if (principal != null) {
            User user = userRepository.findByUsername(principal.getName()).get();
            model.addAttribute("user", user);
        }

        // 🌟ここが無限スクロールの肝！
        // JavaScriptからのリクエストだった場合、「index.html」の中の「topicList」という部分だけを返す
        if ("XMLHttpRequest".equals(requestedWith)) {
            return "index :: topicList";
        }

        return "index"; // 通常のアクセス時は画面全体を返す
    }

    @PostMapping("/post")
    public String addPost(Post post,Principal principal){
        User user=userRepository.findByUsername(principal.getName())
                .orElseThrow(()->new IllegalArgumentException("ユーザーが見つかりません"));
        post.setUser(user);
        postRepository.save(post);
        return "redirect:/";
    }

    @PostMapping("/delete")
    public String deletePost(Long id){
        postRepository.deleteById(id);
        return "redirect:/";
    }

    // 🌟追加：いいねの追加・解除を行う処理
    @PostMapping("/topic/{id}/like")
    public String toggleLike(@PathVariable Long id, Principal principal, HttpServletRequest request) {
        if (principal == null) {
            return "redirect:/login"; // ログインしていなければログイン画面へ
        }

        User user = userRepository.findByUsername(principal.getName()).get();
        Topic topic = topicRepository.findById(id).orElseThrow();

        // もし既にいいねリストに自分が含まれていたら、削除（いいね解除）
        if (topic.getLikedByUsers().contains(user)) {
            topic.getLikedByUsers().remove(user);
        } else {
            // 含まれていなければ、追加（いいね登録）
            topic.getLikedByUsers().add(user);
        }

        topicRepository.save(topic);

        // 「いいね」を押した元のページ（トップ画面か詳細画面か）にそのまま戻る便利な書き方
        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/");
    }

}