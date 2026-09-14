package com.example.sns_app;

import com.example.sns_app.entity.Topic;
import com.example.sns_app.repository.TopicRepository;
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
import java.util.List;
import java.util.UUID;
import java.security.Principal;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;

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

    @GetMapping("/register")
    public String showRegisterForm(Model model){
        model.addAttribute("user",new User());
        return "register";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping("/register")
    public String registerUser(@Validated User user,BindingResult result){

        if(userRepository.findByUsername(user.getUsername()).isPresent()){
            result.rejectValue("username","error.user","そのニックネームは既に使われています");
        }
        if(result.hasErrors()){
            return "register";
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return "redirect:/login";
    }

    @GetMapping("/profile")
    public String showProfile(Principal principal, Model model) {
        User user = userRepository.findByUsername(principal.getName()).get();
        model.addAttribute("user", user);

        List<Topic> myTopics = topicRepository.findByUserOrderByCreatedAtDesc(user);
        List<Post> myPosts = postRepository.findByUserOrderByCreatedAtDesc(user);

        // 🌟ここを追加：自分がいいねしたスレッド一覧を取得
        List<Topic> likedTopics = topicRepository.findByLikedByUsersContainingOrderByCreatedAtDesc(user);

        model.addAttribute("myTopics", myTopics);
        model.addAttribute("myPosts", myPosts);
        model.addAttribute("likedTopics", likedTopics); // 🌟ここを追加

        return "profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(
            Principal principal,
            @RequestParam("username") String newUsername,
            @RequestParam(value = "newPassword", required = false) String newPassword,
            @RequestParam(value = "confirmPassword", required = false) String confirmPassword,
            @RequestParam(value = "currentPassword", required = false) String currentPassword,
            @RequestParam("iconFile") MultipartFile iconFile,
            @RequestParam(value = "bio", required = false) String bio, // 🌟ここを追加しました！
            HttpServletRequest request) {

        User user = userRepository.findByUsername(principal.getName()).get();

        // 1. 名前の重複チェック
        if (!user.getUsername().equals(newUsername)) {
            if (userRepository.findByUsername(newUsername).isPresent()) {
                return "redirect:/profile?duplicateError";
            }
        }
        user.setUsername(newUsername);

        // 2. パスワードの変更処理
        boolean isPasswordChanged = false;
        if (newPassword != null && !newPassword.isEmpty()) {
            if (currentPassword == null || !passwordEncoder.matches(currentPassword, user.getPassword())) {
                return "redirect:/profile?currentPasswordError";
            }
            if (!newPassword.equals(confirmPassword)) {
                return "redirect:/profile?passwordMatchError";
            }
            user.setPassword(passwordEncoder.encode(newPassword));
            isPasswordChanged = true;
        }

        // 3. 画像のアップロード処理
        if (!iconFile.isEmpty()) {
            try {
                String uploadDir = "src/main/resources/static/uploads/";
                Path uploadPath = Paths.get(uploadDir);

                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

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

        // 🌟自己紹介文をセット
        user.setBio(bio);

        userRepository.save(user);

        // 4. ログインID（名前）かパスワードを変更した場合は、強制的にログアウトさせる
        if (!principal.getName().equals(newUsername) || isPasswordChanged) {
            try {
                request.logout(); // 強制ログアウト！
            } catch (ServletException e) {
                e.printStackTrace();
            }
            // ログアウトした後は、ログイン画面に飛ばす
            return "redirect:/login";
        }

        return "redirect:/profile?success";
    }

}