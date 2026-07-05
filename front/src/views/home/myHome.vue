<template>
<div class="own-space">
    <Card>
        <Divider dashed>
            <div style="margin-left: auto; justify-content: space-between; align-items: center; width: 100%;">
                <span>个人门户</span>
                <div style="margin-left: auto; gap: 8px;">
                    <Button type="success" v-show="!editFlag" @click="editFlag = true" size="small">开始编辑</Button>
                    <Button type="warning" v-show="editFlag" @click="saveEdit" size="small">保存提交</Button>
                </div>
            </div>
        </Divider>
        <Form ref="userForm" :model="userForm" :label-width="90" label-position="left">
            <Row :gutter="16">
                <Col :span="12">
                <FormItem label="登录账号" prop="username">
                    <Input v-model="userForm.username" readonly style="width: 100%" />
                </FormItem>
                </Col>
                <Col :span="12">
                <FormItem label="姓名" prop="nickname">
                    <Input v-model="userForm.nickname" readonly style="width: 100%" />
                </FormItem>
                </Col>
            </Row>
            <Row :gutter="16">
                <Col :span="12">
                <FormItem label="账号类型" prop="typeTxt">
                    <Input v-model="userForm.typeTxt" readonly style="width: 100%" />
                </FormItem>
                </Col>
                <Col :span="12">
                <FormItem label="所属部门" prop="departmentTitle">
                    <Input v-model="userForm.departmentTitle" readonly style="width: 100%" />
                </FormItem>
                </Col>
            </Row>
            <Row :gutter="16">
                <Col :span="12">
                <FormItem label="性别" prop="username">
                    <dict dict="sex" v-model="userForm.sex" transfer :readonly="!editFlag" placeholder="请选择性别" style="width: 100%" />
                </FormItem>
                </Col>
                <Col :span="12">
                <FormItem label="头像" prop="avatar">
                    <upload-pic-thumb v-model="displayAvatar" :multiple="false" style="width: 100%"></upload-pic-thumb>
                </FormItem>
                </Col>
            </Row>
            <Row :gutter="16">
                <Col :span="12">
                <FormItem label="家庭地址" prop="nickname">
                    <Input v-model="userForm.street" :readonly="!editFlag" style="width: 100%" />
                </FormItem>
                </Col>
                <Col :span="12">
                <FormItem label="生日" prop="username">
                    <DatePicker v-model="userForm.birth" @on-change="changeBirth" :readonly="!editFlag" style="width: 100%" type="date"></DatePicker>
                </FormItem>
                </Col>
            </Row>
            <Row :gutter="16">
                <Col :span="24">
                <FormItem label="个性签名" prop="nickname">
                    <Input v-model="userForm.description" type="textarea" :readonly="!editFlag" style="width: 100%" :autosize="{minRows: 3,maxRows: 5}" placeholder="个性签名"></Input>
                </FormItem>
                </Col>
            </Row>
        </Form>
    </Card>
    <Divider>个人门户</Divider>
    <Card>
        <Row :gutter="16">
            <Col span="12">
            <Divider>当前常用模块</Divider>
            <List header="---" footer="---" border>
                <ListItem v-for="(item, index) in addMenuTempList" :key="index">
                    {{ item.title }}
                    <Button @click="deleteOneMenu(item)" type="error" shape="circle" icon="md-trash" size="small" style="marginLeft:20px;"></Button>
                </ListItem>
            </List>
            </Col>
            <Col span="12">
            <Divider>
                <div style="margin-left: auto; justify-content: space-between; align-items: center; width: 100%;">
                    <span>个人门户设置</span>
                    <div>
                        <Button @click="addMenuFx" type="primary" size="small">保存更改</Button>
                    </div>
                </div>
            </Divider>
            <Row :gutter="16">
                <div class="desc">在下面的完成选择菜单即可添加</div>
                <search theme="light" type="input" @changeOk="addMenuTempFx" />
            </Row>
            </Col>
        </Row>
    </Card>
</div>
</template>

<script>
import {
    getMyDoorList,
    setMyDoorList,
    userInfoEdit
} from "./api.js";
import uploadPicThumb from "@/views/template/upload-pic-thumb";
import Cookies from "js-cookie";
import dict from "@/views/template/dict";
import search from "./search.vue";
export default {
    components: {
        uploadPicThumb,
        dict,
        search
    },
    name: "my_home_index",
    data() {
        return {
            editFlag: false,
            userForm: {
                id: "",
                avatar: "",
                username: "",
                sex: "",
                status: "",
                type: "",
                typeTxt: "",
                address: "",
                addressArray: []
            },
            saveLoading: false,
            addMenuTempList: []
        };
    },
    computed: {
        displayAvatar: {
            get() {
                let path = this.userForm.avatar;
                // 如果是相对路径，转换为完整URL
                if(path && path.startsWith('/')) {
                    return 'http://localhost:8081' + path;
                }
                return path;
            },
            set(val) {
                // 当上传新头像时，保存新的URL
                this.userForm.avatar = val;
            }
        }
    },
    methods: {
        init() {
            let v = JSON.parse(Cookies.get("userInfo"));
            for (let attr in v) {
                if (v[attr] == null) {
                    v[attr] = "";
                }
            }
            let str = JSON.stringify(v);
            let userInfo = JSON.parse(str);
            userInfo.addressArray = [];
            this.userForm = userInfo;
            if (userInfo.address) {
                this.userForm.address = userInfo.address;
                this.userForm.addressArray = JSON.parse(userInfo.address);
            }
            const typeMap = {
                1: "管理员",
                2: "电池出售方",
                3: "电池买入方"
            };
            this.userForm.typeTxt = typeMap[this.userForm.type] || "未知";
        },
        addMenuTempFx(e) {
            if (this.addMenuTempList.length > 5) {
                this.$Message.warning("最多添加六个常用模块");
            } else {
                this.addMenuTempList.push(e);
            }
        },

        addMenuFx() {
            var str = "";
            var size = this.addMenuTempList.length;
            if (size > 0) {
                str += this.addMenuTempList[0].name;
                for (var i = 1; i < size; i++) {
                    str += "ZWZ666" + this.addMenuTempList[i].name;
                }
            }
            setMyDoorList({
                str: str
            }).then((res) => {
                if (res.success) {
                    this.$Message.warning("操作成功");
                }
            });
        },
        saveEdit() {
            this.saveLoading = true;
            this.editFlag = false;
            if (typeof this.userForm.birth == "object") {
                this.userForm.birth = this.format(this.userForm.birth, "yyyy-MM-dd");
            }
            delete this.userForm.roles;
            userInfoEdit(this.userForm).then(res => {
                this.saveLoading = false;
                if (res.success) {
                    this.$Message.success("保存成功");
                    // 更新用户信息
                    Cookies.set("userInfo", JSON.stringify(this.userForm));
                    // 更新头像
                    this.$store.commit("setAvatarPath", this.userForm.avatar);
                }
            });
        },

        getMyDoorListFx() {
            var that = this;
            getMyDoorList().then((res) => {
                that.addMenuTempList = res.result;
            });
        },
        deleteOneMenu(e) {
            var array = [];
            for (var i = 0; i < this.addMenuTempList.length; i++) {
                if (this.addMenuTempList[i].name != e.name) {
                    array.push(this.addMenuTempList[i]);
                }
            }
            this.addMenuTempList = array;
        },
        changeAddress() {
            this.userForm.address = JSON.stringify(this.userForm.addressArray);
        },
        changeBirth(v) {
            this.userForm.birth = v;
        }
    },
    mounted() {
        this.init();
    }
};
</script>

<style lang="less" scoped>

</style>
