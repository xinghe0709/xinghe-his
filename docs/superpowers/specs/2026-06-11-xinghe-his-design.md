# 星河HIS 系统设计说明书

## 项目定位

个人学习项目，面向 Spring Boot + MyBatis + Vue 3 全栈技术学习。
核心业务闭环：门诊挂号 → 医生看诊 → 处方开药。
架构预留扩展点，后续可按需增加收费、药房、住院等模块。

## 技术选型

| 层级 | 技术 | 说明 |
|------|------|------|
| 后端框架 | Spring Boot 4.1 | Java 21 |
| 数据访问 | MyBatis 3.x | XML Mapper，SQL 显式控制 |
| 数据库 | MySQL 8.0 | 主数据存储 |
| 缓存 | Redis 7 | 会话token缓存、热点数据 |
| 认证授权 | Spring Security + JWT | 角色区分：挂号员/医生/药师 |
| 前端 | Vue 3 + Element Plus | 前后端分离 |
| 部署 | Docker Compose | mysql + redis + backend + frontend |

## 系统架构

经典分层单体架构：

```
Vue 3 + Element Plus (Nginx)
        │ Axios / JSON
        ▼
┌── Controller  ──┐   REST 接口层，参数校验
├── Service      ──┤   业务逻辑层，事务管理
├── Mapper       ──┤   MyBatis 数据访问
├── Entity       ──┤   领域实体
├── Config       ──┤   Spring 配置
├── Common       ──┤   全局异常、统一响应、JWT工具
└── Security     ──┤   Spring Security 配置
        │
        ▼
   MySQL 8.0 + Redis 7
```

### 包结构

```
src/main/java/org/xinghe/xinghehis/
├── XingheHisApplication.java
├── controller/
│   ├── AuthController          # 登录认证
│   ├── PatientController       # 患者管理
│   ├── RegistrationController  # 挂号管理
│   ├── ConsultationController  # 就诊管理
│   └── PrescriptionController  # 处方管理
├── service/
│   ├── AuthService
│   ├── PatientService
│   ├── RegistrationService
│   ├── ConsultationService
│   └── PrescriptionService
├── mapper/
│   ├── UserMapper
│   ├── PatientMapper
│   ├── RegistrationMapper
│   ├── PrescriptionMapper
│   ├── PrescriptionItemMapper
│   └── MedicineMapper
├── entity/
│   ├── User
│   ├── Patient
│   ├── Department
│   ├── Doctor
│   ├── Registration
│   ├── Prescription
│   ├── PrescriptionItem
│   └── Medicine
├── common/
│   ├── Result                   # 统一响应体 {code, message, data}
│   ├── GlobalExceptionHandler   # 全局异常处理
│   ├── SecurityConfig
│   └── JwtUtil
└── config/
    ├── RedisConfig
    └── MyBatisConfig
```

## 数据库ER模型

7张核心表：

### User（用户）
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT PK | |
| username | VARCHAR(50) UNIQUE | 登录名 |
| password | VARCHAR(255) | BCrypt加密 |
| role | VARCHAR(20) | REGISTRAR / DOCTOR / PHARMACIST |
| real_name | VARCHAR(50) | 真实姓名 |
| status | TINYINT | 1启用 0禁用 |
| created_at | DATETIME | |
| updated_at | DATETIME | |

### Patient（患者）
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT PK | |
| name | VARCHAR(50) | |
| gender | TINYINT | 0男 1女 |
| birth_date | DATE | |
| phone | VARCHAR(20) | |
| id_card | VARCHAR(18) | 身份证号 |
| address | VARCHAR(255) | |
| created_at | DATETIME | |
| updated_at | DATETIME | |

### Department（科室）
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT PK | |
| name | VARCHAR(50) | 内科/外科/儿科... |
| code | VARCHAR(20) | 科室编码 |
| status | TINYINT | 1启用 0禁用 |

### Doctor（医生）
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT PK | |
| user_id | BIGINT FK→User | 关联登录账号 |
| name | VARCHAR(50) | |
| title | VARCHAR(50) | 职称（主治医师/副主任医师） |
| department_id | BIGINT FK→Department | 所属科室 |

### Registration（挂号记录）— 核心业务表
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT PK | |
| patient_id | BIGINT FK→Patient | |
| doctor_id | BIGINT FK→Doctor | 就诊医生 |
| department_id | BIGINT FK→Department | 挂号科室 |
| register_time | DATETIME | 挂号时间 |
| status | VARCHAR(20) | WAITING / CONSULTING / COMPLETED / CANCELLED |
| chief_complaint | TEXT | 主诉（医生填写） |
| diagnosis | TEXT | 诊断（医生填写） |
| created_by | BIGINT FK→User | 挂号员 |
| created_at | DATETIME | |
| updated_at | DATETIME | |

### Prescription（处方）
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT PK | |
| registration_id | BIGINT FK→Registration | 关联就诊 |
| doctor_id | BIGINT FK→Doctor | 开方医生 |
| status | VARCHAR(20) | ACTIVE / CANCELLED |
| remark | VARCHAR(500) | 备注 |
| created_at | DATETIME | |
| updated_at | DATETIME | |

### PrescriptionItem（处方明细）
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT PK | |
| prescription_id | BIGINT FK→Prescription | |
| medicine_id | BIGINT FK→Medicine | 药品 |
| dosage | VARCHAR(100) | 用法用量，如"口服 一日三次 一次一片" |
| quantity | INT | 数量 |
| price | DECIMAL(10,2) | 单价（快照） |

### Medicine（药品字典）
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT PK | |
| name | VARCHAR(100) | 药品名称 |
| spec | VARCHAR(100) | 规格，如"0.25g×24片" |
| manufacturer | VARCHAR(200) | 生产厂家 |
| unit | VARCHAR(20) | 单位（盒/瓶/支） |
| price | DECIMAL(10,2) | 单价 |
| status | TINYINT | 1启用 0停用 |

### 关系总结

```
User ──1:1── Doctor (可选，非医生角色无Doctor记录)
Patient ──1:N── Registration
Department ──1:N── Doctor
Department ──1:N── Registration
Doctor ──1:N── Registration
Registration ──1:N── Prescription
Prescription ──1:N── PrescriptionItem
Medicine ──1:N── PrescriptionItem
```

## REST API 设计

统一前缀 `/api`，统一响应格式 `Result { code, message, data }`。

### 认证
- `POST /api/auth/login` — 登录，返回 JWT token
- `GET /api/auth/me` — 获取当前用户信息

### 患者管理（挂号员）
- `GET /api/patients` — 分页列表，支持姓名/手机号搜索
- `POST /api/patients` — 新建
- `GET /api/patients/{id}` — 详情
- `PUT /api/patients/{id}` — 更新

### 挂号管理（挂号员）
- `GET /api/registrations` — 列表（按日期/科室/状态筛选）
- `POST /api/registrations` — 创建挂号
- `PUT /api/registrations/{id}` — 修改状态
- `GET /api/registrations/{id}` — 详情

### 就诊管理（医生）
- `GET /api/consultations` — 看诊列表（待诊/已诊）
- `PUT /api/consultations/{id}` — 填写主诉/诊断
- `GET /api/consultations/{id}` — 就诊详情

### 处方管理（医生）
- `POST /api/prescriptions` — 开具处方（含明细）
- `GET /api/prescriptions/{id}` — 处方详情
- `GET /api/prescriptions?registrationId={id}` — 按就诊查处方

### 药品字典（基础数据）
- `GET /api/medicines` — 分页列表（支持搜索）
- `GET /api/medicines/{id}` — 详情

### 公共接口
- `GET /api/departments` — 科室列表
- `GET /api/departments/{id}/doctors` — 科室下医生列表

## 权限模型

| 角色 | 权限范围 |
|------|---------|
| 挂号员(REGISTRAR) | 患者CRUD、挂号CRUD、科室/医生查询 |
| 医生(DOCTOR) | 就诊列表/填写诊断、处方开具、患者查询 |
| 药师(PHARMACIST) | 处方查看（预留发药功能） |

使用 Spring Security + JWT 实现。JWT payload 包含 userId、username、role。
Redis 存储登录用户 session，支持主动踢出。

## 扩展预留点

- Registration 表中包含 `status` 字段，后续可扩展收费状态
- PrescriptionItem 包含 `price` 快照字段，为后续收费结算预留
- User-Role 使用字符串字段，后续可升级为 RBAC（User-Role-Permission 三表）
- 包结构按领域分包，后续可拆分为多模块

## 前端页面规划

| 页面 | 角色 | 说明 |
|------|------|------|
| 登录页 | 所有 | 用户名+密码 |
| 患者管理 | 挂号员 | 列表+搜索+新建/编辑弹窗 |
| 挂号管理 | 挂号员 | 挂号列表+新建挂号（选患者+科室+医生） |
| 就诊工作台 | 医生 | 待诊列表→接诊（填主诉/诊断/开处方） |
| 处方管理 | 医生 | 历史处方列表+详情 |
| 药品字典 | 挂号员/医生 | 药品分页列表（只读） |

## 部署架构

```
docker-compose.yml
├── mysql:8.0      (port 3306)
├── redis:7         (port 6379)
├── backend         (port 8080, Spring Boot)
└── frontend        (port 80, Nginx → Vue dist)
```

Docker Compose 一键启动，前端 Nginx 代理 `/api` 到后端 8080 端口。
