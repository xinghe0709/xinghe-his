package org.xinghe.xinghehis.controller;

import org.springframework.web.bind.annotation.*;
import org.xinghe.xinghehis.common.Result;
import org.xinghe.xinghehis.entity.Department;
import org.xinghe.xinghehis.entity.Doctor;
import org.xinghe.xinghehis.service.DepartmentService;

import java.util.List;

/**
 * 科室与医生查询控制器
 *
 * 这些接口为公共数据，不需要认证（在 SecurityConfig 中配置 permitAll）。
 * 挂号时挂号员需要查询科室列表和科室下医生列表。
 */
@RestController
@RequestMapping("/api/departments")
public class DepartmentController {

    private final DepartmentService departmentService;

    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    /** 获取所有启用科室列表 */
    @GetMapping
    public Result<List<Department>> list() {
        return Result.ok(departmentService.list());
    }

    /** 获取指定科室下的医生列表 */
    @GetMapping("/{id}/doctors")
    public Result<List<Doctor>> getDoctors(@PathVariable Long id) {
        return Result.ok(departmentService.getDoctors(id));
    }
}
