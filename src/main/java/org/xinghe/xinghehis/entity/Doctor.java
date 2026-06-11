package org.xinghe.xinghehis.entity;

/**
 * 医生实体 — 对应 doctor 表
 *
 * 医生是 User（登录账号）的扩展，通过 user_id 关联。
 * 只有 role=DOCTOR 的用户才有对应的 Doctor 记录。
 * 挂号员和药师不在此表中。
 */
public class Doctor {
    private Long id;
    private Long userId;        // 关联 User 表，用于登录认证
    private String name;        // 医生姓名
    private String title;       // 职称（主治医师/副主任医师等）
    private Long departmentId;  // 所属科室

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public Long getDepartmentId() { return departmentId; }
    public void setDepartmentId(Long departmentId) { this.departmentId = departmentId; }
}
