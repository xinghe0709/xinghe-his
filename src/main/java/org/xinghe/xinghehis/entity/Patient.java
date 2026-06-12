package org.xinghe.xinghehis.entity;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 患者实体 — 对应 patient 表
 *
 * 患者是 HIS 系统的基础数据，由挂号员创建和管理。
 * 一个患者可以多次挂号就诊，每次就诊对应一条 Registration 记录。
 */
@Data
public class Patient {
    private Long id;
    private String name;            // 姓名
    private Integer gender;         // 0=男 1=女
    private LocalDate birthDate;    // 出生日期
    private String phone;           // 手机号，可用于搜索
    private String idCard;          // 身份证号，18位
    private String address;         // 住址
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
