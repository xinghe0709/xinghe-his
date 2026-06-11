package org.xinghe.xinghehis.service;

import org.springframework.stereotype.Service;
import org.xinghe.xinghehis.entity.Department;
import org.xinghe.xinghehis.entity.Doctor;
import org.xinghe.xinghehis.mapper.DepartmentMapper;
import org.xinghe.xinghehis.mapper.DoctorMapper;

import java.util.List;

/**
 * 科室与医生查询服务
 *
 * 提供科室列表和科室下医生列表的查询，属于公共数据接口。
 * 挂号时挂号员需要先选科室，再选该科室下的医生。
 * 这些接口不需要认证即可访问（在 SecurityConfig 中配置）。
 */
@Service
public class DepartmentService {

    private final DepartmentMapper departmentMapper;
    private final DoctorMapper doctorMapper;

    public DepartmentService(DepartmentMapper departmentMapper, DoctorMapper doctorMapper) {
        this.departmentMapper = departmentMapper;
        this.doctorMapper = doctorMapper;
    }

    /** 查询所有启用状态的科室 */
    public List<Department> list() {
        return departmentMapper.findAll();
    }

    /** 查询指定科室下的医生列表（仅包含启用账号的医生） */
    public List<Doctor> getDoctors(Long departmentId) {
        return doctorMapper.findByDepartmentId(departmentId);
    }
}
