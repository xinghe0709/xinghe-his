package org.xinghe.xinghehis.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.xinghe.xinghehis.common.JwtUtil;
import org.xinghe.xinghehis.entity.User;
import org.xinghe.xinghehis.mapper.UserMapper;
import org.xinghe.xinghehis.service.dto.LoginRequest;
import org.xinghe.xinghehis.service.dto.LoginResponse;

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

    public User getCurrentUser(Long userId) {
        return userMapper.findById(userId);
    }
}
