package org.xinghe.xinghehis.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.xinghe.xinghehis.common.Result;
import org.xinghe.xinghehis.common.UserContext;
import org.xinghe.xinghehis.entity.User;
import org.xinghe.xinghehis.service.AuthService;
import org.xinghe.xinghehis.service.dto.LoginRequest;
import org.xinghe.xinghehis.service.dto.LoginResponse;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse resp = authService.login(request);
        return Result.ok(resp);
    }

    @GetMapping("/me")
    public Result<User> me() {
        User user = authService.getCurrentUser(UserContext.getUserId());
        user.setPassword(null);
        return Result.ok(user);
    }
}
