package org.xinghe.xinghehis.entity;

import lombok.Data;

/**
 * 医生实体 — 对应 doctor 表
 *
 * 医生是 User（登录账号）的扩展，通过 user_id 关联。
 * 只有 role=DOCTOR 的用户才有对应的 Doctor 记录。
 * 挂号员和药师不在此表中。
 */
@Data
public class Doctor {
    private Long id;
    private Long userId;        // 关联 User 表，用于登录认证
    private String name;        // 医生姓名
    private String title;       // 职称（主治医师/副主任医师等）
    private Long departmentId;  // 所属科室

}
