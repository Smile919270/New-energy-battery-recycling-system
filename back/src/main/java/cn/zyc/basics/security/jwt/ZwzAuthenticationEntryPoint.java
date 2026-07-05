package cn.zyc.basics.security.jwt;

import cn.zyc.basics.utils.ResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 自定义未认证处理器，返回 JSON 响应而非重定向
 */
@Component
public class ZwzAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        // 对于 AJAX 请求，返回 401 JSON 响应
        ResponseUtil.out(response, ResponseUtil.resultMap(false, 401, "登录状态失效，请重新登录"));
    }
}
