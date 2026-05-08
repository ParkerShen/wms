<template>
  <div>
    <el-card shadow="never" class="search-box">
      <el-form :model="queryParams" inline>
        <el-form-item label="仓库"><el-input v-model="queryParams.whCode" clearable style="width:120px" placeholder="仓库代码" /></el-form-item>
        <el-form-item label="库位"><el-input v-model="queryParams.locCode" clearable style="width:140px" placeholder="库位代码" /></el-form-item>
        <el-form-item label="SKU编码"><el-input v-model="queryParams.skuCode" clearable /></el-form-item>
        <el-form-item label="客户"><el-input v-model="queryParams.custCode" clearable style="width:120px" /></el-form-item>
        <el-form-item><el-button type="primary" @click="handleSearch">查询</el-button><el-button @click="handleReset">重置</el-button></el-form-item>
      </el-form>
    </el-card>
    <el-card shadow="never">
      <el-table :data="tableData" v-loading="loading" stripe border>
        <el-table-column prop="whCode" label="仓库" width="120" />
        <el-table-column prop="locCode" label="库位" width="120" />
        <el-table-column prop="zoneCode" label="库区" width="80" />
        <el-table-column prop="skuCode" label="SKU编码" width="150" />
        <el-table-column prop="custCode" label="客户" width="120" />
        <el-table-column prop="lotNo" label="批次" width="120" />
        <el-table-column prop="qty" label="可用" width="70" align="right" header-align="center">
          <template #default="{row}"><span style="color:#409eff;font-weight:bold">{{ row.qty }}</span></template>
        </el-table-column>
        <el-table-column prop="allocQty" label="分配" width="70" align="right" />
        <el-table-column prop="pickingQty" label="拣货" width="70" align="right" />
        <el-table-column prop="holdQty" label="冻结" width="70" align="right" />
        <el-table-column prop="badQty" label="坏品" width="70" align="right" />
        <el-table-column prop="totalQty" label="总计" width="70" align="right" />
        <el-table-column prop="lastUpdateTime" label="更新时间" width="180" />
      </el-table>
      <div class="page"><el-pagination v-model:current-page="queryParams.page" v-model:page-size="queryParams.pageSize"
        :total="total" :page-sizes="[10,20,50]" layout="total,sizes,prev,pager,next,jumper" @change="fetchData" /></div>
    </el-card>
  </div>
</template>
<script setup lang="ts">
import { ref, reactive, onMounted, watch } from 'vue'
import { getInvLocPageApi } from '@/api/inventory'
import type { InvLocItem, InvLocPageParams } from '@/api/inventory'
import { useUserStore } from '@/store/user'
const userStore = useUserStore()
const loading=ref(false);const tableData=ref<InvLocItem[]>([]);const total=ref(0)
const queryParams=reactive<InvLocPageParams>({page:1,pageSize:20,whCode:'',locCode:'',skuCode:'',custCode:''})
function syncWhCode(){queryParams.whCode=userStore.currentWarehouse?.whCode||''}
async function fetchData(){loading.value=true;syncWhCode();try{const r=await getInvLocPageApi(queryParams);tableData.value=r.records;total.value=r.total}finally{loading.value=false}}
function handleSearch(){queryParams.page=1;fetchData()}
function handleReset(){queryParams.locCode='';queryParams.skuCode='';queryParams.custCode='';handleSearch()}
watch(()=>userStore.currentWarehouse,()=>fetchData())
onMounted(fetchData)
</script>
<style scoped>.search-box{margin-bottom:16px}.page{margin-top:16px;display:flex;justify-content:flex-end}</style>
