package org.xinghe.xinghehis.common;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * JWT 认证过滤器
 *
 * 继承 OncePerRequestFilter 确保每个请求只执行一次过滤。
 * 在 Spring Security 过滤器链中，此过滤器在 UsernamePasswordAuthenticationFilter 之前执行。
 *
 * 认证流程：
 *   1. 从请求头 Authorization 提取 Bearer Token
 *   2. 解析 JWT 获取 userId、username、role
 *   3. 将用户信息存入 UserContext（供业务层使用）
 *   4. 将认证信息存入 SecurityContextHolder（供 Spring Security 鉴权使用）
 *   5. 请求结束后清理 UserContext
 */
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String token = extractToken(request);

        // Token 存在且未过期时进行认证
        if (StringUtils.hasText(token) && !jwtUtil.isTokenExpired(token)) {
            Claims claims = jwtUtil.parseToken(token);
            Long userId = Long.parseLong(claims.getSubject());
            String username = claims.get("username", String.class);
            String role = claims.get("role", String.class);

            // 存入 ThreadLocal，供 Service 层通过 UserContext 获取
            UserContext.set(userId, username, role);

            // 存入 SecurityContext，Spring Security 用它做权限校验
            // ROLE_ 前缀是 Spring Security 的约定，hasRole("ADMIN") 实际匹配 ROLE_ADMIN
            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(
                            userId, null,
                            List.of(new SimpleGrantedAuthority("ROLE_" + role)));
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        // 继续执行后续过滤器链
        filterChain.doFilter(request, response);

        // 请求结束，清理 ThreadLocal 防止内存泄漏
        UserContext.clear();
    }

    /** 从 Authorization 头提取 Bearer Token */
    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
}
