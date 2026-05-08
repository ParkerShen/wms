<template>
  <div class="customer-manage">
    <el-card class="search-box" shadow="never">
      <el-form :model="queryParams" inline>
        <el-form-item label="客户代码"><el-input v-model="queryParams.custCode" placeholder="模糊搜索" clearable /></el-form-item>
        <el-form-item label="客户名称"><el-input v-model="queryParams.custName" placeholder="模糊搜索" clearable /></el-form-item>
        <el-form-item label="平台"><el-select v-model="queryParams.servicePlatform" placeholder="全部" clearable style="width:120px">
          <el-option label="NODE" value="NODE" /><el-option label="TUME" value="TUME" />
        </el-select></el-form-item>
        <el-form-item label="状态"><el-select v-model="queryParams.status" placeholder="全部" clearable style="width:120px">
          <el-option label="启用" value="VALID" /><el-option label="停用" value="INVALID" />
        </el-select></el-form-item>
        <el-form-item><el-button type="primary" @click="handleSearch">查询</el-button><el-button @click="handleReset">重置</el-button></el-form-item>
      </el-form>
    </el-card>
    <el-card shadow="never">
      <div class="table-toolbar"><el-button type="primary" @click="handleAdd" v-permission="'wms:customer:add'">新增客户</el-button></div>
      <el-table :data="tableData" v-loading="loading" stripe border>
        <el-table-column prop="custCode" label="客户代码" width="140" />
        <el-table-column prop="custName" label="客户名称" min-width="180" />
        <el-table-column prop="contact" label="联系人" width="120" />
        <el-table-column prop="telNo" label="电话" width="150" />
        <el-table-column prop="email" label="邮箱" min-width="180" />
        <el-table-column prop="servicePlatform" label="平台" width="80" align="center">
          <template #default="{ row }"><el-tag size="small">{{ row.servicePlatform || '-' }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 'VALID' ? 'success' : 'danger'" size="small">{{ row.status === 'VALID' ? '启用' : '停用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="200" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="handleEdit(row)" v-permission="'wms:customer:edit'">编辑</el-button>
            <el-button type="danger" link size="small" @click="handleDelete(row)" v-permission="'wms:customer:delete'">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination"><el-pagination v-model:current-page="queryParams.page" v-model:page-size="queryParams.pageSize"
        :total="total" :page-sizes="[10,20,50]" layout="total, sizes, prev, pager, next, jumper" @change="fetchData" /></div>
    </el-card>
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑客户' : '新增客户'" width="700px" :close-on-click-modal="false">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="110px">
        <el-row :gutter="20">
          <el-col :span="12"><el-form-item label="客户代码" prop="custCode"><el-input v-model="form.custCode" :disabled="isEdit" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="客户名称" prop="custName"><el-input v-model="form.custName" /></el-form-item></el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="8"><el-form-item label="联系人"><el-input v-model="form.contact" /></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="电话"><el-input v-model="form.telNo" /></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="邮箱"><el-input v-model="form.email" /></el-form-item></el-col>
        </el-row>
        <el-form-item label="地址"><el-input v-model="form.address" /></el-form-item>
        <el-divider content-position="left">API配置</el-divider>
        <el-row :gutter="20">
          <el-col :span="8"><el-form-item label="API账号"><el-input v-model="form.apiAccount" /></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="API密码"><el-input v-model="form.apiPassword" type="password" /></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="平台"><el-select v-model="form.servicePlatform" style="width:100%">
            <el-option label="NODE" value="NODE" /><el-option label="TUME" value="TUME" />
          </el-select></el-form-item></el-col>
        </el-row>
        <el-divider content-position="left">业务配置</el-divider>
        <el-row :gutter="20">
          <el-col :span="8"><el-form-item label="SKU自动审核"><el-switch v-model="form.autoAuditSku" :active-value="1" :inactive-value="0" /></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="自动SKU编码"><el-switch v-model="form.autoSkuCode" :active-value="1" :inactive-value="0" /></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="运单预扣款"><el-switch v-model="form.expressAdvanceFlag" :active-value="1" :inactive-value="0" /></el-form-item></el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="8"><el-form-item label="客供面单"><el-switch v-model="form.supportCustProvider" :active-value="1" :inactive-value="0" /></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="新模板计费"><el-switch v-model="form.newFeeStructure" :active-value="1" :inactive-value="0" /></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="匹配标签"><el-switch v-model="form.matchLabelFlag" :active-value="1" :inactive-value="0" /></el-form-item></el-col>
        </el-row>
        <el-form-item label="状态"><el-radio-group v-model="form.status"><el-radio value="VALID">启用</el-radio><el-radio value="INVALID">停用</el-radio></el-radio-group></el-form-item>
        <el-form-item label="备注"><el-input v-model="form.remark" type="textarea" :rows="2" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="dialogVisible = false">取消</el-button><el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { getCustomerPageApi, createCustomerApi, updateCustomerApi, deleteCustomerApi, getCustomerByIdApi } from '@/api/customer'
import type { CustomerItem, CustomerPageParams, CustomerForm } from '@/api/customer'
const loading = ref(false); const tableData = ref<CustomerItem[]>([]); const total = ref(0)
const queryParams = reactive<CustomerPageParams>({ page:1, pageSize:20, custCode:'', custName:'', status:undefined, servicePlatform:undefined })
const dialogVisible = ref(false); const isEdit = ref(false); const submitLoading = ref(false); const formRef = ref<FormInstance>()
const defaultForm = (): CustomerForm => ({
  custCode:'', custName:'', contact:'', email:'', telNo:'', address:'', apiAccount:'', apiPassword:'', status:'VALID',
  remark:'', autoAuditSku:1, autoSkuCode:0, skuClassifyRequired:0, servicePlatform:'NODE', skuWhSyncType:'',
  expressAdvanceFlag:0, supportCustProvider:0, matchLabelFlag:0, newFeeStructure:0, orderSource:0, shippingCode:'', shippingAddress:'',
})
const form = reactive<CustomerForm>(defaultForm())
const rules: FormRules = { custCode:[{required:true,message:'请输入客户代码',trigger:'blur'}], custName:[{required:true,message:'请输入客户名称',trigger:'blur'}] }

async function fetchData() { loading.value=true; try{const r=await getCustomerPageApi(queryParams); tableData.value=r.records; total.value=r.total}finally{loading.value=false} }
function handleSearch(){ queryParams.page=1; fetchData() }
function handleReset(){ queryParams.custCode=''; queryParams.custName=''; queryParams.status=undefined; queryParams.servicePlatform=undefined; handleSearch() }
function handleAdd(){ isEdit.value=false; Object.assign(form,defaultForm()); dialogVisible.value=true }
async function handleEdit(row:CustomerItem){ isEdit.value=true; const d=await getCustomerByIdApi(row.id); Object.assign(form,d); dialogVisible.value=true }
function handleDelete(row:CustomerItem){ ElMessageBox.confirm(`确认删除客户「${row.custName}」？`,'提示',{type:'warning',appendTo:'#app'}).then(async()=>{await deleteCustomerApi(row.id);fetchData()}).catch(()=>{}) }
async function handleSubmit(){ const v=await formRef.value?.validate().catch(()=>false); if(!v)return; submitLoading.value=true; try{if(isEdit.value){await updateCustomerApi(form)}else{await createCustomerApi(form)} dialogVisible.value=false; fetchData()}catch{}finally{submitLoading.value=false} }
onMounted(fetchData)
</script>

<style scoped lang="scss">
.search-box { margin-bottom: 16px; }
.table-toolbar { margin-bottom: 16px; }
.pagination { margin-top: 16px; display: flex; justify-content: flex-end; }
</style>
