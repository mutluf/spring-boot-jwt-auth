package com.example.auth.controller;

import com.example.auth.dto.AuthenticationResponse;
import com.example.auth.dto.LoginRequest;
import com.example.auth.dto.RefreshTokenRequest;
import com.example.auth.dto.RegisterRequest;
import com.example.auth.model.RefreshToken;
import com.example.auth.model.User;
import com.example.auth.service.UserService;
import com.example.auth.service.jwt.JwtService;
import com.example.auth.service.refreshToken.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

        private final AuthenticationManager authenticationManager;
        private final UserService userService;
        private final JwtService jwtService;
        private final RefreshTokenService refreshTokenService;

        @PostMapping("/register")
        public ResponseEntity<AuthenticationResponse> register(
                        @RequestBody RegisterRequest request) {
                User user = userService.register(request);
                var jwtToken = jwtService.generateToken(user);
                var refreshToken = refreshTokenService.createRefreshToken(user.getUsername());
                return ResponseEntity.ok(AuthenticationResponse.builder()
                                .accessToken(jwtToken)
                                .refreshToken(refreshToken.getToken())
                                .build());
        }

        @PostMapping("/login")
        public ResponseEntity<AuthenticationResponse> authenticate(
                        @RequestBody LoginRequest request) {
                authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(
                                                request.username(),
                                                request.password()));
                var user = userService.findByUsername(request.username());
                var jwtToken = jwtService.generateToken(user);
                var refreshToken = refreshTokenService.createRefreshToken(user.getUsername());
                return ResponseEntity.ok(AuthenticationResponse.builder()
                                .accessToken(jwtToken)
                                .refreshToken(refreshToken.getToken())
                                .build());
        }

        @PostMapping("/refresh-token")
        public ResponseEntity<AuthenticationResponse> refreshToken(
                        @RequestBody RefreshTokenRequest request) {
                return refreshTokenService.findByToken(request.getToken())
                                .map(refreshTokenService::verifyExpiration)
                                .map(RefreshToken::getUser)
                                .map(user -> {
                                        String accessToken = jwtService.generateToken(user);
                                        return ResponseEntity.ok(AuthenticationResponse.builder()
                                                        .accessToken(accessToken)
                                                        .refreshToken(request.getToken())
                                                        .build());
                                }).orElseThrow(() -> new RuntimeException("Refresh token is not in database!"));
        }

}
