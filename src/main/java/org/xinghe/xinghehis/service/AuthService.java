package org.xinghe.xinghehis.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.xinghe.xinghehis.common.JwtUtil;
import org.xinghe.xinghehis.entity.User;
import org.xinghe.xinghehis.mapper.UserMapper;
import org.xinghe.xinghehis.service.dto.LoginRequest;
import org.xinghe.xinghehis.service.dto.LoginResponse;

/**
 * 认证服务
 *
 * 负责用户登录验证和当前用户信息查询。
 * 登录流程：用户名查询用户 → BCrypt 密码比对 → 生成 JWT → 返回 token
 *
 * 依赖：
 *   - UserMapper：查询用户数据
 *   - PasswordEncoder：BCrypt 密码验证
 *   - JwtUtil：生成 JWT Token
 */
@Service
public class AuthService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserMapper userMapper, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    /**
     * 用户登录
     *
     * 安全考虑：
     *   - 用户不存在和密码错误返回相同的错误信息，防止用户名枚举攻击
     *   - 禁用用户（status=0）无法登录
     */
    public LoginResponse login(LoginRequest request) {
        User user = userMapper.findByUsername(request.getUsername());
        if (user == null || user.getStatus() == 0) {
            throw new RuntimeException("用户名或密码错误");
        }
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
        return new LoginResponse(token, user.getRealName(), user.getRole());
    }

    /** 获取当前登录用户信息（密码字段已在上层设为 null） */
    public User getCurrentUser(Long userId) {
        return userMapper.findById(userId);
    }
}
