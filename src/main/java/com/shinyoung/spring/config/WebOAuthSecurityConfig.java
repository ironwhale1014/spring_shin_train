package com.shinyoung.spring.config;


import com.shinyoung.spring.config.jwt.TokenProvider;
import com.shinyoung.spring.config.oauth.OAuth2AuthorizationRequestBasedOnCookieRepository;
import com.shinyoung.spring.config.oauth.OAuth2SuccessHandler;
import com.shinyoung.spring.config.oauth.OAuth2UserCustomService;
import com.shinyoung.spring.repository.RefreshTokenRepository;
import com.shinyoung.spring.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;

@Configuration
@RequiredArgsConstructor
public class WebOAuthSecurityConfig {
    private final OAuth2UserCustomService oAuth2UserCustomService;
    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserService userService;

    @Bean
    public WebSecurityCustomizer configure() {
        return (web) -> web.ignoring()
                .requestMatchers(toH2Console())
                .requestMatchers("/img/**", "/css/**", "/js/**");
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable);

        http.sessionManagement((sessionManagement) -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        http.authorizeHttpRequests((authorizeRequests) -> {
            authorizeRequests.requestMatchers("/api/token").permitAll();
            authorizeRequests.requestMatchers("api/**").authenticated();
            authorizeRequests.anyRequest().permitAll();
        });

        http.oauth2Login((oauth2Login) -> {
            oauth2Login.loginPage("/login");
            oauth2Login.authorizationEndpoint((authorizationEndpoint) ->
                    authorizationEndpoint.authorizationRequestRepository(oAuth2AuthorizationRequestBasedOnCookieRepository()));
            oauth2Login.successHandler(oAuth2SuccessHandler());
            oauth2Login.userInfoEndpoint((userInfo) -> userInfo.userService(oAuth2UserCustomService));
        });


        http.logout((logout) -> logout.logoutSuccessUrl("/login"));

        http.exceptionHandling((exceptionHandlingConfig) ->
                exceptionHandlingConfig.defaultAuthenticationEntryPointFor(
                        new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
                        new AntPathRequestMatcher("/api/**")));

        return http.build();

    }

    @Bean
    public OAuth2SuccessHandler oAuth2SuccessHandler() {
        return new OAuth2SuccessHandler(tokenProvider, refreshTokenRepository, oAuth2AuthorizationRequestBasedOnCookieRepository(), userService);
    }

    @Bean
    public TokenAuthenticationFilter tokenAuthenticationFilter() {

        return new TokenAuthenticationFilter(tokenProvider);
    }

    @Bean
    public OAuth2AuthorizationRequestBasedOnCookieRepository oAuth2AuthorizationRequestBasedOnCookieRepository() {
        return new OAuth2AuthorizationRequestBasedOnCookieRepository();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
