package com.example.sns_app;

import com.example.sns_app.entity.Topic;
import com.example.sns_app.repository.TopicRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.UUID;

/**
 * ユーザー登録・ログイン・プロフィール編集などのユーザー管理処理を行うコントローラー
 */
@Controller
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private PostRepository postRepository;

    /**
     * 新規ユーザー登録画面を表示します。
     */
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    /**
     * ログイン画面を表示します。
     */
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    /**
     * 新規ユーザー登録処理を実行します。
     * ニックネームの重複チェックおよびパスワードのハッシュ化を行ってデータベースに保存します。
     */
    @PostMapping("/register")
    public String registerUser(@Validated User user, BindingResult result) {

        // ユーザー名の重複チェック
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            result.rejectValue("username", "error.user", "そのニックネームは既に使われています");
        }

        // バリデーションエラーがある場合は登録画面に戻る
        if (result.hasErrors()) {
            return "register";
        }

        // パスワードを暗号化して保存
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);

        return "redirect:/login";
    }

    /**
     * マイページ（プロフィール画面）を表示します。
     * ログインユーザーの基本情報、作成スレッド、返信履歴、いいねしたスレッドを取得して画面に渡します。
     */
    @GetMapping("/profile")
    public String showProfile(Principal principal, Model model) {
        User user = userRepository.findByUsername(principal.getName()).get();
        model.addAttribute("user", user);

        // ユーザーに関連する各種投稿・アクティビティ履歴を取得
        List<Topic> myTopics = topicRepository.findByUserOrderByCreatedAtDesc(user);
        List<Post> myPosts = postRepository.findByUserOrderByCreatedAtDesc(user);
        List<Topic> likedTopics = topicRepository.findByLikedByUsersContainingOrderByCreatedAtDesc(user);

        model.addAttribute("myTopics", myTopics);
        model.addAttribute("myPosts", myPosts);
        model.addAttribute("likedTopics", likedTopics);

        return "profile";
    }

    /**
     * プロフィール情報の更新処理（名前、パスワード、アイコン画像、自己紹介文）を実行します。
     */
    @PostMapping("/profile/update")
    public String updateProfile(
            Principal principal,
            @RequestParam("username") String newUsername,
            @RequestParam(value = "newPassword", required = false) String newPassword,
            @RequestParam(value = "confirmPassword", required = false) String confirmPassword,
            @RequestParam(value = "currentPassword", required = false) String currentPassword,
            @RequestParam("iconFile") MultipartFile iconFile,
            @RequestParam(value = "bio", required = false) String bio,
            HttpServletRequest request) {

        User user = userRepository.findByUsername(principal.getName()).get();

        // 1. ニックネームの重複チェックと更新
        if (!user.getUsername().equals(newUsername)) {
            if (userRepository.findByUsername(newUsername).isPresent()) {
                return "redirect:/profile?duplicateError";
            }
        }
        user.setUsername(newUsername);

        // 2. パスワードの変更処理
        boolean isPasswordChanged = false;
        if (newPassword != null && !newPassword.isEmpty()) {
            // 現在のパスワード確認
            if (currentPassword == null || !passwordEncoder.matches(currentPassword, user.getPassword())) {
                return "redirect:/profile?currentPasswordError";
            }
            // 新しいパスワードの一致確認
            if (!newPassword.equals(confirmPassword)) {
                return "redirect:/profile?passwordMatchError";
            }
            // 新しいパスワードをハッシュ化してセット
            user.setPassword(passwordEncoder.encode(newPassword));
            isPasswordChanged = true;
        }

        // 3. アイコン画像のアップロード処理
        if (!iconFile.isEmpty()) {
            try {
                String uploadDir = "src/main/resources/static/uploads/";
                Path uploadPath = Paths.get(uploadDir);

                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                // ファイル名の重複防止用にUUIDを付与
                String originalFilename = iconFile.getOriginalFilename();
                String uniqueFileName = UUID.randomUUID().toString() + "_" + originalFilename;

                Path filePath = uploadPath.resolve(uniqueFileName);
                Files.copy(iconFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                user.setProfileImage(uniqueFileName);

            } catch (Exception e) {
                e.printStackTrace();
                return "redirect:/profile?error";
            }
        }

        // 4. 自己紹介文のセット
        user.setBio(bio);

        // データベースに更新保存
        userRepository.save(user);

        // 5. ログインID（ユーザー名）またはパスワードを変更した場合はセッション破棄のため強制ログアウト
        if (!principal.getName().equals(newUsername) || isPasswordChanged) {
            try {
                request.logout();
            } catch (ServletException e) {
                e.printStackTrace();
            }
            return "redirect:/login";
        }

        return "redirect:/profile?success";
    }

}