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
  const deptRes = await getDepartments()
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
