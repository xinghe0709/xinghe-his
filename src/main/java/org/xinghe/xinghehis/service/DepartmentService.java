package org.xinghe.xinghehis.service;

import org.springframework.stereotype.Service;
import org.xinghe.xinghehis.entity.Department;
import org.xinghe.xinghehis.entity.Doctor;
import org.xinghe.xinghehis.mapper.DepartmentMapper;
import org.xinghe.xinghehis.mapper.DoctorMapper;

import java.util.List;

@Service
public class DepartmentService {

    private final DepartmentMapper departmentMapper;
    private final DoctorMapper doctorMapper;

    public DepartmentService(DepartmentMapper departmentMapper, DoctorMapper doctorMapper) {
        this.departmentMapper = departmentMapper;
        this.doctorMapper = doctorMapper;
    }

    public List<Department> list() {
        return departmentMapper.findAll();
    }

    public List<Doctor> getDoctors(Long departmentId) {
        return doctorMapper.findByDepartmentId(departmentId);
    }
}
