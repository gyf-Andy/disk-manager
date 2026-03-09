<template>
  <div class="disks-page">
    <div class="page-header">
      <h2 class="page-title">磁盘管理</h2>
      <a-button type="primary" @click="refreshDisks">
        <ReloadOutlined />
        刷新磁盘
      </a-button>
    </div>
    
    <a-row :gutter="24">
      <a-col :span="8" v-for="disk in disks" :key="disk.id">
        <a-card class="disk-card card-hover">
          <div class="disk-header">
            <div class="disk-icon">
              <HddOutlined />
            </div>
            <div class="disk-status">
              <a-tag :color="disk.status === 1 ? 'success' : 'error'">
                {{ disk.status === 1 ? '在线' : '离线' }}
              </a-tag>
            </div>
          </div>
          
          <div class="disk-name">{{ disk.diskName }}</div>
          <div class="disk-path">{{ disk.diskPath }}</div>
          
          <div class="disk-usage">
            <div class="usage-info">
              <span>{{ formatSize(disk.usedSpace) }} / {{ formatSize(disk.totalSpace) }}</span>
              <span>{{ getUsagePercent(disk) }}%</span>
            </div>
            <a-progress
              :percent="getUsagePercent(disk)"
              :stroke-color="getProgressColor(getUsagePercent(disk))"
              :show-info="false"
              stroke-linecap="round"
            />
          </div>
          
          <div class="disk-details">
            <div class="detail-item">
              <span class="label">类型</span>
              <span class="value">{{ disk.diskType || '未知' }}</span>
            </div>
            <div class="detail-item">
              <span class="label">可用空间</span>
              <span class="value">{{ formatSize(disk.freeSpace) }}</span>
            </div>
          </div>
          
          <div class="disk-actions">
            <a-button type="primary" ghost @click="scanDisk(disk.id)" :loading="scanningId === disk.id">
              <ScanOutlined />
              扫描文件
            </a-button>
            <a-button @click="viewFiles(disk)">
              <FolderOutlined />
              查看文件
            </a-button>
          </div>
        </a-card>
      </a-col>
    </a-row>
    
    <a-card class="info-card" title="磁盘说明">
      <a-alert
        message="磁盘扫描说明"
        description="点击'扫描文件'按钮将扫描该磁盘上的所有文件并添加到系统中。扫描过程可能需要一些时间，请耐心等待。"
        type="info"
        show-icon
      />
      <div class="tips">
        <h4>使用提示：</h4>
        <ul>
          <li>系统会自动检测系统挂载的硬盘</li>
          <li>Windows系统会显示所有盘符（如C:、D:等）</li>
          <li>Linux/Mac系统会显示挂载点</li>
          <li>扫描前请确保磁盘处于在线状态</li>
        </ul>
      </div>
    </a-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import { useRouter } from 'vue-router'
import { HddOutlined, ReloadOutlined, ScanOutlined, FolderOutlined } from '@ant-design/icons-vue'
import { getDiskList, scanDisk as scanDiskApi } from '@/api/disk'

const router = useRouter()
const disks = ref([])
const scanningId = ref(null)

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

const getUsagePercent = (disk) => {
  if (!disk.totalSpace) return 0
  return Math.round((disk.usedSpace / disk.totalSpace) * 100)
}

const getProgressColor = (percent) => {
  if (percent > 80) return '#ff4d4f'
  if (percent > 60) return '#faad14'
  return '#667eea'
}

const loadDisks = async () => {
  try {
    const res = await getDiskList()
    disks.value = res.data || []
  } catch (error) {
    console.error('加载磁盘失败', error)
  }
}

const refreshDisks = async () => {
  await loadDisks()
  message.success('磁盘列表已刷新')
}

const scanDisk = async (diskId) => {
  scanningId.value = diskId
  try {
    await scanDiskApi(diskId)
    message.success('磁盘扫描完成')
  } catch (error) {
    message.error('磁盘扫描失败')
  } finally {
    scanningId.value = null
  }
}

const viewFiles = (disk) => {
  router.push({
    path: '/files',
    query: { diskId: disk.id }
  })
}

onMounted(() => {
  loadDisks()
})
</script>

<style lang="less" scoped>
.disks-page {
  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24px;
    
    .page-title {
      font-size: 24px;
      font-weight: 600;
      color: #1a1a1a;
      margin: 0;
    }
  }
  
  .disk-card {
    border-radius: 16px;
    margin-bottom: 24px;
    
    .disk-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 16px;
      
      .disk-icon {
        width: 48px;
        height: 48px;
        border-radius: 12px;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 24px;
        color: #fff;
      }
    }
    
    .disk-name {
      font-size: 18px;
      font-weight: 600;
      margin-bottom: 8px;
    }
    
    .disk-path {
      font-size: 13px;
      color: #999;
      margin-bottom: 16px;
      font-family: monospace;
    }
    
    .disk-usage {
      margin-bottom: 16px;
      
      .usage-info {
        display: flex;
        justify-content: space-between;
        margin-bottom: 8px;
        font-size: 13px;
        color: #666;
      }
    }
    
    .disk-details {
      background: #f8f9fa;
      border-radius: 8px;
      padding: 12px;
      margin-bottom: 16px;
      
      .detail-item {
        display: flex;
        justify-content: space-between;
        padding: 4px 0;
        
        .label {
          color: #999;
          font-size: 13px;
        }
        
        .value {
          font-weight: 500;
          font-size: 13px;
        }
      }
    }
    
    .disk-actions {
      display: flex;
      gap: 12px;
      
      .ant-btn {
        flex: 1;
      }
    }
  }
  
  .info-card {
    border-radius: 12px;
    
    .tips {
      margin-top: 16px;
      
      h4 {
        margin-bottom: 8px;
      }
      
      ul {
        padding-left: 20px;
        color: #666;
        
        li {
          margin-bottom: 4px;
        }
      }
    }
  }
}
</style>
