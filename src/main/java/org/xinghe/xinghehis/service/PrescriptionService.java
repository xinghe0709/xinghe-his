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

    @Transactional
    public Prescription create(PrescriptionCreateRequest request) {
        Registration reg = registrationService.getById(request.getRegistrationId());

        Prescription prescription = new Prescription();
        prescription.setRegistrationId(request.getRegistrationId());
        prescription.setDoctorId(reg.getDoctorId());
        prescription.setStatus("ACTIVE");
        prescription.setRemark(request.getRemark());
        prescriptionMapper.insert(prescription);

        for (PrescriptionCreateRequest.Item item : request.getItems()) {
            Medicine medicine = medicineService.getById(item.getMedicineId());
            PrescriptionItem pi = new PrescriptionItem();
            pi.setPrescriptionId(prescription.getId());
            pi.setMedicineId(item.getMedicineId());
            pi.setDosage(item.getDosage());
            pi.setQuantity(item.getQuantity());
            pi.setPrice(medicine.getPrice());
            prescriptionItemMapper.insert(pi);
        }

        return prescriptionMapper.findById(prescription.getId());
    }

    public Prescription getById(Long id) {
        Prescription p = prescriptionMapper.findById(id);
        if (p == null) {
            throw new RuntimeException("处方不存在");
        }
        return p;
    }

    public Prescription getByRegistrationId(Long registrationId) {
        return prescriptionMapper.findByRegistrationId(registrationId);
    }

    public List<Prescription> listByDoctor(Long doctorId) {
        return prescriptionMapper.findByDoctorId(doctorId);
    }
}
