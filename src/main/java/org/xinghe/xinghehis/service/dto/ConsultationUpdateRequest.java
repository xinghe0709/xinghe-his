package org.xinghe.xinghehis.service.dto;

import jakarta.validation.constraints.NotBlank;

public class ConsultationUpdateRequest {
    @NotBlank(message = "主诉不能为空")
    private String chiefComplaint;
    private String diagnosis;

    public String getChiefComplaint() { return chiefComplaint; }
    public void setChiefComplaint(String chiefComplaint) { this.chiefComplaint = chiefComplaint; }
    public String getDiagnosis() { return diagnosis; }
    public void setDiagnosis(String diagnosis) { this.diagnosis = diagnosis; }
}
