import request from '@/utils/request'

export function getAllTags() {
  return request.get('/tag/list')
}

export function getTagsByFileId(fileId) {
  return request.get(`/tag/file/${fileId}`)
}

export function addTag(data) {
  return request.post('/tag', data)
}

export function updateTag(data) {
  return request.put('/tag', data)
}

export function deleteTag(id) {
  return request.delete(`/tag/${id}`)
}
