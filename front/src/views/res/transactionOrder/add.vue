<template>
<div>
    <Card>
        <div slot="title">
            <div class="edit-head">
                <a @click="close" class="back-title">
                    <Icon type="ios-arrow-back" />返回
                </a>
                <div class="head-name">添加</div>
                <span></span>
                <a @click="close" class="window-close">
                    <Icon type="ios-close" size="31" class="ivu-icon-ios-close" />
                </a>
            </div>
        </div>
        <Form ref="form" :model="form" :label-width="100" :rules="formValidate" label-position="left">
            <FormItem label="交易类型" prop="type">
                <Select v-model="form.type" clearable style="width:570px">
                    <Option value="0">求购单</Option>
                    <Option value="1">销售单</Option>
                </Select>
            </FormItem>
            <FormItem label="求购单" prop="orderId" v-show="form.type=='0'">
                <Select v-model="form.resorderIdId" clearable style="width:570px" not-found-text="没有待交易的求购单...">
                    <Option v-for="(item,index) in purchaseList" :key="index" :value="item.id">{{item.resName}} - {{item.releaseName}}</Option>
                </Select>
            </FormItem>
            <FormItem label="销售单" prop="orderId" v-show="form.type=='1'">
                <Select v-model="form.orderId" clearable style="width:570px" not-found-text="没有待交易的销售单...">
                    <Option v-for="(item,index) in saleList" :key="index" :value="item.id">{{item.resName}} - {{item.releaseName}}</Option>
                </Select>
            </FormItem>
            <Form-item class="br">
                <Button @click="addOrderFx" :loading="submitLoading" type="primary">提交并保存</Button>
                <Button type="dashed" @click="close">关闭</Button>
            </Form-item>
        </Form>
    </Card>
</div>
</template>

<script>
import {
    addTransactionOrder,
    getNotSellAllSales,
    getNotSellAllPurchase,
    addOrder
} from "./api.js";
export default {
    name: "add",
    components: {},
    data() {
        return {
            submitLoading: false, // 表单提交状态
            form: { // 添加或编辑表单对象初始化数据
                type: "0",
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
                resorderIdId: "",
                orderId: ""
            },
            // 表单验证规则
            formValidate: {},
            purchaseList: [],
            saleList: []
        };
    },
    methods: {
        init() {
            this.getNotSellAllPurchaseFx();
            this.getNotSellAllSalesFx();
        },
        getNotSellAllPurchaseFx() {
            var that = this;
            getNotSellAllPurchase().then(res => {
                if (res.success) {
                    that.purchaseList = res.result;
                }
            })
        },
        getNotSellAllSalesFx() {
            var that = this;
            getNotSellAllSales().then(res => {
                if (res.success) {
                    that.saleList = res.result;
                }
            })
        },
        addOrderFx() {
            var that = this;
            addOrder({
                type: that.form.type,
                orderId: that.form.type == '0' ? that.form.resorderIdId : that.form.orderId
            }).then(res => {
                if (res.success) {
                    this.$Message.success("操作成功");
                    that.submited();
                }
            })
        },
        handleReset() {
            this.$refs.form.resetFields();
        },
        handleSubmit() {
            this.$refs.form.validate(valid => {
                if (valid) {
                    addTransactionOrder(this.form).then(res => {
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
