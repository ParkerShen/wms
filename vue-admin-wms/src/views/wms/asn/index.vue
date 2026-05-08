<template>
  <div>
    <el-card shadow="never" class="search-box">
      <el-form :model="queryParams" inline>
        <el-form-item label="入库单号"><el-input v-model="queryParams.asnNo" clearable /></el-form-item>
        <el-form-item label="客户"><el-input v-model="queryParams.custCode" clearable style="width:120px" /></el-form-item>
        <el-form-item label="状态"><el-select v-model="queryParams.status" clearable style="width:140px">
          <el-option v-for="s in statusOptions" :key="s.value" :label="s.label" :value="s.value" />
        </el-select></el-form-item>
        <el-form-item><el-button type="primary" @click="handleSearch">查询</el-button><el-button @click="handleReset">重置</el-button></el-form-item>
      </el-form>
    </el-card>
    <el-card shadow="never">
      <div class="tb"><el-button type="primary" @click="handleAdd" v-permission="'wms:asn:add'">新增入库</el-button></div>
      <el-table :data="tableData" v-loading="loading" stripe border>
        <el-table-column prop="asnNo" label="入库单号" width="180" />
        <el-table-column prop="custCode" label="客户" width="120" />
        <el-table-column prop="whCode" label="仓库" width="120" />
        <el-table-column prop="asnType" label="类型" width="80" align="center" />
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{row}"><el-tag :type="statusTag(row.status)" size="small">{{ statusLabel(row.status) }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="custReferenceNo" label="客户参考号" width="150" />
        <el-table-column prop="totalSkuQty" label="SKU数量" width="80" align="right" />
        <el-table-column prop="receiptMode" label="收货方式" width="100" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="280" align="center" fixed="right">
          <template #default="{row}">
            <el-button type="primary" link size="small" @click="handleEdit(row)" v-permission="'wms:asn:edit'">编辑</el-button>
            <el-button type="primary" link size="small" @click="handleSubmit(row)" v-if="row.status==='DRAFT'" v-permission="'wms:asn:submit'">提交</el-button>
            <el-button type="danger" link size="small" @click="handleDelete(row)" v-permission="'wms:asn:delete'">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="page"><el-pagination v-model:current-page="queryParams.page" v-model:page-size="queryParams.pageSize"
        :total="total" :page-sizes="[10,20,50]" layout="total,sizes,prev,pager,next,jumper" @change="fetchData" /></div>
    </el-card>
    <el-dialog v-model="dialogVisible" :title="isEdit?'编辑入库单':'新增入库单'" width="800px" :close-on-click-modal="false">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="110px">
        <el-row :gutter="20">
          <el-col :span="8"><el-form-item label="入库单号" prop="asnNo"><el-input v-model="form.asnNo" :disabled="isEdit" /></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="客户代码"><el-input v-model="form.custCode" /></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="仓库代码"><el-input v-model="form.whCode" /></el-form-item></el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="8"><el-form-item label="类型"><el-select v-model="form.asnType" style="width:100%"><el-option label="正常" value="NORMAL" /><el-option label="调拨" value="TRANSFER" /><el-option label="退货" value="RETURN" /></el-select></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="收货方式"><el-input v-model="form.receiptMode" /></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="客户参考号"><el-input v-model="form.custReferenceNo" /></el-form-item></el-col>
        </el-row>
        <el-form-item label="备注"><el-input v-model="form.remark" type="textarea" :rows="2" /></el-form-item>
        <el-divider content-position="left">入库商品</el-divider>
        <el-button type="success" size="small" @click="addSkuRow">+ 添加商品</el-button>
        <el-table :data="form.skuList" border stripe style="margin-top:8px" max-height="300">
          <el-table-column label="SKU编码" width="150"><template #default="{row,$index}"><el-input v-model="row.skuCode" size="small" @input="calcTotal" /></template></el-table-column>
          <el-table-column label="客户SKU编码" width="150"><template #default="{row}"><el-input v-model="row.custSkuCode" size="small" /></template></el-table-column>
          <el-table-column label="SKU名称" min-width="160"><template #default="{row}"><el-input v-model="row.skuName" size="small" /></template></el-table-column>
          <el-table-column label="预计数量" width="100"><template #default="{row}"><el-input-number v-model="row.expectedQty" :min="0" size="small" style="width:100%" @change="calcTotal" /></template></el-table-column>
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
import { getAsnPageApi, getAsnByIdApi, createAsnApi, updateAsnApi, deleteAsnApi, submitAsnApi } from '@/api/asn'
import type { AsnItem, AsnPageParams, AsnForm, AsnSkuItem } from '@/api/asn'
import { useUserStore } from '@/store/user'
const userStore = useUserStore()
const loading=ref(false);const tableData=ref<AsnItem[]>([]);const total=ref(0)
const statusOptions=[{value:'DRAFT',label:'草稿'},{value:'SUBMITTED',label:'已提交'},{value:'RECEIVING',label:'收货中'},{value:'FINISHED',label:'已完成'},{value:'CANCEL',label:'已取消'}]
const statusLabel=(s:string)=>(statusOptions.find(o=>o.value===s)?.label||s)
const statusTag=(s:string)=>({DRAFT:'info',SUBMITTED:'primary',RECEIVING:'warning',FINISHED:'success',CANCEL:'danger'}[s]||'info')
const queryParams=reactive<AsnPageParams>({page:1,pageSize:20,asnNo:'',custCode:'',status:undefined,whCode:''})
const dialogVisible=ref(false);const isEdit=ref(false);const submitLoading=ref(false);const formRef=ref<FormInstance>()
const defaultForm=():AsnForm=>({asnNo:'',custId:null,custCode:'',whId:null,whCode:'',asnType:'NORMAL',status:'DRAFT',custReferenceNo:'',receiptMode:'',remark:'',totalSkuQty:null,totalPkgQty:null,skuList:[]})
const form=reactive<AsnForm>(defaultForm())
const rules:FormRules={asnNo:[{required:true,message:'请输入入库单号',trigger:'blur'}]}
function calcTotal(){form.totalSkuQty=form.skuList.reduce((s,i)=>s+(i.expectedQty||0),0)}
function addSkuRow(){form.skuList.push({skuCode:'',custSkuCode:'',skuName:'',expectedQty:0})}
function removeSkuRow(row:AsnSkuItem){const i=form.skuList.indexOf(row);if(i>-1)form.skuList.splice(i,1);calcTotal()}
function syncWhCode(){queryParams.whCode=userStore.currentWarehouse?.whCode||''}
async function fetchData(){loading.value=true;syncWhCode();try{const r=await getAsnPageApi(queryParams);tableData.value=r.records;total.value=r.total}finally{loading.value=false}}
function handleSearch(){queryParams.page=1;fetchData()}
function handleReset(){queryParams.asnNo='';queryParams.custCode='';queryParams.status=undefined;handleSearch()}
function handleAdd(){isEdit.value=false;Object.assign(form,defaultForm());form.whCode=userStore.currentWarehouse?.whCode||'';dialogVisible.value=true}
async function handleEdit(row:AsnItem){isEdit.value=true;const d=await getAsnByIdApi(row.id);Object.assign(form,d);dialogVisible.value=true}
function handleSubmit(row:AsnItem){ElMessageBox.confirm(`确认提交入库单「${row.asnNo}」？`,'提示',{type:'info',appendTo:'#app'}).then(async()=>{await submitAsnApi(row.id);fetchData()}).catch(()=>{})}
function handleDelete(row:AsnItem){ElMessageBox.confirm(`确认删除「${row.asnNo}」？`,'提示',{type:'warning',appendTo:'#app'}).then(async()=>{await deleteAsnApi(row.id);fetchData()}).catch(()=>{})}
async function handleSubmitForm(){const v=await formRef.value?.validate().catch(()=>false);if(!v)return;submitLoading.value=true;try{if(isEdit.value){await updateAsnApi(form)}else{await createAsnApi(form)}dialogVisible.value=false;fetchData()}catch{}finally{submitLoading.value=false}}
watch(()=>userStore.currentWarehouse,()=>fetchData())
onMounted(fetchData)
</script>
<style scoped>.search-box{margin-bottom:16px}.tb{margin-bottom:16px}.page{margin-top:16px;display:flex;justify-content:flex-end}</style>
