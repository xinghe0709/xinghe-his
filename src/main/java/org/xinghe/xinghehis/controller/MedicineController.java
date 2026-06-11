package org.xinghe.xinghehis.controller;

import org.springframework.web.bind.annotation.*;
import org.xinghe.xinghehis.common.Result;
import org.xinghe.xinghehis.entity.Medicine;
import org.xinghe.xinghehis.service.MedicineService;

import java.util.List;

/**
 * 药品字典控制器
 *
 * 提供药品搜索和详情查询，医生开处方时使用。
 * GET 请求对所有角色开放（在 SecurityConfig 中配置）。
 */
@RestController
@RequestMapping("/api/medicines")
public class MedicineController {

    private final MedicineService medicineService;

    public MedicineController(MedicineService medicineService) {
        this.medicineService = medicineService;
    }

    /** 搜索药品，keyword 为空时返回所有药品 */
    @GetMapping
    public Result<List<Medicine>> search(@RequestParam(required = false) String keyword) {
        return Result.ok(medicineService.search(keyword));
    }

    @GetMapping("/{id}")
    public Result<Medicine> get(@PathVariable Long id) {
        return Result.ok(medicineService.getById(id));
    }
}
