package com.back.domain.post.post.controller;

import com.back.domain.post.post.entity.Post;
import com.back.domain.post.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController // @Controller + @ResponseBody
@RequiredArgsConstructor
public class ApiV1PostController {
    private final PostService postService;

    @GetMapping("/api/v1/posts")
    public List<Post> getItems() {
        List<Post> items = postService.findAll();

        return items;
    }
}
