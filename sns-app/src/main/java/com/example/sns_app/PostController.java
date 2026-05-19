package com.example.sns_app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class PostController {

    @Autowired
    private PostRepository postRepository;

    @GetMapping
    public String index(Model model){
        model.addAttribute("posts",postRepository.findAllByOrderByCreatedAtDesc());
        return "index";
    }

    @PostMapping("/post")
    public String addPost(Post post){
        postRepository.save(post);
        return "redirect:/";
    }

    @PostMapping("/delete")
    public String deletePost(Long id){
        postRepository.deleteById(id);
        return "redirect:/";
    }

}
