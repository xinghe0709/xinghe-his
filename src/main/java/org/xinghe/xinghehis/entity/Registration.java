package org.xinghe.xinghehis.entity;

import java.time.LocalDateTime;

/**
 * 挂号记录实体 — 对应 registration 表（核心业务表）
 *
 * 整个 HIS 系统的核心："挂号→接诊→开处方"流程都围绕此表展开。
 *
 * 状态流转（挂号员 + 医生共同驱动）：
 *   WAITING    → 挂号员创建挂号后的初始状态，患者等待就诊
 *   CONSULTING → （可选）医生开始接诊
 *   COMPLETED  → 医生填写主诉和诊断后完成
 *   CANCELLED  → 挂号员取消挂号
 *
 * 扩展点：后续可加 BILLED（已收费）、DISPENSED（已发药）等状态。
 */
public class Registration {
    private Long id;
    private Long patientId;         // 关联患者
    private Long doctorId;          // 就诊医生
    private Long departmentId;      // 挂号科室
    private LocalDateTime registerTime; // 挂号时间
    private String status;          // WAITING/CONSULTING/COMPLETED/CANCELLED
    private String chiefComplaint;  // 主诉（医生填写：患者描述的主要症状）
    private String diagnosis;       // 诊断（医生填写：诊断结论）
    private Long createdBy;         // 创建人（挂号员 User ID）
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 以下为关联查询字段，不存入数据库
    // MyBatis 通过 JOIN 查询填充这些字段，方便前端展示
    private String patientName;
    private String doctorName;
    private String departmentName;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getPatientId() { return patientId; }
    public void setPatientId(Long patientId) { this.patientId = patientId; }
    public Long getDoctorId() { return doctorId; }
    public void setDoctorId(Long doctorId) { this.doctorId = doctorId; }
    public Long getDepartmentId() { return departmentId; }
    public void setDepartmentId(Long departmentId) { this.departmentId = departmentId; }
    public LocalDateTime getRegisterTime() { return registerTime; }
    public void setRegisterTime(LocalDateTime registerTime) { this.registerTime = registerTime; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getChiefComplaint() { return chiefComplaint; }
    public void setChiefComplaint(String chiefComplaint) { this.chiefComplaint = chiefComplaint; }
    public String getDiagnosis() { return diagnosis; }
    public void setDiagnosis(String diagnosis) { this.diagnosis = diagnosis; }
    public Long getCreatedBy() { return createdBy; }
    public void setCreatedBy(Long createdBy) { this.createdBy = createdBy; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }
    public String getDoctorName() { return doctorName; }
    public void setDoctorName(String doctorName) { this.doctorName = doctorName; }
    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }
}
