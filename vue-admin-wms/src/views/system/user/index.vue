<template>
  <div class="user-manage">
    <!-- 搜索栏 -->
    <el-card class="search-box" shadow="never">
      <el-form :model="queryParams" inline>
        <el-form-item label="用户名">
          <el-input v-model="queryParams.username" placeholder="模糊搜索" clearable />
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="queryParams.realName" placeholder="模糊搜索" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="全部" clearable style="width:120px">
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

    <!-- 表格 -->
    <el-card shadow="never">
      <div class="table-toolbar">
        <el-button type="primary" @click="handleAdd">新增用户</el-button>
      </div>

      <el-table :data="tableData" v-loading="loading" stripe border style="width:100%">
        <el-table-column prop="id" label="ID" width="70" align="center" />
        <el-table-column prop="username" label="用户名" width="150" />
        <el-table-column prop="realName" label="真实姓名" width="150" />
        <el-table-column prop="email" label="邮箱" min-width="200" />
        <el-table-column prop="phone" label="手机号" width="140" />
        <el-table-column prop="status" label="状态" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="280" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button v-if="row.username !== 'admin'" type="primary" link size="small" @click="handleRole(row)">分配角色</el-button>
            <el-button v-if="row.username !== 'admin'" type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="queryParams.page"
          v-model:page-size="queryParams.pageSize"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          @change="fetchData"
        />
      </div>
    </el-card>

    <!-- 分配角色弹窗 -->
    <el-dialog v-model="roleDialogVisible" title="分配角色" width="450px">
      <el-checkbox-group v-model="selectedRoleIds">
        <div v-for="role in roleList" :key="role.id" class="role-item">
          <el-checkbox
            :label="role.id"
            :disabled="role.roleName === '超级管理员' && (currentUser?.username !== 'admin')"
          >
            {{ role.roleName }}
            <span v-if="role.roleName === '超级管理员'" style="color:#909399;font-size:12px">(仅限admin)</span>
          </el-checkbox>
        </div>
      </el-checkbox-group>
      <div v-if="selectedRoleIds.length === 0" class="empty-tip" style="color:#f56c6c">请至少选择一个角色</div>
      <template #footer>
        <el-button @click="roleDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="roleLoading" @click="handleAssignRole">确定</el-button>
      </template>
    </el-dialog>

    <!-- 新增/编辑弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑用户' : '新增用户'"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input
            v-model="form.password"
            type="password"
            :placeholder="isEdit ? '留空则不修改密码' : '请输入密码'"
          />
        </el-form-item>
        <el-form-item label="真实姓名" prop="realName">
          <el-input v-model="form.realName" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="角色" prop="roleIds">
          <el-select v-model="form.roleIds" multiple placeholder="请选择角色(必选)" style="width:100%">
            <el-option
              v-for="role in allRoles"
              :key="role.id"
              :label="role.roleName + (role.roleName === '超级管理员' ? ' (仅限admin)' : '')"
              :value="role.id"
              :disabled="role.roleName === '超级管理员' && form.username !== 'admin'"
            />
          </el-select>
          <div v-if="form.roleIds?.includes(1) && form.username !== 'admin'" class="role-warning">
            超级管理员角色只能分配给 admin 用户
          </div>
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
import { getUserPageApi, createUserApi, updateUserApi, deleteUserApi, assignUserRoleApi } from '@/api/user'
import type { UserItem, UserPageParams, UserForm } from '@/api/user'
import { getRolePageApi } from '@/api/role'
import type { RoleItem } from '@/api/role'

const loading = ref(false)
const tableData = ref<UserItem[]>([])
const total = ref(0)

// 分配角色
const roleDialogVisible = ref(false)
const roleLoading = ref(false)
const roleList = ref<RoleItem[]>([])
const selectedRoleIds = ref<number[]>([])
const currentUserId = ref(0)
const currentUser = ref<UserItem | null>(null)
const allRoles = ref<RoleItem[]>([])

/** 加载角色列表 */
async function loadRoles() {
  const res = await getRolePageApi({ page: 1, pageSize: 999 })
  allRoles.value = res.records
}

// 查询参数
const queryParams = reactive<UserPageParams>({
  page: 1,
  pageSize: 20,
  username: '',
  realName: '',
  status: undefined,
})

// 弹窗
const dialogVisible = ref(false)
const isEdit = ref(false)
const submitLoading = ref(false)
const formRef = ref<FormInstance>()

const form = reactive<UserForm>({
  username: '',
  password: '',
  realName: '',
  email: '',
  phone: '',
  status: 1,
  roleIds: [],
})

const rules: FormRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  roleIds: [{
    validator: (_rule: any, value: number[], callback: Function) => {
      if (!value || value.length === 0) {
        callback(new Error('请至少选择一个角色'))
      } else {
        callback()
      }
    },
    trigger: 'change',
  }],
}

// 获取数据
async function fetchData() {
  loading.value = true
  try {
    const res = await getUserPageApi(queryParams)
    tableData.value = res.records
    total.value = res.total
  } finally {
    loading.value = false
  }
}

function handleSearch() { queryParams.page = 1; fetchData() }
function handleReset() {
  queryParams.username = ''
  queryParams.realName = ''
  queryParams.status = undefined
  handleSearch()
}

function handleAdd() {
  isEdit.value = false
  form.id = undefined
  form.username = ''
  form.password = ''
  form.realName = ''
  form.email = ''
  form.phone = ''
  form.status = 1
  form.roleIds = []
  loadRoles()
  dialogVisible.value = true
}

function handleEdit(row: UserItem) {
  isEdit.value = true
  form.id = row.id
  form.username = row.username
  form.password = ''
  form.realName = row.realName
  form.email = row.email
  form.phone = row.phone
  form.status = row.status
  form.roleIds = [...(row.roleIds || [])]
  loadRoles()
  dialogVisible.value = true
}

function handleDelete(row: UserItem) {
  ElMessageBox.confirm(`确认删除用户「${row.username}」？`, '提示', { type: 'warning', appendTo: '#app' })
    .then(async () => {
      await deleteUserApi(row.id)
      fetchData()
    })
    .catch(() => {})
}

async function handleSubmit() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  submitLoading.value = true
  try {
    if (isEdit.value) {
      await updateUserApi(form)
    } else {
      await createUserApi(form)
    }
    dialogVisible.value = false
    fetchData()
  } catch {
    // 错误已由 request 拦截器通知
  } finally {
    submitLoading.value = false
  }
}

// 分配角色弹窗
async function handleRole(row: UserItem) {
  currentUserId.value = row.id
  currentUser.value = row
  const roleRes = await getRolePageApi({ page: 1, pageSize: 999 })
  roleList.value = roleRes.records
  selectedRoleIds.value = row.roleIds || []
  roleDialogVisible.value = true
}

async function handleAssignRole() {
  roleLoading.value = true
  try {
    await assignUserRoleApi(currentUserId.value, selectedRoleIds.value)
    roleDialogVisible.value = false
    fetchData()
  } catch {
    // 错误已由 request 拦截器通知
  } finally {
    roleLoading.value = false
  }
}

onMounted(fetchData)
</script>

<style scoped lang="scss">
.search-box { margin-bottom: 16px; }
.table-toolbar { margin-bottom: 16px; }
.pagination { margin-top: 16px; display: flex; justify-content: flex-end; }
.role-item { padding: 6px 0; }
.empty-tip { text-align: center; color: #909399; padding: 20px; }
.role-warning { color: #f56c6c; font-size: 12px; margin-top: 4px; }
</style>
