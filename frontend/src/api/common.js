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
