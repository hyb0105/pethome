package com.example.PetHome.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * 配置密码编码器。Spring Security 默认会注入一个 PasswordEncoder Bean。
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 配置安全过滤器链，这是 Spring Security 的核心配置。
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 禁用 CSRF，因为在前后端分离的 RESTful API 中，我们通常使用 JWT 进行身份验证。
                .csrf(AbstractHttpConfigurer::disable)

                // 配置会话管理策略为无状态，这与 JWT 认证模式相匹配。
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 配置请求授权规则
                .authorizeHttpRequests(authorize -> authorize
                        .anyRequest().authenticated() // 所有其他请求都需要身份验证
                );
        return http.build();
    }

    /**
     * WebSecurityCustomizer 配置可以明确告诉 Spring Security 忽略某些请求，
     * 从而确保它们不会进入安全过滤器链。这对于公共接口非常有用。
     */
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers(
                new AntPathRequestMatcher("/api/user/register"),
                new AntPathRequestMatcher("/api/user/login")
        );
    }
}