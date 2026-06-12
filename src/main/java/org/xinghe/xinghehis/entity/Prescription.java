package org.xinghe.xinghehis.entity;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 处方实体 — 对应 prescription 表
 *
 * 医生在完成接诊（填写主诉和诊断）后开具处方。
 * 一个就诊记录（Registration）可以对应多张处方。
 * 处方由多条明细（PrescriptionItem）组成，指明具体药品和用法用量。
 *
 * MyBatis 通过 ResultMap 的 collection 标签将 PrescriptionItem 列表
 * 自动填充到 items 属性中，实现一对多关联查询。
 */
@Data
public class Prescription {
    private Long id;
    private Long registrationId;    // 关联就诊记录
    private Long doctorId;          // 开方医生
    private String status;          // ACTIVE=生效 CANCELLED=作废
    private String remark;          // 备注（如用药注意事项）
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 关联查询字段
    private List<PrescriptionItem> items;  // 处方明细列表
    private String patientName;            // 患者姓名（JOIN 填充）
    private String doctorName;             // 医生姓名（JOIN 填充）

}
