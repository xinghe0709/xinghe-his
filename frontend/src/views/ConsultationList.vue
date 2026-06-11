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

    <el-dialog v-model="consultVisible" title="接诊" width="700px">
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

      <el-divider>开具处方</el-divider>
      <div v-for="(item, i) in prescriptionItems" :key="i" style="display:flex; gap:8px; margin-bottom:8px">
        <el-select v-model="item.medicineId" filterable placeholder="药品" style="flex:2">
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

onMounted(fetchData)
</script>
