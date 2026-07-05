<template>
  <div class="home-wrap">
    <!-- 统计卡片 -->
    <Row :gutter="16" class="stat-row">
      <Col :xs="12" :sm="12" :md="6">
        <Card class="stat-card" @click.native="goPage('resourceM')">
          <div class="stat-icon icon-orange">
            <Icon type="md-cart" size="28" color="#fff" />
          </div>
          <div class="stat-info">
            <p class="stat-num">{{ stats.totalBatteries }}</p>
          </div>
          <span class="stat-label">电池品类</span>
        </Card>
      </Col>
      <Col :xs="12" :sm="12" :md="6">
        <Card class="stat-card" @click.native="goPage('salesOrderM')">
          <div class="stat-icon icon-green">
            <Icon type="md-log-out" size="28" color="#fff" />
          </div>
          <div class="stat-info">
            <p class="stat-num">{{ stats.totalSalesOrders }}</p>
          </div>
          <span class="stat-label">电池出售单</span>
        </Card>
      </Col>
      <Col :xs="12" :sm="12" :md="6">
        <Card class="stat-card" @click.native="goPage('purchaseOrderM')">
          <div class="stat-icon icon-yellow">
            <Icon type="md-chatbubbles" size="28" color="#fff" />
          </div>
          <div class="stat-info">
            <p class="stat-num">{{ stats.totalPurchaseOrders }}</p>
          </div>
          <span class="stat-label">电池求购单</span>
        </Card>
      </Col>
      <Col :xs="12" :sm="12" :md="6">
        <Card class="stat-card" @click.native="goPage('transactionOrderM')">
          <div class="stat-icon icon-orange">
            <Icon type="logo-yen" size="28" color="#fff" />
          </div>
          <div class="stat-info">
            <p class="stat-num">￥{{ stats.monthlyAmount }}</p>
          </div>
          <span class="stat-label">本月交易额</span>
        </Card>
      </Col>
    </Row>

    <!-- 图表行 -->
    <Row :gutter="16" class="chart-row">
      <Col :xs="24" :md="12">
        <Card class="chart-card">
          <div class="chart-title">电池新旧程度分布</div>
          <div id="batteryTypeChart" style="width: 100%; height: 320px;"></div>
        </Card>
      </Col>
      <Col :xs="24" :md="12">
        <Card class="chart-card">
          <div class="chart-title">电池类型</div>
          <div id="batteryStockChart" style="width: 100%; height: 320px;"></div>
        </Card>
      </Col>
    </Row>

    <!-- 底部三栏 -->
    <Row :gutter="16" class="bottom-row">
      <!-- 系统状态 -->
      <Col :xs="24" :md="8">
        <Card class="info-card">
          <div class="card-title-plain">系统状态</div>
          <div class="sys-status">
            <div class="sys-status-header">
              <Icon type="md-checkmark-circle" size="56" color="#19be6b" v-if="systemOk" />
              <Icon type="md-warning" size="56" color="#ff9900" v-else />
            </div>
            <div class="sys-status-text" :style="{ color: systemOk ? '#19be6b' : '#ff9900' }">
              {{ systemOk ? '正常' : '需要关注' }}
            </div>
            <div class="sys-status-list">
              <div class="sys-item">
                <span class="sys-item-label">未回复资讯</span>
                <span class="sys-item-val">{{ unrepliedConsulting }}</span>
              </div>
              <div class="sys-item">
                <span class="sys-item-label">今日资讯量</span>
                <span class="sys-item-val">{{ todayConsulting }}</span>
              </div>
              <div class="sys-item">
                <span class="sys-item-label">总资讯数量</span>
                <span class="sys-item-val">{{ totalConsulting }}</span>
              </div>
            </div>
          </div>
        </Card>
      </Col>

      <!-- 待办事项 -->
      <Col :xs="24" :md="8">
        <Card class="info-card">
          <div class="card-title-plain">待办事项</div>
          <div class="todo-list">
            <div class="todo-item" v-for="(item, index) in todoItems" :key="index">
              <div class="todo-left">
                <span class="todo-dot" :style="{ background: item.color }"></span>
                <span>{{ item.name }}</span>
              </div>
              <Badge :count="item.count" :class-name="item.count > 0 ? 'todo-badge-active' : 'todo-badge-zero'" />
            </div>
          </div>
        </Card>
      </Col>

      <!-- 电池品类单价 -->
      <Col :xs="24" :md="8">
        <Card class="info-card">
          <div class="card-title-plain">电池品类单价</div>
          <Table :columns="priceColumns" :data="priceList" size="small" :show-header="false" stripe />
        </Card>
      </Col>
    </Row>
  </div>
</template>

<script>
import { Pie, Line } from "@antv/g2plot";
import { getDashboardData } from "@/api/index.js";
import Cookies from "js-cookie";

export default {
  name: "home_index",
  data() {
    return {
      stats: {
        totalBatteries: 0,
        totalSalesOrders: 0,
        totalPurchaseOrders: 0,
        monthlyAmount: 0,
      },
      unrepliedConsulting: 0,
      orgCount: 0,
      todayConsulting: 0,
      totalConsulting: 0,
      systemOk: true,
      todoItems: [],
      priceList: [],
      priceColumns: [
        {
          title: "品类名称",
          key: "name",
          minWidth: 120,
        },
        {
          title: "单价",
          key: "unitPrice",
          width: 100,
          render: (h, params) => {
            var price = params.row.unitPrice || 0;
            if (typeof price === "string") {
              price = price.replace(/[^\d.]/g, "");
            }
            return h("span", "￥" + price);
          },
        },
        {
          title: "单位",
          key: "unit",
          minWidth: 130,
          align: "right",
          render: (h, params) => {
            return h("span", { style: { whiteSpace: "nowrap" } }, params.row.unit || "个");
          },
        },
      ],
      batteryTypeChart: null,
      batteryStockChart: null,
    };
  },
  methods: {
    goPage(name) {
      this.$router.push({ name: name });
    },
    loadStatistics() {
      getDashboardData().then((res) => {
        if (res.success) {
          const d = res.result;
          this.stats.totalBatteries = d.totalBatteries || 0;
          this.stats.totalSalesOrders = d.totalSalesOrders || 0;
          this.stats.totalPurchaseOrders = d.totalPurchaseOrders || 0;
          this.stats.monthlyAmount = d.monthlyAmount || 0;

          this.unrepliedConsulting = d.unrepliedConsulting || 0;
          this.orgCount = d.orgCount || 0;
          this.todayConsulting = d.todayConsulting || 0;
          this.totalConsulting = d.totalConsulting || 0;
          this.systemOk = (d.unrepliedConsulting || 0) === 0;

          this.todoItems = [
            { name: "待处理求购单", count: d.pendingPurchase || 0, color: "#ff9900" },
            { name: "待处理出售单", count: d.pendingSales || 0, color: "#2d8cf0" },
            { name: "已完成交易", count: d.completedTransactions || 0, color: "#19be6b" },
            { name: "未回复资讯", count: d.pendingConsulting || 0, color: "#ed4014" },
          ];

          this.priceList = (d.priceList || []).map((item, index) => {
            return { ...item, _index: index };
          });

          this.$nextTick(() => {
            this.renderDegreeChart(d.statusDistribution || []);
            this.renderTypeChart(d.typeDistribution || []);
          });
        }
      });
    },
    renderDegreeChart(data) {
      if (this.batteryTypeChart) {
        this.batteryTypeChart.changeData(data);
        return;
      }
      this.batteryTypeChart = new Line("batteryTypeChart", {
        data: data,
        xField: "type",
        yField: "value",
        smooth: true,
        point: {
          size: 4,
          shape: "circle",
          style: {
            fill: "#2d8cf0",
            stroke: "#2d8cf0",
            lineWidth: 2,
          },
        },
        state: {
          active: {
            style: {
              fill: "#2d8cf0",
              stroke: "#2d8cf0",
              lineWidth: 2,
            },
          },
        },
        label: {
          style: {
            fill: "#333",
            fontSize: 12,
            fontWeight: 500,
          },
          offsetY: function(datum) {
            var maxVal = Math.max.apply(null, data.map(function(d) { return d.value; }));
            return datum.value >= maxVal ? -16 : -8;
          },
        },
        padding: [32, 20, 30, 30],
        lineStyle: {
          stroke: "#2d8cf0",
          lineWidth: 2,
        },
        xAxis: {
          label: {
            style: { fill: "#666", fontSize: 12 },
          },
        },
        yAxis: {
          label: {
            style: { fill: "#999", fontSize: 11 },
          },
          grid: {
            line: {
              style: { stroke: "#eee", lineWidth: 1, lineDash: [4, 4] },
            },
          },
        },
        tooltip: {
          showMarkers: true,
        },
        meta: {
          value: { alias: "数量", min: 0, nice: true },
        },
      });
      this.batteryTypeChart.render();
    },
    renderTypeChart(data) {
      if (this.batteryStockChart) {
        this.batteryStockChart.changeData(data);
        return;
      }
      this.batteryStockChart = new Pie("batteryStockChart", {
        data: data,
        angleField: "value",
        colorField: "type",
        radius: 0.78,
        innerRadius: 0.2,
        label: false,
        color: ["#f5a623", "#4a90d9", "#e8584f", "#5cb85c", "#9b59b6", "#b0b0b0", "#c4c4c4", "#e06080", "#36cfc9", "#7265e6", "#f5d623", "#ff85c0"],
        legend: {
          position: "right",
          offsetX: -20,
          itemSpacing: 12,
          itemName: {
            style: { fontSize: 12, fill: "#333" },
          },
        },
        statistic: false,
        interactions: [
          { type: "element-active" },
        ],
      });
      this.batteryStockChart.render();
    },
  },
  mounted() {
    this.loadStatistics();
  },
  beforeDestroy() {
    if (this.batteryTypeChart) {
      this.batteryTypeChart.destroy();
    }
    if (this.batteryStockChart) {
      this.batteryStockChart.destroy();
    }
  },
};
</script>

<style lang="less" scoped>
.home-wrap {
  padding: 16px;
}

/* 统计卡片行 */
.stat-row {
  margin-bottom: 16px;
}

.stat-card {
  cursor: pointer;
  border-radius: 10px;
  transition: all 0.3s;
  overflow: hidden;
  background: #fff;
  border: 1px solid #e8e8e8;
}
.stat-card:hover {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transform: translateY(-1px);
}

.stat-card /deep/ .ivu-card-body {
  display: flex;
  align-items: center;
  padding: 20px 16px;
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 14px;
  flex-shrink: 0;
}

.icon-orange {
  background: #ff9900;
}

.icon-green {
  background: #19be6b;
}

.icon-yellow {
  background: #ffbb33;
}

.stat-info {
  flex: 1;
}

.stat-num {
  font-size: 30px;
  font-weight: 700;
  color: #333;
  margin: 0;
  line-height: 1.2;
}

.stat-label {
  font-size: 15px;
  color: #999;
  margin-left: auto;
  flex-shrink: 0;
}

/* 图表行 */
.chart-row {
  margin-bottom: 16px;
}

.chart-card {
  border-radius: 10px;
}

.chart-card /deep/ .ivu-card-body {
  padding: 16px 20px;
}

.chart-title {
  font-size: 17px;
  font-weight: 600;
  color: #333;
  margin-bottom: 12px;
  padding-bottom: 12px;
  border-bottom: 1px solid #e8e8e8;
}

/* 底部信息行 */
.bottom-row {
  margin-bottom: 16px;
}

.info-card {
  border-radius: 10px;
}

.info-card /deep/ .ivu-card-body {
  padding: 16px 20px;
}

.info-card /deep/ .ivu-table td {
  font-size: 15px;
  padding-top: 14px;
  padding-bottom: 14px;
}

.card-title-plain {
  font-size: 17px;
  font-weight: 600;
  color: #333;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f0f0f0;
}

/* 系统状态 */
.sys-status {
  text-align: center;
}

.sys-status-header {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px 0 8px;
}

.sys-status-text {
  font-size: 24px;
  font-weight: 700;
  margin-bottom: 20px;
}

.sys-status-list {
  border-top: 1px solid #f0f0f0;
  padding-top: 12px;
  text-align: left;
}

.sys-item {
  display: flex;
  align-items: center;
  padding: 8px 0;
}

.sys-item-label {
  flex: 1;
  font-size: 15px;
  color: #666;
}

.sys-item-val {
  font-size: 15px;
  color: #333;
  font-weight: 500;
}

/* 待办列表 */
.todo-list {
  padding: 0;
}

.todo-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 0;
  border-bottom: 1px solid #f5f5f5;
}
.todo-item:last-child {
  border-bottom: none;
}

.todo-left {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 15px;
}

.todo-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  display: inline-block;
}

/deep/ .todo-badge-active {
  background: #ed4014 !important;
}

/deep/ .todo-badge-zero {
  background: #c5c8ce !important;
}
</style>
