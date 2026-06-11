package org.xinghe.xinghehis.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 患者实体 — 对应 patient 表
 *
 * 患者是 HIS 系统的基础数据，由挂号员创建和管理。
 * 一个患者可以多次挂号就诊，每次就诊对应一条 Registration 记录。
 */
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

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Integer getGender() { return gender; }
    public void setGender(Integer gender) { this.gender = gender; }
    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getIdCard() { return idCard; }
    public void setIdCard(String idCard) { this.idCard = idCard; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
