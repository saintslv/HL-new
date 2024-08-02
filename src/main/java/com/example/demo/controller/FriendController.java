package com.example.demo.controller;

import com.example.demo.model.Profile;
import com.example.demo.service.FriendService;
import com.example.demo.service.ProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/friend")
public class FriendController {

    private final FriendService friendService;

    public FriendController(FriendService friendService) {
        this.friendService = friendService;
    }

    @PostMapping("/add")
    public ResponseEntity<Void>  addFriend(@RequestBody UUID id, @RequestHeader("Authorization") UUID token) {
        friendService.addFriend(id, token);
        return ResponseEntity.ok().build();
    }

}
