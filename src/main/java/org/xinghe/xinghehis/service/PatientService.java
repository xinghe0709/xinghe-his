package org.xinghe.xinghehis.service;

import org.springframework.stereotype.Service;
import org.xinghe.xinghehis.entity.Patient;
import org.xinghe.xinghehis.mapper.PatientMapper;
import org.xinghe.xinghehis.service.dto.PatientQuery;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 患者管理服务
 *
 * 提供患者的增删改查功能，由挂号员使用。
 * 患者在 HIS 中是基础数据，挂号前必须先建档。
 *
 * 当前分页实现为"内存分页"（先查全部再截取），
 * 适用于学习阶段数据量小的场景。
 * 生产环境应改为数据库分页（LIMIT/OFFSET）。
 */
@Service
public class PatientService {

    private final PatientMapper patientMapper;

    public PatientService(PatientMapper patientMapper) {
        this.patientMapper = patientMapper;
    }

    /** 分页查询患者列表，支持按姓名/手机号模糊搜索 */
    public Map<String, Object> page(PatientQuery query) {
        List<Patient> all = patientMapper.findByKeyword(query.getKeyword());
        int total = all.size();
        int from = (query.getPage() - 1) * query.getPageSize();
        int to = Math.min(from + query.getPageSize(), total);
        List<Patient> page = all.subList(Math.min(from, total), to);

        Map<String, Object> result = new HashMap<>();
        result.put("list", page);
        result.put("total", total);
        result.put("page", query.getPage());
        result.put("pageSize", query.getPageSize());
        return result;
    }

    /** 根据 ID 获取患者详情，不存在则抛异常 */
    public Patient getById(Long id) {
        Patient patient = patientMapper.findById(id);
        if (patient == null) {
            throw new RuntimeException("患者不存在");
        }
        return patient;
    }

    /** 创建新患者，MyBatis 会自动回填自增 ID */
    public Patient create(Patient patient) {
        patientMapper.insert(patient);
        return patient;
    }

    /** 更新患者信息，先校验存在性再更新 */
    public Patient update(Long id, Patient patient) {
        getById(id);  // 校验存在性
        patient.setId(id);
        patientMapper.update(patient);
        return patient;
    }
}
