package com.example.sns_app;

import java.security.Principal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class PostController {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public String index(Model model,Principal principal){
        model.addAttribute("posts",postRepository.findAllByOrderByCreatedAtDesc());
        User user = userRepository.findByUsername(principal.getName()).get();
        model.addAttribute("user", user);
        return "index";
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

}
