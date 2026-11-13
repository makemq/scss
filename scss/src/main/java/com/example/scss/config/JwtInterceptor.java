
package com.example.scss.config;

import com.example.scss.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;

    public JwtInterceptor(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler) throws Exception {

        // 1. 获取请求头中的 Token
        String authHeader = request.getHeader("Authorization");

        // 2. 检查 Token 是否存在，以及格式是否正确
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return unauthorized(response);
        }

        // 3. 去掉 Bearer 前缀，得到真正的 Token
        String token = authHeader.substring(7);

        // 4. 验证 Token，获取用户名
        String username = jwtUtil.getUsernameFromToken(token);

        // 5. Token 无效，拒绝访问
        if (username == null || username.isBlank()) {
            return unauthorized(response);
        }

        // 6. 保存当前登录用户名，方便后续业务使用
        request.setAttribute("username", username);

        // 7. 验证成功，放行请求
        return true;
    }

    private boolean unauthorized(HttpServletResponse response)
            throws Exception {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");

        response.getWriter().write(
                "{\"code\":401,\"message\":\"未登录或登录已过期\",\"data\":null}"
        );

        return false;
    }
}

