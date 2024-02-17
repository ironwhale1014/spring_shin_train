package com.shinyoung.spring.config.jwt;

import com.shinyoung.spring.domain.User;
import com.shinyoung.spring.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import javax.crypto.SecretKey;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class TokenProviderTest {
    @Autowired
    private TokenProvider tokenProvider;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtProperties jwtProperties;

    private SecretKey getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtProperties.getSecretKey());
        return Keys.hmacShaKeyFor(keyBytes);
    }


    @DisplayName("generateToken(): 유저 정보와 만료 기간을 전달해 토큰을 만들 수 있다.")
    @Test
    void generateToken() {
        User testUser = userRepository.save(User.builder().email("user10@gmail.com").password("test").build());
        String token = tokenProvider.generateToken(testUser, Duration.ofDays(14));

        System.out.println("token = " + token);

        SecretKey secretKey = getSignKey();
        Long userId = Jwts.parser().verifyWith(secretKey).build()
                .parseSignedClaims(token).getPayload().get("id", Long.class);

        assertThat(userId).isEqualTo(testUser.getId());
    }
}
