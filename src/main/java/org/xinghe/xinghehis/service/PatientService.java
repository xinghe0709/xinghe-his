package org.xinghe.xinghehis.service;

import org.springframework.stereotype.Service;
import org.xinghe.xinghehis.entity.Patient;
import org.xinghe.xinghehis.mapper.PatientMapper;
import org.xinghe.xinghehis.service.dto.PatientQuery;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PatientService {

    private final PatientMapper patientMapper;

    public PatientService(PatientMapper patientMapper) {
        this.patientMapper = patientMapper;
    }

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

    public Patient getById(Long id) {
        Patient patient = patientMapper.findById(id);
        if (patient == null) {
            throw new RuntimeException("患者不存在");
        }
        return patient;
    }

    public Patient create(Patient patient) {
        patientMapper.insert(patient);
        return patient;
    }

    public Patient update(Long id, Patient patient) {
        getById(id);
        patient.setId(id);
        patientMapper.update(patient);
        return patient;
    }
}
