package com.muamalat.springboot.repository;

import com.muamalat.springboot.entity.OurUser;
import com.muamalat.springboot.entity.UserActivity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserActivityRepository extends JpaRepository<UserActivity, Long> {
}

