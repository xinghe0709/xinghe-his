package org.xinghe.xinghehis.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.xinghe.xinghehis.entity.Doctor;

import java.util.List;

@Mapper
public interface DoctorMapper {
    List<Doctor> findByDepartmentId(@Param("departmentId") Long departmentId);
    Doctor findById(@Param("id") Long id);
}
