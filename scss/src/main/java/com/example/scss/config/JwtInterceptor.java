
package com.example.scss.config;
import com.example.scss.annotation.RequireRole;
import org.springframework.web.method.HandlerMethod;
import com.example.scss.service.UserService;
import com.example.scss.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.scss.service.UserService;
import com.example.scss.entity.SysUser;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;
    @Autowired
    private UserService userService;
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
        // 根据用户名查询用户信息
        SysUser user = userService.findByUsername(username);

// 用户不存在，拒绝访问
        if (user == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }
        request.setAttribute("username", username);
        request.setAttribute("role", user.getRole());

// 获取当前请求对应的 Controller 方法
        if (handler instanceof HandlerMethod handlerMethod) {

            // 获取方法上的角色权限注解
            RequireRole requireRole =
                    handlerMethod.getMethodAnnotation(RequireRole.class);

            // 如果接口声明了角色要求，就检查当前用户角色
            if (requireRole != null) {

                String requiredRole = requireRole.value();
                String currentRole = user.getRole();

                if (!requiredRole.equals(currentRole)) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write(
                            "{\"code\":403,\"message\":\"没有权限访问该接口\",\"data\":null}"
                    );
                    return false;
                }
            }
        }

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

