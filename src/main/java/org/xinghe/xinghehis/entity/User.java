package org.xinghe.xinghehis.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 系统用户实体 — 对应 user 表
 *
 * 角色说明：
 *   REGISTRAR  - 挂号员：管理患者、创建挂号
 *   DOCTOR     - 医生：接诊、填写诊断、开具处方
 *   PHARMACIST - 药师：查看处方（预留发药功能）
 *
 * MyBatis 通过 map-underscore-to-camel-case 自动将下划线字段映射为驼峰属性，
 * 例如 real_name → realName，created_at → createdAt
 */
@Data
public class User {
    private Long id;
    private String username;    // 登录用户名，唯一
    private String password;    // BCrypt 加密存储
    private String role;        // REGISTRAR / DOCTOR / PHARMACIST
    private String realName;    // 真实姓名，用于界面展示
    private Integer status;     // 1=启用 0=禁用
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


}
