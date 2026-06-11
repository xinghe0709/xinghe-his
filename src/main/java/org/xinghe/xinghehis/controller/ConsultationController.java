package org.xinghe.xinghehis.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.xinghe.xinghehis.common.Result;
import org.xinghe.xinghehis.entity.Registration;
import org.xinghe.xinghehis.service.RegistrationService;
import org.xinghe.xinghehis.service.dto.ConsultationUpdateRequest;
import org.xinghe.xinghehis.service.dto.RegistrationQuery;

import java.util.Map;

/**
 * 就诊管理控制器
 *
 * 医生专用接口（在 SecurityConfig 中配置为 hasRole("DOCTOR")）：
 *   GET  /api/consultations      → 看诊列表（按状态筛选：WAITING=待诊 / COMPLETED=已诊）
 *   GET  /api/consultations/{id} → 就诊详情（含患者信息）
 *   PUT  /api/consultations/{id} → 填写主诉和诊断（接诊操作）
 *
 * 此 Controller 本质上是 Registration 的"医生视角"，
 * 复用了 RegistrationService 的逻辑，但权限和语义不同。
 */
@RestController
@RequestMapping("/api/consultations")
public class ConsultationController {

    private final RegistrationService registrationService;

    public ConsultationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    /** 查询看诊列表（默认返回当前医生相关的挂号记录） */
    @GetMapping
    public Result<Map<String, Object>> list(RegistrationQuery query) {
        return Result.ok(registrationService.page(query));
    }

    @GetMapping("/{id}")
    public Result<Registration> get(@PathVariable Long id) {
        return Result.ok(registrationService.getById(id));
    }

    /**
     * 接诊：填写主诉和诊断，完成后状态自动变为 COMPLETED
     * 请求体：{"chiefComplaint": "头痛三天", "diagnosis": "上呼吸道感染"}
     */
    @PutMapping("/{id}")
    public Result<Registration> update(@PathVariable Long id,
                                       @Valid @RequestBody ConsultationUpdateRequest request) {
        return Result.ok(registrationService.consult(id,
                request.getChiefComplaint(), request.getDiagnosis()));
    }
}
