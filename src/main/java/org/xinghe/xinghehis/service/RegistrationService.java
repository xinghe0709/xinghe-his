package org.xinghe.xinghehis.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.xinghe.xinghehis.common.UserContext;
import org.xinghe.xinghehis.entity.Registration;
import org.xinghe.xinghehis.mapper.RegistrationMapper;
import org.xinghe.xinghehis.service.dto.RegistrationQuery;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 挂号与就诊服务
 *
 * 这是 HIS 系统的核心业务服务，管理"挂号→接诊→完成"的完整流程。
 *
 * 挂号流程（挂号员操作）：
 *   1. 创建挂号记录（患者+科室+医生），状态为 WAITING
 *   2. 可取消挂号（WAITING → CANCELLED）
 *
 * 就诊流程（医生操作）：
 *   1. 查看待诊列表（status=WAITING）
 *   2. 接诊并填写主诉+诊断（WAITING → COMPLETED）
 *   3. 开具处方（另行调用 PrescriptionService）
 *
 * @Transactional 注解确保数据库操作的事务一致性
 */
@Service
public class RegistrationService {

    private final RegistrationMapper registrationMapper;
    private final PatientService patientService;

    public RegistrationService(RegistrationMapper registrationMapper, PatientService patientService) {
        this.registrationMapper = registrationMapper;
        this.patientService = patientService;
    }

    /** 分页查询挂号列表，支持按状态/科室/日期范围筛选 */
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

    /** 获取挂号详情（含患者姓名、医生姓名、科室名称） */
    public Registration getById(Long id) {
        Registration reg = registrationMapper.findById(id);
        if (reg == null) {
            throw new RuntimeException("挂号记录不存在");
        }
        return reg;
    }

    /**
     * 创建挂号
     * 自动设置状态为 WAITING，记录创建人（当前登录用户）
     */
    @Transactional
    public Registration create(Registration registration) {
        patientService.getById(registration.getPatientId());  // 校验患者存在
        registration.setStatus("WAITING");
        registration.setRegisterTime(java.time.LocalDateTime.now());
        registration.setCreatedBy(UserContext.getUserId());    // 通过 ThreadLocal 获取当前用户
        registrationMapper.insert(registration);
        return registrationMapper.findById(registration.getId());
    }

    /** 更新挂号状态（如取消挂号） */
    @Transactional
    public Registration updateStatus(Long id, String status) {
        getById(id);  // 校验存在性
        Registration update = new Registration();
        update.setId(id);
        update.setStatus(status);
        registrationMapper.update(update);
        return getById(id);
    }

    /**
     * 医生接诊：填写主诉和诊断，状态变为 COMPLETED
     * 注意：只有 DOCTOR 角色才能访问此方法（由 SecurityConfig 和 ConsultationController 保障）
     */
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
