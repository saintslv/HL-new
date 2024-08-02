package com.example.demo.service;

import com.example.demo.exception.UnauthorizedException;
import com.example.demo.model.Post;
import com.example.demo.repository.PostRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.lettuce.core.api.sync.RedisCommands;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import com.fasterxml.jackson.core.JsonProcessingException;

@Service
public class PostService {
    private final PostRepository postRepository;
    private final AuthService authService;
    private final RedisCommands<String, String> redisCommands;
    private final ObjectMapper objectMapper;
    private static final int CACHE_TTL_SECONDS = 3600; // 1 hour

    public PostService(PostRepository postRepository, AuthService authService, RedisCommands<String, String> redisCommands) {
        this.postRepository = postRepository;
        this.authService = authService;
        this.redisCommands = redisCommands;

        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }
    public List<Post> getFeed(int offset, int limit, UUID token) {
        UUID userId = authService.findUserByToken(token);
        if (userId == null) {
            throw new UnauthorizedException("Invalid or expired token");
        }
        String cacheKey = "user:" + userId + ":feed";
        List<String> cachedFeed = redisCommands.lrange(cacheKey, offset, offset + limit - 1);
        if (!cachedFeed.isEmpty()) {
            try {
                return cachedFeed.stream()
                        .map(json -> {
                            try {
                                return objectMapper.readValue(json, Post.class);
                            } catch (JsonProcessingException e) {
                                e.printStackTrace();
                                return null;
                            }
                        })
                        .filter(post -> post != null) // Filter out any null posts
                        .toList();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        List<Post> feed = postRepository.getFeed(0, 1000, userId);
        try {
            for (Post post : feed) {
                String jsonPost = objectMapper.writeValueAsString(post);
                redisCommands.rpush(cacheKey, jsonPost);
            }
            redisCommands.ltrim(cacheKey, 0, 999);
            redisCommands.expire(cacheKey, CACHE_TTL_SECONDS);
            return feed.subList(offset, Math.min(offset + limit, feed.size()));

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return feed.subList(offset, Math.min(offset + limit, feed.size()));
    }
}
