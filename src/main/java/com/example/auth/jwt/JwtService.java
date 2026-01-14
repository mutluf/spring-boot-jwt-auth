package com.example.auth.jwt;

public interface JwtService {
    String generateToken(String username);
}
