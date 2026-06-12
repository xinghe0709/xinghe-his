package org.xinghe.xinghehis.entity;

import lombok.Data;

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
@Data
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

}
