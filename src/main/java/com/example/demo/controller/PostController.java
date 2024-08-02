package com.example.demo.controller;

import com.example.demo.model.Post;
import com.example.demo.model.Profile;
import com.example.demo.service.FriendService;
import com.example.demo.service.PostService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/feed")
    public ResponseEntity<List<Post>> getFeed(@RequestParam int offset, @RequestParam int limit, @RequestHeader("Authorization") UUID token) {
        List<Post> feed = postService.getFeed(offset, limit, token);
        return ResponseEntity.ok(feed);
    }
}
