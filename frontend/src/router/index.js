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
