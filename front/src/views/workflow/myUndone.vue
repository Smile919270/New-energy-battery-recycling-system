<template>
<div class="undone-page">
    <!-- 页头 -->
    <div class="page-head">
        <div class="page-title">我的未办</div>
        <Button type="primary" icon="md-add" @click="openCreateTask">新建任务</Button>
    </div>

    <!-- 主体布局 -->
    <Row :gutter="16">
        <!-- 左侧：待处理事项 + 用户反馈 -->
        <Col :span="12">
            <Card class="section-card">
                <div class="section-title">待处理事项</div>
                <div class="task-list">
                    <div class="task-item" v-for="item in tasks" :key="item.id" @click="viewTask(item)">
                        <span class="task-name">{{ item.title }}</span>
                        <span class="task-date">{{ item.date }}</span>
                    </div>
                </div>
                <div class="more-link" @click="viewMore">查看更多</div>
            </Card>

            <Card class="section-card">
                <div class="section-title">用户反馈</div>
                <Input type="textarea" v-model="feedback" :rows="5" placeholder="请输入您的意见或建议..." />
                <Button type="primary" @click="submitFeedback" style="margin-top: 12px;">提交反馈</Button>
            </Card>
        </Col>

        <!-- 右侧：快捷操作 + 常见问题 -->
        <Col :span="12">
            <Card class="section-card">
                <div class="section-title">快捷操作</div>
                <Row :gutter="16" class="quick-ops">
                    <Col :span="12">
                        <div class="quick-item" @click="quick('提交报告')">
                            <Icon type="ios-paper" size="28" />
                            <div class="qi-text">提交报告</div>
                        </div>
                    </Col>
                    <Col :span="12">
                        <div class="quick-item" @click="quick('安排运输')">
                            <Icon type="md-car" size="28" />
                            <div class="qi-text">安排运输</div>
                        </div>
                    </Col>
                    <Col :span="12">
                        <div class="quick-item" @click="quick('查看统计')">
                            <Icon type="ios-pie" size="28" />
                            <div class="qi-text">查看统计</div>
                        </div>
                    </Col>
                    <Col :span="12">
                        <div class="quick-item" @click="quick('帮助中心')">
                            <Icon type="ios-help-circle-outline" size="28" />
                            <div class="qi-text">帮助中心</div>
                        </div>
                    </Col>
                </Row>
            </Card>

            <Card class="section-card">
                <div class="section-title">常见问题</div>
                <Collapse accordion>
                    <Panel name="q1">
                        如何提交电池回收申请?
                        <p slot="content" class="qa-answer">在“提交报告”中填写必要信息后提交，管理员审核。</p>
                    </Panel>
                    <Panel name="q2">
                        回收流程需要多长时间?
                        <p slot="content" class="qa-answer">通常3-7个工作日，具体取决于安排和物流。</p>
                    </Panel>
                    <Panel name="q3">
                        如何联系客服?
                        <p slot="content" class="qa-answer">可在“帮助中心”查看联系方式或在线提交工单。</p>
                    </Panel>
                </Collapse>
            </Card>
        </Col>
    </Row>

    <!-- 新建任务弹窗 -->
    <Modal v-model="createModalVisible" title="新建任务" :mask-closable="false">
        <Form ref="createFormRef" :model="createForm" :rules="createRules" :label-width="80">
            <Form-item label="标题" prop="title">
                <Input v-model="createForm.title" placeholder="请输入标题" />
            </Form-item>
            <Form-item label="日期" prop="date">
                <DatePicker type="date" :value="createForm.date" format="yyyy-MM-dd" @on-change="onCreateDateChange" style="width: 200px;" />
            </Form-item>
        </Form>
        <div slot="footer">
            <Button @click="createModalVisible = false">取消</Button>
            <Button type="primary" @click="saveTask">创建</Button>
        </div>
    </Modal>
</div>
</template>

<script>
export default {
    name: "my-undone",
    data() {
        return {
            tasks: [
                { id: 1, title: "电池回收申请审核", date: "2026-04-05" },
                { id: 2, title: "供应商资质认证", date: "2026-04-07" },
                { id: 3, title: "运输计划确认", date: "2026-04-08" }
            ],
            feedback: "",
            createModalVisible: false,
            createForm: {
                title: "",
                date: ""
            },
            createRules: {
                title: [{ required: true, message: "请输入标题", trigger: "blur" }],
                date: [{ required: true, message: "请选择日期", trigger: "change" }]
            }
        };
    },
    methods: {
        openCreateTask() {
            this.createForm = { title: "", date: "" };
            this.createModalVisible = true;
        },
        saveTask() {
            this.$refs.createFormRef.validate(valid => {
                if (!valid) return;
                const maxId = this.tasks.length ? Math.max(...this.tasks.map(t => t.id)) : 0;
                this.tasks.unshift({ id: maxId + 1, title: this.createForm.title, date: this.createForm.date });
                this.createModalVisible = false;
                this.$Message.success("创建成功");
            });
        },
        onCreateDateChange(v) {
            this.createForm.date = v;
        },
        submitFeedback() {
            if (!this.feedback || !this.feedback.trim()) {
                this.$Message.warning("请输入反馈内容");
                return;
            }
            this.$Message.success("反馈已提交，谢谢！");
            this.feedback = "";
        },
        viewTask(item) {
            this.$Message.info(`查看：${item.title}`);
        },
        viewMore() {
            this.$Message.info("更多功能正在完善");
        },
        quick(name) {
            this.$Message.success(`${name} 功能示例`);
        }
    }
};
</script>

<style lang="less" scoped>
.undone-page { padding: 16px; }
.page-head { display: flex; align-items: center; justify-content: space-between; margin-bottom: 12px; }
.page-title { font-size: 18px; font-weight: 600; }
.section-card { margin-bottom: 16px; }
.section-title { font-size: 16px; font-weight: 600; margin-bottom: 12px; }
.task-list { display: flex; flex-direction: column; gap: 10px; }
.task-item { display: flex; align-items: center; justify-content: space-between; background: #f7f7f7; padding: 10px 12px; border-radius: 4px; cursor: pointer; }
.task-item:hover { background: #eef5ff; }
.task-date { color: #808695; }
.more-link { margin-top: 8px; color: #2d8cf0; cursor: pointer; }
.quick-item { display: flex; flex-direction: column; align-items: center; justify-content: center; background: #f7f7f7; padding: 14px; border-radius: 6px; cursor: pointer; }
.quick-item:hover { background: #eef5ff; border: 1px solid #2d8cf0; }
.qi-text { margin-top: 6px; font-size: 14px; color: #515a6e; }
.qa-answer { color: #666; line-height: 1.6; }
</style>