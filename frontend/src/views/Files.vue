<template>
  <div class="files-page">
    <div class="page-header">
      <h2 class="page-title">文件管理</h2>
      <a-space>
        <a-button type="primary" @click="showUploadModal">
          <UploadOutlined />
          上传文件
        </a-button>
      </a-space>
    </div>
    
    <a-card class="search-card">
      <a-form layout="inline" :model="searchForm">
        <a-form-item label="关键词">
          <a-input v-model:value="searchForm.keyword" placeholder="搜索文件名或描述" allow-clear style="width: 200px" />
        </a-form-item>
        <a-form-item label="文件类型">
          <a-select v-model:value="searchForm.fileType" placeholder="选择类型" allow-clear style="width: 120px">
            <a-select-option value="pdf">PDF</a-select-option>
            <a-select-option value="doc">DOC</a-select-option>
            <a-select-option value="docx">DOCX</a-select-option>
            <a-select-option value="xls">XLS</a-select-option>
            <a-select-option value="xlsx">XLSX</a-select-option>
            <a-select-option value="jpg">JPG</a-select-option>
            <a-select-option value="png">PNG</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="分类">
          <a-select v-model:value="searchForm.categoryId" placeholder="选择分类" allow-clear style="width: 150px">
            <a-select-option v-for="cat in categories" :key="cat.id" :value="cat.id">
              {{ cat.name }}
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="标签">
          <a-select v-model:value="searchForm.tagIds" mode="multiple" placeholder="选择标签" allow-clear style="width: 200px">
            <a-select-option v-for="tag in tags" :key="tag.id" :value="tag.id">
              <a-tag :color="tag.color">{{ tag.name }}</a-tag>
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item>
          <a-space>
            <a-button type="primary" @click="handleSearch">
              <SearchOutlined />
              搜索
            </a-button>
            <a-button @click="handleReset">重置</a-button>
          </a-space>
        </a-form-item>
      </a-form>
    </a-card>
    
    <a-card class="table-card">
      <a-table
        :columns="columns"
        :data-source="fileList"
        :loading="loading"
        :pagination="pagination"
        :row-selection="{ selectedRowKeys, onChange: onSelectChange }"
        @change="handleTableChange"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'fileName'">
            <div class="file-name-cell">
              <div class="file-type-icon" :class="record.fileType.toLowerCase()">
                {{ record.fileType.toUpperCase() }}
              </div>
              <div class="file-info">
                <div class="file-name">{{ record.fileName }}</div>
                <div class="file-desc">{{ record.description || '暂无描述' }}</div>
              </div>
            </div>
          </template>
          <template v-if="column.key === 'fileSize'">
            {{ formatSize(record.fileSize) }}
          </template>
          <template v-if="column.key === 'tags'">
            <a-tag v-for="tag in record.tags" :key="tag.id" :color="tag.color" style="margin-bottom: 4px">
              {{ tag.name }}
            </a-tag>
            <span v-if="!record.tags?.length" class="no-tags">暂无标签</span>
          </template>
          <template v-if="column.key === 'createTime'">
            {{ formatDate(record.createTime) }}
          </template>
          <template v-if="column.key === 'action'">
            <a-space>
              <a-tooltip title="预览">
                <a-button type="text" size="small" @click="handlePreview(record)" :disabled="!record.previewAvailable">
                  <EyeOutlined />
                </a-button>
              </a-tooltip>
              <a-tooltip title="下载">
                <a-button type="text" size="small" @click="handleDownload(record)">
                  <DownloadOutlined />
                </a-button>
              </a-tooltip>
              <a-tooltip title="编辑">
                <a-button type="text" size="small" @click="handleEdit(record)">
                  <EditOutlined />
                </a-button>
              </a-tooltip>
              <a-popconfirm
                title="确定要删除这个文件吗？"
                ok-text="确定"
                cancel-text="取消"
                @confirm="handleDelete(record)"
              >
                <a-tooltip title="删除">
                  <a-button type="text" size="small" danger>
                    <DeleteOutlined />
                  </a-button>
                </a-tooltip>
              </a-popconfirm>
            </a-space>
          </template>
        </template>
      </a-table>
      
      <div v-if="selectedRowKeys.length > 0" class="batch-actions">
        <span>已选择 {{ selectedRowKeys.length }} 项</span>
        <a-popconfirm
          title="确定要删除选中的文件吗？"
          ok-text="确定"
          cancel-text="取消"
          @confirm="handleBatchDelete"
        >
          <a-button danger>批量删除</a-button>
        </a-popconfirm>
      </div>
    </a-card>
    
    <a-modal
      v-model:open="uploadVisible"
      title="上传文件"
      :footer="null"
      width="500px"
    >
      <a-upload-dragger
        :before-upload="beforeUpload"
        :file-list="uploadFileList"
        @remove="handleUploadRemove"
      >
        <p class="ant-upload-drag-icon">
          <InboxOutlined />
        </p>
        <p class="ant-upload-text">点击或拖拽文件到此区域上传</p>
        <p class="ant-upload-hint">支持单个或批量上传</p>
      </a-upload-dragger>
      <a-form :model="uploadForm" layout="vertical" style="margin-top: 16px">
        <a-form-item label="分类">
          <a-select v-model:value="uploadForm.categoryId" placeholder="选择分类" allow-clear>
            <a-select-option v-for="cat in categories" :key="cat.id" :value="cat.id">
              {{ cat.name }}
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="描述">
          <a-textarea v-model:value="uploadForm.description" placeholder="文件描述" :rows="3" />
        </a-form-item>
        <a-form-item>
          <a-button type="primary" :loading="uploading" @click="handleUpload">
            开始上传
          </a-button>
        </a-form-item>
      </a-form>
    </a-modal>
    
    <a-modal
      v-model:open="editVisible"
      title="编辑文件"
      @ok="handleEditSubmit"
      :confirm-loading="editLoading"
    >
      <a-form :model="editForm" layout="vertical">
        <a-form-item label="文件名">
          <a-input v-model:value="editForm.fileName" placeholder="请输入文件名" />
        </a-form-item>
        <a-form-item label="分类">
          <a-select v-model:value="editForm.categoryId" placeholder="选择分类" allow-clear>
            <a-select-option v-for="cat in categories" :key="cat.id" :value="cat.id">
              {{ cat.name }}
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="标签">
          <a-select v-model:value="editForm.tagIds" mode="multiple" placeholder="选择标签" allow-clear>
            <a-select-option v-for="tag in tags" :key="tag.id" :value="tag.id">
              <a-tag :color="tag.color">{{ tag.name }}</a-tag>
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="描述">
          <a-textarea v-model:value="editForm.description" placeholder="文件描述" :rows="3" />
        </a-form-item>
      </a-form>
    </a-modal>
    
    <a-modal
      v-model:open="previewVisible"
      :title="previewFile?.fileName"
      :footer="null"
      width="900px"
      centered
      :body-style="{ padding: '0', height: '75vh' }"
    >
      <div class="preview-container">
        <iframe v-if="previewFile" :src="previewUrl" class="preview-iframe" />
      </div>
    </a-modal>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import {
  UploadOutlined,
  SearchOutlined,
  EyeOutlined,
  DownloadOutlined,
  EditOutlined,
  DeleteOutlined,
  InboxOutlined
} from '@ant-design/icons-vue'
import { searchFiles, getFileDetail, updateFile, deleteFile, deleteFiles, uploadFile, previewFile as getPreviewUrl } from '@/api/file'
import { getAllTags, getTagsByFileId } from '@/api/tag'
import { getAllCategories } from '@/api/category'
import dayjs from 'dayjs'

const loading = ref(false)
const fileList = ref([])
const categories = ref([])
const tags = ref([])
const selectedRowKeys = ref([])

const searchForm = reactive({
  keyword: '',
  fileType: null,
  categoryId: null,
  tagIds: []
})

const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0,
  showSizeChanger: true,
  showTotal: (total) => `共 ${total} 条`
})

const columns = [
  { title: '文件名', key: 'fileName', width: 300 },
  { title: '文件类型', dataIndex: 'fileType', width: 100 },
  { title: '文件大小', key: 'fileSize', width: 120 },
  { title: '标签', key: 'tags', width: 200 },
  { title: '创建时间', key: 'createTime', width: 180 },
  { title: '操作', key: 'action', width: 180, fixed: 'right' }
]

const uploadVisible = ref(false)
const uploadFileList = ref([])
const uploadForm = reactive({
  categoryId: null,
  description: ''
})
const uploading = ref(false)

const editVisible = ref(false)
const editLoading = ref(false)
const editForm = reactive({
  id: null,
  fileName: '',
  categoryId: null,
  tagIds: [],
  description: ''
})

const previewVisible = ref(false)
const previewFile = ref(null)
const previewUrl = ref('')

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
  return dayjs(date).format('YYYY-MM-DD HH:mm')
}

const loadCategories = async () => {
  try {
    const res = await getAllCategories()
    categories.value = res.data || []
  } catch (error) {
    console.error('加载分类失败', error)
  }
}

const loadTags = async () => {
  try {
    const res = await getAllTags()
    tags.value = res.data || []
  } catch (error) {
    console.error('加载标签失败', error)
  }
}

const loadFiles = async () => {
  loading.value = true
  try {
    const res = await searchFiles({
      ...searchForm,
      pageNum: pagination.current,
      pageSize: pagination.pageSize
    })
    fileList.value = res.data?.records || []
    pagination.total = res.data?.total || 0
    
    for (let file of fileList.value) {
      try {
        const tagRes = await getTagsByFileId(file.id)
        file.tags = tagRes.data || []
      } catch (e) {
        file.tags = []
      }
    }
  } catch (error) {
    console.error('加载文件失败', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.current = 1
  loadFiles()
}

const handleReset = () => {
  searchForm.keyword = ''
  searchForm.fileType = null
  searchForm.categoryId = null
  searchForm.tagIds = []
  handleSearch()
}

const handleTableChange = (pag) => {
  pagination.current = pag.current
  pagination.pageSize = pag.pageSize
  loadFiles()
}

const onSelectChange = (keys) => {
  selectedRowKeys.value = keys
}

const showUploadModal = () => {
  uploadVisible.value = true
  uploadFileList.value = []
  uploadForm.categoryId = null
  uploadForm.description = ''
}

const beforeUpload = (file) => {
  uploadFileList.value = [...uploadFileList.value, file]
  return false
}

const handleUploadRemove = (file) => {
  const index = uploadFileList.value.indexOf(file)
  uploadFileList.value.splice(index, 1)
}

const handleUpload = async () => {
  if (uploadFileList.value.length === 0) {
    message.warning('请选择要上传的文件')
    return
  }
  
  uploading.value = true
  try {
    for (let file of uploadFileList.value) {
      await uploadFile(file, uploadForm.categoryId, uploadForm.description)
    }
    message.success('上传成功')
    uploadVisible.value = false
    loadFiles()
  } catch (error) {
    message.error('上传失败')
  } finally {
    uploading.value = false
  }
}

const handlePreview = (record) => {
  previewFile.value = record
  previewUrl.value = getPreviewUrl(record.id)
  previewVisible.value = true
}

const handleDownload = (record) => {
  const url = `/api/file/download/${record.id}`
  window.open(url, '_blank')
}

const handleEdit = async (record) => {
  editForm.id = record.id
  editForm.fileName = record.fileName
  editForm.categoryId = record.categoryId
  editForm.description = record.description
  
  try {
    const tagRes = await getTagsByFileId(record.id)
    editForm.tagIds = (tagRes.data || []).map(t => t.id)
  } catch (e) {
    editForm.tagIds = []
  }
  
  editVisible.value = true
}

const handleEditSubmit = async () => {
  editLoading.value = true
  try {
    await updateFile(editForm)
    message.success('更新成功')
    editVisible.value = false
    loadFiles()
  } catch (error) {
    message.error('更新失败')
  } finally {
    editLoading.value = false
  }
}

const handleDelete = async (record) => {
  try {
    await deleteFile(record.id)
    message.success('删除成功')
    loadFiles()
  } catch (error) {
    message.error('删除失败')
  }
}

const handleBatchDelete = async () => {
  try {
    await deleteFiles(selectedRowKeys.value)
    message.success('批量删除成功')
    selectedRowKeys.value = []
    loadFiles()
  } catch (error) {
    message.error('批量删除失败')
  }
}

onMounted(() => {
  loadCategories()
  loadTags()
  loadFiles()
})
</script>

<style lang="less" scoped>
.files-page {
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
  
  .search-card {
    margin-bottom: 16px;
    border-radius: 12px;
  }
  
  .table-card {
    border-radius: 12px;
    
    .file-name-cell {
      display: flex;
      align-items: center;
      gap: 12px;
      
      .file-type-icon {
        width: 40px;
        height: 40px;
        font-size: 10px;
        flex-shrink: 0;
      }
      
      .file-info {
        .file-name {
          font-weight: 500;
          margin-bottom: 4px;
        }
        
        .file-desc {
          font-size: 12px;
          color: #999;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
          max-width: 200px;
        }
      }
    }
    
    .no-tags {
      color: #999;
      font-size: 12px;
    }
    
    .batch-actions {
      margin-top: 16px;
      padding-top: 16px;
      border-top: 1px solid #f0f0f0;
      display: flex;
      align-items: center;
      gap: 16px;
    }
  }
  
  .preview-container {
    width: 100%;
    height: 100%;
    background: #f5f5f5;
    
    .preview-iframe {
      width: 100%;
      height: 100%;
      border: none;
      display: block;
    }
  }
}
</style>

<style lang="less">
.preview-container {
  width: 100%;
  height: 100%;
  background: #f5f5f5;
  
  .preview-iframe {
    width: 100%;
    height: 100%;
    border: none;
    display: block;
  }
}
</style>
