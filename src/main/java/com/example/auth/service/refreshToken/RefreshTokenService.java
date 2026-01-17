package com.example.auth.service.refreshToken;

import com.example.auth.model.RefreshToken;

public interface RefreshTokenService {
    RefreshToken createRefreshToken(String username);
}
