<template>
  <a-config-provider :theme="themeConfig">
    <a-layout class="app-layout">
      <a-layout-sider
        v-model:collapsed="collapsed"
        :trigger="null"
        collapsible
        class="app-sider"
        width="260"
      >
        <div class="logo">
          <div class="logo-icon">
            <FolderOutlined />
          </div>
          <span v-show="!collapsed" class="logo-text">专项部试题资源管理系统</span>
        </div>
        <a-menu
          v-model:selectedKeys="selectedKeys"
          theme="dark"
          mode="inline"
          class="sider-menu"
        >
          <a-menu-item key="dashboard" @click="navigateTo('/dashboard')">
            <DashboardOutlined />
            <span>仪表盘</span>
          </a-menu-item>
          <a-menu-item key="files" @click="navigateTo('/files')">
            <FileOutlined />
            <span>文件管理</span>
          </a-menu-item>
          <a-menu-item key="disks" @click="navigateTo('/disks')">
            <HddOutlined />
            <span>磁盘管理</span>
          </a-menu-item>
          <a-menu-item key="tags" @click="navigateTo('/tags')">
            <TagsOutlined />
            <span>标签管理</span>
          </a-menu-item>
          <a-menu-item key="categories" @click="navigateTo('/categories')">
            <AppstoreOutlined />
            <span>分类管理</span>
          </a-menu-item>
          <a-menu-item key="file-type-config" @click="navigateTo('/file-type-config')">
            <SettingOutlined />
            <span>文件类型配置</span>
          </a-menu-item>
        </a-menu>
      </a-layout-sider>
      <a-layout>
        <a-layout-header class="app-header">
          <div class="header-left">
            <MenuUnfoldOutlined
              v-if="collapsed"
              class="trigger"
              @click="collapsed = !collapsed"
            />
            <MenuFoldOutlined
              v-else
              class="trigger"
              @click="collapsed = !collapsed"
            />
          </div>
          <div class="header-right">
            <a-space>
              <a-badge :count="5" :offset="[-5, 5]">
                <a-button type="text" shape="circle">
                  <BellOutlined />
                </a-button>
              </a-badge>
              <a-dropdown>
                <a-avatar class="user-avatar" :style="{ backgroundColor: '#667eea' }">
                  <template #icon><UserOutlined /></template>
                </a-avatar>
                <template #overlay>
                  <a-menu>
                    <a-menu-item key="settings">
                      <SettingOutlined />
                      系统设置
                    </a-menu-item>
                    <a-menu-divider />
                    <a-menu-item key="logout">
                      <LogoutOutlined />
                      退出登录
                    </a-menu-item>
                  </a-menu>
                </template>
              </a-dropdown>
            </a-space>
          </div>
        </a-layout-header>
        <a-layout-content class="app-content">
          <router-view v-slot="{ Component }">
            <transition name="fade" mode="out-in">
              <component :is="Component" />
            </transition>
          </router-view>
        </a-layout-content>
      </a-layout>
    </a-layout>
  </a-config-provider>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import {
  FolderOutlined,
  DashboardOutlined,
  FileOutlined,
  HddOutlined,
  TagsOutlined,
  AppstoreOutlined,
  MenuFoldOutlined,
  MenuUnfoldOutlined,
  BellOutlined,
  UserOutlined,
  SettingOutlined,
  LogoutOutlined
} from '@ant-design/icons-vue'

const router = useRouter()
const route = useRoute()

const collapsed = ref(false)
const selectedKeys = ref(['dashboard'])

const themeConfig = {
  token: {
    colorPrimary: '#667eea',
    borderRadius: 8,
    colorBgContainer: '#ffffff'
  }
}

const navigateTo = (path) => {
  router.push(path)
}
</script>

<style lang="less" scoped>
.app-layout {
  min-height: 100vh;
}

.app-sider {
  background: linear-gradient(180deg, #1a1f36 0%, #252d4a 100%);
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.15);
  
  .logo {
    height: 64px;
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 16px;
    background: rgba(255, 255, 255, 0.05);
    
    .logo-icon {
      font-size: 28px;
      color: #667eea;
      display: flex;
      align-items: center;
    }
    
    .logo-text {
      margin-left: 12px;
      font-size: 18px;
      font-weight: 600;
      color: #fff;
      white-space: nowrap;
    }
  }
  
  .sider-menu {
    background: transparent;
    border: none;
    
    :deep(.ant-menu-item) {
      margin: 4px 8px;
      border-radius: 8px;
      height: 48px;
      line-height: 48px;
      
      &:hover {
        background: rgba(255, 255, 255, 0.1);
      }
      
      &.ant-menu-item-selected {
        background: linear-gradient(90deg, #667eea 0%, #764ba2 100%);
        
        &::after {
          display: none;
        }
      }
    }
  }
}

.app-header {
  background: #fff;
  padding: 0 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
  
  .trigger {
    font-size: 18px;
    cursor: pointer;
    transition: color 0.3s;
    padding: 0 12px;
    
    &:hover {
      color: #667eea;
    }
  }
  
  .user-avatar {
    cursor: pointer;
  }
}

.app-content {
  margin: 24px;
  padding: 24px;
  background: #fff;
  border-radius: 12px;
  min-height: calc(100vh - 112px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
