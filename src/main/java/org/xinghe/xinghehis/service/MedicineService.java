package org.xinghe.xinghehis.service;

import org.springframework.stereotype.Service;
import org.xinghe.xinghehis.entity.Medicine;
import org.xinghe.xinghehis.mapper.MedicineMapper;

import java.util.List;

@Service
public class MedicineService {

    private final MedicineMapper medicineMapper;

    public MedicineService(MedicineMapper medicineMapper) {
        this.medicineMapper = medicineMapper;
    }

    public List<Medicine> search(String keyword) {
        return medicineMapper.findByKeyword(keyword);
    }

    public Medicine getById(Long id) {
        Medicine medicine = medicineMapper.findById(id);
        if (medicine == null) {
            throw new RuntimeException("药品不存在");
        }
        return medicine;
    }
}
