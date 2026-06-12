package org.xinghe.xinghehis.entity;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 药品字典实体 — 对应 medicine 表
 *
 * 存储医院所有可开具的药品基础信息，是处方的数据来源。
 * 药品价格存储在此表，开处方时快照到 PrescriptionItem.price 中，
 * 这样即使后续调价也不影响历史处方金额。
 */

@Data
public class Medicine {
    private Long id;
    private String name;        // 药品名称
    private String spec;        // 规格，如"0.25g×24片"
    private String manufacturer;// 生产厂家
    private String unit;        // 单位（盒/瓶/支）
    private BigDecimal price;   // 单价，使用 BigDecimal 保证精度
    private Integer status;     // 1=启用 0=停用

}
