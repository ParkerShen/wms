<template>
  <div>
    <el-card shadow="never" class="search-box">
      <el-form :model="queryParams" inline>
        <el-form-item label="出库单号"><el-input v-model="queryParams.soNo" clearable /></el-form-item>
        <el-form-item label="客户"><el-input v-model="queryParams.custCode" clearable style="width:120px" /></el-form-item>
        <el-form-item label="状态"><el-select v-model="queryParams.status" clearable style="width:140px">
          <el-option v-for="s in statusOptions" :key="s.value" :label="s.label" :value="s.value" />
        </el-select></el-form-item>
        <el-form-item><el-button type="primary" @click="handleSearch">查询</el-button><el-button @click="handleReset">重置</el-button></el-form-item>
      </el-form>
    </el-card>
    <el-card shadow="never">
      <div class="tb"><el-button type="primary" @click="handleAdd" v-permission="'wms:so:add'">新增出库</el-button></div>
      <el-table :data="tableData" v-loading="loading" stripe border>
        <el-table-column prop="soNo" label="出库单号" width="180" />
        <el-table-column prop="custCode" label="客户" width="120" />
        <el-table-column prop="whCode" label="仓库" width="120" />
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{row}"><el-tag :type="statusTag(row.status)" size="small">{{ statusLabel(row.status) }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="custReferenceNo" label="客户订单号" width="150" />
        <el-table-column prop="productCode" label="产品" width="120" />
        <el-table-column prop="shippingType" label="发货类型" width="100"><template #default="{row}">{{ {CHANNEL:'渠道',SELF_DELIVERY:'自提',CUSTOMER_PROVIDE:'客供'}[row.shippingType]||row.shippingType }}</template></el-table-column>
        <el-table-column prop="consigneeName" label="收件人" width="120" />
        <el-table-column prop="consigneeCountry" label="国家" width="60" align="center" />
        <el-table-column prop="totalSkuQty" label="数量" width="60" align="right" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="280" align="center" fixed="right">
          <template #default="{row}">
            <el-button type="primary" link size="small" @click="handleEdit(row)" v-permission="'wms:so:edit'">编辑</el-button>
            <el-button type="primary" link size="small" @click="handleSubmit(row)" v-if="row.status==='DRAFT'" v-permission="'wms:so:submit'">提交</el-button>
            <el-button type="danger" link size="small" @click="handleDelete(row)" v-permission="'wms:so:delete'">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="page"><el-pagination v-model:current-page="queryParams.page" v-model:page-size="queryParams.pageSize"
        :total="total" :page-sizes="[10,20,50]" layout="total,sizes,prev,pager,next,jumper" @change="fetchData" /></div>
    </el-card>
    <el-dialog v-model="dialogVisible" :title="isEdit?'编辑出库单':'新增出库单'" width="900px" :close-on-click-modal="false">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="110px">
        <el-row :gutter="20">
          <el-col :span="8"><el-form-item label="出库单号" prop="soNo"><el-input v-model="form.soNo" :disabled="isEdit" /></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="客户代码"><el-input v-model="form.custCode" /></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="仓库代码"><el-input v-model="form.whCode" /></el-form-item></el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="8"><el-form-item label="客户订单号"><el-input v-model="form.custReferenceNo" /></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="产品代码"><el-input v-model="form.productCode" /></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="发货类型"><el-select v-model="form.shippingType" style="width:100%"><el-option label="渠道" value="CHANNEL" /><el-option label="自提" value="SELF_DELIVERY" /><el-option label="客供" value="CUSTOMER_PROVIDE" /></el-select></el-form-item></el-col>
        </el-row>
        <el-divider content-position="left">收货人信息</el-divider>
        <el-row :gutter="20">
          <el-col :span="8"><el-form-item label="收件人"><el-input v-model="form.consigneeName" /></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="电话"><el-input v-model="form.consigneePhone" /></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="邮编"><el-input v-model="form.consigneeZip" /></el-form-item></el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="8"><el-form-item label="国家"><el-input v-model="form.consigneeCountry" /></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="州/省"><el-input v-model="form.consigneeState" /></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="城市"><el-input v-model="form.consigneeCity" /></el-form-item></el-col>
        </el-row>
        <el-form-item label="地址"><el-input v-model="form.consigneeAddress" /></el-form-item>
        <el-form-item label="备注"><el-input v-model="form.remark" type="textarea" :rows="2" /></el-form-item>
        <el-divider content-position="left">出库商品</el-divider>
        <el-button type="success" size="small" @click="addSkuRow">+ 添加商品</el-button>
        <el-table :data="form.skuList" border stripe style="margin-top:8px" max-height="300">
          <el-table-column label="SKU编码" width="150"><template #default="{row,$index}"><el-input v-model="row.skuCode" size="small" @input="calcTotal" /></template></el-table-column>
          <el-table-column label="客户SKU编码" width="150"><template #default="{row}"><el-input v-model="row.custSkuCode" size="small" /></template></el-table-column>
          <el-table-column label="SKU名称" min-width="160"><template #default="{row}"><el-input v-model="row.skuName" size="small" /></template></el-table-column>
          <el-table-column label="数量" width="100"><template #default="{row}"><el-input-number v-model="row.qty" :min="0" size="small" style="width:100%" @change="calcTotal" /></template></el-table-column>
          <el-table-column label="操作" width="60" align="center"><template #default="{row}"><el-button type="danger" link size="small" @click="removeSkuRow(row)">×</el-button></template></el-table-column>
        </el-table>
      </el-form>
      <template #footer><el-button @click="dialogVisible=false">取消</el-button><el-button type="primary" :loading="submitLoading" @click="handleSubmitForm">确定</el-button></template>
    </el-dialog>
  </div>
</template>
<script setup lang="ts">
import { ref, reactive, onMounted, watch } from 'vue'
import { ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { getSoPageApi, getSoByIdApi, createSoApi, updateSoApi, deleteSoApi, submitSoApi } from '@/api/so'
import type { SoItem, SoPageParams, SoForm, SoSkuItem } from '@/api/so'
import { useUserStore } from '@/store/user'
const userStore = useUserStore()
const loading=ref(false);const tableData=ref<SoItem[]>([]);const total=ref(0)
const statusOptions=[{value:'DRAFT',label:'草稿'},{value:'SUBMITTED',label:'已提交'},{value:'PICKING',label:'拣货中'},{value:'PACKING',label:'打包中'},{value:'SHIPPED',label:'已出库'},{value:'CANCEL',label:'已取消'}]
const statusLabel=(s:string)=>(statusOptions.find(o=>o.value===s)?.label||s)
const statusTag=(s:string)=>({DRAFT:'info',SUBMITTED:'primary',PICKING:'warning',PACKING:'warning',SHIPPED:'success',CANCEL:'danger'}[s]||'info')
const queryParams=reactive<SoPageParams>({page:1,pageSize:20,soNo:'',custCode:'',status:undefined,whCode:''})
const dialogVisible=ref(false);const isEdit=ref(false);const submitLoading=ref(false);const formRef=ref<FormInstance>()
const defaultForm=():SoForm=>({soNo:'',custId:null,custCode:'',whId:null,whCode:'',status:'DRAFT',custReferenceNo:'',productId:null,productCode:'',shippingType:'CHANNEL',consigneeName:'',consigneePhone:'',consigneeAddress:'',consigneeCity:'',consigneeState:'',consigneeZip:'',consigneeCountry:'',totalSkuQty:null,remark:'',skuList:[]})
const form=reactive<SoForm>(defaultForm())
const rules:FormRules={soNo:[{required:true,message:'请输入出库单号',trigger:'blur'}]}
function calcTotal(){form.totalSkuQty=form.skuList.reduce((s,i)=>s+(i.qty||0),0)}
function addSkuRow(){form.skuList.push({skuCode:'',custSkuCode:'',skuName:'',qty:0})}
function removeSkuRow(row:SoSkuItem){const i=form.skuList.indexOf(row);if(i>-1)form.skuList.splice(i,1);calcTotal()}
function syncWhCode(){queryParams.whCode=userStore.currentWarehouse?.whCode||''}
async function fetchData(){loading.value=true;syncWhCode();try{const r=await getSoPageApi(queryParams);tableData.value=r.records;total.value=r.total}finally{loading.value=false}}
function handleSearch(){queryParams.page=1;fetchData()}
function handleReset(){queryParams.soNo='';queryParams.custCode='';queryParams.status=undefined;handleSearch()}
function handleAdd(){isEdit.value=false;Object.assign(form,defaultForm());form.whCode=userStore.currentWarehouse?.whCode||'';dialogVisible.value=true}
async function handleEdit(row:SoItem){isEdit.value=true;const d=await getSoByIdApi(row.id);Object.assign(form,d);dialogVisible.value=true}
function handleSubmit(row:SoItem){ElMessageBox.confirm(`确认提交出库单「${row.soNo}」？`,'提示',{type:'info',appendTo:'#app'}).then(async()=>{await submitSoApi(row.id);fetchData()}).catch(()=>{})}
function handleDelete(row:SoItem){ElMessageBox.confirm(`确认删除「${row.soNo}」？`,'提示',{type:'warning',appendTo:'#app'}).then(async()=>{await deleteSoApi(row.id);fetchData()}).catch(()=>{})}
async function handleSubmitForm(){const v=await formRef.value?.validate().catch(()=>false);if(!v)return;submitLoading.value=true;try{if(isEdit.value){await updateSoApi(form)}else{await createSoApi(form)}dialogVisible.value=false;fetchData()}catch{}finally{submitLoading.value=false}}
watch(()=>userStore.currentWarehouse,()=>fetchData())
onMounted(fetchData)
</script>
<style scoped>.search-box{margin-bottom:16px}.tb{margin-bottom:16px}.page{margin-top:16px;display:flex;justify-content:flex-end}</style>
