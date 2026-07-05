<template>
<div class="todo-page">
    <!-- 页头 -->
    <div class="page-head">
        <div class="head-title">我的待办</div>
        <div class="head-desc">这里展示您的待办事项</div>
    </div>
    <!-- 工具栏 -->
    <div class="toolbar">
        <Input v-model="searchKey" clearable :placeholder="'搜索待办事项...'" prefix="ios-search" style="width: 320px;" />
        <div class="toolbar-right">
            <Select v-model="priorityFilter" style="width: 140px;">
                <Option value="all">全部优先级</Option>
                <Option value="high">高优先级</Option>
                <Option value="medium">中优先级</Option>
                <Option value="low">低优先级</Option>
            </Select>
            <Button type="primary" icon="md-add" @click="addTask" style="margin-left: 12px;">新增事项</Button>
        </div>
    </div>
    <!-- 卡片列表 -->
    <Row :gutter="16">
        <Col v-for="task in pagedTasks" :key="task.id" :span="8">
            <Card class="task-card">
                <div class="task-title">
                    <span class="name">{{ task.title }}</span>
                    <Tag :color="priorityColor(task.priority)" class="priority">{{ priorityLabel(task.priority) }}</Tag>
                </div>
                <div class="task-desc">{{ task.desc }}</div>
                <div class="deadline"><span>截止时间：</span><span>{{ task.deadline }}</span></div>
                <div class="status-row">
                    <div class="progress-wrap">
                        <Progress :percent="task.progress" status="active" />
                        <div class="percent-text">{{ task.progress }}% 完成</div>
                    </div>
                    <div class="ops">
                        <Tooltip content="提醒" placement="top">
                            <Icon type="ios-notifications-outline" @click="notifyTask(task)" />
                        </Tooltip>
                        <Tooltip content="编辑" placement="top">
                            <Icon type="ios-create-outline" @click="editTask(task)" />
                        </Tooltip>
                        <Tooltip content="删除" placement="top">
                            <Icon type="ios-trash-outline" @click="deleteTask(task)" />
                        </Tooltip>
                    </div>
                </div>
            </Card>
        </Col>
    </Row>
    <!-- 分页 -->
    <div class="pager">
        <Page :total="filteredTasks.length" :page-size="pageSize" :current="pageNumber" @on-change="changePage" size="small" show-total />
    </div>

    <!-- 编辑/新增弹窗 -->
    <Modal v-model="editModalVisible" :title="isCreate ? '新增待办' : '编辑待办'" :mask-closable="false">
        <Form ref="editForm" :model="editForm" :rules="editRules" :label-width="80">
            <Form-item label="标题" prop="title">
                <Input v-model="editForm.title" placeholder="请输入标题" />
            </Form-item>
            <Form-item label="描述" prop="desc">
                <Input type="textarea" v-model="editForm.desc" :rows="3" placeholder="请输入描述" />
            </Form-item>
            <Form-item label="优先级" prop="priority">
                <Select v-model="editForm.priority" style="width: 200px;">
                    <Option value="high">高优先级</Option>
                    <Option value="medium">中优先级</Option>
                    <Option value="low">低优先级</Option>
                </Select>
            </Form-item>
            <Form-item label="截止时间" prop="deadline">
                <DatePicker type="date" :value="editForm.deadline" format="yyyy-MM-dd" @on-change="onDeadlineChange" style="width: 200px;" />
            </Form-item>
            <Form-item label="进度(%)" prop="progress">
                <InputNumber v-model="editForm.progress" :min="0" :max="100" />
            </Form-item>
        </Form>
        <div slot="footer">
            <Button @click="cancelEdit">取消</Button>
            <Button type="primary" @click="saveEdit">保存</Button>
        </div>
    </Modal>
</div>
</template>

<script>
export default {
    name: "my-todo",
    data() {
        return {
            searchKey: "",
            priorityFilter: "all",
            pageNumber: 1,
            pageSize: 6,
            tasks: [
                { id: 1, title: "审核回收申请", desc: "检查并批准某XX公司的电池回收申请", priority: "high", deadline: "2025-12-15", progress: 30 },
                { id: 2, title: "更新回收价格表", desc: "根据市场行情调整各类电池的回收定价", priority: "medium", deadline: "2025-12-20", progress: 75 },
                { id: 3, title: "联系供应商", desc: "与新的材料供应商建立合作关系", priority: "high", deadline: "2025-12-10", progress: 10 },
                { id: 4, title: "系统维护", desc: "定期备份数据库并优化服务器性能", priority: "low", deadline: "2025-12-25", progress: 55 },
                { id: 5, title: "培训新员工", desc: "组织关于安全操作规程的新员工培训课程", priority: "medium", deadline: "2025-12-18", progress: 42 },
                { id: 6, title: "月度报告编制", desc: "整理本月数据并生成分析报告", priority: "high", deadline: "2025-12-30", progress: 20 }
            ],
            // 是否为新增模式
            isCreate: false,
            editModalVisible: false,
            editForm: {
                id: null,
                title: "",
                desc: "",
                priority: "medium",
                deadline: "",
                progress: 0
            },
            editRules: {
                title: [{ required: true, message: "请输入标题", trigger: "blur" }],
                priority: [{ required: true, message: "请选择优先级", trigger: "change" }],
                deadline: [{ required: true, message: "请选择日期", trigger: "change" }],
                progress: [{ type: "number", required: true, message: "请输入进度", trigger: "change" }]
            }
        };
    },
    computed: {
        filteredTasks() {
            let list = this.tasks;
            if (this.priorityFilter !== "all") {
                list = list.filter(t => t.priority === this.priorityFilter);
            }
            if (this.searchKey && this.searchKey.trim()) {
                const key = this.searchKey.trim().toLowerCase();
                list = list.filter(t => (t.title + t.desc).toLowerCase().includes(key));
            }
            return list;
        },
        pagedTasks() {
            const start = (this.pageNumber - 1) * this.pageSize;
            return this.filteredTasks.slice(start, start + this.pageSize);
        }
    },
    watch: {
        priorityFilter() {
            this.pageNumber = 1;
        },
        searchKey() {
            this.pageNumber = 1;
        }
    },
    methods: {
        changePage(page) {
            this.pageNumber = page;
        },
        addTask() {
            // 进入新增模式，打开表单
            this.isCreate = true;
            this.editForm = {
                id: null,
                title: "",
                desc: "",
                priority: "medium",
                deadline: "",
                progress: 0
            };
            this.editModalVisible = true;
        },
        notifyTask(task) {
            this.$Message.success(`已提醒：${task.title}`);
        },
        editTask(task) {
            this.isCreate = false;
            this.editForm = {
                id: task.id,
                title: task.title,
                desc: task.desc,
                priority: task.priority,
                deadline: task.deadline,
                progress: Number(task.progress)
            };
            this.editModalVisible = true;
        },
        deleteTask(task) {
            this.$Modal.confirm({
                title: "确认删除",
                content: `确定删除「${task.title}」吗？`,
                onOk: () => {
                    this.tasks = this.tasks.filter(t => t.id !== task.id);
                    const maxPage = Math.max(1, Math.ceil(this.filteredTasks.length / this.pageSize));
                    if (this.pageNumber > maxPage) this.pageNumber = maxPage;
                    this.$Message.success("删除成功");
                }
            });
        },
        saveEdit() {
            this.$refs.editForm.validate(valid => {
                if (!valid) return;
                if (this.isCreate) {
                    const maxId = this.tasks.length ? Math.max(...this.tasks.map(t => t.id)) : 0;
                    const newTask = {
                        id: maxId + 1,
                        title: this.editForm.title,
                        desc: this.editForm.desc,
                        priority: this.editForm.priority,
                        deadline: this.editForm.deadline,
                        progress: Number(this.editForm.progress)
                    };
                    this.tasks.unshift(newTask);
                    this.pageNumber = 1;
                    this.$Message.success("新增成功");
                } else {
                    const idx = this.tasks.findIndex(t => t.id === this.editForm.id);
                    if (idx !== -1) {
                        const updated = { ...this.tasks[idx], ...this.editForm, progress: Number(this.editForm.progress) };
                        this.$set(this.tasks, idx, updated);
                        this.$Message.success("保存成功");
                    }
                }
                this.editModalVisible = false;
                this.isCreate = false;
            });
        },
        cancelEdit() {
            this.editModalVisible = false;
            this.isCreate = false;
        },
        onDeadlineChange(v) {
            // v 已按 format 返回 yyyy-MM-dd 字符串
            this.editForm.deadline = v;
        },
        priorityColor(p) {
            if (p === "high") return "#ed4014";
            if (p === "medium") return "#ff9900";
            return "#19be6b";
        },
        priorityLabel(p) {
            if (p === "high") return "高优先级";
            if (p === "medium") return "中优先级";
            return "低优先级";
        }
    }
};
</script>

<style lang="less" scoped>
.todo-page {
    padding: 16px;
}
.page-head {
    display: flex;
    align-items: baseline;
    justify-content: space-between;
    margin-bottom: 12px;
}
.head-title {
    font-size: 20px;
    font-weight: 600;
    color: #17233d;
}
.head-desc {
    color: #808695;
}
.toolbar {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 16px;
}
.toolbar-right {
    display: flex;
    align-items: center;
}
.task-card {
    margin-bottom: 16px;
}
.task-title {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 8px;
}
.task-title .name {
    font-size: 16px;
    font-weight: 500;
    color: #17233d;
}
.task-title .priority {
    border-radius: 2px;
}
.task-desc {
    color: #515a6e;
    margin-bottom: 8px;
    height: 42px;
}
.deadline {
    color: #808695;
    margin-bottom: 8px;
}
.status-row {
    display: flex;
    align-items: center;
    justify-content: space-between;
}
.progress-wrap {
    flex: 1;
}
.percent-text {
    margin-top: 6px;
    font-size: 12px;
    color: #808695;
}
.ops {
    display: flex;
    align-items: center;
    margin-left: 12px;
}
.ops .ivu-icon {
    font-size: 18px;
    color: #808695;
    cursor: pointer;
    margin-left: 10px;
    transition: color .2s ease;
}
.ops .ivu-icon:hover {
    color: #2d8cf0;
}
.pager {
    margin-top: 8px;
    display: flex;
    justify-content: center;
}
</style>