<template>
  <div class="warehouse-manage">
    <!-- 搜索栏 -->
    <el-card class="search-box" shadow="never">
      <el-form :model="queryParams" inline>
        <el-form-item label="仓库代码">
          <el-input v-model="queryParams.whCode" placeholder="模糊搜索" clearable />
        </el-form-item>
        <el-form-item label="仓库名称">
          <el-input v-model="queryParams.whName" placeholder="模糊搜索" clearable />
        </el-form-item>
        <el-form-item label="仓库类型">
          <el-select v-model="queryParams.whType" placeholder="全部" clearable style="width:140px">
            <el-option label="仓库" value="WH" />
            <el-option label="处理中心" value="PC" />
            <el-option label="虚拟仓" value="VL" />
            <el-option label="转运仓" value="WH_TRANSIT" />
            <el-option label="物流站点" value="LGS" />
          </el-select>
        </el-form-item>
        <el-form-item label="国家">
          <el-input v-model="queryParams.countryCode" placeholder="国家代码" clearable style="width:120px" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="全部" clearable style="width:120px">
            <el-option label="启用" value="VALID" />
            <el-option label="停用" value="INVALID" />
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
        <el-button type="primary" @click="handleAdd" v-permission="'wms:warehouse:add'">新增仓库</el-button>
      </div>

      <el-table :data="tableData" v-loading="loading" stripe border style="width:100%">
        <el-table-column prop="whCode" label="仓库代码" width="140" />
        <el-table-column prop="whName" label="仓库名称" min-width="180" />
        <el-table-column prop="whType" label="类型" width="100" align="center">
          <template #default="{ row }">
            <el-tag size="small">{{ typeLabel(row.whType) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="countryCode" label="国家" width="80" align="center" />
        <el-table-column prop="province" label="省/州" width="120" />
        <el-table-column prop="city" label="城市" width="120" />
        <el-table-column prop="contact" label="联系人" width="120" />
        <el-table-column prop="telNo" label="电话" width="140" />
        <el-table-column prop="status" label="状态" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 'VALID' ? 'success' : 'danger'" size="small">
              {{ row.status === 'VALID' ? '启用' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="200" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="handleEdit(row)" v-permission="'wms:warehouse:edit'">编辑</el-button>
            <el-button type="danger" link size="small" @click="handleDelete(row)" v-permission="'wms:warehouse:delete'">删除</el-button>
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

    <!-- 新增/编辑弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑仓库' : '新增仓库'"
      width="800px"
      :close-on-click-modal="false"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="110px">
        <el-divider content-position="left">基本信息</el-divider>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="仓库代码" prop="whCode">
              <el-input v-model="form.whCode" :disabled="isEdit" placeholder="如 WH-001" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="仓库名称" prop="whName">
              <el-input v-model="form.whName" placeholder="如 深圳前海仓" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="仓库类型">
              <el-select v-model="form.whType" style="width:100%">
                <el-option label="仓库" value="WH" />
                <el-option label="处理中心" value="PC" />
                <el-option label="虚拟仓" value="VL" />
                <el-option label="转运仓" value="WH_TRANSIT" />
                <el-option label="物流站点" value="LGS" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态">
              <el-select v-model="form.status" style="width:100%">
                <el-option label="启用" value="VALID" />
                <el-option label="停用" value="INVALID" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="国家">
              <el-input v-model="form.countryCode" placeholder="如 CN" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="币种">
              <el-input v-model="form.currencyCode" placeholder="如 CNY" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="时区">
              <el-input v-model="form.timeZone" placeholder="如 Asia/Shanghai" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="重量单位">
              <el-select v-model="form.weightUnit" style="width:100%">
                <el-option label="KG" value="KG" />
                <el-option label="LB" value="LB" />
                <el-option label="G" value="G" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="长度单位">
              <el-select v-model="form.lengthUnit" style="width:100%">
                <el-option label="CM" value="CM" />
                <el-option label="INCH" value="INCH" />
                <el-option label="M" value="M" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="2" />
        </el-form-item>

        <el-divider content-position="left">联系信息</el-divider>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="联系人">
              <el-input v-model="form.contact" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系电话">
              <el-input v-model="form.telNo" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="邮箱">
              <el-input v-model="form.email" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="邮编">
              <el-input v-model="form.postalCode" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="省/州">
              <el-input v-model="form.province" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="城市">
              <el-input v-model="form.city" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="地址">
              <el-input v-model="form.address1" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider content-position="left">业务配置</el-divider>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="经营方式">
              <el-input v-model="form.bizType" placeholder="00:FBA,01:公共海外仓,02:自建" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="仓租模式">
              <el-input v-model="form.storageType" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="仓储面积(m²)">
              <el-input-number v-model="form.storageArea" :min="0" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="仓储库容(m³)">
              <el-input-number v-model="form.storageVolume" :min="0" style="width:100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="订单处理量">
              <el-input-number v-model="form.throughput" :min="0" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="SKU容量">
              <el-input-number v-model="form.handleCapacity" :min="0" style="width:100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="服务功能">
          <el-input v-model="form.serviceFunction" placeholder="多个用逗号分隔，如 退换货管理,一件代发,FBA中转" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="有效天数">
              <el-input-number v-model="form.validDay" :min="0" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="合作方">
              <el-input v-model="form.coopPartner" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="WMS系统">
              <el-input v-model="form.wmsSystem" />
            </el-form-item>
          </el-col>
        </el-row>
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
import {
  getWarehousePageApi, createWarehouseApi, updateWarehouseApi, deleteWarehouseApi,
} from '@/api/warehouse'
import type { WarehouseItem, WarehousePageParams, WarehouseForm } from '@/api/warehouse'

const loading = ref(false)
const tableData = ref<WarehouseItem[]>([])
const total = ref(0)

// 查询参数
const queryParams = reactive<WarehousePageParams>({
  page: 1,
  pageSize: 20,
  whCode: '',
  whName: '',
  whType: undefined,
  countryCode: '',
  status: undefined,
})

// 弹窗
const dialogVisible = ref(false)
const isEdit = ref(false)
const submitLoading = ref(false)
const formRef = ref<FormInstance>()

const defaultForm = (): WarehouseForm => ({
  whCode: '',
  whName: '',
  whType: 'WH',
  countryCode: '',
  currencyCode: '',
  timeZone: '',
  weightUnit: 'KG',
  lengthUnit: 'CM',
  status: 'VALID',
  remark: '',
  shippingName: '',
  shippingTel: '',
  shippingEmail: '',
  consigneeName: '',
  consigneeTel: '',
  consigneeEmail: '',
  postalCode: '',
  province: '',
  city: '',
  address1: '',
  contact: '',
  email: '',
  telNo: '',
  bizType: '',
  serviceFunction: '',
  storageArea: null,
  storageVolume: null,
  throughput: null,
  handleCapacity: null,
  validDay: null,
  storageType: '',
  coopPartner: '',
  wmsSystem: '',
})

const form = reactive<WarehouseForm>(defaultForm())

const rules: FormRules = {
  whCode: [
    { required: true, message: '请输入仓库代码', trigger: 'blur' },
    { min: 2, max: 64, message: '长度 2-64 个字符', trigger: 'blur' },
  ],
  whName: [{ required: true, message: '请输入仓库名称', trigger: 'blur' }],
}

function typeLabel(type: string) {
  const map: Record<string, string> = { WH: '仓库', PC: '处理中心', VL: '虚拟仓', WH_TRANSIT: '转运仓', LGS: '物流站点' }
  return map[type] || type
}

async function fetchData() {
  loading.value = true
  try {
    const res = await getWarehousePageApi(queryParams)
    tableData.value = res.records
    total.value = res.total
  } finally {
    loading.value = false
  }
}

function handleSearch() { queryParams.page = 1; fetchData() }
function handleReset() {
  queryParams.whCode = ''
  queryParams.whName = ''
  queryParams.whType = undefined
  queryParams.countryCode = ''
  queryParams.status = undefined
  handleSearch()
}

function handleAdd() {
  isEdit.value = false
  Object.assign(form, defaultForm())
  dialogVisible.value = true
}

async function handleEdit(row: WarehouseItem) {
  isEdit.value = true
  // 后端查询完整信息
  const detail = await getWarehouseByIdApi(row.id)
  Object.assign(form, detail)
  dialogVisible.value = true
}

function handleDelete(row: WarehouseItem) {
  ElMessageBox.confirm(`确认删除仓库「${row.whName}」？`, '提示', { type: 'warning', appendTo: '#app' })
    .then(async () => {
      await deleteWarehouseApi(row.id)
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
      await updateWarehouseApi(form)
    } else {
      await createWarehouseApi(form)
    }
    dialogVisible.value = false
    fetchData()
  } catch {
    // 错误已由 request 拦截器通知
  } finally {
    submitLoading.value = false
  }
}

onMounted(fetchData)
</script>

<style scoped lang="scss">
.search-box { margin-bottom: 16px; }
.table-toolbar { margin-bottom: 16px; }
.pagination { margin-top: 16px; display: flex; justify-content: flex-end; }
</style>
