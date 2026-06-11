package org.xinghe.xinghehis.controller;

import org.springframework.web.bind.annotation.*;
import org.xinghe.xinghehis.common.Result;
import org.xinghe.xinghehis.entity.Medicine;
import org.xinghe.xinghehis.service.MedicineService;

import java.util.List;

@RestController
@RequestMapping("/api/medicines")
public class MedicineController {

    private final MedicineService medicineService;

    public MedicineController(MedicineService medicineService) {
        this.medicineService = medicineService;
    }

    @GetMapping
    public Result<List<Medicine>> search(@RequestParam(required = false) String keyword) {
        return Result.ok(medicineService.search(keyword));
    }

    @GetMapping("/{id}")
    public Result<Medicine> get(@PathVariable Long id) {
        return Result.ok(medicineService.getById(id));
    }
}
