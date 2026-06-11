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
