package com.shinyoung.spring.config.oauth;

import com.shinyoung.spring.domain.User;
import com.shinyoung.spring.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@RequiredArgsConstructor
@Service
public class OAuth2UserCustomService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User user = super.loadUser(userRequest);

        saveOrUpdate(user);

        return user;
    }

    private User saveOrUpdate(OAuth2User oAuth2User) {
        Map<String, Object> attribute = oAuth2User.getAttributes();

        String email = (String) attribute.get("email");
        String name = (String) attribute.get("name");

        User user = userRepository.findByEmail(email).map(entity -> entity.update(name))
                .orElse(User.builder().email(email).nickname(name).build());

        return userRepository.save(user);
    }
}
