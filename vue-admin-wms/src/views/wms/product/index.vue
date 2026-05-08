<template>
  <div class="product-manage">
    <el-card class="search-box" shadow="never">
      <el-form :model="queryParams" inline>
        <el-form-item label="产品代码"><el-input v-model="queryParams.productCode" placeholder="模糊搜索" clearable /></el-form-item>
        <el-form-item label="产品名称"><el-input v-model="queryParams.productName" placeholder="模糊搜索" clearable /></el-form-item>
        <el-form-item label="业务类型"><el-select v-model="queryParams.bizType" placeholder="全部" clearable style="width:160px">
          <el-option label="快递共享" value="EXPRESS_SHARING" /><el-option label="仓库派送" value="WAREHOUSE_DELIVERY" />
        </el-select></el-form-item>
        <el-form-item label="状态"><el-select v-model="queryParams.status" placeholder="全部" clearable style="width:120px">
          <el-option label="启用" value="VALID" /><el-option label="停用" value="INVALID" />
        </el-select></el-form-item>
        <el-form-item><el-button type="primary" @click="handleSearch">查询</el-button><el-button @click="handleReset">重置</el-button></el-form-item>
      </el-form>
    </el-card>
    <el-card shadow="never">
      <div class="table-toolbar"><el-button type="primary" @click="handleAdd" v-permission="'wms:product:add'">新增产品</el-button></div>
      <el-table :data="tableData" v-loading="loading" stripe border>
        <el-table-column prop="productCode" label="产品代码" width="140" />
        <el-table-column prop="productName" label="产品名称" min-width="200" />
        <el-table-column prop="bizType" label="业务类型" width="120"><template #default="{row}">{{ row.bizType==='EXPRESS_SHARING'?'快递共享':'仓库派送' }}</template></el-table-column>
        <el-table-column prop="productType" label="产品类型" width="100"><template #default="{row}">{{ row.productType==='SINGLE_PIECE'?'一票一件':'一票多件' }}</template></el-table-column>
        <el-table-column prop="countryCode" label="国家" width="60" align="center" />
        <el-table-column prop="currencyCode" label="币种" width="60" align="center" />
        <el-table-column prop="billableWeightType" label="计费重" width="70" align="center"><template #default="{row}">{{ {GW:'实重',VW:'泡重',MAX:'取大'}[row.billableWeightType]||row.billableWeightType }}</template></el-table-column>
        <el-table-column prop="dimWeightCoefficient" label="体积重系数" width="100" align="center" />
        <el-table-column prop="freightCoefficient" label="运费系数" width="90" align="center" />
        <el-table-column prop="status" label="状态" width="80" align="center">
          <template #default="{row}"><el-tag :type="row.status==='VALID'?'success':'danger'" size="small">{{ row.status==='VALID'?'启用':'停用' }}</el-tag></template>
        </el-table-column>
        <el-table-column label="操作" width="200" align="center" fixed="right">
          <template #default="{row}">
            <el-button type="primary" link size="small" @click="handleEdit(row)" v-permission="'wms:product:edit'">编辑</el-button>
            <el-button type="danger" link size="small" @click="handleDelete(row)" v-permission="'wms:product:delete'">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination"><el-pagination v-model:current-page="queryParams.page" v-model:page-size="queryParams.pageSize"
        :total="total" :page-sizes="[10,20,50]" layout="total, sizes, prev, pager, next, jumper" @change="fetchData" /></div>
    </el-card>
    <el-dialog v-model="dialogVisible" :title="isEdit?'编辑产品':'新增产品'" width="700px" :close-on-click-modal="false">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="120px">
        <el-row :gutter="20">
          <el-col :span="12"><el-form-item label="产品代码" prop="productCode"><el-input v-model="form.productCode" :disabled="isEdit" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="产品名称" prop="productName"><el-input v-model="form.productName" /></el-form-item></el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12"><el-form-item label="业务类型"><el-select v-model="form.bizType" style="width:100%"><el-option label="快递共享" value="EXPRESS_SHARING" /><el-option label="仓库派送" value="WAREHOUSE_DELIVERY" /></el-select></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="产品类型"><el-select v-model="form.productType" style="width:100%"><el-option label="一票一件" value="SINGLE_PIECE" /><el-option label="一票多件" value="MULTI_PIECE" /></el-select></el-form-item></el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="8"><el-form-item label="国家"><el-input v-model="form.countryCode" /></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="币种"><el-input v-model="form.currencyCode" /></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="线上/线下"><el-select v-model="form.onlineFlag" style="width:100%"><el-option label="线上" value="ONLINE" /><el-option label="线下" value="OFFLINE" /></el-select></el-form-item></el-col>
        </el-row>
        <el-divider content-position="left">计费配置</el-divider>
        <el-row :gutter="20">
          <el-col :span="8"><el-form-item label="渠道选择"><el-select v-model="form.channelRules" style="width:100%"><el-option label="成本低" value="COST" /><el-option label="时效快" value="FAST" /><el-option label="利润高" value="PROFIT" /></el-select></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="计费规则"><el-select v-model="form.billingRules" style="width:100%"><el-option label="票" value="ORDER" /><el-option label="件" value="PIECE" /><el-option label="箱" value="BOX" /><el-option label="立方米" value="CBM" /></el-select></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="计费重类型"><el-select v-model="form.billableWeightType" style="width:100%"><el-option label="实重" value="GW" /><el-option label="泡重" value="VW" /><el-option label="取大值" value="MAX" /></el-select></el-form-item></el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="8"><el-form-item label="体积重系数"><el-input-number v-model="form.dimWeightCoefficient" :min="1" :step="1000" style="width:100%" /></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="运费系数"><el-input-number v-model="form.freightCoefficient" :min="0" :precision="4" :step="0.1" style="width:100%" /></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="末端服务商"><el-input v-model="form.endProviderCode" /></el-form-item></el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="8"><el-form-item label="重量单位"><el-select v-model="form.weightUnit" style="width:100%"><el-option label="KG" value="KG" /><el-option label="LB" value="LB" /></el-select></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="长度单位"><el-select v-model="form.lengthUnit" style="width:100%"><el-option label="CM" value="CM" /><el-option label="INCH" value="INCH" /></el-select></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="产品版本"><el-input-number v-model="form.productVersion" :min="1" style="width:100%" /></el-form-item></el-col>
        </el-row>
        <el-divider content-position="left">服务标识</el-divider>
        <el-row :gutter="20">
          <el-col :span="6"><el-form-item label="偏远"><el-switch v-model="form.odaFlag" :active-value="1" :inactive-value="0" /></el-form-item></el-col>
          <el-col :span="6"><el-form-item label="签收证明"><el-switch v-model="form.podFlag" :active-value="1" :inactive-value="0" /></el-form-item></el-col>
          <el-col :span="6"><el-form-item label="保险"><el-switch v-model="form.insuranceFlag" :active-value="1" :inactive-value="0" /></el-form-item></el-col>
          <el-col :span="6"><el-form-item label="危险品"><el-switch v-model="form.dangerousFlag" :active-value="1" :inactive-value="0" /></el-form-item></el-col>
        </el-row>
        <el-form-item label="状态"><el-radio-group v-model="form.status"><el-radio value="VALID">启用</el-radio><el-radio value="INVALID">停用</el-radio></el-radio-group></el-form-item>
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
import { getProductPageApi, createProductApi, updateProductApi, deleteProductApi, getProductByIdApi } from '@/api/product'
import type { ProductItem, ProductPageParams, ProductForm } from '@/api/product'
const loading = ref(false); const tableData = ref<ProductItem[]>([]); const total = ref(0)
const queryParams = reactive<ProductPageParams>({ page:1, pageSize:20, productCode:'', productName:'', bizType:undefined, countryCode:'', status:undefined })
const dialogVisible = ref(false); const isEdit = ref(false); const submitLoading = ref(false); const formRef = ref<FormInstance>()
const defaultForm = (): ProductForm => ({
  productCode:'', productName:'', productVersion:1, bizType:'EXPRESS_SHARING', productType:'SINGLE_PIECE',
  countryCode:'', currencyCode:'', channelRules:'COST', billingRules:'ORDER', billableWeightType:'MAX',
  dimWeightCoefficient:5000, freightCoefficient:1, weightUnit:'KG', lengthUnit:'CM', onlineFlag:'ONLINE',
  status:'VALID', remark:'', odaFlag:0, podFlag:0, insuranceFlag:0, dangerousFlag:0, returnLabelFlag:0,
  endProviderCode:'', zoneTemplateId:null, weightTemplateId:null,
})
const form = reactive<ProductForm>(defaultForm())
const rules: FormRules = { productCode:[{required:true,message:'请输入产品代码',trigger:'blur'}], productName:[{required:true,message:'请输入产品名称',trigger:'blur'}] }

async function fetchData(){ loading.value=true; try{const r=await getProductPageApi(queryParams); tableData.value=r.records; total.value=r.total}finally{loading.value=false} }
function handleSearch(){ queryParams.page=1; fetchData() }
function handleReset(){ queryParams.productCode=''; queryParams.productName=''; queryParams.bizType=undefined; queryParams.countryCode=''; queryParams.status=undefined; handleSearch() }
function handleAdd(){ isEdit.value=false; Object.assign(form,defaultForm()); dialogVisible.value=true }
async function handleEdit(row:ProductItem){ isEdit.value=true; const d=await getProductByIdApi(row.id); Object.assign(form,d); dialogVisible.value=true }
function handleDelete(row:ProductItem){ ElMessageBox.confirm(`确认删除产品「${row.productName}」？`,'提示',{type:'warning',appendTo:'#app'}).then(async()=>{await deleteProductApi(row.id);fetchData()}).catch(()=>{}) }
async function handleSubmit(){ const v=await formRef.value?.validate().catch(()=>false); if(!v)return; submitLoading.value=true; try{if(isEdit.value){await updateProductApi(form)}else{await createProductApi(form)} dialogVisible.value=false; fetchData()}catch{}finally{submitLoading.value=false} }
onMounted(fetchData)
</script>

<style scoped lang="scss">
.search-box { margin-bottom: 16px; }
.table-toolbar { margin-bottom: 16px; }
.pagination { margin-top: 16px; display: flex; justify-content: flex-end; }
</style>
