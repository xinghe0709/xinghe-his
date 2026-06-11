package org.xinghe.xinghehis.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.xinghe.xinghehis.common.Result;
import org.xinghe.xinghehis.entity.Registration;
import org.xinghe.xinghehis.service.RegistrationService;
import org.xinghe.xinghehis.service.dto.RegistrationQuery;

import java.util.Map;

/**
 * 挂号管理控制器
 *
 * 挂号员专用接口：
 *   GET  /api/registrations → 挂号列表（支持按状态/日期/科室筛选）
 *   POST /api/registrations → 创建挂号
 *   PUT  /api/registrations/{id} → 修改状态（如取消挂号）
 *
 * 注意：挂号时需指定 patientId + departmentId + doctorId。
 * 前端应提供级联选择：先选科室 → 加载该科室医生 → 选医生。
 */
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

    /**
     * 更新挂号状态
     * 请求体格式：{"status": "CANCELLED"}
     */
    @PutMapping("/{id}")
    public Result<Registration> update(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return Result.ok(registrationService.updateStatus(id, body.get("status")));
    }
}
