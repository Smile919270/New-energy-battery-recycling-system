<template>
<div class="register-container">
    <h1 class="page-title">新能源动力电池回收系统</h1>
    <div class="register-content" @keydown.enter="submitRegist">
        <!-- 左侧区域 -->
        <div class="register-left-section">
            <div class="illustration">
                <img src="@/assets/login/ev-recycle.png" alt="新能源电池回收" class="recycle-image" />
            </div>
        </div>
        
        <!-- 右侧注册框 -->
        <div class="register-right-section">
            <div class="register-box">
                <div class="register-header">
                    <h2>用户注册</h2>
                </div>
                
                <Form ref="usernameLoginForm" :model="form" :rules="rules" class="register-form">
                    <FormItem prop="username">
                        <Input v-model="form.username" size="large" placeholder="请输入手机号" autocomplete="off">
                            <Icon type="ios-phone-portrait" slot="prefix" />
                        </Input>
                    </FormItem>
                    <FormItem prop="nickname">
                        <Input v-model="form.nickname" size="large" placeholder="请输入姓名" autocomplete="off">
                            <Icon type="ios-person-outline" slot="prefix" />
                        </Input>
                    </FormItem>
                    <FormItem prop="type">
                        <Select v-model="form.type" size="large" placeholder="请选择用户类型">
                            <Option :value="2">电池出售方</Option>
                            <Option :value="3">电池买入方</Option>
                            <Option :value="4">客服人员</Option>
                        </Select>
                    </FormItem>
                    <FormItem prop="password">
                        <Input type="password" v-model="form.password" size="large" placeholder="请输入密码" password autocomplete="off">
                            <Icon type="ios-lock-outline" slot="prefix" />
                        </Input>
                    </FormItem>
                    <FormItem prop="code">
                        <div class="captcha-row">
                            <Input v-model="form.code" size="large" placeholder="请输入验证码" class="captcha-input">
                                <Icon type="ios-barcode-outline" slot="prefix" />
                            </Input>
                            <div class="captcha-image" @click="getCaptchaImg">
                                <Spin v-if="loadingCaptcha" fix></Spin>
                                <img :src="captchaImg" alt="验证码" />
                            </div>
                        </div>
                    </FormItem>
                    <FormItem>
                        <Button class="register-submit-btn" type="primary" size="large" :loading="loading" @click="submitRegist" long>
                            <span v-if="!loading">注册</span>
                            <span v-else>正在注册...</span>
                        </Button>
                    </FormItem>
                </Form>
                
                <div class="login-link-section">
                    <span>已有账号？</span>
                    <router-link to="/login" class="login-link">立即登录</router-link>
                </div>
                
                <div class="footer-links">
                    <a href="javascript:void(0)">用户协议</a>
                    <span class="separator">|</span>
                    <a href="javascript:void(0)">隐私政策</a>
                </div>
            </div>
        </div>
    </div>
    
    <div class="footer-info">
        <span>版权所有 © 2025 新能源汽车动力电池回收系统 版权所有</span>
        <span class="separator">|</span>
        <span>ICP备案 桂ICP备123456789号</span>
        <span class="separator">|</span>
        <img src="@/assets/login/gonganlogo.png" class="gongan-icon" />
        <span>桂公网安备 1234567890号</span>
    </div>
</div>
</template>

<script>
import {
    regist,
    drawCodeImage,
    initCaptcha
} from "@/api/index";
import {
    validateMobile,
    validatePassword
} from "@/libs/validate";
export default {
    components: {
    },
    data() {
        return {
            captchaId: "",
            captchaImg: "",
            error: false,
            loading: false,
            errorCode: "",
            form: {
                username: "",
                type: undefined,
                password: "",
                mobile: "",
                code: "",
                captchaId: ""
            },
            rules: {
                username: [{
                        required: true,
                        message: "请输入手机号",
                        trigger: "blur"
                    },
                    {
                        validator: validateMobile,
                        trigger: "blur"
                    }
                ],
                nickname: [{
                    required: true,
                    message: "请输入您的姓名",
                    trigger: "blur"
                    }],
                type: [{
                    required: true,
                    type: "number",
                    message: "请选择用户类型",
                    trigger: "change"
                }],
                password: [{
                        required: true,
                        message: "请输入登录密码",
                        trigger: "blur"
                    },
                    {
                        validator: validatePassword,
                        trigger: "blur"
                    }
                ]
            }
        };
    },
    methods: {
        getCaptchaImg() {
            this.loadingCaptcha = true;
            initCaptcha().then(res => {
                this.loadingCaptcha = false;
                if (res.success) {
                    this.captchaId = res.result;
                    this.captchaImg = drawCodeImage + this.captchaId;
                }
            });
        },
        submitRegist() {
            this.form.captchaId = this.captchaId;
            this.form.mobile = this.form.username;
            this.$refs.usernameLoginForm.validate(valid => {
                if (valid) {
                    if (!this.form.code) {
                        this.errorCode = "验证码不能为空";
                        return;
                    } else {
                        this.errorCode = "";
                    }
                    this.loading = true;
                    regist(this.form).then(res => {
                        this.loading = false;
                        if (res.success) {
                            this.$router.push({
                                name: "login"
                            });
                        } 
                    });
                }
            });
        }
    },
    mounted() {
        this.getCaptchaImg();
    }
};
</script>

<style lang="less" scoped>
.register-container {
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

.register-content {
    width: 100%;
    max-width: 1200px;
    min-height: 500px;
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 0 60px;
    margin-top: -30px;
}

.register-left-section {
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

.register-right-section {
    width: 420px;
    flex-shrink: 0;
    
    .register-box {
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
    
    .register-header {
        text-align: center;
        margin-bottom: 35px;
        padding-bottom: 14px;
        border-bottom: 2px solid #f0f0f0;
        position: relative;
        
        h2 {
            font-size: 17px;
            font-weight: 600;
            color: #1a1a1a;
            margin: 0;
            position: relative;
            display: inline-block;
            
            &::after {
                content: '';
                position: absolute;
                bottom: -16px;
                left: 0;
                width: 100%;
                height: 3px;
                background: linear-gradient(90deg, #4096ff, #69b1ff);
                border-radius: 2px 2px 0 0;
                animation: slideIn 0.3s ease;
            }
        }
    }
    
    .register-form {
        /deep/ .ivu-form-item {
            margin-bottom: 20px;
        }
        
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

        /deep/ .ivu-select-large .ivu-select-selection {
            height: 50px;
            border-radius: 10px;
            border: 2px solid #e8e8e8;
            background: #fafafa;
            transition: all 0.3s ease;
        }

        /deep/ .ivu-select-large .ivu-select-selection:hover {
            border-color: #d0d0d0;
            background: #fff;
        }

        /deep/ .ivu-select-visible .ivu-select-selection {
            border-color: #4096ff;
            box-shadow: 0 0 0 3px rgba(64, 150, 255, 0.1);
            background: #fff;
            transform: translateY(-1px);
        }

        /deep/ .ivu-select-large .ivu-select-selected-value,
        /deep/ .ivu-select-large .ivu-select-placeholder {
            height: 46px;
            line-height: 46px;
            font-size: 14px;
        }
        
        .captcha-row {
            display: flex;
            align-items: center;
            gap: 12px;
            
            .captcha-input {
                flex: 1;
            }
            
            .captcha-image {
                width: 120px;
                height: 50px;
                border-radius: 10px;
                overflow: hidden;
                cursor: pointer;
                position: relative;
                background: #f0f0f0;
                flex-shrink: 0;
                border: 2px solid #e8e8e8;
                transition: all 0.3s ease;
                box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
                
                img {
                    width: 100%;
                    height: 100%;
                    object-fit: fill;
                }
                
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
    }
    
    .register-submit-btn {
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
    
    .login-link-section {
        text-align: center;
        margin-top: 25px;
        font-size: 14px;
        color: #666;
        
        .login-link {
            color: #4096ff;
            font-weight: normal;
            margin-left: 5px;
            text-decoration: none;
            transition: color 0.3s;
            
            &:hover {
                color: #69b1ff;
            }
        }
    }
    
    .footer-links {
        text-align: center;
        margin-top: 25px;
        padding-top: 20px;
        
        a {
            color: #999;
            font-size: 13px;
            text-decoration: none;
            transition: color 0.3s;
            
            &:hover {
                color: #4096ff;
            }
        }
        
        .separator {
            margin: 0 12px;
            color: #e8e8e8;
        }
    }
}

@keyframes slideIn {
    from { width: 0; opacity: 0; }
    to { width: 100%; opacity: 1; }
}

.footer-info {
    position: absolute;
    bottom: 0;
    left: 0;
    right: 0;
    text-align: center;
    padding: 20px 0 15px 0;
    background: linear-gradient(to bottom, rgba(0, 0, 0, 0), rgba(0, 0, 0, 0.15));
    backdrop-filter: blur(5px);
    font-size: 12px;
    color: rgba(255, 255, 255, 0.8);
    
    .separator {
        margin: 0 10px;
        color: rgba(255, 255, 255, 0.6);
    }
    
    .gongan-icon {
        width: 16px;
        height: 16px;
        margin: 0 5px;
        vertical-align: middle;
    }
}

@media (max-width: 1024px) {
    .register-content { 
        flex-direction: column; 
        padding: 40px 20px; 
    }
    .register-left-section {
        padding-right: 0;
        align-items: center;
        margin-bottom: 40px;
        .illustration { max-width: 300px; }
    }
    .register-right-section { 
        width: 100%; 
        max-width: 420px; 
    }
}
</style>

