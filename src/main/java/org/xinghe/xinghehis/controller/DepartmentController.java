package org.xinghe.xinghehis.controller;

import org.springframework.web.bind.annotation.*;
import org.xinghe.xinghehis.common.Result;
import org.xinghe.xinghehis.entity.Department;
import org.xinghe.xinghehis.entity.Doctor;
import org.xinghe.xinghehis.service.DepartmentService;

import java.util.List;

@RestController
@RequestMapping("/api/departments")
public class DepartmentController {

    private final DepartmentService departmentService;

    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @GetMapping
    public Result<List<Department>> list() {
        return Result.ok(departmentService.list());
    }

    @GetMapping("/{id}/doctors")
    public Result<List<Doctor>> getDoctors(@PathVariable Long id) {
        return Result.ok(departmentService.getDoctors(id));
    }
}
