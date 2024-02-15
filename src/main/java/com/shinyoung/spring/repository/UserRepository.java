package com.shinyoung.spring.repository;

import com.shinyoung.spring.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
   Optional<User> findByEmail(String email);
}
