<template>
  <div>
    <!-- 搜索 -->
    <el-card shadow="never" class="mb-16">
      <el-form :model="queryParams" inline>
        <el-form-item label="角色名称">
          <el-input v-model="queryParams.roleName" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" clearable style="width:120px">
            <el-option label="启用" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card shadow="never">
      <div class="mb-16"><el-button type="primary" @click="handleAdd">新增角色</el-button></div>

      <el-table :data="tableData" v-loading="loading" stripe border>
        <el-table-column prop="id" label="ID" width="70" align="center" />
        <el-table-column prop="roleName" label="角色名称" width="180" />
        <el-table-column prop="roleKey" label="角色标识" width="180" />
        <el-table-column prop="sortOrder" label="排序" width="70" align="center" />
        <el-table-column prop="status" label="状态" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" />
        <el-table-column label="操作" width="280" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button type="primary" link size="small" @click="handleMenu(row)">分配权限</el-button>
            <el-button v-if="row.roleName !== '超级管理员'" type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination">
        <el-pagination
          v-model:current-page="queryParams.page" v-model:page-size="queryParams.pageSize"
          :total="total" :page-sizes="[10,20,50]" layout="total, sizes, prev, pager, next, jumper"
          @change="fetchData" />
      </div>
    </el-card>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑角色' : '新增角色'" width="500px" :close-on-click-modal="false">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model="form.roleName" />
        </el-form-item>
        <el-form-item label="角色标识" prop="roleKey">
          <el-input v-model="form.roleKey" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sortOrder" :min="0" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 分配菜单权限弹窗 -->
    <el-dialog v-model="menuDialogVisible" title="分配菜单权限" width="400px">
      <el-tree
        ref="treeRef"
        :data="menuTree"
        show-checkbox
        node-key="id"
        :props="{ label: 'menuName', children: 'children' }"
        default-expand-all
        :check-strictly="true"
      >
        <template #default="{ data }">
          <span class="menu-node">
            <span>{{ data.menuName }}</span>
            <el-tag v-if="data.menuType === 'DIR'" size="small" type="info" class="menu-type-tag">目录</el-tag>
            <el-tag v-else-if="data.menuType === 'MENU'" size="small" type="success" class="menu-type-tag">菜单</el-tag>
            <el-tag v-else-if="data.menuType === 'BUTTON'" size="small" type="warning" class="menu-type-tag">按钮</el-tag>
            <span v-if="data.permission" class="menu-perm">{{ data.permission }}</span>
          </span>
        </template>
      </el-tree>
      <template #footer>
        <el-button @click="menuDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleAssignMenu">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, nextTick } from 'vue'
import { ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import type { ElTree } from 'element-plus'
import { getRolePageApi, createRoleApi, updateRoleApi, deleteRoleApi, getRoleByIdApi, assignRoleMenuApi } from '@/api/role'

import { getMenuTreeApi } from '@/api/menu'
import type { MenuItem } from '@/api/menu'
import type { RoleItem, RolePageParams, RoleForm } from '@/api/role'

const loading = ref(false)
const tableData = ref<RoleItem[]>([])
const total = ref(0)
const queryParams = reactive<RolePageParams>({ page: 1, pageSize: 20, roleName: '', status: undefined })

// 表单弹窗
const dialogVisible = ref(false)
const isEdit = ref(false)
const submitLoading = ref(false)
const formRef = ref<FormInstance>()
const form = reactive<RoleForm>({ roleName: '', roleKey: '', sortOrder: 0, status: 1, remark: '', menuIds: [] })
const rules: FormRules = {
  roleName: [{ required: true, message: '请输入角色名称', trigger: 'blur' }],
  roleKey: [{ required: true, message: '请输入角色标识', trigger: 'blur' }],
}

// 菜单权限弹窗
const menuDialogVisible = ref(false)
const menuTree = ref<MenuItem[]>([])
const treeRef = ref<InstanceType<typeof ElTree>>()
const currentRoleId = ref(0)

async function fetchData() {
  loading.value = true
  try { const res = await getRolePageApi(queryParams); tableData.value = res.records; total.value = res.total }
  finally { loading.value = false }
}

function handleSearch() { queryParams.page = 1; fetchData() }
function handleReset() { queryParams.roleName = ''; queryParams.status = undefined; handleSearch() }

function handleAdd() {
  isEdit.value = false
  form.id = undefined; form.roleName = ''; form.roleKey = ''; form.sortOrder = 0; form.status = 1; form.remark = ''
  dialogVisible.value = true
}

async function handleEdit(row: RoleItem) {
  isEdit.value = true
  const detail = await getRoleByIdApi(row.id)
  form.id = detail.id; form.roleName = detail.roleName; form.roleKey = detail.roleKey
  form.sortOrder = detail.sortOrder; form.status = detail.status; form.remark = detail.remark
  dialogVisible.value = true
}

function handleDelete(row: RoleItem) {
  ElMessageBox.confirm(`确认删除角色「${row.roleName}」？`, '提示', { type: 'warning', appendTo: '#app' })
    .then(async () => { await deleteRoleApi(row.id); fetchData() })
    .catch(() => {})
}

async function handleSubmit() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  submitLoading.value = true
  try {
    if (isEdit.value) { await updateRoleApi(form) }
    else { await createRoleApi(form) }
    dialogVisible.value = false; fetchData()
  } catch {
    // 错误已由 request 拦截器通知
  } finally { submitLoading.value = false }
}

async function handleMenu(row: RoleItem) {
  currentRoleId.value = row.id
  menuTree.value = await getMenuTreeApi()
  const detail = await getRoleByIdApi(row.id)
  menuDialogVisible.value = true
  await nextTick()
  treeRef.value?.setCheckedKeys(detail.menuIds || [])
}

async function handleAssignMenu() {
  const keys = treeRef.value?.getCheckedKeys(false, false) as number[]
  try {
    await assignRoleMenuApi(currentRoleId.value, keys)
    menuDialogVisible.value = false
  } catch {
    // 错误已由 request 拦截器通知
  }
}

onMounted(fetchData)
</script>

<style scoped>
.mb-16 { margin-bottom: 16px; }
.pagination { margin-top: 16px; display: flex; justify-content: flex-end; }
.menu-node { display: flex; align-items: center; gap: 6px; }
.menu-type-tag { font-size: 10px; padding: 0 4px; min-height: 18px; line-height: 18px; }
.menu-perm { font-size: 11px; color: #909399; }
</style>
