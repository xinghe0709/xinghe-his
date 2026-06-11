package org.xinghe.xinghehis.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.xinghe.xinghehis.common.Result;
import org.xinghe.xinghehis.entity.Prescription;
import org.xinghe.xinghehis.service.PrescriptionService;
import org.xinghe.xinghehis.service.dto.PrescriptionCreateRequest;

import java.util.List;

/**
 * 处方管理控制器
 *
 * 医生和药师可访问：
 *   POST /api/prescriptions → 开具处方（含明细）
 *   GET  /api/prescriptions/{id} → 处方详情
 *   GET  /api/prescriptions?registrationId={id} → 按就诊查处方
 *
 * 处方开具时，每条明细需指定 medicineId、dosage、quantity。
 * 药品价格由服务端自动从 Medicine 表获取并快照存储。
 */
@RestController
@RequestMapping("/api/prescriptions")
public class PrescriptionController {

    private final PrescriptionService prescriptionService;

    public PrescriptionController(PrescriptionService prescriptionService) {
        this.prescriptionService = prescriptionService;
    }

    /**
     * 开具处方
     * 请求体示例：
     * {
     *   "registrationId": 1,
     *   "remark": "饭后服用",
     *   "items": [
     *     {"medicineId": 1, "dosage": "口服 一日三次 一次一粒", "quantity": 2},
     *     {"medicineId": 3, "dosage": "口服 一日两次 一次一片", "quantity": 1}
     *   ]
     * }
     */
    @PostMapping
    public Result<Prescription> create(@Valid @RequestBody PrescriptionCreateRequest request) {
        return Result.ok(prescriptionService.create(request));
    }

    @GetMapping("/{id}")
    public Result<Prescription> get(@PathVariable Long id) {
        return Result.ok(prescriptionService.getById(id));
    }

    /** 按就诊 ID 查询处方，用于查看某次就诊的处方信息 */
    @GetMapping
    public Result<Object> findByRegistration(@RequestParam(required = false) Long registrationId) {
        if (registrationId != null) {
            return Result.ok(prescriptionService.getByRegistrationId(registrationId));
        }
        return Result.ok(List.of());
    }
}
