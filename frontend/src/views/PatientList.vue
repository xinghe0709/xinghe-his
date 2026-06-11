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
const rules = { name: [{ required: true, message: '请输入姓名', trigger: 'blur' }] }

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
