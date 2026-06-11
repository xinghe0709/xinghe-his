package org.xinghe.xinghehis.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.xinghe.xinghehis.entity.Patient;

import java.util.List;

@Mapper
public interface PatientMapper {
    List<Patient> findByKeyword(@Param("keyword") String keyword);
    Patient findById(@Param("id") Long id);
    int insert(Patient patient);
    int update(Patient patient);
}
