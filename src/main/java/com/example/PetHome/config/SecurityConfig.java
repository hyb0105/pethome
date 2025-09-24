package com.example.PetHome.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 禁用 CSRF，因为在前后端分离项目中我们使用 JWT
                .csrf(AbstractHttpConfigurer::disable)

                // 配置 Session 管理为无状态，不创建和使用 Session
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 配置请求授权规则
                .authorizeHttpRequests(authorize -> authorize
                        // 允许所有人访问 /api/user/register 和 /api/user/login
                        .requestMatchers("/api/user/register", "/api/user/login").permitAll()
                        // 所有其他请求都需要身份验证
                        .anyRequest().authenticated()
                );

        return http.build();
    }
}