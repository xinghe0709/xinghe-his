package org.xinghe.xinghehis.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.xinghe.xinghehis.entity.Medicine;
import org.xinghe.xinghehis.entity.Prescription;
import org.xinghe.xinghehis.entity.PrescriptionItem;
import org.xinghe.xinghehis.entity.Registration;
import org.xinghe.xinghehis.mapper.PrescriptionItemMapper;
import org.xinghe.xinghehis.mapper.PrescriptionMapper;
import org.xinghe.xinghehis.service.dto.PrescriptionCreateRequest;

import java.util.List;

/**
 * 处方服务
 *
 * 医生在完成接诊后开具处方。一张处方包含多条明细（PrescriptionItem），
 * 每条明细对应一种药品及其用法用量。
 *
 * 设计要点：
 *   - 药品价格在开方时快照存储（PrescriptionItem.price），不受后续药价调整影响。
 *   - 整个处方创建在一个事务中完成，确保处方和明细同时成功或同时回滚。
 *   - doctor_id 从挂号记录中自动获取，不依赖前端传入（安全考虑）。
 */
@Service
public class PrescriptionService {

    private final PrescriptionMapper prescriptionMapper;
    private final PrescriptionItemMapper prescriptionItemMapper;
    private final MedicineService medicineService;
    private final RegistrationService registrationService;

    public PrescriptionService(PrescriptionMapper prescriptionMapper,
                               PrescriptionItemMapper prescriptionItemMapper,
                               MedicineService medicineService,
                               RegistrationService registrationService) {
        this.prescriptionMapper = prescriptionMapper;
        this.prescriptionItemMapper = prescriptionItemMapper;
        this.medicineService = medicineService;
        this.registrationService = registrationService;
    }

    /**
     * 创建处方及其明细
     *
     * 事务流程：
     *   1. 校验挂号记录存在
     *   2. 插入处方主记录（获取处方 ID）
     *   3. 逐条插入处方明细（从药品表获取当前价格做快照）
     *   4. 查询完整的处方信息（含明细和药品名称）返回
     *
     * @param request 包含 registrationId 和处方明细列表
     */
    @Transactional
    public Prescription create(PrescriptionCreateRequest request) {
        Registration reg = registrationService.getById(request.getRegistrationId());

        // 创建处方主记录
        Prescription prescription = new Prescription();
        prescription.setRegistrationId(request.getRegistrationId());
        prescription.setDoctorId(reg.getDoctorId());  // 从挂号记录获取，确保数据一致性
        prescription.setStatus("ACTIVE");
        prescription.setRemark(request.getRemark());
        prescriptionMapper.insert(prescription);  // 插入后 prescription.id 被自动回填

        // 逐条创建处方明细
        for (PrescriptionCreateRequest.Item item : request.getItems()) {
            Medicine medicine = medicineService.getById(item.getMedicineId());
            PrescriptionItem pi = new PrescriptionItem();
            pi.setPrescriptionId(prescription.getId());
            pi.setMedicineId(item.getMedicineId());
            pi.setDosage(item.getDosage());
            pi.setQuantity(item.getQuantity());
            pi.setPrice(medicine.getPrice());  // 价格快照：取当前药品价格
            prescriptionItemMapper.insert(pi);
        }

        // 返回完整的处方信息（含明细列表和药品名称）
        return prescriptionMapper.findById(prescription.getId());
    }

    /** 获取处方详情（含明细和药品名称，通过 MyBatis ResultMap 一对多映射） */
    public Prescription getById(Long id) {
        Prescription p = prescriptionMapper.findById(id);
        if (p == null) {
            throw new RuntimeException("处方不存在");
        }
        return p;
    }

    /** 根据就诊 ID 查询处方（一次就诊可能有多张处方） */
    public Prescription getByRegistrationId(Long registrationId) {
        return prescriptionMapper.findByRegistrationId(registrationId);
    }

    /** 查询某位医生的所有历史处方 */
    public List<Prescription> listByDoctor(Long doctorId) {
        return prescriptionMapper.findByDoctorId(doctorId);
    }
}
