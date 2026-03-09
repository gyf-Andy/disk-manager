import request from '@/utils/request'

export function getCategoryTree() {
  return request.get('/category/tree')
}

export function getAllCategories() {
  return request.get('/category/list')
}

export function addCategory(data) {
  return request.post('/category', data)
}

export function updateCategory(data) {
  return request.put('/category', data)
}

export function deleteCategory(id) {
  return request.delete(`/category/${id}`)
}
