package org.xinghe.xinghehis.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.xinghe.xinghehis.common.Result;
import org.xinghe.xinghehis.entity.Patient;
import org.xinghe.xinghehis.service.PatientService;
import org.xinghe.xinghehis.service.dto.PatientQuery;

import java.util.Map;

@RestController
@RequestMapping("/api/patients")
public class PatientController {

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @GetMapping
    public Result<Map<String, Object>> list(PatientQuery query) {
        return Result.ok(patientService.page(query));
    }

    @GetMapping("/{id}")
    public Result<Patient> get(@PathVariable Long id) {
        return Result.ok(patientService.getById(id));
    }

    @PostMapping
    public Result<Patient> create(@Valid @RequestBody Patient patient) {
        return Result.ok(patientService.create(patient));
    }

    @PutMapping("/{id}")
    public Result<Patient> update(@PathVariable Long id, @Valid @RequestBody Patient patient) {
        return Result.ok(patientService.update(id, patient));
    }
}
