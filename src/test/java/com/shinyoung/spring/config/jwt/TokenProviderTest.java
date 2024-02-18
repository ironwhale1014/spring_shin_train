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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.util.Date;
import java.util.Map;

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
        User testUser = userRepository.save(User.builder().email("user14@gmail.com").password("test").build());
        String token = tokenProvider.generateToken(testUser, Duration.ofDays(14));

        System.out.println("token = " + token);

        SecretKey secretKey = getSignKey();
        Long userId = Jwts.parser().verifyWith(secretKey).build()
                .parseSignedClaims(token).getPayload().get("id", Long.class);

        assertThat(userId).isEqualTo(testUser.getId());
    }

    @DisplayName("validToken(): 만료된 토큰인 때에 유효성검증에 실패한다.")
    @Test
    void validToken_invalidToken() {
        String token = JwtFactory.builder().expiration(new Date(new Date().getTime() - Duration.ofDays(7).toMillis())).build().createToken(jwtProperties);

        boolean result = tokenProvider.validToken(token);
        assertThat(result).isFalse();
    }

    @DisplayName("getAuthentication(): 토큰 기반으로 인증 정보를 가져올 수 있다.")
    @Test
    void getAuthentication() {
        String userEmail = "user10@gmail.com";
        String token = JwtFactory.builder().subject(userEmail).build().createToken(jwtProperties);
        Authentication authentication = tokenProvider.getAuthentication(token);
        assertThat(((UserDetails) authentication.getPrincipal()).getUsername()).isEqualTo(userEmail);
    }

    @DisplayName("토근으로 유저 아이디를 가져올 수 있다.")
    @Test
    void getUserId() {
        Long userId = 17L;
        String token = JwtFactory.builder().claims(Map.of("id", userId)).build().createToken(jwtProperties);

        Long userIdByToken = tokenProvider.getUserId(token);

        assertThat((userIdByToken)).isEqualTo(userId);
    }
}
