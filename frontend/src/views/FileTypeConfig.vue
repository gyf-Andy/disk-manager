<template>
  <div class="file-type-config-page">
    <div class="page-header">
      <h2 class="page-title">文件类型配置</h2>
      <a-space>
        <a-button @click="handleBatchToggle(1)" :disabled="selectedRowKeys.length === 0">
          <CheckCircleOutlined />
          批量启用
        </a-button>
        <a-button @click="handleBatchToggle(0)" :disabled="selectedRowKeys.length === 0">
          <StopOutlined />
          批量禁用
        </a-button>
        <a-button type="primary" @click="showAddModal">
          <PlusOutlined />
          新增类型
        </a-button>
      </a-space>
    </div>
    
    <a-card class="info-card">
      <a-alert
        message="配置说明"
        description="在此配置需要扫描的文件类型。启用后的文件类型将在磁盘扫描时被识别并添加到系统中，未启用的类型将被忽略。"
        type="info"
        show-icon
        style="margin-bottom: 16px"
      />
    </a-card>
    
    <a-card class="table-card">
      <a-table
        :columns="columns"
        :data-source="configList"
        :loading="loading"
        :pagination="false"
        :row-selection="{ selectedRowKeys, onChange: onSelectChange }"
        row-key="id"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'extension'">
            <div class="extension-cell">
              <div class="extension-tag" :class="getExtensionClass(record.extension)">
                {{ record.extension.toUpperCase() }}
              </div>
              <span>{{ record.description || '暂无描述' }}</span>
            </div>
          </template>
          <template v-if="column.key === 'enabled'">
            <a-switch
              :checked="record.enabled === 1"
              @change="(checked) => handleToggle(record.id, checked ? 1 : 0)"
              checked-children="启用"
              un-checked-children="禁用"
            />
          </template>
          <template v-if="column.key === 'previewAvailable'">
            <a-tag :color="record.previewAvailable === 1 ? 'green' : 'default'">
              {{ record.previewAvailable === 1 ? '支持' : '不支持' }}
            </a-tag>
          </template>
          <template v-if="column.key === 'action'">
            <a-space>
              <a-button type="text" size="small" @click="handleEdit(record)">
                <EditOutlined />
                编辑
              </a-button>
              <a-popconfirm
                title="确定要删除这个文件类型配置吗？"
                ok-text="确定"
                cancel-text="取消"
                @confirm="handleDelete(record.id)"
              >
                <a-button type="text" size="small" danger>
                  <DeleteOutlined />
                  删除
                </a-button>
              </a-popconfirm>
            </a-space>
          </template>
        </template>
      </a-table>
    </a-card>
    
    <a-modal
      v-model:open="modalVisible"
      :title="isEdit ? '编辑文件类型' : '新增文件类型'"
      @ok="handleSubmit"
      :confirm-loading="submitLoading"
    >
      <a-form :model="form" layout="vertical">
        <a-form-item label="文件扩展名" required>
          <a-input
            v-model:value="form.extension"
            placeholder="请输入文件扩展名（如：pdf、docx）"
            :disabled="isEdit"
          >
            <template #prefix>.</template>
          </a-input>
        </a-form-item>
        <a-form-item label="类型描述">
          <a-input v-model:value="form.description" placeholder="请输入类型描述" />
        </a-form-item>
        <a-form-item label="是否启用">
          <a-switch v-model:checked="form.enabledBool" checked-children="启用" un-checked-children="禁用" />
        </a-form-item>
        <a-form-item label="是否支持预览">
          <a-switch v-model:checked="form.previewAvailableBool" checked-children="支持" un-checked-children="不支持" />
        </a-form-item>
        <a-form-item label="排序">
          <a-input-number v-model:value="form.sortOrder" :min="0" placeholder="排序值" style="width: 100%" />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import {
  PlusOutlined,
  EditOutlined,
  DeleteOutlined,
  CheckCircleOutlined,
  StopOutlined
} from '@ant-design/icons-vue'
import {
  getAllConfigs,
  addConfig,
  updateConfig,
  deleteConfig,
  toggleEnabled,
  batchToggleEnabled
} from '@/api/fileTypeConfig'

const loading = ref(false)
const configList = ref([])
const selectedRowKeys = ref([])

const modalVisible = ref(false)
const isEdit = ref(false)
const submitLoading = ref(false)

const form = reactive({
  id: null,
  extension: '',
  description: '',
  enabledBool: true,
  previewAvailableBool: false,
  sortOrder: 0
})

const columns = [
  { title: '扩展名', key: 'extension', width: 250 },
  { title: '启用状态', key: 'enabled', width: 120 },
  { title: '预览支持', key: 'previewAvailable', width: 100 },
  { title: '排序', dataIndex: 'sortOrder', width: 80 },
  { title: '操作', key: 'action', width: 180 }
]

const getExtensionClass = (ext) => {
  const typeMap = {
    pdf: 'pdf',
    doc: 'doc', docx: 'doc',
    xls: 'xls', xlsx: 'xls',
    ppt: 'ppt', pptx: 'ppt',
    jpg: 'img', jpeg: 'img', png: 'img', gif: 'img', bmp: 'img',
    mp4: 'video', avi: 'video', mkv: 'video',
    mp3: 'audio', wav: 'audio',
    zip: 'archive', rar: 'archive'
  }
  return typeMap[ext.toLowerCase()] || 'default'
}

const loadConfigs = async () => {
  loading.value = true
  try {
    const res = await getAllConfigs()
    configList.value = res.data || []
  } catch (error) {
    console.error('加载配置失败', error)
  } finally {
    loading.value = false
  }
}

const onSelectChange = (keys) => {
  selectedRowKeys.value = keys
}

const showAddModal = () => {
  isEdit.value = false
  form.id = null
  form.extension = ''
  form.description = ''
  form.enabledBool = true
  form.previewAvailableBool = false
  form.sortOrder = 0
  modalVisible.value = true
}

const handleEdit = (record) => {
  isEdit.value = true
  form.id = record.id
  form.extension = record.extension
  form.description = record.description
  form.enabledBool = record.enabled === 1
  form.previewAvailableBool = record.previewAvailable === 1
  form.sortOrder = record.sortOrder
  modalVisible.value = true
}

const handleSubmit = async () => {
  if (!form.extension) {
    message.warning('请输入文件扩展名')
    return
  }
  
  submitLoading.value = true
  try {
    const data = {
      id: form.id,
      extension: form.extension.toLowerCase().replace(/^\./, ''),
      description: form.description,
      enabled: form.enabledBool ? 1 : 0,
      previewAvailable: form.previewAvailableBool ? 1 : 0,
      sortOrder: form.sortOrder
    }
    
    if (isEdit.value) {
      await updateConfig(data)
      message.success('更新成功')
    } else {
      await addConfig(data)
      message.success('添加成功')
    }
    modalVisible.value = false
    loadConfigs()
  } catch (error) {
    message.error('操作失败')
  } finally {
    submitLoading.value = false
  }
}

const handleToggle = async (id, enabled) => {
  try {
    await toggleEnabled(id, enabled)
    message.success(enabled === 1 ? '已启用' : '已禁用')
    loadConfigs()
  } catch (error) {
    message.error('操作失败')
  }
}

const handleBatchToggle = async (enabled) => {
  if (selectedRowKeys.value.length === 0) {
    message.warning('请选择要操作的配置项')
    return
  }
  
  try {
    await batchToggleEnabled(selectedRowKeys.value, enabled)
    message.success(enabled === 1 ? '批量启用成功' : '批量禁用成功')
    selectedRowKeys.value = []
    loadConfigs()
  } catch (error) {
    message.error('操作失败')
  }
}

const handleDelete = async (id) => {
  try {
    await deleteConfig(id)
    message.success('删除成功')
    loadConfigs()
  } catch (error) {
    message.error('删除失败')
  }
}

onMounted(() => {
  loadConfigs()
})
</script>

<style lang="less" scoped>
.file-type-config-page {
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
  
  .info-card {
    margin-bottom: 16px;
    border-radius: 12px;
  }
  
  .table-card {
    border-radius: 12px;
    
    .extension-cell {
      display: flex;
      align-items: center;
      gap: 12px;
      
      .extension-tag {
        width: 48px;
        height: 32px;
        border-radius: 6px;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 11px;
        font-weight: 600;
        color: #fff;
        
        &.pdf { background: linear-gradient(135deg, #ff6b6b 0%, #ee5a5a 100%); }
        &.doc { background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%); }
        &.xls { background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%); }
        &.ppt { background: linear-gradient(135deg, #fa709a 0%, #fee140 100%); }
        &.img { background: linear-gradient(135deg, #a18cd1 0%, #fbc2eb 100%); }
        &.video { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); }
        &.audio { background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%); }
        &.archive { background: linear-gradient(135deg, #ffecd2 0%, #fcb69f 100%); color: #333; }
        &.default { background: linear-gradient(135deg, #bdc3c7 0%, #95a5a6 100%); }
      }
    }
  }
}
</style>
