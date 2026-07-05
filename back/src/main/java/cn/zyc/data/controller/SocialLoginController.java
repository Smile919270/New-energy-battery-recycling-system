package cn.zyc.data.controller;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.http.HttpRequest;
import cn.zyc.basics.baseVo.Result;
import cn.zyc.basics.log.LogType;
import cn.zyc.basics.log.SystemLog;
import cn.zyc.basics.parameter.CommonConstant;
import cn.zyc.basics.parameter.SocialLoginProperties;
import cn.zyc.basics.redis.RedisTemplateHelper;
import cn.zyc.basics.utils.ResultUtil;
import cn.zyc.basics.utils.SecurityUtil;
import cn.zyc.data.entity.Role;
import cn.zyc.data.entity.User;
import cn.zyc.data.entity.UserRole;
import cn.zyc.data.service.IRoleService;
import cn.zyc.data.service.IUserRoleService;
import cn.zyc.data.service.IUserService;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
@Api(tags = "第三方登录接口")
@RequestMapping("/zyc/user")
@Transactional
public class SocialLoginController {

    private static final String SOCIAL_LOGIN_STATE_PRE = "socialLogin::state::";

    @Autowired
    private IUserService iUserService;

    @Autowired
    private IRoleService iRoleService;

    @Autowired
    private IUserRoleService iUserRoleService;

    @Autowired
    private RedisTemplateHelper redisTemplate;

    @Autowired
    private SecurityUtil securityUtil;

    @Autowired
    private SocialLoginProperties socialLoginProperties;

    @PersistenceContext
    private EntityManager entityManager;

    @ApiOperation(value = "获取第三方登录授权地址")
    @GetMapping("/social/authorize")
    public Result<Object> authorize(@RequestParam String provider,
                                    @RequestParam String role,
                                    @RequestParam(required = false, defaultValue = "false") Boolean saveLogin) {
        String providerKey = normalizeProvider(provider);
        if (StrUtil.isBlank(providerKey)) {
            return ResultUtil.error("请选择第三方登录方式");
        }
        if (StrUtil.isBlank(role)) {
            return ResultUtil.error("请先选择登录角色");
        }
        ProviderContext context = getProviderContext(providerKey);
        if (context == null) {
            return ResultUtil.error("暂不支持该第三方登录方式");
        }
        String providerError = validateProviderCredentials(context);
        if (StrUtil.isNotBlank(providerError)) {
            return ResultUtil.error(providerError);
        }
        String state = UUID.randomUUID().toString().replace("-", "");
        SocialLoginState loginState = new SocialLoginState();
        loginState.setProvider(providerKey);
        loginState.setRole(role);
        loginState.setSaveLogin(Boolean.TRUE.equals(saveLogin));
        loginState.setCreatedTime(new Date().getTime());
        redisTemplate.set(SOCIAL_LOGIN_STATE_PRE + state, JSON.toJSONString(loginState), 10L, TimeUnit.MINUTES);
        String callbackUrl = socialLoginProperties.getBackendBaseUrl() + "/zyc/user/social/callback/" + providerKey;
        String authUrl = buildAuthorizeUrl(context, callbackUrl, state);
        return ResultUtil.data(authUrl);
    }

    @ApiOperation(value = "第三方登录回调")
    @GetMapping("/social/callback/{provider}")
    public void callback(@PathVariable String provider,
                         @RequestParam String code,
                         @RequestParam String state,
                         HttpServletResponse response) throws IOException {
        String providerKey = normalizeProvider(provider);
        ProviderContext context = getProviderContext(providerKey);
        if (context == null) {
            redirectLoginError(response, "暂不支持该第三方登录方式");
            return;
        }
        String stateText = redisTemplate.get(SOCIAL_LOGIN_STATE_PRE + state);
        if (StrUtil.isBlank(stateText)) {
            redirectLoginError(response, "第三方登录状态已过期，请重新登录");
            return;
        }
        redisTemplate.delete(SOCIAL_LOGIN_STATE_PRE + state);
        SocialLoginState loginState = JSON.parseObject(stateText, SocialLoginState.class);
        if (loginState == null || !Objects.equals(providerKey, loginState.getProvider())) {
            redirectLoginError(response, "第三方登录状态校验失败，请重新登录");
            return;
        }
        SocialProfile profile = fetchProfile(context, code, buildCallbackUrl(providerKey));
        if (profile == null || StrUtil.isBlank(profile.getProviderUserId())) {
            redirectLoginError(response, "第三方登录失败，请稍后重试");
            return;
        }
        User user = findOrCreateUser(providerKey, profile, loginState.getRole());
        if (user == null) {
            redirectLoginError(response, "未找到可用的本地账号，请先完成角色配置");
            return;
        }
        String accessToken = securityUtil.getToken(user.getUsername(), loginState.getSaveLogin());
        StringBuilder redirect = new StringBuilder(socialLoginProperties.getFrontLoginUrl());
        redirect.append("?socialToken=").append(URLEncoder.encode(accessToken, StandardCharsets.UTF_8));
        redirect.append("&role=").append(URLEncoder.encode(loginState.getRole(), StandardCharsets.UTF_8));
        redirect.append("&saveLogin=").append(loginState.getSaveLogin());
        response.sendRedirect(redirect.toString());
    }

    private String buildAuthorizeUrl(ProviderContext context, String callbackUrl, String state) {
        StringBuilder url = new StringBuilder(context.getAuthorizeUrl());
        if (Objects.equals(context.getProviderKey(), "wechat")) {
            url.append("?appid=").append(encode(context.getClientId()))
                    .append("&redirect_uri=").append(encode(callbackUrl))
                    .append("&response_type=code")
                    .append("&scope=").append(encode(context.getScope()))
                    .append("&state=").append(encode(state))
                    .append("#wechat_redirect");
            return url.toString();
        }
        if (Objects.equals(context.getProviderKey(), "qq")) {
            url.append("?response_type=code")
                    .append("&client_id=").append(encode(context.getClientId()))
                    .append("&redirect_uri=").append(encode(callbackUrl))
                    .append("&scope=").append(encode(context.getScope()))
                    .append("&state=").append(encode(state));
            return url.toString();
        }
        url.append("?response_type=code")
                .append("&client_id=").append(encode(context.getClientId()))
                .append("&redirect_uri=").append(encode(callbackUrl))
                .append("&scope=").append(encode(context.getScope()))
                .append("&state=").append(encode(state))
                .append("&access_type=online")
                .append("&prompt=consent");
        return url.toString();
    }

    private SocialProfile fetchProfile(ProviderContext context, String code, String callbackUrl) {
        if (Objects.equals(context.getProviderKey(), "wechat")) {
            return fetchWechatProfile(context, code, callbackUrl);
        }
        if (Objects.equals(context.getProviderKey(), "qq")) {
            return fetchQqProfile(context, code, callbackUrl);
        }
        return fetchGoogleProfile(context, code, callbackUrl);
    }

    private SocialProfile fetchWechatProfile(ProviderContext context, String code, String callbackUrl) {
        String tokenBody = HttpRequest.get(context.getTokenUrl())
                .form("appid", context.getClientId())
                .form("secret", context.getClientSecret())
                .form("code", code)
                .form("grant_type", "authorization_code")
                .execute()
                .body();
        JSONObject tokenJson = JSON.parseObject(tokenBody);
        if (tokenJson == null || tokenJson.getString("openid") == null) {
            return null;
        }
        String accessToken = tokenJson.getString("access_token");
        String openId = tokenJson.getString("openid");
        String userInfoBody = HttpRequest.get(context.getUserInfoUrl())
                .form("access_token", accessToken)
                .form("openid", openId)
                .form("lang", "zh_CN")
                .execute()
                .body();
        JSONObject userInfo = JSON.parseObject(userInfoBody);
        SocialProfile profile = new SocialProfile();
        profile.setProviderUserId(openId);
        profile.setUnionId(tokenJson.getString("unionid"));
        if (userInfo != null) {
            profile.setNickname(userInfo.getString("nickname"));
            profile.setAvatar(userInfo.getString("headimgurl"));
        }
        return profile;
    }

    private SocialProfile fetchQqProfile(ProviderContext context, String code, String callbackUrl) {
        String tokenBody = HttpRequest.get(context.getTokenUrl())
                .form("grant_type", "authorization_code")
                .form("client_id", context.getClientId())
                .form("client_secret", context.getClientSecret())
                .form("code", code)
                .form("redirect_uri", callbackUrl)
                .execute()
                .body();
        String accessToken = parseQueryValue(tokenBody, "access_token");
        if (StrUtil.isBlank(accessToken)) {
            return null;
        }
        String openIdBody = HttpRequest.get(context.getUserInfoUrl())
                .form("access_token", accessToken)
                .execute()
                .body();
        String openId = parseJsonValueFromCallback(openIdBody, "openid");
        if (StrUtil.isBlank(openId)) {
            return null;
        }
        String userInfoBody = HttpRequest.get("https://graph.qq.com/user/get_user_info")
                .form("access_token", accessToken)
                .form("oauth_consumer_key", context.getClientId())
                .form("openid", openId)
                .execute()
                .body();
        JSONObject userInfo = JSON.parseObject(userInfoBody);
        SocialProfile profile = new SocialProfile();
        profile.setProviderUserId(openId);
        if (userInfo != null) {
            profile.setNickname(userInfo.getString("nickname"));
            profile.setAvatar(userInfo.getString("figureurl_qq_2"));
        }
        return profile;
    }

    private SocialProfile fetchGoogleProfile(ProviderContext context, String code, String callbackUrl) {
        String tokenBody = HttpRequest.post(context.getTokenUrl())
                .form("client_id", context.getClientId())
                .form("client_secret", context.getClientSecret())
                .form("code", code)
                .form("grant_type", "authorization_code")
                .form("redirect_uri", callbackUrl)
                .execute()
                .body();
        JSONObject tokenJson = JSON.parseObject(tokenBody);
        if (tokenJson == null || StrUtil.isBlank(tokenJson.getString("access_token"))) {
            return null;
        }
        String accessToken = tokenJson.getString("access_token");
        String userInfoBody = HttpRequest.get(context.getUserInfoUrl())
                .header("Authorization", "Bearer " + accessToken)
                .execute()
                .body();
        JSONObject userInfo = JSON.parseObject(userInfoBody);
        if (userInfo == null) {
            return null;
        }
        SocialProfile profile = new SocialProfile();
        profile.setProviderUserId(userInfo.getString("sub"));
        profile.setNickname(StrUtil.nullToDefault(userInfo.getString("name"), userInfo.getString("email")));
        profile.setAvatar(userInfo.getString("picture"));
        profile.setEmail(userInfo.getString("email"));
        return profile;
    }

    private User findOrCreateUser(String provider, SocialProfile profile, String role) {
        Integer userType = resolveUserType(role);
        if (userType == null) {
            return null;
        }
        String providerField = getProviderField(provider);
        if (StrUtil.isBlank(providerField)) {
            return null;
        }
        QueryWrapper<User> bindQw = new QueryWrapper<>();
        bindQw.eq(providerField, profile.getProviderUserId());
        bindQw.last("limit 1");
        User user = iUserService.getOne(bindQw);
        if (user != null) {
            touchProfile(user, provider, profile);
            iUserService.saveOrUpdate(user);
            ensureRoleBinding(user.getId(), userType);
            return user;
        }
        if (StrUtil.isNotBlank(profile.getEmail())) {
            QueryWrapper<User> emailQw = new QueryWrapper<>();
            emailQw.eq("email", profile.getEmail());
            emailQw.last("limit 1");
            User matchedByEmail = iUserService.getOne(emailQw);
            if (matchedByEmail != null) {
                touchProfile(matchedByEmail, provider, profile);
                iUserService.saveOrUpdate(matchedByEmail);
                ensureRoleBinding(matchedByEmail.getId(), userType);
                return matchedByEmail;
            }
        }
        User newUser = new User();
        newUser.setNickname(StrUtil.nullToDefault(profile.getNickname(), getDefaultNickname(provider)));
        newUser.setUsername(buildGeneratedUsername(provider, profile.getProviderUserId()));
        newUser.setPassword(new BCryptPasswordEncoder().encode(UUID.randomUUID().toString()));
        newUser.setPassStrength("3");
        newUser.setType(userType);
        newUser.setStatus(CommonConstant.USER_STATUS_NORMAL);
        newUser.setAvatar(profile.getAvatar());
        newUser.setEmail(StrUtil.isNotBlank(profile.getEmail()) ? profile.getEmail() : null);
        applyProviderField(newUser, provider, profile.getProviderUserId());
        iUserService.saveOrUpdate(newUser);
        ensureRoleBinding(newUser.getId(), userType);
        return newUser;
    }

    private void touchProfile(User user, String provider, SocialProfile profile) {
        user.setNickname(StrUtil.nullToDefault(profile.getNickname(), user.getNickname()));
        if (StrUtil.isNotBlank(profile.getAvatar())) {
            user.setAvatar(profile.getAvatar());
        }
        if (StrUtil.isNotBlank(profile.getEmail()) && StrUtil.isBlank(user.getEmail())) {
            user.setEmail(profile.getEmail());
        }
        applyProviderField(user, provider, profile.getProviderUserId());
    }

    private void applyProviderField(User user, String provider, String providerUserId) {
        if (Objects.equals(provider, "wechat")) {
            user.setWechatOpenId(providerUserId);
        } else if (Objects.equals(provider, "qq")) {
            user.setQqOpenId(providerUserId);
        } else if (Objects.equals(provider, "google")) {
            user.setGoogleSub(providerUserId);
        }
    }

    private String getProviderField(String provider) {
        if (Objects.equals(provider, "wechat")) {
            return "wechatOpenId";
        }
        if (Objects.equals(provider, "qq")) {
            return "qqOpenId";
        }
        if (Objects.equals(provider, "google")) {
            return "googleSub";
        }
        return null;
    }

    private void ensureRoleBinding(String userId, Integer userType) {
        Role role = getMappedRoleByUserType(userType);
        if (role == null) {
            return;
        }
        QueryWrapper<UserRole> userRoleQw = new QueryWrapper<>();
        userRoleQw.eq("user_id", userId);
        userRoleQw.eq("role_id", role.getId());
        if (iUserRoleService.count(userRoleQw) < 1L) {
            iUserRoleService.saveOrUpdate(new UserRole().setUserId(userId).setRoleId(role.getId()));
        }
    }

    private Role getMappedRoleByUserType(Integer userType) {
        if (userType == null) {
            return null;
        }
        QueryWrapper<Role> roleQw = new QueryWrapper<>();
        if (Objects.equals(userType, 1)) {
            roleQw.and(wrapper -> wrapper.eq("name", "ROLE_ADMIN").or().like("description", "管理员"));
        } else if (Objects.equals(userType, 2)) {
            roleQw.and(wrapper -> wrapper.like("description", "电池出售方").or().like("description", "出售方"));
        } else if (Objects.equals(userType, 3)) {
            roleQw.and(wrapper -> wrapper.like("description", "电池买入方").or().like("description", "买入方"));
        } else {
            return null;
        }
        roleQw.orderByDesc("default_role");
        roleQw.orderByAsc("create_time");
        List<Role> roleList = iRoleService.list(roleQw);
        if (roleList == null || roleList.isEmpty()) {
            return null;
        }
        return roleList.get(0);
    }

    private Integer resolveUserType(String role) {
        if (StrUtil.isBlank(role)) {
            return null;
        }
        String normalizedRole = role.trim().toUpperCase();
        if (Objects.equals(normalizedRole, "ADMIN")) {
            return 1;
        }
        if (Objects.equals(normalizedRole, "SELLER")) {
            return 2;
        }
        if (Objects.equals(normalizedRole, "BUYER")) {
            return 3;
        }
        return null;
    }

    private String buildCallbackUrl(String provider) {
        return socialLoginProperties.getBackendBaseUrl() + "/zyc/user/social/callback/" + provider;
    }

    private ProviderContext getProviderContext(String provider) {
        if (Objects.equals(provider, "wechat")) {
            return new ProviderContext(provider, socialLoginProperties.getWechat());
        }
        if (Objects.equals(provider, "qq")) {
            return new ProviderContext(provider, socialLoginProperties.getQq());
        }
        if (Objects.equals(provider, "google")) {
            return new ProviderContext(provider, socialLoginProperties.getGoogle());
        }
        return null;
    }

    private String validateProviderCredentials(ProviderContext context) {
        if (context == null) {
            return "暂不支持该第三方登录方式";
        }
        if (StrUtil.isBlank(context.getClientId()) || StrUtil.isBlank(context.getClientSecret())) {
            return "请先在配置文件中填写 " + context.getProviderKey() + " 的 client-id 和 client-secret";
        }
        return null;
    }

    private String normalizeProvider(String provider) {
        return StrUtil.blankToDefault(provider, "").trim().toLowerCase();
    }

    private String getDefaultNickname(String provider) {
        if (Objects.equals(provider, "wechat")) {
            return "微信用户";
        }
        if (Objects.equals(provider, "qq")) {
            return "QQ用户";
        }
        return "Google用户";
    }

    private String buildGeneratedUsername(String provider, String providerUserId) {
        String hash = SecureUtil.md5(provider + ":" + providerUserId).substring(0, 10);
        String prefix = Objects.equals(provider, "wechat") ? "wx_" : Objects.equals(provider, "qq") ? "qq_" : "gg_";
        return prefix + hash;
    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    private String parseQueryValue(String response, String key) {
        if (StrUtil.isBlank(response) || StrUtil.isBlank(key)) {
            return null;
        }
        String[] parts = response.split("&");
        for (String part : parts) {
            String[] kv = part.split("=");
            if (kv.length == 2 && Objects.equals(kv[0], key)) {
                return kv[1];
            }
        }
        return null;
    }

    private String parseJsonValueFromCallback(String response, String key) {
        if (StrUtil.isBlank(response)) {
            return null;
        }
        int left = response.indexOf('{');
        int right = response.lastIndexOf('}');
        if (left < 0 || right < 0 || right <= left) {
            return null;
        }
        JSONObject jsonObject = JSON.parseObject(response.substring(left, right + 1));
        return jsonObject == null ? null : jsonObject.getString(key);
    }

    private void redirectLoginError(HttpServletResponse response, String message) throws IOException {
        StringBuilder redirect = new StringBuilder(socialLoginProperties.getFrontLoginUrl());
        redirect.append("?socialError=").append(URLEncoder.encode(message, StandardCharsets.UTF_8));
        response.sendRedirect(redirect.toString());
    }

    private static class ProviderContext {
        private final String providerKey;
        private final String clientId;
        private final String clientSecret;
        private final String scope;
        private final String authorizeUrl;
        private final String tokenUrl;
        private final String userInfoUrl;

        private ProviderContext(String providerKey, SocialLoginProperties.ProviderConfig config) {
            this.providerKey = providerKey;
            this.clientId = config.getClientId();
            this.clientSecret = config.getClientSecret();
            this.scope = config.getScope();
            this.authorizeUrl = config.getAuthorizeUrl();
            this.tokenUrl = config.getTokenUrl();
            this.userInfoUrl = config.getUserInfoUrl();
        }

        public String getProviderKey() {
            return providerKey;
        }

        public String getClientId() {
            return clientId;
        }

        public String getClientSecret() {
            return clientSecret;
        }

        public String getScope() {
            return scope;
        }

        public String getAuthorizeUrl() {
            return authorizeUrl;
        }

        public String getTokenUrl() {
            return tokenUrl;
        }

        public String getUserInfoUrl() {
            return userInfoUrl;
        }
    }

    private static class SocialLoginState {
        private String provider;
        private String role;
        private Boolean saveLogin;
        private Long createdTime;

        public String getProvider() {
            return provider;
        }

        public void setProvider(String provider) {
            this.provider = provider;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public Boolean getSaveLogin() {
            return saveLogin;
        }

        public void setSaveLogin(Boolean saveLogin) {
            this.saveLogin = saveLogin;
        }

        public Long getCreatedTime() {
            return createdTime;
        }

        public void setCreatedTime(Long createdTime) {
            this.createdTime = createdTime;
        }
    }

    private static class SocialProfile {
        private String providerUserId;
        private String nickname;
        private String avatar;
        private String email;
        private String unionId;

        public String getProviderUserId() {
            return providerUserId;
        }

        public void setProviderUserId(String providerUserId) {
            this.providerUserId = providerUserId;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getUnionId() {
            return unionId;
        }

        public void setUnionId(String unionId) {
            this.unionId = unionId;
        }
    }
}
