package com.example.sns_app;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PostRepository  extends JpaRepository<Post, Long>{
    List<Post> findAllByOrderByCreatedAtDesc();
}
