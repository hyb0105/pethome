package com.example.PetHome.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * 配置密码编码器。我们使用 BCryptPasswordEncoder，它是一种强大的密码哈希算法。
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
                // 禁用 CSRF，因为在前后端分离的 RESTful API 中，我们通常使用 JWT 进行身份验证，而不是 Session。
                .csrf(AbstractHttpConfigurer::disable)

                // 配置会话管理策略为无状态（Stateless），这与 JWT 认证模式相匹配。
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 配置授权规则
                .authorizeHttpRequests(authorize -> authorize
                        // 允许所有对注册和登录接口的请求，无需身份认证。
                        .requestMatchers("/api/user/register", "/api/user/login").permitAll()
                        // 所有其他请求都需要经过身份认证。
                        .anyRequest().authenticated()
                );
        return http.build();
    }
}
