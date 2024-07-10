package com.muamalat.springboot.controller;

import com.muamalat.springboot.pojo.AuthResponse;
import com.muamalat.springboot.pojo.LoginRequest;
import com.muamalat.springboot.pojo.ReqRes;
import com.muamalat.springboot.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<ReqRes> signUp(@ModelAttribute ReqRes signUpRequest,
                                         @RequestPart(value = "profilePicture", required = false) MultipartFile profilePicture) {
        return authService.signUp(signUpRequest, profilePicture);
    }

    @PostMapping("/signin")
    public ResponseEntity<ReqRes> signIn(@ModelAttribute ReqRes signInRequest) {
        return authService.signIn(signInRequest);
    }

    @PostMapping("/refresh")
    public ResponseEntity<ReqRes> refreshToken(@RequestBody ReqRes refreshTokenRequest) {
        return authService.refreshToken(refreshTokenRequest);
    }
}
