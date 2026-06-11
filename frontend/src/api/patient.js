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
