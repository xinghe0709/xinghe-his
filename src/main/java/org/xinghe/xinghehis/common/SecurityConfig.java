package org.xinghe.xinghehis.common;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Spring Security 核心配置
 *
 * 采用"无状态 JWT 认证"模式：
 *   - 关闭 CSRF（前后端分离不需要）
 *   - 关闭 Session（使用 JWT，服务器不保存用户状态）
 *   - 自定义 JwtAuthFilter 插入过滤器链，在每次请求时验证 Token
 *
 * 权限设计（基于角色）：
 *   登录接口 /api/auth/login → 所有人可访问
 *   公共接口（科室、医生）→ 所有人可访问
 *   患者管理 → 挂号员、医生
 *   挂号管理 → 仅挂号员
 *   就诊管理 → 仅医生
 *   处方管理 → 医生、药师
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtUtil jwtUtil;

    public SecurityConfig(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    /**
     * BCrypt 密码编码器
     * 用户密码入库前用 BCrypt 加密，登录时用 matches() 比对
     * BCrypt 内置盐值，每次加密结果不同，安全性高
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 安全过滤器链 — Spring Security 的核心配置入口
     * 这里定义了所有安全规则：哪些接口需要认证、哪些角色可以访问等
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // CORS 配置：允许前端开发服务器（5173）和生产环境（80）的跨域请求
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            // 关闭 CSRF：前后端分离 + JWT 认证不需要这个保护
            .csrf(csrf -> csrf.disable())
            // 无状态会话：不创建 HTTP Session，每个请求独立认证
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // 接口权限配置（注意顺序：前面的规则优先匹配）
            .authorizeHttpRequests(auth -> auth
                // 登录接口不需要认证
                .requestMatchers("/api/auth/login").permitAll()
                // 科室和医生列表是公共数据，挂号时需要查询
                .requestMatchers(HttpMethod.GET, "/api/departments", "/api/departments/*/doctors").permitAll()
                // 患者管理：挂号员和医生都可以查看和编辑
                .requestMatchers("/api/patients/**").hasAnyRole("REGISTRAR", "DOCTOR")
                // 挂号管理：只有挂号员可以操作
                .requestMatchers("/api/registrations/**").hasRole("REGISTRAR")
                // 就诊管理：只有医生可以接诊
                .requestMatchers("/api/consultations/**").hasRole("DOCTOR")
                // 处方管理：医生可以开方，药师可以查看
                .requestMatchers("/api/prescriptions/**").hasAnyRole("DOCTOR", "PHARMACIST")
                // 药品查询：所有角色都可以查看
                .requestMatchers(HttpMethod.GET, "/api/medicines/**").hasAnyRole("REGISTRAR", "DOCTOR", "PHARMACIST")
                // 其他所有请求需要认证
                .anyRequest().authenticated()
            )
            // 将 JwtAuthFilter 插入到 UsernamePasswordAuthenticationFilter 之前
            // 这样每个请求到达 Controller 之前都会先验证 JWT
            .addFilterBefore(new JwtAuthFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * CORS 跨域配置
     * 前端开发时 Vite 运行在 localhost:5173，后端在 localhost:8080，
     * 浏览器会阻止跨域请求，需要后端配置允许的源。
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:5173", "http://localhost:80", "http://localhost"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);  // 允许携带认证信息（JWT Token）
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
