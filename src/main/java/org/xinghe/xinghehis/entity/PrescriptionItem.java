package org.xinghe.xinghehis.entity;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 处方明细实体 — 对应 prescription_item 表
 *
 * 每条明细指定处方中的一种药品及其用法用量。
 * price 字段存储开具处方时的药品价格快照，
 * 这样后续药价调整不会影响已开处方的金额。（财务一致性设计）
 */
@Data
public class PrescriptionItem {
    private Long id;
    private Long prescriptionId;    // 所属处方
    private Long medicineId;        // 药品 ID
    private String dosage;          // 用法用量，如"口服 一日三次 一次一片"
    private Integer quantity;       // 数量
    private BigDecimal price;       // 单价快照（开方时的药品价格）

    // 关联查询字段
    private String medicineName;    // 药品名称（JOIN 填充）


}