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

                        .requestMatchers(HttpMethod.GET, "/api/posts/my").authenticated()

                        // 【【【 新增：评论功能 】】】
                        .requestMatchers(HttpMethod.GET, "/api/comments/pet/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/comments").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/comments/all").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/comments/**").authenticated()

                        // 【【【 新增：点赞/浏览功能 】】】
                        .requestMatchers(HttpMethod.POST, "/api/posts/*/like").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/posts/*/view").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/posts/my/likes").authenticated()

                        // 【【【 在这里修复了路径 】】】
                        .requestMatchers(HttpMethod.PUT, "/api/posts/*/audit").hasAuthority("ROLE_ADMIN") // 仅管理员审核 (使用 * 替代 **)

                        // (管理员) 申请和用户列表
                        .requestMatchers(HttpMethod.GET, "/api/applications").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/user/all").hasAuthority("ROLE_ADMIN")

                        .requestMatchers(HttpMethod.GET, "/api/pets", "/api/pets/**", "/api/carousels").permitAll() // 允许公开访问上传的文件
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}