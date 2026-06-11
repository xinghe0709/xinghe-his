# 星河HIS 系统实现计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 构建"挂号→看诊→开药"核心闭环的 HIS 学习系统，覆盖后端 API、数据库、认证授权、前端页面、Docker Compose 一键部署。

**Architecture:** Spring Boot 经典分层单体 → Controller/Service/Mapper 三层，MyBatis 访问 MySQL，Spring Security + JWT 鉴权，Redis 缓存 Token。Vue 3 + Element Plus 前后端分离，Nginx 代理 /api 请求。Docker Compose 统一编排。

**Tech Stack:** Spring Boot 4.1 (Java 21) / MyBatis 3.x / MySQL 8.0 / Redis 7 / Spring Security + JWT / Vue 3 + Element Plus + Vite / Docker Compose

---

## 任务概览

| 阶段 | 任务 | 内容 |
|------|------|------|
| 1 | Task 1-3 | 基础设施：依赖、配置、Docker Compose、数据库初始化 |
| 2 | Task 4-6 | 公共基础：Entity、统一响应、异常处理、JWT、Security |
| 3 | Task 7-10 | 业务 API：患者、科室/医生/药品、挂号、就诊/处方 |
| 4 | Task 11-15 | 前端：项目搭建、登录、患者管理、挂号管理、医生工作台 |
| 5 | Task 16-17 | 部署完善：Dockerfile、docker-compose 集成、种子数据 |

---

### Task 1: 添加 Maven 依赖

**Files:**
- Modify: `pom.xml`

- [ ] **Step 1: 补充所有依赖**

将 pom.xml 的 `<dependencies>` 替换为：

```xml
<dependencies>
    <!-- Spring Boot Web -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>

    <!-- MyBatis -->
    <dependency>
        <groupId>org.mybatis.spring.boot</groupId>
        <artifactId>mybatis-spring-boot-starter</artifactId>
        <version>3.0.4</version>
    </dependency>

    <!-- MySQL -->
    <dependency>
        <groupId>com.mysql</groupId>
        <artifactId>mysql-connector-j</artifactId>
        <scope>runtime</scope>
    </dependency>

    <!-- Redis -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-redis</artifactId>
    </dependency>

    <!-- Spring Security -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>

    <!-- Validation -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>

    <!-- JWT -->
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-api</artifactId>
        <version>0.12.6</version>
    </dependency>
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-impl</artifactId>
        <version>0.12.6</version>
        <scope>runtime</scope>
    </dependency>
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-jackson</artifactId>
        <version>0.12.6</version>
        <scope>runtime</scope>
    </dependency>

    <!-- Test -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.springframework.security</groupId>
        <artifactId>spring-security-test</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

- [ ] **Step 2: 验证依赖下载**

```bash
./mvnw dependency:resolve -q
```

Expected: BUILD SUCCESS

- [ ] **Step 3: 提交**

```bash
git add pom.xml
git commit -m "feat: add Spring Boot Web, MyBatis, MySQL, Redis, Security, JWT dependencies"
```

---

### Task 2: 配置 application.yaml 与 Docker Compose 基础设施

**Files:**
- Create: `docker-compose.yml`
- Create: `src/main/resources/application.yaml` (覆盖)
- Create: `src/main/resources/application-dev.yaml`

- [ ] **Step 1: 编写 docker-compose.yml（MySQL + Redis）**

```yaml
version: '3.8'
services:
  mysql:
    image: mysql:8.0
    container_name: his-mysql
    environment:
      MYSQL_ROOT_PASSWORD: root123
      MYSQL_DATABASE: xinghe_his
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql
      - ./sql/init.sql:/docker-entrypoint-initdb.d/init.sql
    healthcheck:
      test: ["CMD", "mysqladmin", "ping", "-h", "localhost"]
      interval: 10s
      timeout: 5s
      retries: 5

  redis:
    image: redis:7-alpine
    container_name: his-redis
    ports:
      - "6379:6379"
    healthcheck:
      test: ["CMD", "redis-cli", "ping"]
      interval: 10s
      timeout: 5s
      retries: 5

volumes:
  mysql_data:
```

- [ ] **Step 2: 编写 application.yaml（主配置）**

```yaml
spring:
  application:
    name: xinghe-his
  profiles:
    active: dev

server:
  port: 8080

mybatis:
  mapper-locations: classpath:mapper/*.xml
  type-aliases-package: org.xinghe.xinghehis.entity
  configuration:
    map-underscore-to-camel-case: true

jwt:
  secret: xinghe-his-jwt-secret-key-2026-must-be-at-least-256-bits-long!!
  expiration: 86400000
```

- [ ] **Step 3: 编写 application-dev.yaml（开发环境）**

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/xinghe_his?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai
    username: root
    password: root123
    driver-class-name: com.mysql.cj.jdbc.Driver
  data:
    redis:
      host: localhost
      port: 6379
```

- [ ] **Step 4: 编写 application-test.yaml（测试环境，H2）**

```yaml
spring:
  datasource:
    url: jdbc:h2:mem:testdb;MODE=MySQL;DATABASE_TO_LOWER=TRUE
    driver-class-name: org.h2.Driver
    username: sa
    password:
  sql:
    init:
      mode: always
      schema-locations: classpath:schema.sql
  data:
    redis:
      host: localhost
      port: 6379
```

- [ ] **Step 5: 创建 SQL 目录**

```bash
mkdir -p sql
```

- [ ] **Step 6: 提交**

```bash
git add docker-compose.yml src/main/resources/ sql/
git commit -m "feat: add Docker Compose and application config"
```

---

### Task 3: 数据库建表 SQL

**Files:**
- Create: `sql/init.sql`
- Create: `src/main/resources/schema.sql` (测试用)

- [ ] **Step 1: 编写 sql/init.sql（Docker 初始化 + 种子数据）**

```sql
CREATE TABLE IF NOT EXISTS `user` (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL COMMENT 'REGISTRAR/DOCTOR/PHARMACIST',
    real_name VARCHAR(50) NOT NULL,
    status TINYINT NOT NULL DEFAULT 1,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS department (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    code VARCHAR(20) NOT NULL,
    status TINYINT NOT NULL DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS doctor (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    name VARCHAR(50) NOT NULL,
    title VARCHAR(50),
    department_id BIGINT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES `user`(id),
    FOREIGN KEY (department_id) REFERENCES department(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS patient (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    gender TINYINT NOT NULL COMMENT '0男 1女',
    birth_date DATE,
    phone VARCHAR(20),
    id_card VARCHAR(18),
    address VARCHAR(255),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS registration (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id BIGINT NOT NULL,
    doctor_id BIGINT NOT NULL,
    department_id BIGINT NOT NULL,
    register_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) NOT NULL DEFAULT 'WAITING' COMMENT 'WAITING/CONSULTING/COMPLETED/CANCELLED',
    chief_complaint TEXT,
    diagnosis TEXT,
    created_by BIGINT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES patient(id),
    FOREIGN KEY (doctor_id) REFERENCES doctor(id),
    FOREIGN KEY (department_id) REFERENCES department(id),
    FOREIGN KEY (created_by) REFERENCES `user`(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS medicine (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    spec VARCHAR(100),
    manufacturer VARCHAR(200),
    unit VARCHAR(20),
    price DECIMAL(10,2) NOT NULL DEFAULT 0,
    status TINYINT NOT NULL DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS prescription (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    registration_id BIGINT NOT NULL,
    doctor_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' COMMENT 'ACTIVE/CANCELLED',
    remark VARCHAR(500),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (registration_id) REFERENCES registration(id),
    FOREIGN KEY (doctor_id) REFERENCES doctor(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS prescription_item (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    prescription_id BIGINT NOT NULL,
    medicine_id BIGINT NOT NULL,
    dosage VARCHAR(100),
    quantity INT NOT NULL DEFAULT 1,
    price DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (prescription_id) REFERENCES prescription(id),
    FOREIGN KEY (medicine_id) REFERENCES medicine(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 种子数据
INSERT INTO `user` (username, password, role, real_name) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5Eh', 'REGISTRAR', '张挂号'),
('doctor1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5Eh', 'DOCTOR', '李医生'),
('pharmacist1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5Eh', 'PHARMACIST', '王药师');

INSERT INTO department (name, code) VALUES
('内科', 'NK'),
('外科', 'WK'),
('儿科', 'EK'),
('妇产科', 'FCK');

INSERT INTO doctor (user_id, name, title, department_id) VALUES
(2, '李医生', '主治医师', 1);

INSERT INTO medicine (name, spec, manufacturer, unit, price) VALUES
('阿莫西林胶囊', '0.25g×24片', '华北制药', '盒', 12.50),
('布洛芬缓释胶囊', '0.3g×20粒', '中美史克', '盒', 18.00),
('头孢克肟片', '0.1g×12片', '广州白云山', '盒', 35.00),
('复方氨酚烷胺片', '12片', '哈药六厂', '盒', 8.50),
('蒙脱石散', '3g×10袋', '扬子江药业', '盒', 15.00);
```

> 密码均为 `123456` 的 BCrypt 哈希，首次登录后可通过接口修改。

- [ ] **Step 2: 编写 src/main/resources/schema.sql（测试用，无种子数据）**

```sql
CREATE TABLE IF NOT EXISTS `user` (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    real_name VARCHAR(50) NOT NULL,
    status TINYINT NOT NULL DEFAULT 1,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS department (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    code VARCHAR(20) NOT NULL,
    status TINYINT NOT NULL DEFAULT 1
);

CREATE TABLE IF NOT EXISTS doctor (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    name VARCHAR(50) NOT NULL,
    title VARCHAR(50),
    department_id BIGINT NOT NULL
);

CREATE TABLE IF NOT EXISTS patient (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    gender TINYINT NOT NULL,
    birth_date DATE,
    phone VARCHAR(20),
    id_card VARCHAR(18),
    address VARCHAR(255),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS registration (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id BIGINT NOT NULL,
    doctor_id BIGINT NOT NULL,
    department_id BIGINT NOT NULL,
    register_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) NOT NULL DEFAULT 'WAITING',
    chief_complaint TEXT,
    diagnosis TEXT,
    created_by BIGINT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS medicine (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    spec VARCHAR(100),
    manufacturer VARCHAR(200),
    unit VARCHAR(20),
    price DECIMAL(10,2) NOT NULL DEFAULT 0,
    status TINYINT NOT NULL DEFAULT 1
);

CREATE TABLE IF NOT EXISTS prescription (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    registration_id BIGINT NOT NULL,
    doctor_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    remark VARCHAR(500),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS prescription_item (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    prescription_id BIGINT NOT NULL,
    medicine_id BIGINT NOT NULL,
    dosage VARCHAR(100),
    quantity INT NOT NULL DEFAULT 1,
    price DECIMAL(10,2) NOT NULL
);
```

- [ ] **Step 3: 验证 Docker Compose 启动 MySQL + Redis**

```bash
docker compose up -d mysql redis
```

Expected: 两个服务启动成功，MySQL 自动执行 init.sql。

- [ ] **Step 4: 提交**

```bash
git add sql/init.sql src/main/resources/schema.sql
git commit -m "feat: add database schema and seed data"
```

---

### Task 4: Entity 实体类

**Files:**
- Create: `src/main/java/org/xinghe/xinghehis/entity/User.java`
- Create: `src/main/java/org/xinghe/xinghehis/entity/Department.java`
- Create: `src/main/java/org/xinghe/xinghehis/entity/Doctor.java`
- Create: `src/main/java/org/xinghe/xinghehis/entity/Patient.java`
- Create: `src/main/java/org/xinghe/xinghehis/entity/Registration.java`
- Create: `src/main/java/org/xinghe/xinghehis/entity/Prescription.java`
- Create: `src/main/java/org/xinghe/xinghehis/entity/PrescriptionItem.java`
- Create: `src/main/java/org/xinghe/xinghehis/entity/Medicine.java`

- [ ] **Step 1: 创建 User.java**

```java
package org.xinghe.xinghehis.entity;

import java.time.LocalDateTime;

public class User {
    private Long id;
    private String username;
    private String password;
    private String role;
    private String realName;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getRealName() { return realName; }
    public void setRealName(String realName) { this.realName = realName; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
```

- [ ] **Step 2: 创建 Department.java**

```java
package org.xinghe.xinghehis.entity;

public class Department {
    private Long id;
    private String name;
    private String code;
    private Integer status;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
}
```

- [ ] **Step 3: 创建 Doctor.java**

```java
package org.xinghe.xinghehis.entity;

public class Doctor {
    private Long id;
    private Long userId;
    private String name;
    private String title;
    private Long departmentId;

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
```

- [ ] **Step 4: 创建 Patient.java**

```java
package org.xinghe.xinghehis.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Patient {
    private Long id;
    private String name;
    private Integer gender;
    private LocalDate birthDate;
    private String phone;
    private String idCard;
    private String address;
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
```

- [ ] **Step 5: 创建 Registration.java**

```java
package org.xinghe.xinghehis.entity;

import java.time.LocalDateTime;

public class Registration {
    private Long id;
    private Long patientId;
    private Long doctorId;
    private Long departmentId;
    private LocalDateTime registerTime;
    private String status;
    private String chiefComplaint;
    private String diagnosis;
    private Long createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 关联查询字段
    private String patientName;
    private String doctorName;
    private String departmentName;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getPatientId() { return patientId; }
    public void setPatientId(Long patientId) { this.patientId = patientId; }
    public Long getDoctorId() { return doctorId; }
    public void setDoctorId(Long doctorId) { this.doctorId = doctorId; }
    public Long getDepartmentId() { return departmentId; }
    public void setDepartmentId(Long departmentId) { this.departmentId = departmentId; }
    public LocalDateTime getRegisterTime() { return registerTime; }
    public void setRegisterTime(LocalDateTime registerTime) { this.registerTime = registerTime; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getChiefComplaint() { return chiefComplaint; }
    public void setChiefComplaint(String chiefComplaint) { this.chiefComplaint = chiefComplaint; }
    public String getDiagnosis() { return diagnosis; }
    public void setDiagnosis(String diagnosis) { this.diagnosis = diagnosis; }
    public Long getCreatedBy() { return createdBy; }
    public void setCreatedBy(Long createdBy) { this.createdBy = createdBy; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }
    public String getDoctorName() { return doctorName; }
    public void setDoctorName(String doctorName) { this.doctorName = doctorName; }
    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }
}
```

- [ ] **Step 6: 创建 Prescription.java**

```java
package org.xinghe.xinghehis.entity;

import java.time.LocalDateTime;
import java.util.List;

public class Prescription {
    private Long id;
    private Long registrationId;
    private Long doctorId;
    private String status;
    private String remark;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<PrescriptionItem> items;
    private String patientName;
    private String doctorName;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getRegistrationId() { return registrationId; }
    public void setRegistrationId(Long registrationId) { this.registrationId = registrationId; }
    public Long getDoctorId() { return doctorId; }
    public void setDoctorId(Long doctorId) { this.doctorId = doctorId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public List<PrescriptionItem> getItems() { return items; }
    public void setItems(List<PrescriptionItem> items) { this.items = items; }
    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }
    public String getDoctorName() { return doctorName; }
    public void setDoctorName(String doctorName) { this.doctorName = doctorName; }
}
```

- [ ] **Step 7: 创建 PrescriptionItem.java**

```java
package org.xinghe.xinghehis.entity;

import java.math.BigDecimal;

public class PrescriptionItem {
    private Long id;
    private Long prescriptionId;
    private Long medicineId;
    private String dosage;
    private Integer quantity;
    private BigDecimal price;

    private String medicineName;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getPrescriptionId() { return prescriptionId; }
    public void setPrescriptionId(Long prescriptionId) { this.prescriptionId = prescriptionId; }
    public Long getMedicineId() { return medicineId; }
    public void setMedicineId(Long medicineId) { this.medicineId = medicineId; }
    public String getDosage() { return dosage; }
    public void setDosage(String dosage) { this.dosage = dosage; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public String getMedicineName() { return medicineName; }
    public void setMedicineName(String medicineName) { this.medicineName = medicineName; }
}
```

- [ ] **Step 8: 创建 Medicine.java**

```java
package org.xinghe.xinghehis.entity;

import java.math.BigDecimal;

public class Medicine {
    private Long id;
    private String name;
    private String spec;
    private String manufacturer;
    private String unit;
    private BigDecimal price;
    private Integer status;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSpec() { return spec; }
    public void setSpec(String spec) { this.spec = spec; }
    public String getManufacturer() { return manufacturer; }
    public void setManufacturer(String manufacturer) { this.manufacturer = manufacturer; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
}
```

- [ ] **Step 9: 提交**

```bash
git add src/main/java/org/xinghe/xinghehis/entity/
git commit -m "feat: add entity classes for all 8 tables"
```

---

### Task 5: Common 公共类（Result、异常处理、JWT、Security）

**Files:**
- Create: `src/main/java/org/xinghe/xinghehis/common/Result.java`
- Create: `src/main/java/org/xinghe/xinghehis/common/GlobalExceptionHandler.java`
- Create: `src/main/java/org/xinghe/xinghehis/common/JwtUtil.java`
- Create: `src/main/java/org/xinghe/xinghehis/common/SecurityConfig.java`
- Create: `src/main/java/org/xinghe/xinghehis/common/JwtAuthFilter.java`
- Create: `src/main/java/org/xinghe/xinghehis/common/UserContext.java`

- [ ] **Step 1: 创建 Result.java（统一响应体）**

```java
package org.xinghe.xinghehis.common;

public class Result<T> {
    private int code;
    private String message;
    private T data;

    private Result(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> Result<T> ok(T data) {
        return new Result<>(200, "success", data);
    }

    public static <T> Result<T> ok() {
        return new Result<>(200, "success", null);
    }

    public static <T> Result<T> fail(int code, String message) {
        return new Result<>(code, message, null);
    }

    public static <T> Result<T> fail(String message) {
        return new Result<>(500, message, null);
    }

    public int getCode() { return code; }
    public void setCode(int code) { this.code = code; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public T getData() { return data; }
    public void setData(T data) { this.data = data; }
}
```

- [ ] **Step 2: 创建 GlobalExceptionHandler.java**

```java
package org.xinghe.xinghehis.common;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleValidation(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getFieldErrors().stream()
                .map(f -> f.getField() + ": " + f.getDefaultMessage())
                .reduce((a, b) -> a + "; " + b)
                .orElse("参数校验失败");
        return Result.fail(400, msg);
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleRuntime(RuntimeException e) {
        return Result.fail(400, e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<Void> handleException(Exception e) {
        return Result.fail(500, "服务器内部错误");
    }
}
```

- [ ] **Step 3: 创建 JwtUtil.java**

```java
package org.xinghe.xinghehis.common;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    private final SecretKey key;
    private final long expiration;

    public JwtUtil(@Value("${jwt.secret}") String secret,
                   @Value("${jwt.expiration}") long expiration) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expiration = expiration;
    }

    public String generateToken(Long userId, String username, String role) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + expiration);
        return Jwts.builder()
                .subject(userId.toString())
                .claim("username", username)
                .claim("role", role)
                .issuedAt(now)
                .expiration(exp)
                .signWith(key)
                .compact();
    }

    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean isTokenExpired(String token) {
        try {
            return parseToken(token).getExpiration().before(new Date());
        } catch (Exception e) {
            return true;
        }
    }
}
```

- [ ] **Step 4: 创建 UserContext.java（当前登录用户上下文）**

```java
package org.xinghe.xinghehis.common;

public class UserContext {
    private static final ThreadLocal<Long> userId = new ThreadLocal<>();
    private static final ThreadLocal<String> username = new ThreadLocal<>();
    private static final ThreadLocal<String> role = new ThreadLocal<>();

    public static void set(Long id, String name, String r) {
        userId.set(id);
        username.set(name);
        role.set(r);
    }

    public static Long getUserId() { return userId.get(); }
    public static String getUsername() { return username.get(); }
    public static String getRole() { return role.get(); }

    public static void clear() {
        userId.remove();
        username.remove();
        role.remove();
    }
}
```

- [ ] **Step 5: 创建 JwtAuthFilter.java**

```java
package org.xinghe.xinghehis.common;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String token = extractToken(request);
        if (StringUtils.hasText(token) && !jwtUtil.isTokenExpired(token)) {
            Claims claims = jwtUtil.parseToken(token);
            Long userId = Long.parseLong(claims.getSubject());
            String username = claims.get("username", String.class);
            String role = claims.get("role", String.class);

            UserContext.set(userId, username, role);

            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(
                            userId, null,
                            List.of(new SimpleGrantedAuthority("ROLE_" + role)));
            SecurityContextHolder.getContext().setAuthentication(auth);
        }
        filterChain.doFilter(request, response);
        UserContext.clear();
    }

    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
}
```

- [ ] **Step 6: 创建 SecurityConfig.java**

```java
package org.xinghe.xinghehis.common;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtUtil jwtUtil;

    public SecurityConfig(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/login").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/departments", "/api/departments/*/doctors").permitAll()
                .requestMatchers("/api/patients/**").hasAnyRole("REGISTRAR", "DOCTOR")
                .requestMatchers("/api/registrations/**").hasRole("REGISTRAR")
                .requestMatchers("/api/consultations/**").hasRole("DOCTOR")
                .requestMatchers("/api/prescriptions/**").hasAnyRole("DOCTOR", "PHARMACIST")
                .requestMatchers(HttpMethod.GET, "/api/medicines/**").hasAnyRole("REGISTRAR", "DOCTOR", "PHARMACIST")
                .anyRequest().authenticated()
            )
            .addFilterBefore(new JwtAuthFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:5173", "http://localhost:80", "http://localhost"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
```

- [ ] **Step 7: 提交**

```bash
git add src/main/java/org/xinghe/xinghehis/common/
git commit -m "feat: add Result, exception handler, JWT, and security config"
```

---

### Task 6: Mapper 层（数据访问）

**Files:**
- Create: `src/main/java/org/xinghe/xinghehis/mapper/UserMapper.java`
- Create: `src/main/java/org/xinghe/xinghehis/mapper/PatientMapper.java`
- Create: `src/main/java/org/xinghe/xinghehis/mapper/RegistrationMapper.java`
- Create: `src/main/java/org/xinghe/xinghehis/mapper/DepartmentMapper.java`
- Create: `src/main/java/org/xinghe/xinghehis/mapper/DoctorMapper.java`
- Create: `src/main/java/org/xinghe/xinghehis/mapper/MedicineMapper.java`
- Create: `src/main/java/org/xinghe/xinghehis/mapper/PrescriptionMapper.java`
- Create: `src/main/java/org/xinghe/xinghehis/mapper/PrescriptionItemMapper.java`
- Create: `src/main/resources/mapper/UserMapper.xml`
- Create: `src/main/resources/mapper/PatientMapper.xml`
- Create: `src/main/resources/mapper/RegistrationMapper.xml`
- Create: `src/main/resources/mapper/DepartmentMapper.xml`
- Create: `src/main/resources/mapper/DoctorMapper.xml`
- Create: `src/main/resources/mapper/MedicineMapper.xml`
- Create: `src/main/resources/mapper/PrescriptionMapper.xml`
- Create: `src/main/resources/mapper/PrescriptionItemMapper.xml`

- [ ] **Step 1: 创建 UserMapper.java 和 XML**

```java
package org.xinghe.xinghehis.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.xinghe.xinghehis.entity.User;

@Mapper
public interface UserMapper {
    User findByUsername(@Param("username") String username);
    User findById(@Param("id") Long id);
}
```

`src/main/resources/mapper/UserMapper.xml`:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="org.xinghe.xinghehis.mapper.UserMapper">
    <select id="findByUsername" resultType="org.xinghe.xinghehis.entity.User">
        SELECT id, username, password, role, real_name, status, created_at, updated_at
        FROM `user` WHERE username = #{username}
    </select>
    <select id="findById" resultType="org.xinghe.xinghehis.entity.User">
        SELECT id, username, password, role, real_name, status, created_at, updated_at
        FROM `user` WHERE id = #{id}
    </select>
</mapper>
```

- [ ] **Step 2: 创建 PatientMapper.java 和 XML**

```java
package org.xinghe.xinghehis.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.xinghe.xinghehis.entity.Patient;

import java.util.List;

@Mapper
public interface PatientMapper {
    List<Patient> findByKeyword(@Param("keyword") String keyword);
    Patient findById(@Param("id") Long id);
    int insert(Patient patient);
    int update(Patient patient);
}
```

`src/main/resources/mapper/PatientMapper.xml`:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="org.xinghe.xinghehis.mapper.PatientMapper">
    <select id="findByKeyword" resultType="org.xinghe.xinghehis.entity.Patient">
        SELECT * FROM patient
        <where>
            <if test="keyword != null and keyword != ''">
                AND (name LIKE CONCAT('%', #{keyword}, '%')
                OR phone LIKE CONCAT('%', #{keyword}, '%'))
            </if>
        </where>
        ORDER BY created_at DESC
    </select>

    <select id="findById" resultType="org.xinghe.xinghehis.entity.Patient">
        SELECT * FROM patient WHERE id = #{id}
    </select>

    <insert id="insert" useGeneratedKeys="true" keyProperty="id">
        INSERT INTO patient (name, gender, birth_date, phone, id_card, address)
        VALUES (#{name}, #{gender}, #{birthDate}, #{phone}, #{idCard}, #{address})
    </insert>

    <update id="update">
        UPDATE patient SET name=#{name}, gender=#{gender}, birth_date=#{birthDate},
        phone=#{phone}, id_card=#{idCard}, address=#{address} WHERE id=#{id}
    </update>
</mapper>
```

- [ ] **Step 3: 创建 DepartmentMapper.java 和 XML**

```java
package org.xinghe.xinghehis.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.xinghe.xinghehis.entity.Department;

import java.util.List;

@Mapper
public interface DepartmentMapper {
    List<Department> findAll();
}
```

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="org.xinghe.xinghehis.mapper.DepartmentMapper">
    <select id="findAll" resultType="org.xinghe.xinghehis.entity.Department">
        SELECT * FROM department WHERE status = 1 ORDER BY id
    </select>
</mapper>
```

- [ ] **Step 4: 创建 DoctorMapper.java 和 XML**

```java
package org.xinghe.xinghehis.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.xinghe.xinghehis.entity.Doctor;

import java.util.List;

@Mapper
public interface DoctorMapper {
    List<Doctor> findByDepartmentId(@Param("departmentId") Long departmentId);
    Doctor findById(@Param("id") Long id);
}
```

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="org.xinghe.xinghehis.mapper.DoctorMapper">
    <select id="findByDepartmentId" resultType="org.xinghe.xinghehis.entity.Doctor">
        SELECT d.* FROM doctor d JOIN `user` u ON d.user_id = u.id
        WHERE d.department_id = #{departmentId} AND u.status = 1
    </select>
    <select id="findById" resultType="org.xinghe.xinghehis.entity.Doctor">
        SELECT * FROM doctor WHERE id = #{id}
    </select>
</mapper>
```

- [ ] **Step 5: 创建 RegistrationMapper.java 和 XML**

```java
package org.xinghe.xinghehis.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.xinghe.xinghehis.entity.Registration;

import java.util.List;

@Mapper
public interface RegistrationMapper {
    List<Registration> findByCondition(@Param("status") String status,
                                       @Param("departmentId") Long departmentId,
                                       @Param("startDate") String startDate,
                                       @Param("endDate") String endDate);
    Registration findById(@Param("id") Long id);
    int insert(Registration registration);
    int update(Registration registration);
}
```

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="org.xinghe.xinghehis.mapper.RegistrationMapper">
    <select id="findByCondition" resultType="org.xinghe.xinghehis.entity.Registration">
        SELECT r.*, p.name AS patient_name, d.name AS doctor_name, dept.name AS department_name
        FROM registration r
        JOIN patient p ON r.patient_id = p.id
        JOIN doctor d ON r.doctor_id = d.id
        JOIN department dept ON r.department_id = dept.id
        <where>
            <if test="status != null and status != ''">
                AND r.status = #{status}
            </if>
            <if test="departmentId != null">
                AND r.department_id = #{departmentId}
            </if>
            <if test="startDate != null and startDate != ''">
                AND r.register_time &gt;= #{startDate}
            </if>
            <if test="endDate != null and endDate != ''">
                AND r.register_time &lt;= #{endDate}
            </if>
        </where>
        ORDER BY r.register_time DESC
    </select>

    <select id="findById" resultType="org.xinghe.xinghehis.entity.Registration">
        SELECT r.*, p.name AS patient_name, d.name AS doctor_name, dept.name AS department_name
        FROM registration r
        JOIN patient p ON r.patient_id = p.id
        JOIN doctor d ON r.doctor_id = d.id
        JOIN department dept ON r.department_id = dept.id
        WHERE r.id = #{id}
    </select>

    <insert id="insert" useGeneratedKeys="true" keyProperty="id">
        INSERT INTO registration (patient_id, doctor_id, department_id, register_time, status, created_by)
        VALUES (#{patientId}, #{doctorId}, #{departmentId}, #{registerTime}, #{status}, #{createdBy})
    </insert>

    <update id="update">
        UPDATE registration
        <set>
            <if test="status != null">status = #{status},</if>
            <if test="chiefComplaint != null">chief_complaint = #{chiefComplaint},</if>
            <if test="diagnosis != null">diagnosis = #{diagnosis},</if>
        </set>
        WHERE id = #{id}
    </update>
</mapper>
```

- [ ] **Step 6: 创建 MedicineMapper.java 和 XML**

```java
package org.xinghe.xinghehis.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.xinghe.xinghehis.entity.Medicine;

import java.util.List;

@Mapper
public interface MedicineMapper {
    List<Medicine> findByKeyword(@Param("keyword") String keyword);
    Medicine findById(@Param("id") Long id);
}
```

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="org.xinghe.xinghehis.mapper.MedicineMapper">
    <select id="findByKeyword" resultType="org.xinghe.xinghehis.entity.Medicine">
        SELECT * FROM medicine
        <where>
            AND status = 1
            <if test="keyword != null and keyword != ''">
                AND name LIKE CONCAT('%', #{keyword}, '%')
            </if>
        </where>
        ORDER BY id
    </select>
    <select id="findById" resultType="org.xinghe.xinghehis.entity.Medicine">
        SELECT * FROM medicine WHERE id = #{id}
    </select>
</mapper>
```

- [ ] **Step 7: 创建 PrescriptionMapper.java 和 XML**

```java
package org.xinghe.xinghehis.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.xinghe.xinghehis.entity.Prescription;

import java.util.List;

@Mapper
public interface PrescriptionMapper {
    int insert(Prescription prescription);
    Prescription findById(@Param("id") Long id);
    Prescription findByRegistrationId(@Param("registrationId") Long registrationId);
    List<Prescription> findByDoctorId(@Param("doctorId") Long doctorId);
}
```

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="org.xinghe.xinghehis.mapper.PrescriptionMapper">
    <insert id="insert" useGeneratedKeys="true" keyProperty="id">
        INSERT INTO prescription (registration_id, doctor_id, status, remark)
        VALUES (#{registrationId}, #{doctorId}, #{status}, #{remark})
    </insert>

    <select id="findById" resultMap="prescriptionMap">
        SELECT pr.*, pi.id AS pi_id, pi.medicine_id, pi.dosage, pi.quantity, pi.price,
               m.name AS medicine_name
        FROM prescription pr
        LEFT JOIN prescription_item pi ON pr.id = pi.prescription_id
        LEFT JOIN medicine m ON pi.medicine_id = m.id
        WHERE pr.id = #{id}
    </select>

    <select id="findByRegistrationId" resultMap="prescriptionMap">
        SELECT pr.*, pi.id AS pi_id, pi.medicine_id, pi.dosage, pi.quantity, pi.price,
               m.name AS medicine_name
        FROM prescription pr
        LEFT JOIN prescription_item pi ON pr.id = pi.prescription_id
        LEFT JOIN medicine m ON pi.medicine_id = m.id
        WHERE pr.registration_id = #{registrationId}
    </select>

    <select id="findByDoctorId" resultMap="prescriptionMap">
        SELECT pr.*, pi.id AS pi_id, pi.medicine_id, pi.dosage, pi.quantity, pi.price,
               m.name AS medicine_name, p.name AS patient_name
        FROM prescription pr
        LEFT JOIN prescription_item pi ON pr.id = pi.prescription_id
        LEFT JOIN medicine m ON pi.medicine_id = m.id
        LEFT JOIN registration r ON pr.registration_id = r.id
        LEFT JOIN patient p ON r.patient_id = p.id
        WHERE pr.doctor_id = #{doctorId}
        ORDER BY pr.created_at DESC
    </select>

    <resultMap id="prescriptionMap" type="org.xinghe.xinghehis.entity.Prescription">
        <id property="id" column="id"/>
        <result property="registrationId" column="registration_id"/>
        <result property="doctorId" column="doctor_id"/>
        <result property="status" column="status"/>
        <result property="remark" column="remark"/>
        <result property="createdAt" column="created_at"/>
        <result property="updatedAt" column="updated_at"/>
        <result property="patientName" column="patient_name"/>
        <collection property="items" ofType="org.xinghe.xinghehis.entity.PrescriptionItem">
            <id property="id" column="pi_id"/>
            <result property="medicineId" column="medicine_id"/>
            <result property="dosage" column="dosage"/>
            <result property="quantity" column="quantity"/>
            <result property="price" column="price"/>
            <result property="medicineName" column="medicine_name"/>
        </collection>
    </resultMap>
</mapper>
```

- [ ] **Step 8: 创建 PrescriptionItemMapper.java 和 XML**

```java
package org.xinghe.xinghehis.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.xinghe.xinghehis.entity.PrescriptionItem;

@Mapper
public interface PrescriptionItemMapper {
    int insert(PrescriptionItem item);
}
```

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="org.xinghe.xinghehis.mapper.PrescriptionItemMapper">
    <insert id="insert">
        INSERT INTO prescription_item (prescription_id, medicine_id, dosage, quantity, price)
        VALUES (#{prescriptionId}, #{medicineId}, #{dosage}, #{quantity}, #{price})
    </insert>
</mapper>
```

- [ ] **Step 9: 创建 mapper 资源目录**

```bash
mkdir -p src/main/resources/mapper
```

然后将上述所有 XML 文件放入该目录。

- [ ] **Step 10: 提交**

```bash
git add src/main/java/org/xinghe/xinghehis/mapper/ src/main/resources/mapper/
git commit -m "feat: add MyBatis mapper interfaces and XML for all tables"
```

---

### Task 7: Service 层与 Controller 层 — 认证模块

**Files:**
- Create: `src/main/java/org/xinghe/xinghehis/service/AuthService.java`
- Create: `src/main/java/org/xinghe/xinghehis/controller/AuthController.java`
- Create: `src/main/java/org/xinghe/xinghehis/service/dto/LoginRequest.java`
- Create: `src/main/java/org/xinghe/xinghehis/service/dto/LoginResponse.java`

- [ ] **Step 1: 创建 DTO 类**

```java
// LoginRequest.java
package org.xinghe.xinghehis.service.dto;

import jakarta.validation.constraints.NotBlank;

public class LoginRequest {
    @NotBlank(message = "用户名不能为空")
    private String username;
    @NotBlank(message = "密码不能为空")
    private String password;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
```

```java
// LoginResponse.java
package org.xinghe.xinghehis.service.dto;

public class LoginResponse {
    private String token;
    private String realName;
    private String role;

    public LoginResponse(String token, String realName, String role) {
        this.token = token;
        this.realName = realName;
        this.role = role;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getRealName() { return realName; }
    public void setRealName(String realName) { this.realName = realName; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
```

- [ ] **Step 2: 创建 AuthService.java**

```java
package org.xinghe.xinghehis.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.xinghe.xinghehis.common.JwtUtil;
import org.xinghe.xinghehis.entity.User;
import org.xinghe.xinghehis.mapper.UserMapper;
import org.xinghe.xinghehis.service.dto.LoginRequest;
import org.xinghe.xinghehis.service.dto.LoginResponse;

@Service
public class AuthService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserMapper userMapper, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public LoginResponse login(LoginRequest request) {
        User user = userMapper.findByUsername(request.getUsername());
        if (user == null || user.getStatus() == 0) {
            throw new RuntimeException("用户名或密码错误");
        }
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
        return new LoginResponse(token, user.getRealName(), user.getRole());
    }

    public User getCurrentUser(Long userId) {
        return userMapper.findById(userId);
    }
}
```

- [ ] **Step 3: 创建 AuthController.java**

```java
package org.xinghe.xinghehis.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.xinghe.xinghehis.common.Result;
import org.xinghe.xinghehis.common.UserContext;
import org.xinghe.xinghehis.entity.User;
import org.xinghe.xinghehis.service.AuthService;
import org.xinghe.xinghehis.service.dto.LoginRequest;
import org.xinghe.xinghehis.service.dto.LoginResponse;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse resp = authService.login(request);
        return Result.ok(resp);
    }

    @GetMapping("/me")
    public Result<User> me() {
        User user = authService.getCurrentUser(UserContext.getUserId());
        user.setPassword(null);
        return Result.ok(user);
    }
}
```

- [ ] **Step 4: 启动应用验证登录接口**

```bash
./mvnw spring-boot:run
```

用 curl 测试:
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"123456"}'
```

Expected: 返回 JWT token。

- [ ] **Step 5: 提交**

```bash
git add src/main/java/org/xinghe/xinghehis/service/ src/main/java/org/xinghe/xinghehis/controller/
git commit -m "feat: add auth service and login API"
```

---

### Task 8: Service 层与 Controller 层 — 患者管理 API

**Files:**
- Create: `src/main/java/org/xinghe/xinghehis/service/PatientService.java`
- Create: `src/main/java/org/xinghe/xinghehis/controller/PatientController.java`
- Create: `src/main/java/org/xinghe/xinghehis/service/dto/PatientQuery.java`

- [ ] **Step 1: 创建 PatientQuery.java**

```java
package org.xinghe.xinghehis.service.dto;

public class PatientQuery {
    private String keyword;
    private Integer page = 1;
    private Integer pageSize = 20;

    public String getKeyword() { return keyword; }
    public void setKeyword(String keyword) { this.keyword = keyword; }
    public Integer getPage() { return page; }
    public void setPage(Integer page) { this.page = page; }
    public Integer getPageSize() { return pageSize; }
    public void setPageSize(Integer pageSize) { this.pageSize = pageSize; }
}
```

- [ ] **Step 2: 创建 PatientService.java**

```java
package org.xinghe.xinghehis.service;

import org.springframework.stereotype.Service;
import org.xinghe.xinghehis.entity.Patient;
import org.xinghe.xinghehis.mapper.PatientMapper;
import org.xinghe.xinghehis.service.dto.PatientQuery;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PatientService {

    private final PatientMapper patientMapper;

    public PatientService(PatientMapper patientMapper) {
        this.patientMapper = patientMapper;
    }

    public Map<String, Object> page(PatientQuery query) {
        List<Patient> all = patientMapper.findByKeyword(query.getKeyword());
        int total = all.size();
        int from = (query.getPage() - 1) * query.getPageSize();
        int to = Math.min(from + query.getPageSize(), total);
        List<Patient> page = all.subList(Math.min(from, total), to);

        Map<String, Object> result = new HashMap<>();
        result.put("list", page);
        result.put("total", total);
        result.put("page", query.getPage());
        result.put("pageSize", query.getPageSize());
        return result;
    }

    public Patient getById(Long id) {
        Patient patient = patientMapper.findById(id);
        if (patient == null) {
            throw new RuntimeException("患者不存在");
        }
        return patient;
    }

    public Patient create(Patient patient) {
        patientMapper.insert(patient);
        return patient;
    }

    public Patient update(Long id, Patient patient) {
        Patient existing = getById(id);
        patient.setId(id);
        patientMapper.update(patient);
        return patient;
    }
}
```

- [ ] **Step 3: 创建 PatientController.java**

```java
package org.xinghe.xinghehis.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.xinghe.xinghehis.common.Result;
import org.xinghe.xinghehis.entity.Patient;
import org.xinghe.xinghehis.service.PatientService;
import org.xinghe.xinghehis.service.dto.PatientQuery;

import java.util.Map;

@RestController
@RequestMapping("/api/patients")
public class PatientController {

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @GetMapping
    public Result<Map<String, Object>> list(PatientQuery query) {
        return Result.ok(patientService.page(query));
    }

    @GetMapping("/{id}")
    public Result<Patient> get(@PathVariable Long id) {
        return Result.ok(patientService.getById(id));
    }

    @PostMapping
    public Result<Patient> create(@Valid @RequestBody Patient patient) {
        return Result.ok(patientService.create(patient));
    }

    @PutMapping("/{id}")
    public Result<Patient> update(@PathVariable Long id, @Valid @RequestBody Patient patient) {
        return Result.ok(patientService.update(id, patient));
    }
}
```

- [ ] **Step 4: 提交**

```bash
git add src/main/java/org/xinghe/xinghehis/service/PatientService.java \
        src/main/java/org/xinghe/xinghehis/service/dto/PatientQuery.java \
        src/main/java/org/xinghe/xinghehis/controller/PatientController.java
git commit -m "feat: add patient CRUD API"
```

---

### Task 9: Service 层与 Controller 层 — 科室/医生/药品公共 API

**Files:**
- Create: `src/main/java/org/xinghe/xinghehis/service/DepartmentService.java`
- Create: `src/main/java/org/xinghe/xinghehis/service/MedicineService.java`
- Create: `src/main/java/org/xinghe/xinghehis/controller/DepartmentController.java`
- Create: `src/main/java/org/xinghe/xinghehis/controller/MedicineController.java`

- [ ] **Step 1: 创建 DepartmentService.java**

```java
package org.xinghe.xinghehis.service;

import org.springframework.stereotype.Service;
import org.xinghe.xinghehis.entity.Department;
import org.xinghe.xinghehis.entity.Doctor;
import org.xinghe.xinghehis.mapper.DepartmentMapper;
import org.xinghe.xinghehis.mapper.DoctorMapper;

import java.util.List;

@Service
public class DepartmentService {

    private final DepartmentMapper departmentMapper;
    private final DoctorMapper doctorMapper;

    public DepartmentService(DepartmentMapper departmentMapper, DoctorMapper doctorMapper) {
        this.departmentMapper = departmentMapper;
        this.doctorMapper = doctorMapper;
    }

    public List<Department> list() {
        return departmentMapper.findAll();
    }

    public List<Doctor> getDoctors(Long departmentId) {
        return doctorMapper.findByDepartmentId(departmentId);
    }
}
```

- [ ] **Step 2: 创建 DepartmentController.java**

```java
package org.xinghe.xinghehis.controller;

import org.springframework.web.bind.annotation.*;
import org.xinghe.xinghehis.common.Result;
import org.xinghe.xinghehis.entity.Department;
import org.xinghe.xinghehis.entity.Doctor;
import org.xinghe.xinghehis.service.DepartmentService;

import java.util.List;

@RestController
@RequestMapping("/api/departments")
public class DepartmentController {

    private final DepartmentService departmentService;

    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @GetMapping
    public Result<List<Department>> list() {
        return Result.ok(departmentService.list());
    }

    @GetMapping("/{id}/doctors")
    public Result<List<Doctor>> getDoctors(@PathVariable Long id) {
        return Result.ok(departmentService.getDoctors(id));
    }
}
```

- [ ] **Step 3: 创建 MedicineService.java**

```java
package org.xinghe.xinghehis.service;

import org.springframework.stereotype.Service;
import org.xinghe.xinghehis.entity.Medicine;
import org.xinghe.xinghehis.mapper.MedicineMapper;

import java.util.List;

@Service
public class MedicineService {

    private final MedicineMapper medicineMapper;

    public MedicineService(MedicineMapper medicineMapper) {
        this.medicineMapper = medicineMapper;
    }

    public List<Medicine> search(String keyword) {
        return medicineMapper.findByKeyword(keyword);
    }

    public Medicine getById(Long id) {
        Medicine medicine = medicineMapper.findById(id);
        if (medicine == null) {
            throw new RuntimeException("药品不存在");
        }
        return medicine;
    }
}
```

- [ ] **Step 4: 创建 MedicineController.java**

```java
package org.xinghe.xinghehis.controller;

import org.springframework.web.bind.annotation.*;
import org.xinghe.xinghehis.common.Result;
import org.xinghe.xinghehis.entity.Medicine;
import org.xinghe.xinghehis.service.MedicineService;

import java.util.List;

@RestController
@RequestMapping("/api/medicines")
public class MedicineController {

    private final MedicineService medicineService;

    public MedicineController(MedicineService medicineService) {
        this.medicineService = medicineService;
    }

    @GetMapping
    public Result<List<Medicine>> search(@RequestParam(required = false) String keyword) {
        return Result.ok(medicineService.search(keyword));
    }

    @GetMapping("/{id}")
    public Result<Medicine> get(@PathVariable Long id) {
        return Result.ok(medicineService.getById(id));
    }
}
```

- [ ] **Step 5: 提交**

```bash
git add src/main/java/org/xinghe/xinghehis/service/DepartmentService.java \
        src/main/java/org/xinghe/xinghehis/service/MedicineService.java \
        src/main/java/org/xinghe/xinghehis/controller/DepartmentController.java \
        src/main/java/org/xinghe/xinghehis/controller/MedicineController.java
git commit -m "feat: add department, doctor, and medicine public APIs"
```

---

### Task 10: Service 层与 Controller 层 — 挂号、就诊、处方 API

**Files:**
- Create: `src/main/java/org/xinghe/xinghehis/service/RegistrationService.java`
- Create: `src/main/java/org/xinghe/xinghehis/service/PrescriptionService.java`
- Create: `src/main/java/org/xinghe/xinghehis/controller/RegistrationController.java`
- Create: `src/main/java/org/xinghe/xinghehis/controller/ConsultationController.java`
- Create: `src/main/java/org/xinghe/xinghehis/controller/PrescriptionController.java`
- Create: `src/main/java/org/xinghe/xinghehis/service/dto/RegistrationQuery.java`
- Create: `src/main/java/org/xinghe/xinghehis/service/dto/ConsultationUpdateRequest.java`
- Create: `src/main/java/org/xinghe/xinghehis/service/dto/PrescriptionCreateRequest.java`

- [ ] **Step 1: 创建 DTO 类**

```java
// RegistrationQuery.java
package org.xinghe.xinghehis.service.dto;

import java.time.LocalDate;

public class RegistrationQuery {
    private String status;
    private Long departmentId;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer page = 1;
    private Integer pageSize = 20;

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Long getDepartmentId() { return departmentId; }
    public void setDepartmentId(Long departmentId) { this.departmentId = departmentId; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public Integer getPage() { return page; }
    public void setPage(Integer page) { this.page = page; }
    public Integer getPageSize() { return pageSize; }
    public void setPageSize(Integer pageSize) { this.pageSize = pageSize; }
}
```

```java
// ConsultationUpdateRequest.java
package org.xinghe.xinghehis.service.dto;

import jakarta.validation.constraints.NotBlank;

public class ConsultationUpdateRequest {
    @NotBlank(message = "主诉不能为空")
    private String chiefComplaint;
    private String diagnosis;

    public String getChiefComplaint() { return chiefComplaint; }
    public void setChiefComplaint(String chiefComplaint) { this.chiefComplaint = chiefComplaint; }
    public String getDiagnosis() { return diagnosis; }
    public void setDiagnosis(String diagnosis) { this.diagnosis = diagnosis; }
}
```

```java
// PrescriptionCreateRequest.java
package org.xinghe.xinghehis.service.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class PrescriptionCreateRequest {
    @NotNull(message = "就诊ID不能为空")
    private Long registrationId;
    private String remark;
    @NotEmpty(message = "处方明细不能为空")
    private List<Item> items;

    public Long getRegistrationId() { return registrationId; }
    public void setRegistrationId(Long registrationId) { this.registrationId = registrationId; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
    public List<Item> getItems() { return items; }
    public void setItems(List<Item> items) { this.items = items; }

    public static class Item {
        private Long medicineId;
        private String dosage;
        private Integer quantity;

        public Long getMedicineId() { return medicineId; }
        public void setMedicineId(Long medicineId) { this.medicineId = medicineId; }
        public String getDosage() { return dosage; }
        public void setDosage(String dosage) { this.dosage = dosage; }
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
    }
}
```

- [ ] **Step 2: 创建 RegistrationService.java**

```java
package org.xinghe.xinghehis.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.xinghe.xinghehis.common.UserContext;
import org.xinghe.xinghehis.entity.Patient;
import org.xinghe.xinghehis.entity.Registration;
import org.xinghe.xinghehis.mapper.RegistrationMapper;
import org.xinghe.xinghehis.service.dto.RegistrationQuery;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RegistrationService {

    private final RegistrationMapper registrationMapper;
    private final PatientService patientService;

    public RegistrationService(RegistrationMapper registrationMapper, PatientService patientService) {
        this.registrationMapper = registrationMapper;
        this.patientService = patientService;
    }

    public Map<String, Object> page(RegistrationQuery query) {
        String startDate = query.getStartDate() != null ? query.getStartDate().toString() : null;
        String endDate = query.getEndDate() != null ? query.getEndDate().toString() : null;
        List<Registration> all = registrationMapper.findByCondition(
                query.getStatus(), query.getDepartmentId(), startDate, endDate);
        int total = all.size();
        int from = (query.getPage() - 1) * query.getPageSize();
        int to = Math.min(from + query.getPageSize(), total);
        List<Registration> page = all.subList(Math.min(from, total), to);

        Map<String, Object> result = new HashMap<>();
        result.put("list", page);
        result.put("total", total);
        result.put("page", query.getPage());
        result.put("pageSize", query.getPageSize());
        return result;
    }

    public Registration getById(Long id) {
        Registration reg = registrationMapper.findById(id);
        if (reg == null) {
            throw new RuntimeException("挂号记录不存在");
        }
        return reg;
    }

    @Transactional
    public Registration create(Registration registration) {
        Patient patient = patientService.getById(registration.getPatientId());
        registration.setStatus("WAITING");
        registration.setCreatedBy(UserContext.getUserId());
        registrationMapper.insert(registration);
        return registrationMapper.findById(registration.getId());
    }

    @Transactional
    public Registration updateStatus(Long id, String status) {
        Registration reg = getById(id);
        Registration update = new Registration();
        update.setId(id);
        update.setStatus(status);
        registrationMapper.update(update);
        return getById(id);
    }

    @Transactional
    public Registration consult(Long id, String chiefComplaint, String diagnosis) {
        Registration reg = getById(id);
        Registration update = new Registration();
        update.setId(id);
        update.setChiefComplaint(chiefComplaint);
        update.setDiagnosis(diagnosis);
        update.setStatus("COMPLETED");
        registrationMapper.update(update);
        return getById(id);
    }
}
```

- [ ] **Step 3: 创建 RegistrationController.java**

```java
package org.xinghe.xinghehis.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.xinghe.xinghehis.common.Result;
import org.xinghe.xinghehis.entity.Registration;
import org.xinghe.xinghehis.service.RegistrationService;
import org.xinghe.xinghehis.service.dto.RegistrationQuery;

import java.util.Map;

@RestController
@RequestMapping("/api/registrations")
public class RegistrationController {

    private final RegistrationService registrationService;

    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @GetMapping
    public Result<Map<String, Object>> list(RegistrationQuery query) {
        return Result.ok(registrationService.page(query));
    }

    @GetMapping("/{id}")
    public Result<Registration> get(@PathVariable Long id) {
        return Result.ok(registrationService.getById(id));
    }

    @PostMapping
    public Result<Registration> create(@Valid @RequestBody Registration registration) {
        return Result.ok(registrationService.create(registration));
    }

    @PutMapping("/{id}")
    public Result<Registration> update(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return Result.ok(registrationService.updateStatus(id, body.get("status")));
    }
}
```

- [ ] **Step 4: 创建 ConsultationController.java**

```java
package org.xinghe.xinghehis.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.xinghe.xinghehis.common.Result;
import org.xinghe.xinghehis.entity.Registration;
import org.xinghe.xinghehis.service.RegistrationService;
import org.xinghe.xinghehis.service.dto.ConsultationUpdateRequest;

import java.util.Map;

@RestController
@RequestMapping("/api/consultations")
public class ConsultationController {

    private final RegistrationService registrationService;

    public ConsultationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @GetMapping
    public Result<Map<String, Object>> list(RegistrationQuery query) {
        return Result.ok(registrationService.page(query));
    }

    @GetMapping("/{id}")
    public Result<Registration> get(@PathVariable Long id) {
        return Result.ok(registrationService.getById(id));
    }

    @PutMapping("/{id}")
    public Result<Registration> update(@PathVariable Long id,
                                       @Valid @RequestBody ConsultationUpdateRequest request) {
        return Result.ok(registrationService.consult(id,
                request.getChiefComplaint(), request.getDiagnosis()));
    }
}
```

- [ ] **Step 5: 创建 PrescriptionService.java**

```java
package org.xinghe.xinghehis.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.xinghe.xinghehis.common.UserContext;
import org.xinghe.xinghehis.entity.Medicine;
import org.xinghe.xinghehis.entity.Prescription;
import org.xinghe.xinghehis.entity.PrescriptionItem;
import org.xinghe.xinghehis.entity.Registration;
import org.xinghe.xinghehis.mapper.PrescriptionItemMapper;
import org.xinghe.xinghehis.mapper.PrescriptionMapper;
import org.xinghe.xinghehis.service.dto.PrescriptionCreateRequest;

import java.util.List;

@Service
public class PrescriptionService {

    private final PrescriptionMapper prescriptionMapper;
    private final PrescriptionItemMapper prescriptionItemMapper;
    private final MedicineService medicineService;
    private final RegistrationService registrationService;

    public PrescriptionService(PrescriptionMapper prescriptionMapper,
                               PrescriptionItemMapper prescriptionItemMapper,
                               MedicineService medicineService,
                               RegistrationService registrationService) {
        this.prescriptionMapper = prescriptionMapper;
        this.prescriptionItemMapper = prescriptionItemMapper;
        this.medicineService = medicineService;
        this.registrationService = registrationService;
    }

    @Transactional
    public Prescription create(PrescriptionCreateRequest request) {
        Registration reg = registrationService.getById(request.getRegistrationId());

        // 获取医生的 doctor_id
        Prescription prescription = new Prescription();
        prescription.setRegistrationId(request.getRegistrationId());
        prescription.setDoctorId(reg.getDoctorId());
        prescription.setStatus("ACTIVE");
        prescription.setRemark(request.getRemark());
        prescriptionMapper.insert(prescription);

        for (PrescriptionCreateRequest.Item item : request.getItems()) {
            Medicine medicine = medicineService.getById(item.getMedicineId());
            PrescriptionItem pi = new PrescriptionItem();
            pi.setPrescriptionId(prescription.getId());
            pi.setMedicineId(item.getMedicineId());
            pi.setDosage(item.getDosage());
            pi.setQuantity(item.getQuantity());
            pi.setPrice(medicine.getPrice());
            prescriptionItemMapper.insert(pi);
        }

        return prescriptionMapper.findById(prescription.getId());
    }

    public Prescription getById(Long id) {
        Prescription p = prescriptionMapper.findById(id);
        if (p == null) {
            throw new RuntimeException("处方不存在");
        }
        return p;
    }

    public Prescription getByRegistrationId(Long registrationId) {
        return prescriptionMapper.findByRegistrationId(registrationId);
    }

    public List<Prescription> listByDoctor(Long doctorId) {
        return prescriptionMapper.findByDoctorId(doctorId);
    }
}
```

- [ ] **Step 6: 创建 PrescriptionController.java**

```java
package org.xinghe.xinghehis.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.xinghe.xinghehis.common.Result;
import org.xinghe.xinghehis.entity.Prescription;
import org.xinghe.xinghehis.service.PrescriptionService;
import org.xinghe.xinghehis.service.dto.PrescriptionCreateRequest;

import java.util.List;

@RestController
@RequestMapping("/api/prescriptions")
public class PrescriptionController {

    private final PrescriptionService prescriptionService;

    public PrescriptionController(PrescriptionService prescriptionService) {
        this.prescriptionService = prescriptionService;
    }

    @PostMapping
    public Result<Prescription> create(@Valid @RequestBody PrescriptionCreateRequest request) {
        return Result.ok(prescriptionService.create(request));
    }

    @GetMapping("/{id}")
    public Result<Prescription> get(@PathVariable Long id) {
        return Result.ok(prescriptionService.getById(id));
    }

    @GetMapping
    public Result<Object> findByRegistration(@RequestParam(required = false) Long registrationId) {
        if (registrationId != null) {
            return Result.ok(prescriptionService.getByRegistrationId(registrationId));
        }
        return Result.ok(List.of());
    }
}
```

- [ ] **Step 7: 添加 Redis 配置**

创建 `src/main/java/org/xinghe/xinghehis/config/RedisConfig.java`:

```java
package org.xinghe.xinghehis.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }
}
```

- [ ] **Step 8: 提交**

```bash
git add src/main/java/org/xinghe/xinghehis/service/ \
        src/main/java/org/xinghe/xinghehis/controller/ \
        src/main/java/org/xinghe/xinghehis/config/
git commit -m "feat: add registration, consultation, and prescription APIs"
```

---

### Task 11: 前端项目搭建（Vue 3 + Element Plus）

**Files:**
- Create: `frontend/` (整个项目目录)

- [ ] **Step 1: 使用 npm create vue 创建项目**

```bash
npm create vue@latest frontend -- --router --pinia
```

如果上述命令不可用，使用 Vite 手动创建:

```bash
npm create vite@latest frontend -- --template vue
cd frontend
npm install
npm install vue-router@4 pinia axios element-plus @element-plus/icons-vue
```

- [ ] **Step 2: 创建 frontend/vite.config.js**

```javascript
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, 'src')
    }
  },
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})
```

- [ ] **Step 3: 配置 main.js**

```javascript
import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

import App from './App.vue'
import router from './router'

const app = createApp(App)
app.use(createPinia())
app.use(router)
app.use(ElementPlus, { locale: { el: { ... } } })

for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    app.component(key, component)
}

app.mount('#app')
```

- [ ] **Step 4: 创建 API 工具 frontend/src/api/request.js**

```javascript
import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'

const request = axios.create({
  baseURL: '/api',
  timeout: 10000
})

request.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

request.interceptors.response.use(
  response => response.data,
  error => {
    if (error.response) {
      if (error.response.status === 401 || error.response.status === 403) {
        localStorage.removeItem('token')
        localStorage.removeItem('user')
        router.push('/login')
      }
      ElMessage.error(error.response.data?.message || '请求失败')
    }
    return Promise.reject(error)
  }
)

export default request
```

- [ ] **Step 5: 创建 API 模块 frontend/src/api/auth.js**

```javascript
import request from './request'

export function login(data) {
  return request.post('/auth/login', data)
}

export function getMe() {
  return request.get('/auth/me')
}
```

- [ ] **Step 6: 创建 frontend/src/api/patient.js**

```javascript
import request from './request'

export function getPatients(params) {
  return request.get('/patients', { params })
}

export function getPatient(id) {
  return request.get(`/patients/${id}`)
}

export function createPatient(data) {
  return request.post('/patients', data)
}

export function updatePatient(id, data) {
  return request.put(`/patients/${id}`, data)
}
```

- [ ] **Step 7: 创建 frontend/src/api/registration.js**

```javascript
import request from './request'

export function getRegistrations(params) {
  return request.get('/registrations', { params })
}

export function createRegistration(data) {
  return request.post('/registrations', data)
}

export function updateRegistration(id, data) {
  return request.put(`/registrations/${id}`, data)
}
```

- [ ] **Step 8: 创建 frontend/src/api/consultation.js**

```javascript
import request from './request'

export function getConsultations(params) {
  return request.get('/consultations', { params })
}

export function getConsultation(id) {
  return request.get(`/consultations/${id}`)
}

export function updateConsultation(id, data) {
  return request.put(`/consultations/${id}`, data)
}
```

- [ ] **Step 9: 创建 frontend/src/api/prescription.js**

```javascript
import request from './request'

export function createPrescription(data) {
  return request.post('/prescriptions', data)
}

export function getPrescription(id) {
  return request.get(`/prescriptions/${id}`)
}

export function getPrescriptionByRegistration(registrationId) {
  return request.get('/prescriptions', { params: { registrationId } })
}
```

- [ ] **Step 10: 创建 frontend/src/api/common.js**

```javascript
import request from './request'

export function getDepartments() {
  return request.get('/departments')
}

export function getDoctors(departmentId) {
  return request.get(`/departments/${departmentId}/doctors`)
}

export function getMedicines(keyword) {
  return request.get('/medicines', { params: { keyword } })
}
```

- [ ] **Step 11: 提交**

```bash
git add frontend/
git commit -m "feat: scaffold Vue 3 frontend with Element Plus, router, and API modules"
```

---

### Task 12: 前端 — 登录页

**Files:**
- Create: `frontend/src/views/LoginView.vue`
- Modify: `frontend/src/router/index.js`
- Create: `frontend/src/stores/user.js`

- [ ] **Step 1: 创建 user store**

`frontend/src/stores/user.js`:

```javascript
import { defineStore } from 'pinia'
import { ref } from 'vue'
import { login as loginApi, getMe } from '@/api/auth'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const user = ref(JSON.parse(localStorage.getItem('user') || 'null'))

  async function login(username, password) {
    const res = await loginApi({ username, password })
    token.value = res.data.token
    user.value = { realName: res.data.realName, role: res.data.role }
    localStorage.setItem('token', token.value)
    localStorage.setItem('user', JSON.stringify(user.value))
  }

  function logout() {
    token.value = ''
    user.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('user')
  }

  return { token, user, login, logout }
})
```

- [ ] **Step 2: 创建 LoginView.vue**

```vue
<template>
  <div class="login-container">
    <div class="login-card">
      <h2>星河HIS系统</h2>
      <el-form :model="form" :rules="rules" ref="formRef" @submit.prevent="handleLogin">
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="用户名" prefix-icon="User" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" placeholder="密码" prefix-icon="Lock" show-password />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleLogin" :loading="loading" style="width:100%">
            登 录
          </el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()
const loading = ref(false)
const form = ref({ username: '', password: '' })
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

async function handleLogin() {
  loading.value = true
  try {
    await userStore.login(form.value.username, form.value.password)
    router.push('/')
  } catch {
    // error handled by interceptor
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}
.login-card {
  width: 400px;
  padding: 40px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.15);
}
.login-card h2 {
  text-align: center;
  margin-bottom: 30px;
  color: #303133;
}
</style>
```

- [ ] **Step 3: 配置路由 frontend/src/router/index.js**

```javascript
import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/LoginView.vue'),
    meta: { noAuth: true }
  },
  {
    path: '/',
    redirect: '/patients'
  },
  {
    path: '/patients',
    name: 'Patients',
    component: () => import('@/views/PatientList.vue'),
    meta: { role: 'REGISTRAR' }
  },
  {
    path: '/registrations',
    name: 'Registrations',
    component: () => import('@/views/RegistrationList.vue'),
    meta: { role: 'REGISTRAR' }
  },
  {
    path: '/consultations',
    name: 'Consultations',
    component: () => import('@/views/ConsultationList.vue'),
    meta: { role: 'DOCTOR' }
  },
  {
    path: '/prescriptions',
    name: 'Prescriptions',
    component: () => import('@/views/PrescriptionList.vue'),
    meta: { role: 'DOCTOR' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  if (to.meta.noAuth) {
    next()
  } else if (!token) {
    next('/login')
  } else {
    const user = JSON.parse(localStorage.getItem('user') || '{}')
    if (to.meta.role && user.role !== to.meta.role) {
      next('/login')
    } else {
      next()
    }
  }
})

export default router
```

- [ ] **Step 4: 提交**

```bash
git add frontend/src/
git commit -m "feat: add login page with Pinia store and router guards"
```

---

### Task 13: 前端 — 布局与患者管理页

**Files:**
- Create: `frontend/src/App.vue` (覆盖)
- Create: `frontend/src/views/PatientList.vue`

- [ ] **Step 1: 创建 App.vue（主布局）**

```vue
<template>
  <div v-if="route.meta.noAuth">
    <router-view />
  </div>
  <el-container v-else style="min-height:100vh">
    <el-aside width="220px" style="background:#304156">
      <div style="color:#fff; text-align:center; padding:20px 0; font-size:18px; font-weight:bold">
        星河HIS
      </div>
      <el-menu
        :default-active="route.path"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409eff"
        router
      >
        <template v-if="user.role === 'REGISTRAR'">
          <el-menu-item index="/patients">
            <el-icon><User /></el-icon>
            <span>患者管理</span>
          </el-menu-item>
          <el-menu-item index="/registrations">
            <el-icon><Tickets /></el-icon>
            <span>挂号管理</span>
          </el-menu-item>
        </template>
        <template v-if="user.role === 'DOCTOR'">
          <el-menu-item index="/consultations">
            <el-icon><Memo /></el-icon>
            <span>就诊工作台</span>
          </el-menu-item>
          <el-menu-item index="/prescriptions">
            <el-icon><Document /></el-icon>
            <span>处方记录</span>
          </el-menu-item>
        </template>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header style="display:flex; justify-content:flex-end; align-items:center; border-bottom:1px solid #e6e6e6">
        <span>{{ user.realName }} ({{ user.role }})</span>
        <el-button type="danger" text @click="handleLogout" style="margin-left:16px">退出</el-button>
      </el-header>
      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const user = userStore.user || JSON.parse(localStorage.getItem('user') || '{}')

function handleLogout() {
  userStore.logout()
  router.push('/login')
}
</script>
```

- [ ] **Step 2: 创建 PatientList.vue**

```vue
<template>
  <div>
    <div class="toolbar">
      <el-input v-model="keyword" placeholder="搜索患者姓名/手机号" style="width:300px" clearable @input="fetchData" />
      <el-button type="primary" @click="showDialog">新建患者</el-button>
    </div>

    <el-table :data="list" border stripe v-loading="loading">
      <el-table-column prop="name" label="姓名" width="100" />
      <el-table-column prop="gender" label="性别" width="60">
        <template #default="{ row }">{{ row.gender === 0 ? '男' : '女' }}</template>
      </el-table-column>
      <el-table-column prop="phone" label="手机号" width="140" />
      <el-table-column prop="idCard" label="身份证号" width="180" />
      <el-table-column prop="birthDate" label="出生日期" width="120" />
      <el-table-column prop="address" label="地址" min-width="150" />
      <el-table-column label="操作" width="120">
        <template #default="{ row }">
          <el-button type="primary" text @click="editPatient(row)">编辑</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination style="margin-top:16px; justify-content:flex-end"
      v-model:current-page="page" :page-size="pageSize"
      :total="total" layout="total, prev, pager, next" @current-change="fetchData" />

    <el-dialog v-model="dialogVisible" :title="editing ? '编辑患者' : '新建患者'" width="500px">
      <el-form :model="form" ref="formRef" :rules="rules" label-width="80px">
        <el-form-item label="姓名" prop="name">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="性别" prop="gender">
          <el-radio-group v-model="form.gender">
            <el-radio :value="0">男</el-radio>
            <el-radio :value="1">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="出生日期" prop="birthDate">
          <el-date-picker v-model="form.birthDate" type="date" placeholder="选择日期" value-format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" />
        </el-form-item>
        <el-form-item label="身份证号" prop="idCard">
          <el-input v-model="form.idCard" />
        </el-form-item>
        <el-form-item label="地址" prop="address">
          <el-input v-model="form.address" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getPatients, createPatient, updatePatient } from '@/api/patient'

const list = ref([])
const loading = ref(false)
const keyword = ref('')
const page = ref(1)
const pageSize = ref(20)
const total = ref(0)

const dialogVisible = ref(false)
const editing = ref(null)
const form = ref({ name: '', gender: 0, birthDate: '', phone: '', idCard: '', address: '' })
const rules = {
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }]
}

async function fetchData() {
  loading.value = true
  try {
    const res = await getPatients({ keyword: keyword.value, page: page.value, pageSize: pageSize.value })
    list.value = res.data.list
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

function showDialog() {
  editing.value = null
  form.value = { name: '', gender: 0, birthDate: '', phone: '', idCard: '', address: '' }
  dialogVisible.value = true
}

function editPatient(row) {
  editing.value = row
  form.value = { ...row }
  dialogVisible.value = true
}

async function handleSave() {
  if (editing.value) {
    await updatePatient(editing.value.id, form.value)
    ElMessage.success('更新成功')
  } else {
    await createPatient(form.value)
    ElMessage.success('创建成功')
  }
  dialogVisible.value = false
  fetchData()
}

onMounted(fetchData)
</script>

<style scoped>
.toolbar {
  display: flex;
  justify-content: space-between;
  margin-bottom: 16px;
}
</style>
```

- [ ] **Step 3: 提交**

```bash
git add frontend/src/App.vue frontend/src/views/PatientList.vue
git commit -m "feat: add app layout and patient management page"
```

---

### Task 14: 前端 — 挂号管理页与医生工作台

**Files:**
- Create: `frontend/src/views/RegistrationList.vue`
- Create: `frontend/src/views/ConsultationList.vue`
- Create: `frontend/src/views/PrescriptionList.vue`

- [ ] **Step 1: 创建 RegistrationList.vue**

```vue
<template>
  <div>
    <div class="toolbar">
      <el-select v-model="filters.status" placeholder="状态" clearable style="width:140px" @change="fetchData">
        <el-option label="待诊" value="WAITING" />
        <el-option label="就诊中" value="CONSULTING" />
        <el-option label="已完成" value="COMPLETED" />
        <el-option label="已取消" value="CANCELLED" />
      </el-select>
      <el-date-picker v-model="filters.dateRange" type="daterange" range-separator="至"
        start-placeholder="开始日期" end-placeholder="结束日期" value-format="YYYY-MM-DD"
        style="margin-left:8px" @change="fetchData" />
      <el-button type="primary" @click="showDialog" style="margin-left:auto">新建挂号</el-button>
    </div>

    <el-table :data="list" border stripe v-loading="loading">
      <el-table-column prop="patientName" label="患者" width="100" />
      <el-table-column prop="departmentName" label="科室" width="100" />
      <el-table-column prop="doctorName" label="医生" width="100" />
      <el-table-column prop="registerTime" label="挂号时间" width="160" />
      <el-table-column prop="status" label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="statusType(row.status)">{{ statusText(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="120">
        <template #default="{ row }">
          <el-button v-if="row.status === 'WAITING'" type="danger" text @click="cancelReg(row)">取消</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination style="margin-top:16px; justify-content:flex-end"
      v-model:current-page="page" :page-size="pageSize"
      :total="total" layout="total, prev, pager, next" @current-change="fetchData" />

    <el-dialog v-model="dialogVisible" title="新建挂号" width="450px">
      <el-form :model="form" ref="formRef" label-width="80px">
        <el-form-item label="患者">
          <el-select v-model="form.patientId" filterable remote
            :remote-method="searchPatients" placeholder="搜索患者">
            <el-option v-for="p in patients" :key="p.id" :label="p.name + ' - ' + p.phone" :value="p.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="科室">
          <el-select v-model="form.departmentId" placeholder="选择科室" @change="onDeptChange">
            <el-option v-for="d in departments" :key="d.id" :label="d.name" :value="d.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="医生">
          <el-select v-model="form.doctorId" placeholder="选择医生">
            <el-option v-for="d in doctors" :key="d.id" :label="d.name + ' - ' + d.title" :value="d.id" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleCreate">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getRegistrations, createRegistration, updateRegistration } from '@/api/registration'
import { getPatients } from '@/api/patient'
import { getDepartments, getDoctors } from '@/api/common'

const list = ref([])
const loading = ref(false)
const page = ref(1)
const pageSize = ref(20)
const total = ref(0)
const filters = ref({ status: '', dateRange: null })

const dialogVisible = ref(false)
const patients = ref([])
const departments = ref([])
const doctors = ref([])
const form = ref({ patientId: null, departmentId: null, doctorId: null })

function statusType(s) {
  return { WAITING: 'warning', CONSULTING: '', COMPLETED: 'success', CANCELLED: 'info' }[s] || ''
}
function statusText(s) {
  return { WAITING: '待诊', CONSULTING: '就诊中', COMPLETED: '已完成', CANCELLED: '已取消' }[s] || s
}

async function fetchData() {
  loading.value = true
  try {
    const [startDate, endDate] = filters.value.dateRange || []
    const res = await getRegistrations({
      status: filters.value.status, startDate, endDate,
      page: page.value, pageSize: pageSize.value
    })
    list.value = res.data.list
    total.value = res.data.total
  } finally { loading.value = false }
}

async function showDialog() {
  form.value = { patientId: null, departmentId: null, doctorId: null }
  const [deptRes] = await Promise.all([getDepartments()])
  departments.value = deptRes.data
  dialogVisible.value = true
}

async function searchPatients(query) {
  if (query) {
    const res = await getPatients({ keyword: query, pageSize: 50 })
    patients.value = res.data.list
  }
}

async function onDeptChange(deptId) {
  const res = await getDoctors(deptId)
  doctors.value = res.data
}

async function handleCreate() {
  await createRegistration(form.value)
  ElMessage.success('挂号成功')
  dialogVisible.value = false
  fetchData()
}

async function cancelReg(row) {
  await updateRegistration(row.id, { status: 'CANCELLED' })
  ElMessage.success('已取消')
  fetchData()
}

onMounted(() => { fetchData() })
</script>

<style scoped>
.toolbar { display: flex; align-items: center; margin-bottom: 16px; }
</style>
```

- [ ] **Step 2: 创建 ConsultationList.vue（医生工作台）**

```vue
<template>
  <div>
    <el-tabs v-model="activeTab" @tab-change="fetchData">
      <el-tab-pane label="待诊患者" name="WAITING" />
      <el-tab-pane label="已诊患者" name="COMPLETED" />
    </el-tabs>

    <el-table :data="list" border stripe v-loading="loading" @row-click="showDetail">
      <el-table-column prop="patientName" label="患者" width="100" />
      <el-table-column prop="departmentName" label="科室" width="100" />
      <el-table-column prop="registerTime" label="挂号时间" width="160" />
      <el-table-column label="操作" width="120">
        <template #default="{ row }">
          <el-button v-if="activeTab === 'WAITING'" type="primary" size="small" @click.stop="showConsult(row)">
            接诊
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination style="margin-top:16px; justify-content:flex-end"
      v-model:current-page="page" :page-size="pageSize"
      :total="total" layout="total, prev, pager, next" @current-change="fetchData" />

    <!-- 接诊弹窗 -->
    <el-dialog v-model="consultVisible" title="接诊" width="700px" @close="refreshDetail">
      <el-descriptions v-if="detail" :column="2" border>
        <el-descriptions-item label="患者">{{ detail.patientName }}</el-descriptions-item>
        <el-descriptions-item label="科室">{{ detail.departmentName }}</el-descriptions-item>
        <el-descriptions-item label="挂号时间">{{ detail.registerTime }}</el-descriptions-item>
        <el-descriptions-item label="状态">{{ detail.status }}</el-descriptions-item>
      </el-descriptions>
      <el-form :model="consultForm" label-width="80px" style="margin-top:16px">
        <el-form-item label="主诉">
          <el-input v-model="consultForm.chiefComplaint" type="textarea" :rows="2" placeholder="患者主要症状" />
        </el-form-item>
        <el-form-item label="诊断">
          <el-input v-model="consultForm.diagnosis" type="textarea" :rows="2" placeholder="诊断结果" />
        </el-form-item>
      </el-form>

      <!-- 处方区域 -->
      <el-divider>开具处方</el-divider>
      <div v-for="(item, i) in prescriptionItems" :key="i" style="display:flex; gap:8px; margin-bottom:8px">
        <el-select v-model="item.medicineId" filterable placeholder="药品" style="flex:2"
          @change="(val) => onMedicineChange(i, val)">
          <el-option v-for="m in medicines" :key="m.id" :label="m.name + ' (' + m.spec + ')'" :value="m.id" />
        </el-select>
        <el-input v-model="item.dosage" placeholder="用法用量" style="flex:2" />
        <el-input-number v-model="item.quantity" :min="1" :max="99" style="width:80px" />
        <el-button type="danger" @click="prescriptionItems.splice(i, 1)" :disabled="prescriptionItems.length===1">
          删除
        </el-button>
      </div>
      <el-button type="primary" @click="prescriptionItems.push({ medicineId: null, dosage: '', quantity: 1 })">
        添加药品
      </el-button>

      <template #footer>
        <el-button @click="consultVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">提交诊断</el-button>
      </template>
    </el-dialog>

    <!-- 详情弹窗 -->
    <el-dialog v-model="detailVisible" title="就诊详情" width="700px">
      <el-descriptions v-if="detail" :column="2" border>
        <el-descriptions-item label="患者">{{ detail.patientName }}</el-descriptions-item>
        <el-descriptions-item label="科室">{{ detail.departmentName }}</el-descriptions-item>
        <el-descriptions-item label="主诉" :span="2">{{ detail.chiefComplaint || '无' }}</el-descriptions-item>
        <el-descriptions-item label="诊断" :span="2">{{ detail.diagnosis || '无' }}</el-descriptions-item>
      </el-descriptions>

      <el-divider>处方信息</el-divider>
      <el-table :data="prescriptionData" border size="small" v-if="prescriptionData">
        <el-table-column prop="medicineName" label="药品" />
        <el-table-column prop="dosage" label="用法用量" />
        <el-table-column prop="quantity" label="数量" width="60" />
        <el-table-column prop="price" label="单价" width="80" />
      </el-table>
      <p v-else style="color:#999">暂无处方</p>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getConsultations, getConsultation, updateConsultation } from '@/api/consultation'
import { createPrescription, getPrescriptionByRegistration } from '@/api/prescription'
import { getMedicines } from '@/api/common'

const list = ref([])
const loading = ref(false)
const page = ref(1)
const pageSize = ref(20)
const total = ref(0)
const activeTab = ref('WAITING')

const consultVisible = ref(false)
const detailVisible = ref(false)
const detail = ref(null)
const consultForm = ref({ chiefComplaint: '', diagnosis: '' })
const prescriptionItems = ref([{ medicineId: null, dosage: '', quantity: 1 }])
const medicines = ref([])
const prescriptionData = ref(null)

async function fetchData() {
  loading.value = true
  try {
    const res = await getConsultations({ status: activeTab.value, page: page.value, pageSize: pageSize.value })
    list.value = res.data.list
    total.value = res.data.total
  } finally { loading.value = false }
}

async function showConsult(row) {
  detail.value = row
  consultForm.value = { chiefComplaint: '', diagnosis: '' }
  prescriptionItems.value = [{ medicineId: null, dosage: '', quantity: 1 }]
  const medRes = await getMedicines('')
  medicines.value = medRes.data
  consultVisible.value = true
}

async function showDetail(row) {
  detail.value = row
  const [regDetail, presc] = await Promise.all([
    getConsultation(row.id),
    getPrescriptionByRegistration(row.id)
  ])
  detail.value = regDetail.data
  prescriptionData.value = presc.data?.items || null
  detailVisible.value = true
}

async function handleSubmit() {
  await updateConsultation(detail.value.id, consultForm.value)

  const validItems = prescriptionItems.value.filter(i => i.medicineId)
  if (validItems.length > 0) {
    await createPrescription({
      registrationId: detail.value.id,
      items: validItems
    })
  }

  ElMessage.success('接诊完成')
  consultVisible.value = false
  fetchData()
}

async function refreshDetail() {
  // re-fetch if needed
}

function onMedicineChange(index, medicineId) {
  // placeholder for price auto-fill etc.
}

onMounted(fetchData)
</script>
```

- [ ] **Step 3: 创建 PrescriptionList.vue（处方记录）**

```vue
<template>
  <div>
    <el-table :data="list" border stripe v-loading="loading">
      <el-table-column prop="patientName" label="患者" width="100" />
      <el-table-column prop="status" label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.status === 'ACTIVE' ? 'success' : 'info'">{{ row.status }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="remark" label="备注" min-width="150" />
      <el-table-column prop="createdAt" label="开具时间" width="160" />
      <el-table-column label="操作" width="100">
        <template #default="{ row }">
          <el-button type="primary" text @click="showDetail(row)">详情</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" title="处方详情" width="600px">
      <el-descriptions v-if="detail" :column="2" border>
        <el-descriptions-item label="患者">{{ detail.patientName }}</el-descriptions-item>
        <el-descriptions-item label="状态">{{ detail.status }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ detail.remark || '无' }}</el-descriptions-item>
      </el-descriptions>
      <el-table :data="detail?.items || []" border size="small" style="margin-top:16px">
        <el-table-column prop="medicineName" label="药品" />
        <el-table-column prop="dosage" label="用法用量" />
        <el-table-column prop="quantity" label="数量" width="60" />
        <el-table-column prop="price" label="单价" width="80" />
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getConsultations } from '@/api/consultation'
import { getPrescription, getPrescriptionByRegistration } from '@/api/prescription'

const list = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const detail = ref(null)

async function fetchData() {
  loading.value = true
  try {
    const res = await getConsultations({ status: 'COMPLETED', page: 1, pageSize: 100 })
    list.value = res.data.list
  } finally { loading.value = false }
}

async function showDetail(row) {
  const presc = await getPrescriptionByRegistration(row.id)
  detail.value = presc.data
  dialogVisible.value = true
}

onMounted(fetchData)
</script>

- [ ] **Step 4: 提交**

```bash
git add frontend/src/views/RegistrationList.vue \
        frontend/src/views/ConsultationList.vue \
        frontend/src/views/PrescriptionList.vue
git commit -m "feat: add registration, consultation, and prescription pages"
```

---

### Task 15: 后端 Dockerfile

**Files:**
- Create: `Dockerfile` (后端)
- Create: `frontend/Dockerfile`
- Create: `frontend/nginx.conf`

- [ ] **Step 1: 创建后端 Dockerfile**

```dockerfile
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

- [ ] **Step 2: 创建前端 Dockerfile**

```dockerfile
FROM node:20-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build

FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
```

- [ ] **Step 3: 创建前端 Nginx 配置**

`frontend/nginx.conf`:

```nginx
server {
    listen 80;
    server_name localhost;

    location / {
        root /usr/share/nginx/html;
        index index.html;
        try_files $uri $uri/ /index.html;
    }

    location /api/ {
        proxy_pass http://backend:8080/api/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

- [ ] **Step 4: 更新 docker-compose.yml 加入 backend 和 frontend 服务**

在 `docker-compose.yml` 末尾（volumes 之前）添加:

```yaml
  backend:
    build:
      context: .
      dockerfile: Dockerfile
    container_name: his-backend
    ports:
      - "8080:8080"
    environment:
      SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/xinghe_his?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai
      SPRING_DATASOURCE_USERNAME: root
      SPRING_DATASOURCE_PASSWORD: root123
      SPRING_DATA_REDIS_HOST: redis
    depends_on:
      mysql:
        condition: service_healthy
      redis:
        condition: service_healthy

  frontend:
    build:
      context: ./frontend
      dockerfile: Dockerfile
    container_name: his-frontend
    ports:
      - "80:80"
    depends_on:
      - backend
```

- [ ] **Step 5: 提交**

```bash
git add Dockerfile frontend/Dockerfile frontend/nginx.conf docker-compose.yml
git commit -m "feat: add Dockerfiles and complete docker-compose setup"
```

---

### Task 16: 验证与收尾

- [ ] **Step 1: 验证 Docker Compose 一键启动**

```bash
docker compose down -v
docker compose up -d --build
```

Expected: 四个服务全部启动成功。

- [ ] **Step 2: 验证登录流程**

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"123456"}'
```

提取 token 后:

```bash
TOKEN="<从上面获取>"
curl -H "Authorization: Bearer $TOKEN" http://localhost:8080/api/patients
```

Expected: 返回空列表。

- [ ] **Step 3: 验证患者 CRUD 流程**

```bash
# 创建患者
curl -X POST http://localhost:8080/api/patients \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"name":"张三","gender":0,"phone":"13800138000"}'

# 查询患者
curl -H "Authorization: Bearer $TOKEN" http://localhost:8080/api/patients
```

- [ ] **Step 4: 验证挂号→接诊→处方完整流程**

创建患者后:
```bash
PATIENT_ID=1
DOCTOR_ID=1
DEPT_ID=1

# 挂号
curl -X POST http://localhost:8080/api/registrations \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d "{\"patientId\":$PATIENT_ID,\"doctorId\":$DOCTOR_ID,\"departmentId\":$DEPT_ID}"

# 换医生token登录
TOKEN_DOCTOR=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"doctor1","password":"123456"}' | jq -r '.data.token')

# 接诊（填主诉+诊断）
curl -X PUT http://localhost:8080/api/consultations/1 \
  -H "Authorization: Bearer $TOKEN_DOCTOR" \
  -H "Content-Type: application/json" \
  -d '{"chiefComplaint":"头痛发热三天","diagnosis":"上呼吸道感染"}'

# 开处方
curl -X POST http://localhost:8080/api/prescriptions \
  -H "Authorization: Bearer $TOKEN_DOCTOR" \
  -H "Content-Type: application/json" \
  -d '{"registrationId":1,"items":[{"medicineId":1,"dosage":"口服 一日三次 一次一粒","quantity":2}]}'
```

- [ ] **Step 5: 验证前端页面**

浏览器打开 `http://localhost`，分别用 admin/doctor1 账号登录验证各页面功能。

- [ ] **Step 6: 提交**

```bash
git add -A
git commit -m "chore: final adjustments and cleanup"
```

---

## 扩展指引

本计划完成后的系统是一套可运行的基础框架。后续可按需扩展:

1. **收费模块** — Registration 加 PAYMENT 状态，新增 Bill/Payment 实体
2. **药房模块** — Prescription 加 DISPENSED 状态，加库存表
3. **RBAC 升级** — 新增 Permission/Role 表，用户-角色多对多
4. **Redis 缓存** — 药品列表/科室列表等热点数据缓存到 Redis
5. **日志系统** — AOP 操作日志，ELK 集成
6. **报表统计** — 接诊量统计、药品开方排行
