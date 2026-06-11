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
