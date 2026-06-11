package org.xinghe.xinghehis.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.xinghe.xinghehis.entity.Prescription;

import java.util.List;

@Mapper
public interface PrescriptionMapper {
    int insert(Prescription prescription);
    Prescription findById(@Param("id") Long id);
    Prescription findByRegistrationId(@Param("registrationId") Long registrationId);
    List<Prescription> findByDoctorId(@Param("doctorId") Long doctorId);
}
