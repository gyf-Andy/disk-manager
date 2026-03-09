import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    redirect: '/dashboard'
  },
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: () => import('@/views/Dashboard.vue')
  },
  {
    path: '/files',
    name: 'Files',
    component: () => import('@/views/Files.vue')
  },
  {
    path: '/disks',
    name: 'Disks',
    component: () => import('@/views/Disks.vue')
  },
  {
    path: '/tags',
    name: 'Tags',
    component: () => import('@/views/Tags.vue')
  },
  {
    path: '/categories',
    name: 'Categories',
    component: () => import('@/views/Categories.vue')
  },
  {
    path: '/file-type-config',
    name: 'FileTypeConfig',
    component: () => import('@/views/FileTypeConfig.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
