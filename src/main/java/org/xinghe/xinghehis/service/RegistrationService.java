package org.xinghe.xinghehis.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.xinghe.xinghehis.common.UserContext;
import org.xinghe.xinghehis.entity.Patient;
import org.xinghe.xinghehis.entity.Registration;
import org.xinghe.xinghehis.mapper.RegistrationMapper;
import org.xinghe.xinghehis.service.dto.RegistrationQuery;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RegistrationService {

    private final RegistrationMapper registrationMapper;
    private final PatientService patientService;

    public RegistrationService(RegistrationMapper registrationMapper, PatientService patientService) {
        this.registrationMapper = registrationMapper;
        this.patientService = patientService;
    }

    public Map<String, Object> page(RegistrationQuery query) {
        String startDate = query.getStartDate() != null ? query.getStartDate().toString() : null;
        String endDate = query.getEndDate() != null ? query.getEndDate().toString() : null;
        List<Registration> all = registrationMapper.findByCondition(
                query.getStatus(), query.getDepartmentId(), startDate, endDate);
        int total = all.size();
        int from = (query.getPage() - 1) * query.getPageSize();
        int to = Math.min(from + query.getPageSize(), total);
        List<Registration> page = all.subList(Math.min(from, total), to);

        Map<String, Object> result = new HashMap<>();
        result.put("list", page);
        result.put("total", total);
        result.put("page", query.getPage());
        result.put("pageSize", query.getPageSize());
        return result;
    }

    public Registration getById(Long id) {
        Registration reg = registrationMapper.findById(id);
        if (reg == null) {
            throw new RuntimeException("挂号记录不存在");
        }
        return reg;
    }

    @Transactional
    public Registration create(Registration registration) {
        patientService.getById(registration.getPatientId());
        registration.setStatus("WAITING");
        registration.setCreatedBy(UserContext.getUserId());
        registrationMapper.insert(registration);
        return registrationMapper.findById(registration.getId());
    }

    @Transactional
    public Registration updateStatus(Long id, String status) {
        getById(id);
        Registration update = new Registration();
        update.setId(id);
        update.setStatus(status);
        registrationMapper.update(update);
        return getById(id);
    }

    @Transactional
    public Registration consult(Long id, String chiefComplaint, String diagnosis) {
        getById(id);
        Registration update = new Registration();
        update.setId(id);
        update.setChiefComplaint(chiefComplaint);
        update.setDiagnosis(diagnosis);
        update.setStatus("COMPLETED");
        registrationMapper.update(update);
        return getById(id);
    }
}
