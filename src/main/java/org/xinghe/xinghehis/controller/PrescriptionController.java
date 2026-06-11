package org.xinghe.xinghehis.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.xinghe.xinghehis.common.Result;
import org.xinghe.xinghehis.entity.Prescription;
import org.xinghe.xinghehis.service.PrescriptionService;
import org.xinghe.xinghehis.service.dto.PrescriptionCreateRequest;

import java.util.List;

@RestController
@RequestMapping("/api/prescriptions")
public class PrescriptionController {

    private final PrescriptionService prescriptionService;

    public PrescriptionController(PrescriptionService prescriptionService) {
        this.prescriptionService = prescriptionService;
    }

    @PostMapping
    public Result<Prescription> create(@Valid @RequestBody PrescriptionCreateRequest request) {
        return Result.ok(prescriptionService.create(request));
    }

    @GetMapping("/{id}")
    public Result<Prescription> get(@PathVariable Long id) {
        return Result.ok(prescriptionService.getById(id));
    }

    @GetMapping
    public Result<Object> findByRegistration(@RequestParam(required = false) Long registrationId) {
        if (registrationId != null) {
            return Result.ok(prescriptionService.getByRegistrationId(registrationId));
        }
        return Result.ok(List.of());
    }
}
