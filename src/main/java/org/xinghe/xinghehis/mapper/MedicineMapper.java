package org.xinghe.xinghehis.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.xinghe.xinghehis.entity.Medicine;

import java.util.List;

@Mapper
public interface MedicineMapper {
    List<Medicine> findByKeyword(@Param("keyword") String keyword);
    Medicine findById(@Param("id") Long id);
}
