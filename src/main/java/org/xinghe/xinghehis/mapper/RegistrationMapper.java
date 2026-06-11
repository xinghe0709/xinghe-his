package org.xinghe.xinghehis.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.xinghe.xinghehis.entity.Registration;

import java.util.List;

@Mapper
public interface RegistrationMapper {
    List<Registration> findByCondition(@Param("status") String status,
                                       @Param("departmentId") Long departmentId,
                                       @Param("startDate") String startDate,
                                       @Param("endDate") String endDate);
    Registration findById(@Param("id") Long id);
    int insert(Registration registration);
    int update(Registration registration);
}
