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
import { getPrescriptionByRegistration } from '@/api/prescription'

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
