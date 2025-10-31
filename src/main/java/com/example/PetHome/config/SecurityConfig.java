package com.example.PetHome.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

// 【新增】导入CORS相关的类
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;
import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    @Lazy
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 【新增】启用CORS配置
                .cors(withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/user/register", "/api/user/login").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/pets", "/api/pets/**").permitAll()

                        .requestMatchers(HttpMethod.PUT, "/api/user/profile").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/user/password").authenticated()

                        // 帖子功能的安全规则
                        .requestMatchers(HttpMethod.GET, "/api/posts", "/api/posts/**").permitAll() // 公开查看帖子
                        .requestMatchers(HttpMethod.POST, "/api/posts").authenticated() // 登录后发帖
                        .requestMatchers(HttpMethod.DELETE, "/api/posts/**").authenticated() // 登录后删帖(Service会验证权限)
                        .requestMatchers(HttpMethod.PUT, "/api/posts/**/audit").hasAuthority("ROLE_ADMIN") // 仅管理员审核


                        // 【【【 在这里添加以下 2 行 】】】
                        .requestMatchers(HttpMethod.GET, "/api/applications").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/user/all").hasAuthority("ROLE_ADMIN")


                        .requestMatchers("/uploads/**").permitAll() // 允许公开访问上传的文件
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // 【新增】CORS配置的核心Bean
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // 允许来自您前端开发服务器的请求
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173"));
        // 允许所有常见的HTTP方法
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        // 允许所有请求头
        configuration.setAllowedHeaders(Arrays.asList("*"));
        // 允许浏览器发送Cookie等凭证
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // 对所有URL路径应用这个CORS配置
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}