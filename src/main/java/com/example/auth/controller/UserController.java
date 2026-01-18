package com.example.auth.controller;

import com.example.auth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/promote/{username}")
    public ResponseEntity<Void> promoteToAdmin(@PathVariable String username) {
        userService.promoteToAdmin(username);
        return ResponseEntity.ok().build();
    }
}
