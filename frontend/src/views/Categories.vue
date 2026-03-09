<template>
  <div class="categories-page">
    <div class="page-header">
      <h2 class="page-title">分类管理</h2>
      <a-button type="primary" @click="showAddModal">
        <PlusOutlined />
        新建分类
      </a-button>
    </div>
    
    <a-row :gutter="24">
      <a-col :span="8">
        <a-card class="tree-card" title="分类树">
          <a-tree
            :tree-data="treeData"
            :field-names="{ title: 'name', key: 'id', children: 'children' }"
            default-expand-all
            @select="handleSelect"
          >
            <template #title="{ name, id }">
              <div class="tree-node">
                <span>{{ name }}</span>
                <a-space class="node-actions">
                  <a-button type="text" size="small" @click.stop="handleEdit(id)">
                    <EditOutlined />
                  </a-button>
                  <a-popconfirm
                    title="确定要删除这个分类吗？"
                    ok-text="确定"
                    cancel-text="取消"
                    @confirm="handleDelete(id)"
                  >
                    <a-button type="text" size="small" danger @click.stop>
                      <DeleteOutlined />
                    </a-button>
                  </a-popconfirm>
                </a-space>
              </div>
            </template>
          </a-tree>
        </a-card>
      </a-col>
      
      <a-col :span="16">
        <a-card class="list-card" title="分类列表">
          <a-table
            :columns="columns"
            :data-source="categories"
            :pagination="false"
            :loading="loading"
          >
            <template #bodyCell="{ column, record }">
              <template v-if="column.key === 'action'">
                <a-space>
                  <a-button type="text" size="small" @click="handleEdit(record.id)">
                    <EditOutlined />
                    编辑
                  </a-button>
                  <a-popconfirm
                    title="确定要删除这个分类吗？"
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
      </a-col>
    </a-row>
    
    <a-modal
      v-model:open="modalVisible"
      :title="isEdit ? '编辑分类' : '新建分类'"
      @ok="handleSubmit"
      :confirm-loading="submitLoading"
    >
      <a-form :model="form" layout="vertical">
        <a-form-item label="分类名称" required>
          <a-input v-model:value="form.name" placeholder="请输入分类名称" />
        </a-form-item>
        <a-form-item label="父分类">
          <a-tree-select
            v-model:value="form.parentId"
            :tree-data="treeData"
            :field-names="{ label: 'name', value: 'id', children: 'children' }"
            placeholder="请选择父分类"
            allow-clear
            tree-default-expand-all
          />
        </a-form-item>
        <a-form-item label="分类描述">
          <a-textarea v-model:value="form.description" placeholder="请输入分类描述" :rows="3" />
        </a-form-item>
        <a-form-item label="排序">
          <a-input-number v-model:value="form.sortOrder" :min="0" placeholder="排序值" style="width: 100%" />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import { PlusOutlined, EditOutlined, DeleteOutlined } from '@ant-design/icons-vue'
import { getCategoryTree, getAllCategories, addCategory, updateCategory, deleteCategory } from '@/api/category'

const loading = ref(false)
const categories = ref([])
const treeData = ref([])
const modalVisible = ref(false)
const isEdit = ref(false)
const submitLoading = ref(false)

const form = reactive({
  id: null,
  name: '',
  parentId: null,
  description: '',
  sortOrder: 0
})

const columns = [
  { title: 'ID', dataIndex: 'id', width: 80 },
  { title: '分类名称', dataIndex: 'name', width: 200 },
  { title: '描述', dataIndex: 'description' },
  { title: '排序', dataIndex: 'sortOrder', width: 80 },
  { title: '操作', key: 'action', width: 180 }
]

const loadCategories = async () => {
  loading.value = true
  try {
    const [listRes, treeRes] = await Promise.all([
      getAllCategories(),
      getCategoryTree()
    ])
    categories.value = listRes.data || []
    treeData.value = treeRes.data || []
  } catch (error) {
    console.error('加载分类失败', error)
  } finally {
    loading.value = false
  }
}

const handleSelect = (keys) => {
  if (keys.length > 0) {
    handleEdit(keys[0])
  }
}

const showAddModal = () => {
  isEdit.value = false
  form.id = null
  form.name = ''
  form.parentId = null
  form.description = ''
  form.sortOrder = 0
  modalVisible.value = true
}

const handleEdit = async (id) => {
  const category = categories.value.find(c => c.id === id)
  if (category) {
    isEdit.value = true
    form.id = category.id
    form.name = category.name
    form.parentId = category.parentId || null
    form.description = category.description
    form.sortOrder = category.sortOrder
    modalVisible.value = true
  }
}

const handleSubmit = async () => {
  if (!form.name) {
    message.warning('请输入分类名称')
    return
  }
  
  submitLoading.value = true
  try {
    if (isEdit.value) {
      await updateCategory(form)
      message.success('更新成功')
    } else {
      await addCategory(form)
      message.success('创建成功')
    }
    modalVisible.value = false
    loadCategories()
  } catch (error) {
    message.error('操作失败')
  } finally {
    submitLoading.value = false
  }
}

const handleDelete = async (id) => {
  try {
    await deleteCategory(id)
    message.success('删除成功')
    loadCategories()
  } catch (error) {
    message.error('删除失败')
  }
}

onMounted(() => {
  loadCategories()
})
</script>

<style lang="less" scoped>
.categories-page {
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
  
  .tree-card, .list-card {
    border-radius: 12px;
    height: 100%;
    
    .tree-node {
      display: flex;
      justify-content: space-between;
      align-items: center;
      width: 100%;
      
      .node-actions {
        opacity: 0;
        transition: opacity 0.3s;
      }
      
      &:hover .node-actions {
        opacity: 1;
      }
    }
  }
}
</style>
