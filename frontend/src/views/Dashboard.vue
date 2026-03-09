<template>
  <div class="dashboard-page">
    <div class="page-header">
      <h2 class="page-title">仪表盘</h2>
      <p class="page-desc">欢迎使用文件管理系统</p>
    </div>
    
    <a-row :gutter="24" class="stat-row">
      <a-col :span="6">
        <div class="stat-card card-hover">
          <div class="stat-icon">
            <FileOutlined />
          </div>
          <div class="stat-value">{{ stats.totalFiles }}</div>
          <div class="stat-label">文件总数</div>
        </div>
      </a-col>
      <a-col :span="6">
        <div class="stat-card card-hover" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);">
          <div class="stat-icon">
            <HddOutlined />
          </div>
          <div class="stat-value">{{ stats.totalDisks }}</div>
          <div class="stat-label">磁盘数量</div>
        </div>
      </a-col>
      <a-col :span="6">
        <div class="stat-card card-hover" style="background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);">
          <div class="stat-icon">
            <TagsOutlined />
          </div>
          <div class="stat-value">{{ stats.totalTags }}</div>
          <div class="stat-label">标签数量</div>
        </div>
      </a-col>
      <a-col :span="6">
        <div class="stat-card card-hover" style="background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);">
          <div class="stat-icon">
            <AppstoreOutlined />
          </div>
          <div class="stat-value">{{ stats.totalCategories }}</div>
          <div class="stat-label">分类数量</div>
        </div>
      </a-col>
    </a-row>
    
    <a-row :gutter="24" class="content-row">
      <a-col :span="16">
        <a-card title="磁盘使用情况" class="info-card">
          <template #extra>
            <a-button type="link" @click="$router.push('/disks')">查看详情</a-button>
          </template>
          <div class="disk-list">
            <div v-for="disk in disks" :key="disk.id" class="disk-item">
              <div class="disk-info">
                <div class="disk-name">
                  <HddOutlined />
                  <span>{{ disk.diskName }}</span>
                </div>
                <div class="disk-size">
                  {{ formatSize(disk.usedSpace) }} / {{ formatSize(disk.totalSpace) }}
                </div>
              </div>
              <a-progress
                :percent="getUsagePercent(disk)"
                :stroke-color="getProgressColor(getUsagePercent(disk))"
                :show-info="false"
              />
            </div>
          </div>
        </a-card>
      </a-col>
      <a-col :span="8">
        <a-card title="文件类型分布" class="info-card">
          <div class="file-type-list">
            <div v-for="item in fileTypeStats" :key="item.type" class="file-type-item">
              <div class="file-type-icon" :class="item.type.toLowerCase()">
                {{ item.type.toUpperCase() }}
              </div>
              <div class="file-type-info">
                <div class="file-type-name">{{ item.type.toUpperCase() }} 文件</div>
                <div class="file-type-count">{{ item.count }} 个文件</div>
              </div>
            </div>
          </div>
        </a-card>
      </a-col>
    </a-row>
    
    <a-row :gutter="24">
      <a-col :span="24">
        <a-card title="最近文件" class="info-card">
          <template #extra>
            <a-button type="link" @click="$router.push('/files')">查看全部</a-button>
          </template>
          <a-table
            :columns="columns"
            :data-source="recentFiles"
            :pagination="false"
            :loading="loading"
          >
            <template #bodyCell="{ column, record }">
              <template v-if="column.key === 'fileName'">
                <div class="file-name-cell">
                  <div class="file-type-icon small" :class="record.fileType.toLowerCase()">
                    {{ record.fileType.toUpperCase() }}
                  </div>
                  <span>{{ record.fileName }}</span>
                </div>
              </template>
              <template v-if="column.key === 'fileSize'">
                {{ formatSize(record.fileSize) }}
              </template>
              <template v-if="column.key === 'createTime'">
                {{ formatDate(record.createTime) }}
              </template>
            </template>
          </a-table>
        </a-card>
      </a-col>
    </a-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { FileOutlined, HddOutlined, TagsOutlined, AppstoreOutlined } from '@ant-design/icons-vue'
import { getDiskList } from '@/api/disk'
import { searchFiles } from '@/api/file'
import { getAllTags } from '@/api/tag'
import { getAllCategories } from '@/api/category'
import dayjs from 'dayjs'

const loading = ref(false)
const stats = ref({
  totalFiles: 0,
  totalDisks: 0,
  totalTags: 0,
  totalCategories: 0
})
const disks = ref([])
const recentFiles = ref([])
const fileTypeStats = ref([])

const columns = [
  { title: '文件名', key: 'fileName', dataIndex: 'fileName' },
  { title: '文件类型', dataIndex: 'fileType', width: 100 },
  { title: '文件大小', key: 'fileSize', dataIndex: 'fileSize', width: 120 },
  { title: '创建时间', key: 'createTime', dataIndex: 'createTime', width: 180 }
]

const formatSize = (bytes) => {
  if (!bytes) return '0 B'
  const units = ['B', 'KB', 'MB', 'GB', 'TB']
  let i = 0
  while (bytes >= 1024 && i < units.length - 1) {
    bytes /= 1024
    i++
  }
  return bytes.toFixed(2) + ' ' + units[i]
}

const formatDate = (date) => {
  return dayjs(date).format('YYYY-MM-DD HH:mm:ss')
}

const getUsagePercent = (disk) => {
  if (!disk.totalSpace) return 0
  return Math.round((disk.usedSpace / disk.totalSpace) * 100)
}

const getProgressColor = (percent) => {
  if (percent > 80) return '#ff4d4f'
  if (percent > 60) return '#faad14'
  return '#667eea'
}

const loadData = async () => {
  loading.value = true
  try {
    const [diskRes, fileRes, tagRes, categoryRes] = await Promise.all([
      getDiskList(),
      searchFiles({ pageNum: 1, pageSize: 10 }),
      getAllTags(),
      getAllCategories()
    ])
    
    disks.value = diskRes.data || []
    recentFiles.value = fileRes.data?.records || []
    stats.value = {
      totalFiles: fileRes.data?.total || 0,
      totalDisks: diskRes.data?.length || 0,
      totalTags: tagRes.data?.length || 0,
      totalCategories: categoryRes.data?.length || 0
    }
    
    const typeMap = {}
    recentFiles.value.forEach(file => {
      const type = file.fileType || 'other'
      typeMap[type] = (typeMap[type] || 0) + 1
    })
    fileTypeStats.value = Object.entries(typeMap)
      .map(([type, count]) => ({ type, count }))
      .slice(0, 5)
  } catch (error) {
    console.error('加载数据失败', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadData()
})
</script>

<style lang="less" scoped>
.dashboard-page {
  .page-header {
    margin-bottom: 24px;
    
    .page-title {
      font-size: 24px;
      font-weight: 600;
      color: #1a1a1a;
      margin-bottom: 8px;
    }
    
    .page-desc {
      color: #666;
      font-size: 14px;
    }
  }
  
  .stat-row {
    margin-bottom: 24px;
  }
  
  .content-row {
    margin-bottom: 24px;
  }
  
  .info-card {
    border-radius: 12px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
    
    :deep(.ant-card-head) {
      border-bottom: 1px solid #f0f0f0;
    }
  }
  
  .disk-list {
    .disk-item {
      margin-bottom: 20px;
      
      &:last-child {
        margin-bottom: 0;
      }
      
      .disk-info {
        display: flex;
        justify-content: space-between;
        margin-bottom: 8px;
        
        .disk-name {
          display: flex;
          align-items: center;
          gap: 8px;
          font-weight: 500;
        }
        
        .disk-size {
          color: #666;
          font-size: 13px;
        }
      }
    }
  }
  
  .file-type-list {
    .file-type-item {
      display: flex;
      align-items: center;
      gap: 12px;
      padding: 12px 0;
      border-bottom: 1px solid #f0f0f0;
      
      &:last-child {
        border-bottom: none;
      }
      
      .file-type-icon {
        width: 40px;
        height: 40px;
        font-size: 12px;
      }
      
      .file-type-info {
        .file-type-name {
          font-weight: 500;
          margin-bottom: 4px;
        }
        
        .file-type-count {
          font-size: 12px;
          color: #999;
        }
      }
    }
  }
  
  .file-name-cell {
    display: flex;
    align-items: center;
    gap: 12px;
    
    .file-type-icon.small {
      width: 32px;
      height: 32px;
      font-size: 10px;
    }
  }
}
</style>
