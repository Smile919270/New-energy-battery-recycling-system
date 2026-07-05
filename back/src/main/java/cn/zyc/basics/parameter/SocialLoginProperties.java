package cn.zyc.basics.parameter;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "social-login")
@ApiOperation(value = "第三方登录配置")
public class SocialLoginProperties {

    @ApiModelProperty(value = "前端登录页地址")
    private String frontLoginUrl = "http://localhost:8080/login";

    @ApiModelProperty(value = "后端回调基础地址")
    private String backendBaseUrl = "http://localhost:8081";

    @ApiModelProperty(value = "微信配置")
    private ProviderConfig wechat = new ProviderConfig(
            "",
            "",
            "snsapi_login",
            "https://open.weixin.qq.com/connect/qrconnect",
            "https://api.weixin.qq.com/sns/oauth2/access_token",
            "https://api.weixin.qq.com/sns/userinfo"
    );

    @ApiModelProperty(value = "QQ配置")
    private ProviderConfig qq = new ProviderConfig(
            "",
            "",
            "get_user_info",
            "https://graph.qq.com/oauth2.0/authorize",
            "https://graph.qq.com/oauth2.0/token",
            "https://graph.qq.com/oauth2.0/me"
    );

    @ApiModelProperty(value = "Google配置")
    private ProviderConfig google = new ProviderConfig(
            "",
            "",
            "openid email profile",
            "https://accounts.google.com/o/oauth2/v2/auth",
            "https://oauth2.googleapis.com/token",
            "https://openidconnect.googleapis.com/v1/userinfo"
    );

    @Data
    public static class ProviderConfig {
        @ApiModelProperty(value = "客户端ID")
        private String clientId;

        @ApiModelProperty(value = "客户端密钥")
        private String clientSecret;

        @ApiModelProperty(value = "授权范围")
        private String scope;

        @ApiModelProperty(value = "授权地址")
        private String authorizeUrl;

        @ApiModelProperty(value = "令牌地址")
        private String tokenUrl;

        @ApiModelProperty(value = "用户信息地址")
        private String userInfoUrl;

        public ProviderConfig(String clientId, String clientSecret, String scope, String authorizeUrl, String tokenUrl, String userInfoUrl) {
            this.clientId = clientId;
            this.clientSecret = clientSecret;
            this.scope = scope;
            this.authorizeUrl = authorizeUrl;
            this.tokenUrl = tokenUrl;
            this.userInfoUrl = userInfoUrl;
        }

        public ProviderConfig() {
        }
    }
}
