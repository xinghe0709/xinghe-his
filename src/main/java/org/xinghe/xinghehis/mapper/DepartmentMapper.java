package org.xinghe.xinghehis.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.xinghe.xinghehis.entity.Department;

import java.util.List;

@Mapper
public interface DepartmentMapper {
    List<Department> findAll();
}
