<template>
<div class="processed-page">
    <!-- 页头 -->
    <div class="page-head">
        <div class="page-title">我的经办事项</div>
        <div class="head-actions">
            <Button type="primary" icon="md-add" @click="openCreate">新建回收任务</Button>
            <Button type="text" icon="ios-time" @click="viewHistory" class="text-btn">历史记录</Button>
        </div>
    </div>

    <!-- 统计概览 -->
    <Row :gutter="16" class="stats-row">
        <Col :span="6">
            <Card class="stat-card">
                <div class="stat-title">今日处理</div>
                <div class="stat-num">{{ stats.today }} 项</div>
            </Card>
        </Col>
        <Col :span="6">
            <Card class="stat-card">
                <div class="stat-title">本周处理</div>
                <div class="stat-num">{{ stats.week }} 项</div>
            </Card>
        </Col>
        <Col :span="6">
            <Card class="stat-card">
                <div class="stat-title">本月处理</div>
                <div class="stat-num">{{ stats.month }} 项</div>
            </Card>
        </Col>
        <Col :span="6">
            <Card class="stat-card">
                <div class="stat-title">累计处理</div>
                <div class="stat-num">{{ stats.total.toLocaleString() }} 项</div>
            </Card>
        </Col>
    </Row>

    <!-- 任务列表 -->
    <Card class="table-card">
        <Table :columns="columns" :data="tableData" stripe />
    </Card>

    <!-- 常见问题 -->
    <Card class="faq-card">
        <div class="faq-title">常见问题</div>
        <Row :gutter="16">
            <Col :span="12">
                <div class="faq-item">
                    <div class="q">如何提交新的回收任务?</div>
                    <div class="a">点击页面右上方“新建回收任务”按钮，填写相关信息并提交即可。</div>
                </div>
                <div class="faq-item">
                    <div class="q">如何查看历史记录?</div>
                    <div class="a">点击右上角“历史记录”按钮，可查看所有历史任务。</div>
                </div>
            </Col>
            <Col :span="12">
                <div class="faq-item">
                    <div class="q">任务状态有哪些?</div>
                    <div class="a">包含“待处理”、“处理中”、“已完成”三种状态。</div>
                </div>
                <div class="faq-item">
                    <div class="q">数据是否实时更新?</div>
                    <div class="a">是的，系统数据每 5 分钟自动刷新一次。</div>
                </div>
            </Col>
        </Row>
    </Card>

    <!-- 新建/编辑任务弹窗 -->
    <Modal v-model="editVisible" :title="editForm.id ? '编辑回收任务' : '新建回收任务'" :mask-closable="false">
        <Form ref="editFormRef" :model="editForm" :rules="editRules" :label-width="90">
            <Form-item label="任务编号" prop="id">
                <Input v-model="editForm.id" placeholder="自动生成，可修改" />
            </Form-item>
            <Form-item label="电池类型" prop="type">
                <Select v-model="editForm.type" style="width: 220px;">
                    <Option value="磷酸铁锂电池">磷酸铁锂电池</Option>
                    <Option value="三元锂电池">三元锂电池</Option>
                    <Option value="钴酸锂电池">钴酸锂电池</Option>
                    <Option value="猛酸锂电池">猛酸锂电池</Option>
                    <Option value="钛酸锂电池">钛酸锂电池</Option>
                </Select>
            </Form-item>
            <Form-item label="回收数量" prop="amount">
                <InputNumber v-model="editForm.amount" :min="1" />
            </Form-item>
            <Form-item label="处理状态" prop="status">
                <Select v-model="editForm.status" style="width: 220px;">
                    <Option value="pending">待处理</Option>
                    <Option value="processing">处理中</Option>
                    <Option value="done">已完成</Option>
                </Select>
            </Form-item>
            <Form-item label="创建时间" prop="createdAt">
                <DatePicker type="datetime" :value="editForm.createdAt" format="yyyy-MM-dd HH:mm" @on-change="onDateChange" style="width: 220px;" />
            </Form-item>
        </Form>
        <div slot="footer">
            <Button @click="editVisible = false">取消</Button>
            <Button type="primary" @click="saveTask">保存</Button>
        </div>
    </Modal>
</div>
</template>

<script>
export default {
    name: "my-processed",
    data() {
        return {
            stats: { today: 12, week: 86, month: 342, total: 2145 },
            tableData: [
                { id: "TASK20250405001", type: "磷酸铁锂电池", amount: 200, status: "done", createdAt: "2026-04-05 09:30" },
                { id: "TASK20250405002", type: "三元锂电池", amount: 150, status: "processing", createdAt: "2026-04-05 10:15" },
                { id: "TASK20250405003", type: "钴酸锂电池", amount: 80, status: "pending", createdAt: "2026-04-05 11:00" },
                { id: "TASK20250404001", type: "固态电池", amount: 120, status: "done", createdAt: "2026-04-04 14:20" },
                { id: "TASK20250404002", type: "镍氢电池", amount: 60, status: "done", createdAt: "2026-04-04 16:45" }
            ],
            columns: [
                { title: "任务编号", key: "id" },
                { title: "电池类型", key: "type" },
                { title: "回收数量", key: "amount", render: (h, p) => h("span", `${p.row.amount} 块`) },
                { title: "处理状态", key: "status", render: (h, p) => {
                    const map = { done: { text: "已完成", color: "#19be6b" }, processing: { text: "处理中", color: "#ff9900" }, pending: { text: "待处理", color: "#2d8cf0" } };
                    const s = map[p.row.status] || map.pending;
                    return h("Tag", { props: { color: s.color } }, s.text);
                } },
                { title: "创建时间", key: "createdAt" },
                { title: "操作", key: "action", width: 140, render: (h, p) => {
                    return h("div", { class: "op-icons" }, [
                        h("Tooltip", { props: { content: "查看", placement: "top" } }, [h("Icon", { props: { type: "ios-eye-outline" }, on: { click: () => this.viewTask(p.row) } })]),
                        h("Tooltip", { props: { content: "编辑", placement: "top" } }, [h("Icon", { props: { type: "ios-create-outline" }, on: { click: () => this.editTask(p.row) } })]),
                        h("Tooltip", { props: { content: "链接", placement: "top" } }, [h("Icon", { props: { type: "md-link" }, on: { click: () => this.linkTask(p.row) } })])
                    ]);
                } }
            ],
            editVisible: false,
            editForm: { id: "", type: "", amount: 1, status: "pending", createdAt: "" },
            editRules: {
                id: [{ required: true, message: "请输入任务编号", trigger: "blur" }],
                type: [{ required: true, message: "请选择类型", trigger: "change" }],
                amount: [{ type: "number", required: true, message: "请输入数量", trigger: "change" }],
                status: [{ required: true, message: "请选择状态", trigger: "change" }],
                createdAt: [{ required: true, message: "请选择时间", trigger: "change" }]
            }
        };
    },
    methods: {
        openCreate() {
            const ts = this.formatDate(new Date(), "yyyyMMddHHmm");
            this.editForm = { id: `TASK${ts}`, type: "", amount: 1, status: "pending", createdAt: this.formatDate(new Date(), "yyyy-MM-dd HH:mm") };
            this.editVisible = true;
        },
        viewHistory() {
            this.$Message.info("历史记录功能示例");
        },
        viewTask(row) {
            this.$Modal.info({ title: "任务详情", content: `编号：${row.id}<br>类型：${row.type}<br>数量：${row.amount} 块<br>状态：${this.statusText(row.status)}<br>创建：${row.createdAt}` });
        },
        editTask(row) {
            this.editForm = { ...row };
            this.editVisible = true;
        },
        linkTask(row) {
            this.$Message.success(`跳转到 ${row.id} 的详情（示例）`);
        },
        saveTask() {
            this.$refs.editFormRef.validate(valid => {
                if (!valid) return;
                const idx = this.tableData.findIndex(t => t.id === this.editForm.id);
                if (idx !== -1) {
                    this.$set(this.tableData, idx, { ...this.editForm });
                } else {
                    this.tableData.unshift({ ...this.editForm });
                }
                this.editVisible = false;
                this.$Message.success("保存成功");
            });
        },
        onDateChange(v) {
            this.editForm.createdAt = v;
        },
        statusText(s) {
            if (s === "done") return "已完成";
            if (s === "processing") return "处理中";
            return "待处理";
        },
        formatDate(date, fmt) {
            const pad = n => (n < 10 ? "0" + n : "" + n);
            const y = date.getFullYear();
            const M = pad(date.getMonth() + 1);
            const d = pad(date.getDate());
            const h = pad(date.getHours());
            const m = pad(date.getMinutes());
            return fmt
                .replace("yyyy", y)
                .replace("MM", M)
                .replace("dd", d)
                .replace("HH", h)
                .replace("mm", m);
        }
    }
};
</script>

<style lang="less" scoped>
.processed-page { padding: 16px; }
.page-head { display: flex; align-items: center; justify-content: space-between; margin-bottom: 12px; }
.page-title { font-size: 20px; font-weight: 600; color: #17233d; }
.head-actions { display: flex; align-items: center; gap: 8px; }
.text-btn { color: #2d8cf0; }
.stats-row { margin-bottom: 12px; }
.stat-card { text-align: left; }
.stat-title { color: #808695; }
.stat-num { font-size: 24px; font-weight: 600; margin-top: 6px; }
.table-card { margin-top: 8px; }
.faq-card { margin-top: 12px; }
.faq-title { font-size: 16px; font-weight: 600; margin-bottom: 8px; }
.faq-item { margin-bottom: 12px; }
.faq-item .q { font-weight: 600; margin-bottom: 4px; }
.op-icons { display: flex; align-items: center; gap: 10px; font-size: 18px; }
</style>