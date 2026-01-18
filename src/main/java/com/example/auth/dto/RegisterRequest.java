package com.example.auth.dto;

import com.example.auth.model.Role;

public record RegisterRequest(String username, String password, Role role){}
