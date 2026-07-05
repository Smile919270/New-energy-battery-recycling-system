<template>
<div class="login-container">
    <h1 class="page-title">新能源动力电池回收系统</h1>
    <div class="login-content" @keydown.enter="handleEnter">
        <!-- 左侧区域 -->
        <div class="login-left-section">
            <div class="illustration">
                <!-- 新能源汽车充电回收插图 -->
                <img src="@/assets/login/ev-recycle.png" alt="新能源电池回收" class="recycle-image" />
            </div>
        </div>
        
        <!-- 右侧登录框 -->
        <div class="login-right-section">
            <div class="login-box">
                <div class="login-tabs">
                    <span :class="['tab-item', { active: tabName === 'account' }]" @click="tabName = 'account'">账户登录</span>
                    <span :class="['tab-item', { active: tabName === 'sms' }]" @click="tabName = 'sms'">短信登录</span>
                </div>

                <div class="role-selector">
                    <Select v-model="selectedRole" size="large" placeholder="请选择登录角色">
                        <Option v-for="item in roleOptions" :value="item.value" :key="item.value">{{ item.label }}</Option>
                    </Select>
                </div>
                
                <Form v-show="tabName === 'account'" ref="usernameLoginForm" :model="form" :rules="usernameLoginFormRules" class="login-form">
                    <FormItem prop="username">
                        <Input v-model="form.username" size="large" placeholder="请输入账号" autocomplete="off">
                            <Icon type="ios-person-outline" slot="prefix" />
                        </Input>
                    </FormItem>
                    <FormItem prop="password">
                        <Input type="password" v-model="form.password" size="large" placeholder="请输入密码" password autocomplete="off">
                            <Icon type="ios-lock-outline" slot="prefix" />
                        </Input>
                    </FormItem>
                    <FormItem prop="code">
                        <div class="captcha-row">
                            <Input v-model="form.code" size="large" placeholder="请输入验证码" autocomplete="off" class="captcha-input">
                                <Icon type="ios-barcode-outline" slot="prefix" />
                            </Input>
                            <img v-if="captchaId" :src="captchaUrl" @click="refreshCaptcha" class="captcha-img" title="点击刷新验证码" alt="验证码" />
                        </div>
                    </FormItem>
                    <div class="login-options">
                        <Checkbox v-model="saveLogin">记住我</Checkbox>
                        <div class="login-links">
                            <a class="forget-link" href="javascript:void(0)">忘记密码?</a>
                            <span class="separator">|</span>
                            <router-link to="/regist" class="register-link">注册</router-link>
                        </div>
                    </div>
                    <FormItem>
                        <Button class="login-submit-btn" type="primary" size="large" :loading="loading" @click="submitLogin" long>
                            <span v-if="!loading">登录</span>
                            <span v-else>登录中...</span>
                        </Button>
                    </FormItem>
                </Form>
                
                <Form v-show="tabName === 'sms'" ref="smsLoginForm" :model="smsForm" :rules="smsLoginFormRules" class="login-form">
                    <FormItem prop="mobile">
                        <Input v-model="smsForm.mobile" size="large" placeholder="请输入手机号" autocomplete="off">
                            <Icon type="ios-phone-portrait" slot="prefix" />
                        </Input>
                    </FormItem>
                    <FormItem prop="code">
                        <div class="sms-code-row">
                            <Input v-model="smsForm.code" size="large" placeholder="请输入验证码" class="sms-input">
                                <Icon type="ios-mail-outline" slot="prefix" />
                            </Input>
                            <Button class="sms-btn" :disabled="smsCountdown > 0" @click="sendSmsCode">
                                {{ smsCountdown > 0 ? smsCountdown + 's' : '获取验证码' }}
                            </Button>
                        </div>
                    </FormItem>
                    <div class="login-options">
                        <Checkbox v-model="saveLogin">记住我</Checkbox>
                        <div class="login-links">
                            <a class="forget-link" href="javascript:void(0)">忘记密码?</a>
                            <span class="separator">|</span>
                            <router-link to="/regist" class="register-link">注册</router-link>
                        </div>
                    </div>
                    <FormItem>
                        <Button class="login-submit-btn" type="primary" size="large" :loading="loading" @click="submitSmsLogin" long>
                            <span v-if="!loading">登录</span>
                            <span v-else>登录中...</span>
                        </Button>
                    </FormItem>
                </Form>
                
                <div class="other-login">
                    <div class="divider"><span>其他登录方式</span></div>
                    <div class="social-icons">
                        <div class="social-icon wechat" title="微信登录" @click="startSocialLogin('wechat')">
                            <Icon type="ios-chatbubbles" />
                        </div>
                        <div class="social-icon qq" title="QQ登录" @click="startSocialLogin('qq')">
                            <Icon type="md-chatbubbles" />
                        </div>
                        <div class="social-icon google" title="Google登录" @click="startSocialLogin('google')">
                            <span class="google-text">G</span>
                        </div>
                    </div>
                </div>
                
                <div class="login-footer">
                    <a href="javascript:void(0)">用户协议</a>
                    <span class="separator">|</span>
                    <a href="javascript:void(0)">隐私政策</a>
                </div>
            </div>
        </div>
    </div>
    <div class="login-bottom-info">
        <p style="margin: 0;">
            <span style="color:rgba(255,255,255,0.8); font-size: 13px; margin-right: 20px;">版权所有 © 2025 新能源汽车动力电池回收系统 版权所有</span>
            <a target="_blank" href="https://beian.miit.gov.cn" style="color:rgba(255,255,255,0.7); text-decoration: none; margin: 0 12px; font-size: 12px;">
                ICP备案 桂ICP备123456789号
            </a>
            <a target="_blank" href="http://www.beian.gov.cn/portal/registerSystemInfo?recordcode=XXXXXXXXXXXXXX" style="color:rgba(255,255,255,0.7); text-decoration: none; margin: 0 12px; font-size: 12px;">
                <img src="@/assets/login/gonganlogo.png" style="width: 14px; height: 14px; margin-right: 4px; vertical-align: middle;" />
                <span>桂公网安备 1234567890号</span>
            </a>
        </p>
    </div>
    <div class="star-decoration">✦</div>
</div>
</template>

<script>
import {
    login,
    sendSmsCode as sendSmsCodeRequest,
    smsLogin as smsLoginRequest,
    socialAuthorize,
    userInfo,
    initCaptcha
} from "@/api/index";
import Cookies from "js-cookie";
import util from "@/libs/util.js";

export default {
    data() {
        return {
            tabName: "account",
            saveLogin: false,
            loading: false,
            smsCountdown: 0,
            selectedRole: "",
            roleOptions: [
                { label: "管理员", value: "ADMIN", aliases: ["ROLE_ADMIN", "管理员", "超级管理员", "1"] },
                { label: "电池出售方", value: "SELLER", aliases: ["ROLE_SELLER", "电池出售方", "出售方", "卖方", "2"] },
                { label: "电池买入方", value: "BUYER", aliases: ["ROLE_BUYER", "电池买入方", "买入方", "买方", "3"] },
                { label: "客服人员", value: "SERVICE", aliases: ["ROLE_SERVICE", "ROLE_CUSTOMER_SERVICE", "客服人员", "客服", "咨询接待人员", "4"] }
            ],
            form: {
                username: "",
                password: "",
                code: ""
            },
            captchaId: "",
            smsForm: {
                mobile: "",
                code: ""
            },
            usernameLoginFormRules: {
                username: [{
                    required: true,
                    message: "请输入账号",
                    trigger: "blur"
                }],
                password: [{
                    required: true,
                    message: "请输入密码",
                    trigger: "blur"
                }],
                code: [{
                    required: true,
                    message: "请输入验证码",
                    trigger: "blur"
                }]
            },
            smsLoginFormRules: {
                mobile: [{
                    required: true,
                    message: "请输入手机号",
                    trigger: "blur"
                }],
                code: [{
                    required: true,
                    message: "请输入验证码",
                    trigger: "blur"
                }]
            }
        };
    },
    computed: {
        captchaUrl() {
            return this.captchaId ? `/zyc/common/draw/${this.captchaId}` : '';
        }
    },
    methods: {
        normalizeRoleValue(value) {
            return (value || "").toString().trim().toUpperCase();
        },
        getSelectedRoleOption() {
            return this.roleOptions.find(item => item.value === this.selectedRole) || null;
        },
        matchSelectedRole(userInfo) {
            if (!this.selectedRole) {
                return true;
            }
            const selectedRoleOption = this.getSelectedRoleOption();
            const acceptedRoleValues = new Set();
            if (selectedRoleOption) {
                [selectedRoleOption.value, selectedRoleOption.label].concat(selectedRoleOption.aliases || []).forEach(item => {
                    acceptedRoleValues.add(this.normalizeRoleValue(item));
                });
            }
            if (typeof userInfo.type !== "undefined" && userInfo.type !== null) {
                const typeMap = {
                    ADMIN: 1,
                    SELLER: 2,
                    BUYER: 3,
                    SERVICE: 4
                };
                if (Object.prototype.hasOwnProperty.call(typeMap, this.selectedRole)) {
                    if (Number(userInfo.type) === typeMap[this.selectedRole]) {
                        return true;
                    }
                }
            }
            if (!Array.isArray(userInfo.roles)) {
                return false;
            }
            return userInfo.roles.some((role) => {
                return [role && role.name, role && role.description].some((item) => {
                    if (!item) {
                        return false;
                    }
                    return acceptedRoleValues.has(this.normalizeRoleValue(item));
                });
            });
        },
        handleEnter() {
            if (this.tabName === 'sms') {
                this.submitSmsLogin();
                return;
            }
            this.submitLogin();
        },
        initCaptcha() {
            initCaptcha().then(res => {
                if (res.success) {
                    this.captchaId = res.result;
                }
            }).catch(() => {
                this.$Message.error('验证码加载失败');
            });
        },
        refreshCaptcha() {
            this.form.code = '';
            this.initCaptcha();
        },
        sendSmsCode() {
            if (!this.smsForm.mobile) {
                this.$Message.warning('请先输入手机号');
                return;
            }
            // 验证手机号格式
            const mobileReg = /^1[3-9]\d{9}$/;
            if (!mobileReg.test(this.smsForm.mobile)) {
                this.$Message.warning('请输入正确的手机号');
                return;
            }
            sendSmsCodeRequest({ mobile: this.smsForm.mobile }).then(res => {
                if (res.success) {
                    this.$Message.success(`验证码已发送，演示验证码：${res.result}`);
                    this.smsCountdown = 60;
                    const timer = setInterval(() => {
                        this.smsCountdown--;
                        if (this.smsCountdown <= 0) {
                            clearInterval(timer);
                        }
                    }, 1000);
                }
            });
        },
        handleSocialLoginRedirect() {
            const query = this.$route.query || {};
            if (query.socialError) {
                this.$Message.error(query.socialError);
                this.$router.replace({ path: this.$route.path }).catch(() => {});
                return;
            }
            if (!query.socialToken) {
                return;
            }
            if (query.role) {
                this.selectedRole = query.role;
            }
            if (typeof query.saveLogin !== 'undefined') {
                this.saveLogin = query.saveLogin === 'true';
            }
            this.$router.replace({ path: this.$route.path }).catch(() => {});
            this.afterLogin({ result: query.socialToken });
        },
        startSocialLogin(provider) {
            if (!this.selectedRole) {
                this.$Message.warning('请先选择登录角色');
                return;
            }
            socialAuthorize({
                provider,
                role: this.selectedRole,
                saveLogin: this.saveLogin
            }).then(res => {
                if (res.success && res.result) {
                    window.location.href = res.result;
                    return;
                }
                this.$Message.error(res.message || '获取第三方登录地址失败');
            }).catch(() => {
                this.$Message.error('获取第三方登录地址失败');
            });
        },
        afterLogin(res) {
            let accessToken = res.result;
            this.setStore("accessToken", accessToken);
            userInfo().then((res) => {
                if (res.success) {
                    delete res.result.permissions;
                    if (!this.matchSelectedRole(res.result)) {
                        this.loading = false;
                        this.removeStore("accessToken");
                        this.$Message.error("所选角色与账号权限不匹配，请重新选择");
                        return;
                    }
                    let roles = [];
                    res.result.roles.forEach((e) => {
                        roles.push(e.name);
                    });
                    delete res.result.roles;
                    this.setStore("roles", roles);
                    this.setStore("saveLogin", this.saveLogin);
                    if (this.saveLogin) {
                        Cookies.set("userInfo", JSON.stringify(res.result), {
                            expires: 7,
                        });
                    } else {
                        Cookies.set("userInfo", JSON.stringify(res.result));
                    }
                    this.setStore("userInfo", res.result);
                    this.$store.commit("setAvatarPath", res.result.avatar);
                    util.initRouter(this);
                    const targetRouteName = Number(res.result.type) === 1 ? "home_index" : "my_home_index";
                    this.$router.push({
                        name: targetRouteName,
                    });
                } else {
                    this.loading = false;
                }
            });
        },
        submitLogin() {
            if (!this.selectedRole) {
                this.$Message.warning('请先选择登录角色');
                return;
            }
            this.$refs.usernameLoginForm.validate(valid => {
                if (valid) {
                    this.loading = true;
                    login({
                        username: this.form.username,
                        password: this.form.password,
                        code: this.form.code,
                        captchaId: this.captchaId,
                        saveLogin: this.saveLogin
                    }).then(res => {
                        if (res.success) {
                            this.afterLogin(res);
                        } else {
                            this.loading = false;
                            this.refreshCaptcha();
                        }
                    });
                }
            });
        },
        submitSmsLogin() {
            if (!this.selectedRole) {
                this.$Message.warning('请先选择登录角色');
                return;
            }
            this.$refs.smsLoginForm.validate(valid => {
                if (valid) {
                    this.loading = true;
                    smsLoginRequest({
                        mobile: this.smsForm.mobile,
                        code: this.smsForm.code,
                        saveLogin: this.saveLogin
                    }).then(res => {
                        if (res.success) {
                            this.afterLogin(res);
                        } else {
                            this.loading = false;
                        }
                    });
                }
            });
        }
    },
    mounted() {
        this.initCaptcha();
        this.handleSocialLoginRedirect();
    }
};
</script>

<style lang="less" scoped>
.login-container {
    width: 100%;
    height: 100vh;
    background: #3dd598;
    display: flex;
    align-items: center;
    justify-content: center;
    overflow: hidden;
    position: relative;
    font-family: 'Microsoft YaHei', sans-serif;
}

.page-title {
    position: absolute;
    top: 40px;
    left: 60px;
    font-size: 50px;
    font-weight: bold;
    color: white;
    margin: 0;
    letter-spacing: 2px;
    text-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
    z-index: 10;
}

.login-content {
    width: 100%;
    max-width: 1200px;
    min-height: 500px;
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 0 60px;
    margin-top: -30px;
}

.login-left-section {
    flex: 1;
    display: flex;
    flex-direction: column;
    align-items: flex-start;
    padding-right: 60px;
    
    .illustration {
        width: 100%;
        max-width: 650px;
        
        .recycle-image {
            width: 100%;
            height: auto;
        }
    }
}

.login-right-section {
    width: 420px;
    flex-shrink: 0;
    
    .login-box {
        background: linear-gradient(135deg, #ffffff 0%, #f8f9fa 100%);
        border-radius: 20px;
        padding: 45px 40px;
        box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15), 0 0 0 1px rgba(255, 255, 255, 0.5);
        backdrop-filter: blur(10px);
        transition: all 0.3s ease;
        
        &:hover {
            box-shadow: 0 25px 70px rgba(0, 0, 0, 0.2), 0 0 0 1px rgba(255, 255, 255, 0.6);
            transform: translateY(-2px);
        }
    }
}

.login-tabs {
    display: flex;
    margin-bottom: 35px;
    border-bottom: 2px solid #f0f0f0;
    
    .tab-item {
        padding: 0 0 14px 0;
        margin-right: 35px;
        font-size: 17px;
        color: #999;
        cursor: pointer;
        position: relative;
        transition: all 0.3s ease;
        
        &:hover { 
            color: #4096ff;
            transform: translateY(-1px);
        }
        
        &.active {
            color: #1a1a1a;
            font-weight: 600;
            
            &::after {
                content: '';
                position: absolute;
                bottom: -2px;
                left: 0;
                width: 100%;
                height: 3px;
                background: linear-gradient(90deg, #4096ff, #69b1ff);
                border-radius: 2px 2px 0 0;
                animation: slideIn 0.3s ease;
            }
        }
    }
}

@keyframes slideIn {
    from { width: 0; opacity: 0; }
    to { width: 100%; opacity: 1; }
}

.login-form {
    /deep/ .ivu-form-item { margin-bottom: 20px; }
    /deep/ .ivu-input-prefix { 
        left: 14px; 
        font-size: 18px; 
        color: #bfbfbf;
        display: flex;
        align-items: center;
        height: 100%;
        top: 0;
        line-height: 1;
    }
    /deep/ .ivu-input-suffix,
    /deep/ .ivu-input-icon-validate,
    /deep/ .ivu-icon-ios-close-circle,
    /deep/ .ivu-icon-ios-checkmark-circle {
        display: none !important;
    }
    /deep/ .ivu-input-large {
        height: 50px;
        padding-left: 42px;
        padding-right: 16px !important;
        border-radius: 10px;
        border: 2px solid #e8e8e8;
        font-size: 14px;
        transition: all 0.3s ease;
        background: #fafafa;
        
        &:hover {
            border-color: #d0d0d0;
            background: #fff;
        }
        
        &:focus { 
            border-color: #4096ff; 
            box-shadow: 0 0 0 3px rgba(64, 150, 255, 0.1);
            background: #fff;
            transform: translateY(-1px);
        }
    }
    /deep/ .ivu-input-icon:not(.ivu-input-prefix) { 
        display: none !important;
    }
    /deep/ .ivu-input-icon.ivu-input-prefix { 
        line-height: 48px;
        display: flex;
        align-items: center;
    }
}

.role-selector {
    margin-bottom: 20px;

    /deep/ .ivu-select-large .ivu-select-selection {
        height: 50px;
        border-radius: 10px;
        border: 2px solid #e8e8e8;
        background: #fafafa;
        transition: all 0.3s ease;

        &:hover {
            border-color: #d0d0d0;
            background: #fff;
        }
    }

    /deep/ .ivu-select-placeholder,
    /deep/ .ivu-select-selected-value {
        height: 46px;
        line-height: 46px;
        font-size: 14px;
    }

    /deep/ .ivu-select-visible .ivu-select-selection {
        border-color: #4096ff;
        box-shadow: 0 0 0 3px rgba(64, 150, 255, 0.1);
        background: #fff;
    }
}

.login-options {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
    /deep/ .ivu-checkbox-wrapper { font-size: 14px; color: #666; }
    .login-links {
        display: flex;
        align-items: center;
        gap: 8px;
        .separator { color: #e8e8e8; font-size: 12px; }
    }
    .forget-link, .register-link { 
        font-size: 14px; 
        color: #4096ff; 
        text-decoration: none; 
        &:hover { color: #69b1ff; } 
    }
}

.sms-code-row {
    display: flex;
    gap: 12px;
    .sms-input { flex: 1; }
    .sms-btn { width: 120px; height: 48px; border-radius: 6px; }
}

.captcha-row {
    display: flex;
    gap: 12px;
    align-items: center;
    .captcha-input { flex: 1; }
    .captcha-img {
        width: 120px;
        height: 50px;
        border-radius: 10px;
        cursor: pointer;
        border: 2px solid #e8e8e8;
        transition: all 0.3s ease;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
        object-fit: fill;
        
        &:hover {
            border-color: #4096ff;
            transform: scale(1.05) rotate(1deg);
            box-shadow: 0 4px 12px rgba(64, 150, 255, 0.3);
        }
        
        &:active {
            transform: scale(0.98);
        }
    }
}

.login-submit-btn {
    width: 100%;
    height: 52px;
    background: linear-gradient(135deg, #4096ff 0%, #69b1ff 100%);
    border: none;
    border-radius: 12px;
    font-size: 16px;
    font-weight: 600;
    letter-spacing: 1px;
    box-shadow: 0 4px 15px rgba(64, 150, 255, 0.4);
    transition: all 0.3s ease;
    position: relative;
    overflow: hidden;
    
    &::before {
        content: '';
        position: absolute;
        top: 0;
        left: -100%;
        width: 100%;
        height: 100%;
        background: linear-gradient(90deg, transparent, rgba(255,255,255,0.3), transparent);
        transition: left 0.5s;
    }
    
    &:hover {
        background: linear-gradient(135deg, #69b1ff 0%, #4096ff 100%);
        box-shadow: 0 6px 20px rgba(64, 150, 255, 0.5);
        transform: translateY(-2px);
        
        &::before {
            left: 100%;
        }
    }
    
    &:active {
        transform: translateY(0);
    }
}

.other-login {
    margin-top: 25px;
    .divider {
        display: flex;
        align-items: center;
        margin-bottom: 20px;
        &::before, &::after { content: ''; flex: 1; height: 1px; background: #e8e8e8; }
        span { padding: 0 15px; font-size: 13px; color: #999; }
    }
    .social-icons {
        display: flex;
        justify-content: center;
        gap: 20px;
        .social-icon {
            width: 48px;
            height: 48px;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            cursor: pointer;
            transition: transform 0.3s, box-shadow 0.3s;
            &:hover { transform: translateY(-3px); box-shadow: 0 6px 16px rgba(0, 0, 0, 0.2); }
            i {
                font-size: 24px;
                color: white;
            }
            .google-text {
                font-size: 24px;
                font-weight: bold;
                color: white;
            }
            &.wechat { 
                background: #07c160;
            }
            &.qq { 
                background: #12b7f5;
            }
            &.google { 
                background: #ea4335;
            }
        }
    }
}

.login-footer {
    text-align: center;
    margin-top: 25px;
    padding-top: 20px;
    a { font-size: 13px; color: #999; text-decoration: none; &:hover { color: #4096ff; } }
    .separator { margin: 0 12px; color: #e8e8e8; }
}

.star-decoration {
    position: absolute;
    bottom: 40px;
    right: 60px;
    color: rgba(255, 255, 255, 0.6);
    font-size: 28px;
    animation: twinkle 2s infinite;
}

.login-bottom-info {
    position: absolute;
    bottom: 0;
    left: 0;
    right: 0;
    text-align: center;
    padding: 20px 0 15px 0;
    background: linear-gradient(to bottom, rgba(0, 0, 0, 0), rgba(0, 0, 0, 0.15));
    backdrop-filter: blur(5px);
    p {
        margin: 0;
        line-height: 22px;
        font-size: 12px;
        font-family: 'Microsoft YaHei', sans-serif;
    }
    a {
        transition: color 0.3s;
        &:hover {
            color: rgba(255, 255, 255, 0.9) !important;
        }
    }
}

@keyframes twinkle {
    0%, 100% { opacity: 0.4; transform: scale(1); }
    50% { opacity: 1; transform: scale(1.2); }
}

@media (max-width: 1024px) {
    .login-content { flex-direction: column; padding: 40px 20px; }
    .login-left-section {
        padding-right: 0;
        align-items: center;
        margin-bottom: 40px;
        .main-title { font-size: 36px; text-align: center; }
        .illustration { max-width: 300px; }
    }
    .login-right-section { width: 100%; max-width: 420px; }
}
</style>
