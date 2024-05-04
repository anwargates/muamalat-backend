package com.muamalat.springboot.pojo;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;

    // Constructors can be generated automatically by Lombok
}

