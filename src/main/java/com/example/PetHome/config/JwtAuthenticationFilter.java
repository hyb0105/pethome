// 文件路径: src/main/java/com/example/PetHome/config/JwtAuthenticationFilter.java
package com.example.PetHome.config;

import com.example.PetHome.entity.User;
import com.example.PetHome.service.UserService;
import com.example.PetHome.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserService userService;

    // 【关键修改】 我们不再需要 logger.warn，直接在catch块中处理即可

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            try {
                Claims claims = jwtUtils.getClaimsByToken(token);
                username = claims.getSubject();
            } catch (Exception e) {
                // Token 无效 (例如过期或签名错误)，直接打印到控制台
                System.err.println("JWT Token is expired or invalid: " + e.getMessage());
            }
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            User user = userService.findByUsername(username);

            if (user != null) {
                System.out.println("===== 权限检查: 用户名: " + user.getUsername() + ", 角色: " + user.getRole() + " =====");
                List<GrantedAuthority> authorities = new ArrayList<>();
                if (user.getRole() == 1) {
                    authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
                } else {
                    authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
                }
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        user.getUsername(), null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}