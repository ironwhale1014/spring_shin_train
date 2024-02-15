package com.shinyoung.spring.service;


import com.shinyoung.spring.domain.User;
import com.shinyoung.spring.dto.AddUserRequest;
import com.shinyoung.spring.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public Long save(AddUserRequest dto) {
        return userRepository.save(User.builder().email(dto.getEmail()).password(bCryptPasswordEncoder.encode(dto.getPassword())).build()).getId();
    }
}
