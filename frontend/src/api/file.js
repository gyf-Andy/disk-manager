import request from '@/utils/request'

export function searchFiles(params) {
  return request.post('/file/search', params)
}

export function getFileDetail(id) {
  return request.get(`/file/${id}`)
}

export function updateFile(data) {
  return request.put('/file', data)
}

export function deleteFile(id) {
  return request.delete(`/file/${id}`)
}

export function deleteFiles(ids) {
  return request.delete('/file/batch', { data: ids })
}

export function addTagsToFile(fileId, tagIds) {
  return request.post('/file/tag', null, {
    params: { fileId, tagIds: tagIds.join(',') }
  })
}

export function removeTagFromFile(fileId, tagId) {
  return request.delete('/file/tag', {
    params: { fileId, tagId }
  })
}

export function uploadFile(file, categoryId, description) {
  const formData = new FormData()
  formData.append('file', file)
  if (categoryId) formData.append('categoryId', categoryId)
  if (description) formData.append('description', description)
  
  return request.post('/file/upload', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

export function downloadFile(id) {
  return `/api/file/download/${id}`
}

export function previewFile(id) {
  return `/api/file/preview/${id}`
}

export function getFilesByTag(tagId) {
  return request.get(`/file/tag/${tagId}`)
}
