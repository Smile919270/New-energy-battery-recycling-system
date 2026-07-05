<template>
<div>
    <Card>
        <div slot="title">
            <div class="edit-head">
                <a @click="close" class="back-title">
                    <Icon type="ios-arrow-back" />返回
                </a>
                <div class="head-name">查看交易单详情</div>
                <span></span>
                <a @click="close" class="window-close">
                    <Icon type="ios-close" size="31" class="ivu-icon-ios-close" />
                </a>
            </div>
        </div>
        <Form ref="form" :model="form" :label-width="100" :rules="formValidate" label-position="left">
            <FormItem label="交易类型" prop="type">
                <Input v-model="form.type" readonly style="width:570px" />
            </FormItem>
            <FormItem label="电池ID" prop="resId">
                <Input v-model="form.resId" readonly style="width:570px" />
            </FormItem>
            <FormItem label="电池名称" prop="resName">
                <Input v-model="form.resName" readonly style="width:570px" />
            </FormItem>
            <FormItem label="发布人ID" prop="releaseId">
                <Input v-model="form.releaseId" readonly style="width:570px" />
            </FormItem>
            <FormItem label="发布人姓名" prop="releaseName">
                <Input v-model="form.releaseName" readonly style="width:570px" />
            </FormItem>
            <FormItem label="交易量" prop="number">
                <InputNumber v-model="form.number" min="0" max="5000000" readonly style="width:570px"></InputNumber>
            </FormItem>
            <FormItem label="交易价格" prop="price">
                <InputNumber v-model="form.price" min="0" max="5000000" readonly style="width:570px"></InputNumber>
            </FormItem>
            <FormItem label="需求详情" prop="content">
                <Input v-model="form.content" readonly style="width:570px" />
            </FormItem>
            <FormItem label="交易人ID" prop="finishId">
                <Input v-model="form.finishId" readonly style="width:570px" />
            </FormItem>
            <FormItem label="交易人姓名" prop="finishName">
                <Input v-model="form.finishName" readonly style="width:570px" />
            </FormItem>
            <FormItem label="交易时间" prop="finishTime">
                <Input v-model="form.finishTime" readonly style="width:570px" />
            </FormItem>
            <Form-item class="br">
                <Button type="dashed" @click="close">关闭</Button>
            </Form-item>
        </Form>
    </Card>
</div>
</template>

<script>
import {
    editTransactionOrder
} from "./api.js";
export default {
    name: "edit",
    components: {},
    props: {
        data: Object
    },
    data() {
        return {
            submitLoading: false, // 表单提交状态
            form: { // 添加或编辑表单对象初始化数据
                type: "",
                resId: "",
                resName: "",
                releaseId: "",
                releaseName: "",
                number: 0,
                price: 0,
                content: "",
                finishId: "",
                finishName: "",
                finishTime: "",
            },
            // 表单验证规则
            formValidate: {}
        };
    },
    methods: {
        init() {
            this.handleReset();
            this.form = this.data;
        },
        handleReset() {
            this.$refs.form.resetFields();
        },
        handleSubmit() {
            this.$refs.form.validate(valid => {
                if (valid) {
                    editTransactionOrder(this.form).then(res => {
                        this.submitLoading = false;
                        if (res.success) {
                            this.$Message.success("操作成功");
                            this.submited();
                        }
                    });
                }
            });
        },
        close() {
            this.$emit("close", true);
        },
        submited() {
            this.$emit("submited", true);
        }
    },
    mounted() {
        this.init();
    }
};
</script>

<style lang="less">
// 建议引入通用样式 具体路径自行修改 可删除下面样式代码
// @import "../../../styles/single-common.less";
.edit-head {
    display: flex;
    align-items: center;
    justify-content: space-between;
    position: relative;

    .back-title {
        color: #515a6e;
        display: flex;
        align-items: center;
    }

    .head-name {
        display: inline-block;
        height: 20px;
        line-height: 20px;
        font-size: 16px;
        color: #17233d;
        font-weight: 500;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
    }

    .window-close {
        z-index: 1;
        font-size: 12px;
        position: absolute;
        right: 0px;
        top: -5px;
        overflow: hidden;
        cursor: pointer;

        .ivu-icon-ios-close {
            color: #999;
            transition: color .2s ease;
        }
    }

    .window-close .ivu-icon-ios-close:hover {
        color: #444;
    }
}
</style>
