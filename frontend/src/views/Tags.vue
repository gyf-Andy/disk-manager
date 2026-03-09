<template>
  <div class="tags-page">
    <div class="page-header">
      <h2 class="page-title">标签管理</h2>
      <a-button type="primary" @click="showAddModal">
        <PlusOutlined />
        新建标签
      </a-button>
    </div>
    
    <a-row :gutter="24">
      <a-col :span="6" v-for="tag in tags" :key="tag.id">
        <a-card class="tag-card card-hover">
          <div class="tag-content">
            <div class="tag-color" :style="{ background: tag.color }">
              <TagOutlined />
            </div>
            <div class="tag-info">
              <div class="tag-name">{{ tag.name }}</div>
              <div class="tag-desc">{{ tag.description || '暂无描述' }}</div>
            </div>
          </div>
          <div class="tag-actions">
            <a-button type="text" size="small" @click="handleEdit(tag)">
              <EditOutlined />
            </a-button>
            <a-popconfirm
              title="确定要删除这个标签吗？"
              ok-text="确定"
              cancel-text="取消"
              @confirm="handleDelete(tag.id)"
            >
              <a-button type="text" size="small" danger>
                <DeleteOutlined />
              </a-button>
            </a-popconfirm>
          </div>
        </a-card>
      </a-col>
    </a-row>
    
    <a-card class="search-card" title="按标签搜索文件">
      <div class="tag-search">
        <div class="tag-list">
          <a-tag
            v-for="tag in tags"
            :key="tag.id"
            :color="selectedTagId === tag.id ? tag.color : 'default'"
            class="search-tag"
            @click="searchByTag(tag.id)"
          >
            {{ tag.name }}
          </a-tag>
        </div>
      </div>
      
      <a-table
        v-if="selectedTagId"
        :columns="columns"
        :data-source="searchResults"
        :loading="searchLoading"
        :pagination="false"
        style="margin-top: 16px"
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
        </template>
      </a-table>
    </a-card>
    
    <a-modal
      v-model:open="modalVisible"
      :title="isEdit ? '编辑标签' : '新建标签'"
      @ok="handleSubmit"
      :confirm-loading="submitLoading"
    >
      <a-form :model="form" layout="vertical">
        <a-form-item label="标签名称" required>
          <a-input v-model:value="form.name" placeholder="请输入标签名称" />
        </a-form-item>
        <a-form-item label="标签颜色">
          <div class="color-picker">
            <div
              v-for="color in colorOptions"
              :key="color"
              class="color-item"
              :style="{ background: color }"
              :class="{ active: form.color === color }"
              @click="form.color = color"
            />
          </div>
        </a-form-item>
        <a-form-item label="标签描述">
          <a-textarea v-model:value="form.description" placeholder="请输入标签描述" :rows="3" />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import { PlusOutlined, EditOutlined, DeleteOutlined, TagOutlined } from '@ant-design/icons-vue'
import { getAllTags, addTag, updateTag, deleteTag } from '@/api/tag'
import { getFilesByTag } from '@/api/file'

const tags = ref([])
const modalVisible = ref(false)
const isEdit = ref(false)
const submitLoading = ref(false)
const selectedTagId = ref(null)
const searchResults = ref([])
const searchLoading = ref(false)

const form = reactive({
  id: null,
  name: '',
  color: '#1890ff',
  description: ''
})

const colorOptions = [
  '#f5222d', '#fa541c', '#fa8c16', '#faad14', '#fadb14',
  '#a0d911', '#52c41a', '#13c2c2', '#1890ff', '#2f54eb',
  '#722ed1', '#eb2f96', '#667eea', '#764ba2'
]

const columns = [
  { title: '文件名', key: 'fileName', width: 300 },
  { title: '文件类型', dataIndex: 'fileType', width: 100 },
  { title: '文件大小', key: 'fileSize', width: 120 }
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

const loadTags = async () => {
  try {
    const res = await getAllTags()
    tags.value = res.data || []
  } catch (error) {
    console.error('加载标签失败', error)
  }
}

const showAddModal = () => {
  isEdit.value = false
  form.id = null
  form.name = ''
  form.color = '#1890ff'
  form.description = ''
  modalVisible.value = true
}

const handleEdit = (tag) => {
  isEdit.value = true
  form.id = tag.id
  form.name = tag.name
  form.color = tag.color
  form.description = tag.description
  modalVisible.value = true
}

const handleSubmit = async () => {
  if (!form.name) {
    message.warning('请输入标签名称')
    return
  }
  
  submitLoading.value = true
  try {
    if (isEdit.value) {
      await updateTag(form)
      message.success('更新成功')
    } else {
      await addTag(form)
      message.success('创建成功')
    }
    modalVisible.value = false
    loadTags()
  } catch (error) {
    message.error('操作失败')
  } finally {
    submitLoading.value = false
  }
}

const handleDelete = async (id) => {
  try {
    await deleteTag(id)
    message.success('删除成功')
    if (selectedTagId.value === id) {
      selectedTagId.value = null
      searchResults.value = []
    }
    loadTags()
  } catch (error) {
    message.error('删除失败')
  }
}

const searchByTag = async (tagId) => {
  selectedTagId.value = tagId
  searchLoading.value = true
  try {
    const res = await getFilesByTag(tagId)
    searchResults.value = res.data || []
  } catch (error) {
    console.error('搜索失败', error)
  } finally {
    searchLoading.value = false
  }
}

onMounted(() => {
  loadTags()
})
</script>

<style lang="less" scoped>
.tags-page {
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
  
  .tag-card {
    border-radius: 12px;
    margin-bottom: 24px;
    
    .tag-content {
      display: flex;
      align-items: center;
      gap: 12px;
      margin-bottom: 12px;
      
      .tag-color {
        width: 40px;
        height: 40px;
        border-radius: 10px;
        display: flex;
        align-items: center;
        justify-content: center;
        color: #fff;
        font-size: 18px;
      }
      
      .tag-info {
        flex: 1;
        
        .tag-name {
          font-weight: 600;
          margin-bottom: 4px;
        }
        
        .tag-desc {
          font-size: 12px;
          color: #999;
        }
      }
    }
    
    .tag-actions {
      display: flex;
      justify-content: flex-end;
      gap: 8px;
    }
  }
  
  .search-card {
    margin-top: 24px;
    border-radius: 12px;
    
    .tag-search {
      .tag-list {
        display: flex;
        flex-wrap: wrap;
        gap: 8px;
        
        .search-tag {
          cursor: pointer;
          transition: all 0.3s;
          
          &:hover {
            transform: scale(1.05);
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
  
  .color-picker {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
    
    .color-item {
      width: 32px;
      height: 32px;
      border-radius: 6px;
      cursor: pointer;
      transition: all 0.3s;
      border: 2px solid transparent;
      
      &:hover {
        transform: scale(1.1);
      }
      
      &.active {
        border-color: #1a1a1a;
        box-shadow: 0 0 0 2px rgba(0, 0, 0, 0.1);
      }
    }
  }
}
</style>
