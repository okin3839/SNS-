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
        model.addAttribute("posts",postRepository.findAll());
        return "index";
    }

    @PostMapping("/post")
    public String addPost(String content){
        Post post = new Post();
        post.setContent(content);
        postRepository.save(post);
        return "redirect:/";
    }

}
