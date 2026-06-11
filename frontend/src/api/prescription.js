import request from './request'

export function createPrescription(data) {
  return request.post('/prescriptions', data)
}

export function getPrescription(id) {
  return request.get(`/prescriptions/${id}`)
}

export function getPrescriptionByRegistration(registrationId) {
  return request.get('/prescriptions', { params: { registrationId } })
}
