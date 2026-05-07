<template>
  <div>
    <el-card shadow="never">
      <div class="mb-16"><el-button type="primary" @click="handleAdd">新增菜单</el-button></div>

      <el-table :data="tableData" v-loading="loading" stripe border row-key="id" default-expand-all
        :tree-props="{ children: 'children', hasChildren: 'hasChildren' }">
        <el-table-column prop="menuName" label="菜单名称" min-width="200">
          <template #default="{ row }">
            <span v-if="row.menuType === 'BUTTON'"><el-tag size="small" type="info">按钮</el-tag> {{ row.menuName }}</span>
            <span v-else><el-tag size="small">{{ row.menuType === 'MENU' ? '菜单' : '目录' }}</el-tag> {{ row.menuName }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="icon" label="图标" width="80" align="center">
          <template #default="{ row }"><el-icon v-if="row.icon"><component :is="row.icon" /></el-icon></template>
        </el-table-column>
        <el-table-column prop="path" label="路由路径" width="200" />
        <el-table-column prop="permission" label="权限标识" width="200" />
        <el-table-column prop="sortOrder" label="排序" width="70" align="center" />
        <el-table-column prop="status" label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button type="primary" link size="small" @click="handleAddChild(row)">新增子</el-button>
            <el-button type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑菜单' : '新增菜单'" width="600px" :close-on-click-modal="false">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="上级菜单">
          <el-cascader v-model="form.parentId" :options="menuTree" :props="cascaderProps"
            clearable style="width:100%" placeholder="留空为顶级菜单" />
        </el-form-item>
        <el-form-item label="菜单类型" prop="menuType">
          <el-radio-group v-model="form.menuType">
            <el-radio value="MENU">菜单</el-radio>
            <el-radio value="BUTTON">按钮</el-radio>
            <el-radio value="DIR">目录</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="菜单名称" prop="menuName">
          <el-input v-model="form.menuName" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="路由路径">
              <el-input v-model="form.path" placeholder="如 /system/user" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="组件路径">
              <el-input v-model="form.component" placeholder="如 system/user/index" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="权限标识">
          <el-input v-model="form.permission" placeholder="如 system:user:list" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="图标">
              <el-input v-model="form.icon" placeholder="如 User" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="排序">
              <el-input-number v-model="form.sortOrder" :min="0" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">正常</el-radio>
            <el-radio :value="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { getMenuTreeApi, createMenuApi, updateMenuApi, deleteMenuApi, getMenuByIdApi } from '@/api/menu'
import type { MenuItem, MenuForm } from '@/api/menu'

const loading = ref(false)
const tableData = ref<MenuItem[]>([])

// 弹窗
const dialogVisible = ref(false)
const isEdit = ref(false)
const submitLoading = ref(false)
const formRef = ref<FormInstance>()
const menuTree = ref<MenuItem[]>([])

const form = reactive<MenuForm>({
  parentId: 0, menuName: '', menuType: 'MENU', path: '', component: '',
  permission: '', icon: '', sortOrder: 0, visible: 1, status: 1,
})

const rules: FormRules = {
  menuName: [{ required: true, message: '请输入菜单名称', trigger: 'blur' }],
  menuType: [{ required: true, message: '请选择菜单类型', trigger: 'change' }],
}

const cascaderProps = {
  value: 'id', label: 'menuName', children: 'children',
  checkStrictly: true, emitPath: false,
}

async function fetchData() {
  loading.value = true
  try {
    tableData.value = await getMenuTreeApi()
    menuTree.value = await getMenuTreeApi()
  } finally { loading.value = false }
}

function handleAdd() {
  isEdit.value = false
  form.parentId = 0; form.id = undefined; form.menuName = ''; form.menuType = 'MENU'
  form.path = ''; form.component = ''; form.permission = ''; form.icon = ''
  form.sortOrder = 0; form.status = 1
  dialogVisible.value = true
}

function handleAddChild(row: MenuItem) {
  isEdit.value = false
  form.parentId = row.id; form.id = undefined; form.menuName = ''; form.menuType = 'MENU'
  form.path = ''; form.component = ''; form.permission = ''; form.icon = ''
  form.sortOrder = 0; form.status = 1
  dialogVisible.value = true
}

async function handleEdit(row: MenuItem) {
  isEdit.value = true
  const detail = await getMenuByIdApi(row.id)
  form.id = detail.id; form.parentId = detail.parentId || 0
  form.menuName = detail.menuName; form.menuType = detail.menuType
  form.path = detail.path; form.component = detail.component
  form.permission = detail.permission; form.icon = detail.icon
  form.sortOrder = detail.sortOrder; form.visible = detail.visible; form.status = detail.status
  dialogVisible.value = true
}

function handleDelete(row: MenuItem) {
  ElMessageBox.confirm(`确认删除菜单「${row.menuName}」？`, '提示', { type: 'warning', appendTo: '#app' })
    .then(async () => { await deleteMenuApi(row.id); fetchData() })
    .catch(() => {})
}

async function handleSubmit() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  submitLoading.value = true
  try {
    if (isEdit.value) { await updateMenuApi(form) }
    else { await createMenuApi(form) }
    dialogVisible.value = false; fetchData()
  } catch {
    // 错误已由 request 拦截器通知
  } finally { submitLoading.value = false }
}

onMounted(fetchData)
</script>

<style scoped>
.mb-16 { margin-bottom: 16px; }
</style>
