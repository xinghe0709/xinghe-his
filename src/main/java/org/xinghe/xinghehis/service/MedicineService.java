package org.xinghe.xinghehis.service;

import org.springframework.stereotype.Service;
import org.xinghe.xinghehis.entity.Medicine;
import org.xinghe.xinghehis.mapper.MedicineMapper;

import java.util.List;

/**
 * 药品字典服务
 *
 * 提供药品的查询功能，医生开处方时从此处选择药品。
 * 药品数据通常由药房管理员维护（本系统暂未实现管理功能，仅查询）。
 *
 * 扩展点：可对接外部药品主数据系统，或增加药品管理 CRUD 功能。
 */
@Service
public class MedicineService {

    private final MedicineMapper medicineMapper;

    public MedicineService(MedicineMapper medicineMapper) {
        this.medicineMapper = medicineMapper;
    }

    /** 按关键词搜索药品（模糊匹配药品名称） */
    public List<Medicine> search(String keyword) {
        return medicineMapper.findByKeyword(keyword);
    }

    /** 获取药品详情，用于处方开具时获取药品价格 */
    public Medicine getById(Long id) {
        Medicine medicine = medicineMapper.findById(id);
        if (medicine == null) {
            throw new RuntimeException("药品不存在");
        }
        return medicine;
    }
}
