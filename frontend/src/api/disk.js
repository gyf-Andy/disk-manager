import request from '@/utils/request'

export function getDiskList() {
  return request.get('/disk/list')
}

export function getDiskInfo(id) {
  return request.get(`/disk/${id}`)
}

export function refreshDisk(id) {
  return request.post(`/disk/refresh/${id}`)
}

export function scanDisk(id) {
  return request.post(`/disk/scan/${id}`)
}
