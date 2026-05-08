<template>
  <div class="sku-manage">
    <el-card class="search-box" shadow="never">
      <el-form :model="queryParams" inline>
        <el-form-item label="SKU编码"><el-input v-model="queryParams.skuCode" placeholder="模糊搜索" clearable /></el-form-item>
        <el-form-item label="名称"><el-input v-model="queryParams.skuName" placeholder="模糊搜索" clearable /></el-form-item>
        <el-form-item label="客户"><el-input v-model="queryParams.custCode" placeholder="客户代码" clearable style="width:140px" /></el-form-item>
        <el-form-item label="状态"><el-select v-model="queryParams.status" placeholder="全部" clearable style="width:120px">
          <el-option label="启用" value="VALID" /><el-option label="停用" value="INVALID" />
        </el-select></el-form-item>
        <el-form-item><el-button type="primary" @click="handleSearch">查询</el-button><el-button @click="handleReset">重置</el-button></el-form-item>
      </el-form>
    </el-card>
    <el-card shadow="never">
      <div class="table-toolbar"><el-button type="primary" @click="handleAdd" v-permission="'wms:sku:add'">新增SKU</el-button></div>
      <el-table :data="tableData" v-loading="loading" stripe border>
        <el-table-column prop="skuCode" label="SKU编码" width="150" />
        <el-table-column prop="skuNameZh" label="中文名称" min-width="160" />
        <el-table-column prop="skuNameEn" label="英文名称" min-width="160" />
        <el-table-column prop="custCode" label="客户" width="120" />
        <el-table-column prop="barCode" label="条码" width="140" />
        <el-table-column prop="hsCode" label="海关编码" width="100" />
        <el-table-column prop="brand" label="品牌" width="120" />
        <el-table-column prop="originCountry" label="原产地" width="80" align="center" />
        <el-table-column prop="declaredWeight" label="重量(kg)" width="90" align="right" />
        <el-table-column prop="status" label="状态" width="80" align="center">
          <template #default="{ row }"><el-tag :type="row.status==='VALID'?'success':'danger'" size="small">{{ row.status==='VALID'?'启用':'停用' }}</el-tag></template>
        </el-table-column>
        <el-table-column label="操作" width="200" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="handleEdit(row)" v-permission="'wms:sku:edit'">编辑</el-button>
            <el-button type="danger" link size="small" @click="handleDelete(row)" v-permission="'wms:sku:delete'">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination"><el-pagination v-model:current-page="queryParams.page" v-model:page-size="queryParams.pageSize"
        :total="total" :page-sizes="[10,20,50]" layout="total, sizes, prev, pager, next, jumper" @change="fetchData" /></div>
    </el-card>
    <el-dialog v-model="dialogVisible" :title="isEdit?'编辑SKU':'新增SKU'" width="750px" :close-on-click-modal="false">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="110px">
        <el-divider content-position="left">基本信息</el-divider>
        <el-row :gutter="20">
          <el-col :span="8"><el-form-item label="SKU编码" prop="skuCode"><el-input v-model="form.skuCode" :disabled="isEdit" /></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="客户SKU编码"><el-input v-model="form.customerSkuCode" /></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="SKU类型"><el-select v-model="form.skuType" style="width:100%"><el-option label="常规" value="SKU" /><el-option label="临时" value="TEMP_SKU" /><el-option label="包裹" value="PACKAGE" /></el-select></el-form-item></el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12"><el-form-item label="中文名称"><el-input v-model="form.skuNameZh" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="英文名称"><el-input v-model="form.skuNameEn" /></el-form-item></el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="8"><el-form-item label="条码"><el-input v-model="form.barCode" /></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="海关编码"><el-input v-model="form.hsCode" /></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="品牌"><el-input v-model="form.brand" /></el-form-item></el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="8"><el-form-item label="客户代码"><el-input v-model="form.custCode" /></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="原产地"><el-input v-model="form.originCountry" /></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="状态"><el-select v-model="form.status" style="width:100%"><el-option label="启用" value="VALID" /><el-option label="停用" value="INVALID" /></el-select></el-form-item></el-col>
        </el-row>
        <el-divider content-position="left">申报信息</el-divider>
        <el-row :gutter="20">
          <el-col :span="8"><el-form-item label="申报价值"><el-input-number v-model="form.declaredAmount" :min="0" style="width:100%" /></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="重量(kg)"><el-input-number v-model="form.declaredWeight" :min="0" :precision="4" style="width:100%" /></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="体积(m³)"><el-input-number v-model="form.declaredVolume" :min="0" :precision="4" style="width:100%" /></el-form-item></el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="8"><el-form-item label="长(cm)"><el-input-number v-model="form.declaredLength" :min="0" style="width:100%" /></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="宽(cm)"><el-input-number v-model="form.declaredWidth" :min="0" style="width:100%" /></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="高(cm)"><el-input-number v-model="form.declaredHeight" :min="0" style="width:100%" /></el-form-item></el-col>
        </el-row>
        <el-divider content-position="left">属性标识</el-divider>
        <el-row :gutter="20">
          <el-col :span="6"><el-form-item label="磁性"><el-switch v-model="form.magneticFlag" :active-value="1" :inactive-value="0" /></el-form-item></el-col>
          <el-col :span="6"><el-form-item label="危险品"><el-switch v-model="form.dangerFlag" :active-value="1" :inactive-value="0" /></el-form-item></el-col>
          <el-col :span="6"><el-form-item label="带电"><el-switch v-model="form.chargedFlag" :active-value="1" :inactive-value="0" /></el-form-item></el-col>
          <el-col :span="6"><el-form-item label="液体"><el-switch v-model="form.liquidFlag" :active-value="1" :inactive-value="0" /></el-form-item></el-col>
        </el-row>
        <el-form-item label="备注"><el-input v-model="form.remark" type="textarea" :rows="2" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="dialogVisible=false">取消</el-button><el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { getSkuPageApi, createSkuApi, updateSkuApi, deleteSkuApi, getSkuByIdApi } from '@/api/sku'
import type { SkuItem, SkuPageParams, SkuForm } from '@/api/sku'
const loading = ref(false); const tableData = ref<SkuItem[]>([]); const total = ref(0)
const queryParams = reactive<SkuPageParams>({ page:1, pageSize:20, skuCode:'', skuName:'', custCode:'', status:undefined })
const dialogVisible = ref(false); const isEdit = ref(false); const submitLoading = ref(false); const formRef = ref<FormInstance>()
const defaultForm = (): SkuForm => ({
  skuCode:'', customerSkuCode:'', barCode:'', hsCode:'', skuType:'SKU',
  skuNameZh:'', skuNameEn:'', skuNameFr:'', custId:null, custCode:'',
  declaredAmount:null, declaredWeight:null, declaredLength:null, declaredWidth:null, declaredHeight:null, declaredVolume:null,
  classifyId:null, brand:'', originCountry:'', snType:'NONE', lotType:'NONE',
  magneticFlag:0, dangerFlag:0, chargedFlag:0, liquidFlag:0, status:'VALID', remark:''
})
const form = reactive<SkuForm>(defaultForm())
const rules: FormRules = { skuCode:[{required:true,message:'请输入SKU编码',trigger:'blur'}] }

async function fetchData(){ loading.value=true; try{const r=await getSkuPageApi(queryParams); tableData.value=r.records; total.value=r.total}finally{loading.value=false} }
function handleSearch(){ queryParams.page=1; fetchData() }
function handleReset(){ queryParams.skuCode=''; queryParams.skuName=''; queryParams.custCode=''; queryParams.status=undefined; handleSearch() }
function handleAdd(){ isEdit.value=false; Object.assign(form,defaultForm()); dialogVisible.value=true }
async function handleEdit(row:SkuItem){ isEdit.value=true; const d=await getSkuByIdApi(row.id); Object.assign(form,d); dialogVisible.value=true }
function handleDelete(row:SkuItem){ ElMessageBox.confirm(`确认删除SKU「${row.skuCode}」？`,'提示',{type:'warning',appendTo:'#app'}).then(async()=>{await deleteSkuApi(row.id);fetchData()}).catch(()=>{}) }
async function handleSubmit(){ const v=await formRef.value?.validate().catch(()=>false); if(!v)return; submitLoading.value=true; try{if(isEdit.value){await updateSkuApi(form)}else{await createSkuApi(form)} dialogVisible.value=false; fetchData()}catch{}finally{submitLoading.value=false} }
onMounted(fetchData)
</script>

<style scoped lang="scss">
.search-box { margin-bottom: 16px; }
.table-toolbar { margin-bottom: 16px; }
.pagination { margin-top: 16px; display: flex; justify-content: flex-end; }
</style>
