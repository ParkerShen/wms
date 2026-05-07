<template>
  <div class="dashboard">
    <el-row :gutter="20">
      <el-col :span="6" v-for="item in stats" :key="item.label">
        <el-card shadow="hover">
          <div class="stat-item">
            <div class="stat-value" :style="{ color: item.color }">{{ item.value }}</div>
            <div class="stat-label">{{ item.label }}</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card class="welcome" shadow="hover">
      <div class="welcome-content">
        <h2>欢迎使用星瀚WMS</h2>
        <p>仓储管理系统</p>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
const stats = [
  { label: '今日入库', value: '--', color: '#409eff' },
  { label: '今日出库', value: '--', color: '#67c23a' },
  { label: '库存SKU', value: '--', color: '#e6a23c' },
  { label: '待处理订单', value: '--', color: '#f56c6c' },
]
</script>

<style scoped lang="scss">
.dashboard {
  .stat-item {
    text-align: center;
    padding: 20px 0;
    .stat-value { font-size: 32px; font-weight: bold; }
    .stat-label { font-size: 14px; color: #909399; margin-top: 8px; }
  }
  .welcome {
    margin-top: 20px; text-align: center; padding: 40px;
    h2 { font-size: 24px; color: #303133; margin-bottom: 12px; }
    p { font-size: 14px; color: #909399; }
  }
}
</style>
