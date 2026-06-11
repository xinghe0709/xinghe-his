package org.xinghe.xinghehis.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.xinghe.xinghehis.common.Result;
import org.xinghe.xinghehis.entity.Registration;
import org.xinghe.xinghehis.service.RegistrationService;
import org.xinghe.xinghehis.service.dto.RegistrationQuery;

import java.util.Map;

@RestController
@RequestMapping("/api/registrations")
public class RegistrationController {

    private final RegistrationService registrationService;

    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @GetMapping
    public Result<Map<String, Object>> list(RegistrationQuery query) {
        return Result.ok(registrationService.page(query));
    }

    @GetMapping("/{id}")
    public Result<Registration> get(@PathVariable Long id) {
        return Result.ok(registrationService.getById(id));
    }

    @PostMapping
    public Result<Registration> create(@Valid @RequestBody Registration registration) {
        return Result.ok(registrationService.create(registration));
    }

    @PutMapping("/{id}")
    public Result<Registration> update(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return Result.ok(registrationService.updateStatus(id, body.get("status")));
    }
}
