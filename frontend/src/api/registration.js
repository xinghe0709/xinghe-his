import request from './request'

export function getRegistrations(params) {
  return request.get('/registrations', { params })
}

export function createRegistration(data) {
  return request.post('/registrations', data)
}

export function updateRegistration(id, data) {
  return request.put(`/registrations/${id}`, data)
}
