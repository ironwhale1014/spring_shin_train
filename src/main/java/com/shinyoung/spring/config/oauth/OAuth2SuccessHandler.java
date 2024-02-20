package com.shinyoung.spring.config.oauth;


import com.shinyoung.spring.config.jwt.TokenProvider;
import com.shinyoung.spring.domain.RefreshToken;
import com.shinyoung.spring.domain.User;
import com.shinyoung.spring.repository.RefreshTokenRepository;
import com.shinyoung.spring.service.UserService;
import com.shinyoung.spring.util.CookieUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.time.Duration;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    public static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";
    public static final Duration REFRESH_TOKEN_DURATION = Duration.ofDays(14);
    public static final Duration ACCESS_TOKEN_DURATION = Duration.ofDays(1);
    public static final String REDIRECT_PATH = "/articles";


    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final OAuth2AuthorizationRequestBasedOnCookieRepository authorizationRequestBasedOnCookieRepository;
    private final UserService userService;


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        User user = userService.findByEmail((String) oAuth2User.getAttributes().get("email"));
        String refreshToken = tokenProvider.generateToken(user, REFRESH_TOKEN_DURATION);

        saveRefreshToken(user.getId(), refreshToken);
        addRefreshTokenToCookie(request, response, refreshToken);

        String accessToken = tokenProvider.generateToken(user, ACCESS_TOKEN_DURATION);
        String targetUrl = getTargetUrl(accessToken);

        clearAuthenticationAttributes(request, response);

        getRedirectStrategy().sendRedirect(request, response, targetUrl);

    }


    private void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        authorizationRequestBasedOnCookieRepository.removeAuthorizationRequestCookies(request, response);

    }

    private String getTargetUrl(String token) {
        return UriComponentsBuilder.fromUriString(REDIRECT_PATH).queryParam("token", token).build().toString();
    }

    private void addRefreshTokenToCookie(HttpServletRequest request, HttpServletResponse response, String refreshToken) {
        int cookieMaxAge = (int) REFRESH_TOKEN_DURATION.toSeconds();
        CookieUtil.deleteCookies(request, response, REFRESH_TOKEN_COOKIE_NAME);
        CookieUtil.addCookies(response, REFRESH_TOKEN_COOKIE_NAME, refreshToken, cookieMaxAge);

    }

    private void saveRefreshToken(Long userId, String newRefreshToken) {
        RefreshToken refreshToken = refreshTokenRepository.findByUserId(userId)
                .map(entity -> entity.update(newRefreshToken)).orElse(new RefreshToken(userId, newRefreshToken));
        refreshTokenRepository.save(refreshToken);

    }
}
