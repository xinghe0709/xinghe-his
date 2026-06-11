package org.xinghe.xinghehis.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.xinghe.xinghehis.entity.PrescriptionItem;

@Mapper
public interface PrescriptionItemMapper {
    int insert(PrescriptionItem item);
}
