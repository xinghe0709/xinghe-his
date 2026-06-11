package org.xinghe.xinghehis.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.xinghe.xinghehis.common.Result;
import org.xinghe.xinghehis.entity.Patient;
import org.xinghe.xinghehis.service.PatientService;
import org.xinghe.xinghehis.service.dto.PatientQuery;

import java.util.Map;

/**
 * 患者管理控制器
 *
 * RESTful 风格设计：
 *   GET    /api/patients     → 分页列表（?keyword=张三&page=1&pageSize=20）
 *   GET    /api/patients/{id} → 患者详情
 *   POST   /api/patients     → 新建患者（请求体携带患者信息）
 *   PUT    /api/patients/{id} → 更新患者
 *
 * 访问权限：REGISTRAR（挂号员）、DOCTOR（医生）
 */
@RestController
@RequestMapping("/api/patients")
public class PatientController {

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    /** 分页查询患者列表，PatientQuery 从 URL 查询参数自动绑定 */
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
