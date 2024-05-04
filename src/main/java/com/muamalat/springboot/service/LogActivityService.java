package com.muamalat.springboot.service;

import com.muamalat.springboot.entity.OurUser;
import com.muamalat.springboot.entity.UserActivity;
import com.muamalat.springboot.pojo.ReqRes;
import com.muamalat.springboot.repository.OurUserRepository;
import com.muamalat.springboot.repository.UserActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class LogActivityService {

    @Autowired
    private UserActivityRepository userActivityRepository;

    public void recordActivity(Long userId, String email, String activityType) {
        UserActivity userActivity = new UserActivity();
        userActivity.setUserId(userId);
        userActivity.setEmail(email);
        userActivity.setActivityType(activityType);
        userActivityRepository.save(userActivity);
    }

    public void recordActivity(String email, String activityType) {
        UserActivity userActivity = new UserActivity();
        userActivity.setEmail(email);
        userActivity.setActivityType(activityType);
        userActivityRepository.save(userActivity);
    }
}

