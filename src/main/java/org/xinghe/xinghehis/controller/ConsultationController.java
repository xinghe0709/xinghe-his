package org.xinghe.xinghehis.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.xinghe.xinghehis.common.Result;
import org.xinghe.xinghehis.entity.Registration;
import org.xinghe.xinghehis.service.RegistrationService;
import org.xinghe.xinghehis.service.dto.ConsultationUpdateRequest;
import org.xinghe.xinghehis.service.dto.RegistrationQuery;

import java.util.Map;

@RestController
@RequestMapping("/api/consultations")
public class ConsultationController {

    private final RegistrationService registrationService;

    public ConsultationController(RegistrationService registrationService) {
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

    @PutMapping("/{id}")
    public Result<Registration> update(@PathVariable Long id,
                                       @Valid @RequestBody ConsultationUpdateRequest request) {
        return Result.ok(registrationService.consult(id,
                request.getChiefComplaint(), request.getDiagnosis()));
    }
}
