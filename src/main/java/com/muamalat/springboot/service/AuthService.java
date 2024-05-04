package com.muamalat.springboot.service;

import com.muamalat.springboot.entity.OurUser;
import com.muamalat.springboot.entity.UserActivity;
import com.muamalat.springboot.pojo.ReqRes;
import com.muamalat.springboot.repository.OurUserRepository;
import com.muamalat.springboot.repository.UserActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class AuthService {

    @Autowired
    private OurUserRepository ourUserRepository;

    @Autowired
    private JWTUtils jwtUtils;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private LogActivityService logActivityService;

    public ResponseEntity<ReqRes> signUp(ReqRes registrationRequest) {
        ReqRes resp = new ReqRes();
        UserActivity userActivity = new UserActivity();
        try {
            OurUser ourUser = new OurUser();
            ourUser.setEmail(registrationRequest.getEmail());
            ourUser.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
            ourUser.setRole(registrationRequest.getRole());
            ourUser.setName(registrationRequest.getName());
            OurUser ourUserResult = ourUserRepository.save(ourUser);
            if (ourUserResult != null && ourUserResult.getId() > 0) {
                resp.setUsers(ourUserResult);
                resp.setMessage("User saved successfully");
                resp.setStatusCode(200);

                // record activity
                logActivityService.recordActivity(
                        ourUser.getId(),
                        ourUser.getEmail(),
                        "SIGN UP SUCCESS"
                );
                return ResponseEntity.ok().body(resp);
            }
            return ResponseEntity.ok().body(resp);
        } catch (Exception e) {
            resp.setMessage(e.getMessage());
            resp.setStatusCode(500);

            // record activity
            logActivityService.recordActivity(
                    registrationRequest.getEmail(),
                    "SIGN UP FAILED"
            );
            return ResponseEntity.internalServerError().body(resp);
        }
    }

    public ResponseEntity<ReqRes> signIn(ReqRes signInRequest) {
        ReqRes resp = new ReqRes();
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(signInRequest.getEmail(), signInRequest.getPassword()));
            var user = ourUserRepository.findByEmail(signInRequest.getEmail()).orElseThrow();
            System.out.println("USER IS: " + user);
            var jwt = jwtUtils.generateToken(user);
            var refreshToken = jwtUtils.generateRefreshToken(new HashMap<>(), user);
            resp.setMessage("Successfully Signed In");
            resp.setStatusCode(200);
            resp.setName(user.getName());
            resp.setToken(jwt);
            resp.setRefreshToken(refreshToken);
            resp.setExpirationTime("25Hr");

            // record activity
            logActivityService.recordActivity(
                    user.getId(),
                    user.getEmail(),
                    "SIGN IN SUCCESS"
            );
            return ResponseEntity.ok().body(resp);
        } catch (Exception e) {
            resp.setMessage(e.getMessage());
            resp.setStatusCode(500);

            // record activity
            logActivityService.recordActivity(
                    signInRequest.getEmail(),
                    "SIGN IN FAILED"
            );
            return ResponseEntity.internalServerError().body(resp);
        }
    }

    public ResponseEntity<ReqRes> refreshToken(ReqRes refreshTokenRequest) {
        ReqRes resp = new ReqRes();
        String ourEmail = jwtUtils.extractUsername(refreshTokenRequest.getToken());
        OurUser users = ourUserRepository.findByEmail(ourEmail).orElseThrow();
        if (jwtUtils.isTokenValid(refreshTokenRequest.getToken(), users)) {
            var jwt = jwtUtils.generateToken(users);
            resp.setMessage("Successfully Refreshed Token");
            resp.setStatusCode(200);
            resp.setToken(jwt);
            resp.setRefreshToken(refreshTokenRequest.getToken());
            resp.setExpirationTime("25Hr");

            // record activity
            logActivityService.recordActivity(
                    users.getId(),
                    users.getEmail(),
                    "REFRESH TOKEN SUCCESS"
            );
            return ResponseEntity.ok().body(resp);
        } else {
            resp.setStatusCode(500);

            // record activity
            logActivityService.recordActivity(
                    ourEmail,
                    "REFRESH TOKEN FAILED"
            );
            return ResponseEntity.internalServerError().body(resp);
        }
    }
}

