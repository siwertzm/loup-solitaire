package com.loupsolitaire.backend.request;

import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String password;
}
