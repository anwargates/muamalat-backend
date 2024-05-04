package com.muamalat.springboot.repository;

import com.muamalat.springboot.entity.OurUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OurUserRepository extends JpaRepository<OurUser, Long> {
    Optional<OurUser> findByEmail(String email);
}

