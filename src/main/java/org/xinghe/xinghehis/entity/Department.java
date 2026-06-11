package org.xinghe.xinghehis.entity;

/**
 * 科室实体 — 对应 department 表
 *
 * 医院组织架构的基础单位，如内科、外科、儿科等。
 * 每个科室下有多位医生，挂号时需指定科室。
 */
public class Department {
    private Long id;
    private String name;    // 科室名称（内科/外科/儿科/妇产科）
    private String code;    // 科室编码（NK/WK/EK/FCK），便于程序处理
    private Integer status; // 1=启用 0=禁用

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
}
