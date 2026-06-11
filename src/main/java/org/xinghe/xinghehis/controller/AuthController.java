package org.xinghe.xinghehis.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.xinghe.xinghehis.common.Result;
import org.xinghe.xinghehis.common.UserContext;
import org.xinghe.xinghehis.entity.User;
import org.xinghe.xinghehis.service.AuthService;
import org.xinghe.xinghehis.service.dto.LoginRequest;
import org.xinghe.xinghehis.service.dto.LoginResponse;

/**
 * 认证控制器
 *
 * 提供用户登录和当前用户信息查询接口。
 * POST /api/auth/login → 不需要认证（在 SecurityConfig 中配置 permitAll）
 * GET  /api/auth/me   → 需要有效 JWT，返回当前登录用户信息
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * 用户登录
     * @Valid 注解触发 LoginRequest 的参数校验（username 和 password 不能为空）
     */
    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse resp = authService.login(request);
        return Result.ok(resp);
    }

    /**
     * 获取当前登录用户信息
     * 通过 UserContext.getUserId() 从 ThreadLocal 获取当前用户ID
     * 返回前清除密码字段，防止密码泄露
     */
    @GetMapping("/me")
    public Result<User> me() {
        User user = authService.getCurrentUser(UserContext.getUserId());
        user.setPassword(null);  // 安全：不返回密码
        return Result.ok(user);
    }
}
