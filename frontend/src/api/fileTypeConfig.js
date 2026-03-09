import request from '@/utils/request'

export function getAllConfigs() {
  return request.get('/file-type-config/list')
}

export function getEnabledExtensions() {
  return request.get('/file-type-config/enabled-extensions')
}

export function addConfig(data) {
  return request.post('/file-type-config', data)
}

export function updateConfig(data) {
  return request.put('/file-type-config', data)
}

export function deleteConfig(id) {
  return request.delete(`/file-type-config/${id}`)
}

export function toggleEnabled(id, enabled) {
  return request.put(`/file-type-config/toggle/${id}`, null, {
    params: { enabled }
  })
}

export function batchToggleEnabled(ids, enabled) {
  return request.put('/file-type-config/batch-toggle', ids, {
    params: { enabled }
  })
}
